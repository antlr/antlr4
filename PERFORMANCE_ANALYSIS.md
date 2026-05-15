# ANTLR4 C# Runtime — Span/Memory Performance Analysis

## Current State

- **TFM**: `netstandard2.0` (+ `net45` on Windows)
- **Span/Memory usage**: Zero. No `System.Memory` polyfill.
- **C# version**: ~5/6 style. No modern features.
- **Assembly**: `Antlr4.Runtime.Standard` (strong-named)

## Prerequisite: TFM Upgrade Strategy

netstandard2.0 has no native Span<T>. Two paths:

### Option A: Multi-target (recommended)
```xml
<TargetFrameworks>netstandard2.0;netstandard2.1;net8.0</TargetFrameworks>
```
- netstandard2.0: add `System.Memory` NuGet polyfill (works on .NET Framework 4.6.1+)
- netstandard2.1: native Span, no polyfill needed
- net8.0: full access to modern APIs (SearchValues, stackalloc relaxations, etc.)
- Use `#if` guards for features only available on newer TFMs

### Option B: Add polyfill only
Add `<PackageReference Include="System.Memory" />` conditional on netstandard2.0.
Gets Span/Memory everywhere but no stackalloc expressions, no ref struct in generics, etc.

**Recommendation**: Option A. Multi-target lets you do stackalloc + Span on net8.0 while
maintaining full backwards compat on netstandard2.0.

---

## Tier 1 — High Impact, Non-Breaking

These changes are internal implementation details. The public API surface is unchanged.

### 1. AntlrInputStream: Eliminate double allocation on construction

**Current**: `string input` → `input.ToCharArray()` → stored as `char[] data`
**Problem**: Every parse starts with a full copy of the input string into a char[].
**Fix**: Store the original `string` directly. Access via `str.AsSpan()` internally.

```csharp
// Before
protected internal char[] data;  // ← this is protected internal (visible to subclasses)

// After (internal fast path, keep char[] for compat)
private string _source;          // store string directly
protected internal char[] data => _dataArray ??= _source.ToCharArray(); // lazy for subclass compat
```

The `char[] data` field is `protected internal`, so subclasses may reference it. The lazy
materialization preserves that contract while avoiding the copy for the common path where
data is accessed only through `LA()` and `GetText()`.

**Estimated savings**: 2× input size in allocations eliminated per parse.

### 2. AntlrInputStream.GetText(): Return slice instead of allocating

**Current**: `new string(data, start, count)` — allocates on every call.
**Fix**: Add internal `ReadOnlySpan<char> GetTextSpan(int start, int count)` used by
internal callers (Lexer, CommonToken). Keep `GetText(Interval)` for public API.

If storing string internally: `_source.AsSpan(start, count)` — zero alloc.

### 3. CommonToken.Text: Defer string materialization

**Current**: `Text` getter calls `input.GetText(Interval)` → allocates string, caches in `_text`.
**Problem**: Called for every token, but many tokens' text is never read by user code.
**Fix**: Add an internal `ReadOnlyMemory<char>` field that captures the slice lazily.
Only materialize to string when the public `Text` property is actually accessed.

```csharp
private ReadOnlyMemory<char>? _textMemory;  // cheap slice
private string _text;                        // materialized on demand

public virtual string Text {
    get {
        if (_text != null) return _text;
        if (_textMemory.HasValue) {
            _text = _textMemory.Value.ToString();
            return _text;
        }
        // existing GetText path
    }
}
```

**Estimated savings**: Eliminates string allocation for every token whose text is never read.

### 4. Tuple → ValueTuple for token source pairs

**Current**: `Tuple<ITokenSource, ICharStream>` — heap allocation per pair.
**Fix**: `(ITokenSource, ICharStream)` ValueTuple — stack allocated.

```csharp
// Before
protected internal Tuple<ITokenSource, ICharStream> source;
// After  
protected internal (ITokenSource Source, ICharStream Stream) source;
```

**Breaking?**: The field is `protected internal`. Subclasses accessing `.Item1`/`.Item2` break.
Mitigate by keeping a `[Obsolete]` Tuple property or making this change only on net8.0+ TFM.

### 5. CharStreams.fromPath/fromReader: BMP fast path

**Current**: All input goes through `CodePointCharStream` which allocates `int[]`.
**Fix**: Detect if input is BMP-only (no surrogate pairs) and use the char-based
`AntlrInputStream` directly. BMP-only is the overwhelmingly common case (ASCII, Latin, CJK).

```csharp
public static ICharStream fromString(string s, string sourceName = "") {
    // Fast BMP check: if no surrogates, use char-based stream
    if (!ContainsSurrogates(s.AsSpan()))
        return new AntlrInputStream(s) { name = sourceName };
    return new CodePointCharStream(s, sourceName);
}

private static bool ContainsSurrogates(ReadOnlySpan<char> s) {
    foreach (char c in s)
        if (char.IsSurrogate(c)) return true;
    return false;
}
```

---

## Tier 2 — Medium Impact, Internal Changes

### 6. BufferedTokenStream.GetText(): Use ValueStringBuilder

**Current**: `new StringBuilder()` per call.
**Fix**: Use a stack-allocated `ValueStringBuilder` (a la dotnet/runtime's internal type).
Copy the ~200-line struct into a Sharpen/ or Misc/ utility file.

On net8.0+ with stackalloc:
```csharp
Span<char> buffer = stackalloc char[256];
var vsb = new ValueStringBuilder(buffer);
```

### 7. Object pooling for ATN simulation hot objects

The ATN simulator allocates these on every prediction:
- `HashSet<ATNConfig> closureBusy` — can be enormous
- `ATNConfigSet` — multiple per ComputeReachSet call
- `DoubleKeyMap mergeCache`

**Fix**: Use `ObjectPool<T>` (or a simple thread-local pool) to reuse these between predictions.

```csharp
private static readonly ObjectPool<HashSet<ATNConfig>> s_closureBusyPool = 
    ObjectPool.Create<HashSet<ATNConfig>>();

// In ComputeReachSet:
var closureBusy = s_closureBusyPool.Get();
try { ... }
finally {
    closureBusy.Clear();
    s_closureBusyPool.Return(closureBusy);
}
```

**Estimated savings**: Major reduction in Gen2 GC pressure for complex grammars.

### 8. ArrayPool for DFA edges arrays

**Current**: `new DFAState[maxTokenType + 2]` per DFA state.
**Fix**: Rent from `ArrayPool<DFAState>`. DFA states are long-lived (cached), so this
helps during the initial build phase when many states are created rapidly.

### 9. UnbufferedCharStream: ArrayPool for buffer growth

**Current**: `Arrays.CopyOf(data, data.Length * 2)` — alloc + copy.
**Fix**: Use `ArrayPool<int>.Shared.Rent()` and return old buffer on growth.

---

## Tier 3 — Additive API Surface (Non-Breaking)

### 10. ICharStream2 interface with Memory-based GetText

Add a new interface that char streams can optionally implement:

```csharp
public interface ICharStreamMemory : ICharStream {
    ReadOnlyMemory<char> GetTextMemory(Interval interval);
}
```

Internal code (CommonToken, Lexer) checks `if (stream is ICharStreamMemory mem)` and
uses the zero-alloc path. External consumers get no API change. AntlrInputStream implements
both interfaces.

### 11. ReadOnlySpan overloads on ATNDeserializer

**Current**: `Deserialize(int[] data)`
**Add**: `Deserialize(ReadOnlySpan<int> data)` — avoids copying the static ATN array
in generated parsers. The generated code has `static readonly int[] _serializedATN` which
is already an array, but future codegen could use `ReadOnlySpan<int>` on net8.0+.

---

## Tier 4 — Codegen Changes (antlr-ng coordination)

The antlr-ng C# template (`CSharp.stg`) generates:
- `int[]` serialized ATN data
- `string[]` for rule names, token names, channel names
- Constructor taking `ITokenStream` / `ICharStream`
- `int.Parse(token.Text)` for numeric literals

### Future codegen improvements (require template changes):
1. **ATN data as ReadOnlySpan<int>**: `private static ReadOnlySpan<int> _serializedATN => new int[] { ... };`
   Only on net8.0+ (ROS<T> property pattern). Eliminates static array field allocation.
2. **Token name arrays as ReadOnlySpan<string>**: Same pattern.
3. These require `#if NET8_0_OR_GREATER` guards in templates.

---

## Backwards Compatibility Matrix

| Change | Public API Break? | Protected API Break? | Binary Break? | Notes |
|--------|:-:|:-:|:-:|-------|
| Store string internally in AntlrInputStream | No | Mitigatable | No | Lazy char[] for subclasses |
| Add ICharStreamMemory interface | No | No | No | Additive |
| ValueTuple in CommonToken.source | No | **Yes** | **Yes** | Guard with #if or keep shim |
| ValueStringBuilder in GetText | No | No | No | Internal impl |
| Object pooling in ATN simulator | No | No | No | Internal impl |
| ArrayPool for buffers | No | No | No | Internal impl |
| Add Span overloads to ATNDeserializer | No | No | No | Additive overload |
| BMP fast path in CharStreams | No | No | No | Internal impl |

---

## Suggested Implementation Order

1. **Multi-target the csproj** — add netstandard2.1 + net8.0 TFMs, add System.Memory polyfill for ns2.0
2. **AntlrInputStream string storage** — biggest single win, easiest change
3. **BMP fast path in CharStreams** — eliminates int[] for 99% of inputs  
4. **CommonToken deferred text** — eliminates per-token string allocation
5. **ValueStringBuilder** — drop-in replacement for all StringBuilder.ToString() patterns
6. **ICharStreamMemory** — additive interface for zero-alloc text access
7. **Object pooling in ATN simulator** — biggest GC pressure reduction for complex grammars
8. **ArrayPool for buffers** — finish off remaining allocation hotspots

---

## Benchmarking Plan

Before any changes, establish baselines with BenchmarkDotNet:

```csharp
[MemoryDiagnoser]
[SimpleJob(RuntimeMoniker.Net80)]
public class ParserBenchmarks {
    [Benchmark] public void ParseSmallInput()   { /* ~100 tokens */ }
    [Benchmark] public void ParseMediumInput()   { /* ~10K tokens */ }
    [Benchmark] public void ParseLargeInput()   { /* ~100K tokens */ }
}
```

Key metrics: Gen0/Gen1/Gen2 collections, allocated bytes, throughput (ops/sec).
Test with a real grammar (e.g., a C# or Java grammar from grammars-v4).
