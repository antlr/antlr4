# ANTLR4 C# Runtime — Round 2 Optimization Opportunities

Findings from analysis of all critical paths: lexer, parser, and utility/data-structure layers.
Items marked [DONE] were addressed in the first optimization pass.

## Tier 1 — High Impact, Hot Path

### 1. MergeCache: nested Dictionary → flat tuple-keyed Dictionary
**File:** `Atn/MergeCache.cs`
Current: `Dictionary<PredictionContext, Dictionary<PredictionContext, PredictionContext>>`
Every `Get()` does TWO dictionary lookups. Every new `a` key allocates an inner dictionary.
**Fix:** `Dictionary<(PredictionContext, PredictionContext), PredictionContext>` — single
lookup, no inner dict allocations.
**Risk:** Low — MergeCache is internal.

### 2. ConfigHashSet: Dictionary<ATNConfig,ATNConfig> → HashSet + TryGetValue
**File:** `Atn/ATNConfigSet.cs` (line 408)
Stores each config as BOTH key AND value, doubling reference storage.
On netstandard2.1+, `HashSet<T>.TryGetValue` gives the existing element directly.
On net8.0, `CollectionsMarshal.GetValueRefOrAddDefault` avoids double lookup on the dictionary path.
**Fix:** #if switch: `HashSet<ATNConfig>` on netstandard2.1+, keep dictionary on netstandard2.0.
**Risk:** Low-medium — ConfigHashSet is internal, but GetOrAdd semantics must be preserved.

### 3. Pool OrderedATNConfigSet in LexerATNSimulator.ComputeTargetState
**File:** `Atn/LexerATNSimulator.cs` (line 260)
`new OrderedATNConfigSet()` on every DFA miss. During DFA warmup this is per-character.
**Fix:** `ObjectPool<OrderedATNConfigSet>` + `PoolReset()` (already exists).
**Risk:** Low — same pattern as ParserATNSimulator pooling [DONE].

### 4. Pool/reuse ATNConfigSet allocations in ParserATNSimulator
**File:** `Atn/ParserATNSimulator.cs` (lines 1003, 1033, 1219, 1459-1460)
Several methods create `new ATNConfigSet(...)` without using the existing pool.
**Fix:** Extend the pool pattern to cover ComputeStartState, RemoveAllConfigs,
ApplyPrecedenceFilter, and SplitAccordingToSemanticValidity.
**Risk:** Low — must ensure returned sets aren't retained by callers.

### 5. IntervalSet.Contains: linear scan → binary search
**File:** `Misc/IntervalSet.cs` (line 476)
Called extremely frequently during prediction. Intervals are sorted.
**Fix:** Binary search on `intervals[i].a` / `intervals[i].b`.
**Risk:** None — pure optimization, identical semantics.

### 6. PredictionContext.MergeArrays: double allocation → ArrayPool
**File:** `Atn/PredictionContext.cs` (lines 250-253, 323-324)
Allocates `int[a+b]` and `PredictionContext[a+b]`, then `Arrays.CopyOf` to trim.
**Fix:** `ArrayPool<int>.Shared.Rent()` for the working buffers, single exact-size alloc at end.
**Risk:** Low — must return rented arrays in finally blocks.

## Tier 2 — Medium Impact

### 7. LexerActionExecutor.Append: array copy per action
**File:** `Atn/LexerActionExecutor.cs` (lines 95-104)
`Arrays.CopyOf` + new `LexerActionExecutor` on every Append.
**Fix:** Use `ArrayPool<ILexerAction>` for the new array.
**Risk:** Low.

### 8. LexerActionExecutor.FixOffsetBeforeMatch: Clone()
**File:** `Atn/LexerActionExecutor.cs` (line 155)
Array.Clone() per config with actions during ATN sim.
**Fix:** ArrayPool or track offset without copying.
**Risk:** Low.

### 9. IntervalSet.Equals: LINQ SequenceEqual → manual loop
**File:** `Misc/IntervalSet.cs` (line 608)
Allocates two enumerators. Interval is a struct → boxes on each comparison.
**Fix:** Index-based loop: `intervals[i].a == other.intervals[i].a`.
**Risk:** None.

### 10. Interval: implement IEquatable<Interval>
**File:** `Misc/Interval.cs` (line 62)
`Equals(object)` boxes the struct. Used in list comparisons throughout.
**Fix:** Add `IEquatable<Interval>`, add `== / !=` operators.
**Risk:** None — purely additive.

### 11. SemanticContext.FilterPrecedencePredicates: LINQ + bug
**File:** `Atn/SemanticContext.cs` (lines 444-449)
- Iterates collection TWICE with `OfType<>().Any()` then `OfType<>().ToList()`
- BUG: line 445 discards `EmptyList` result — missing `return`
**Fix:** Single manual loop. Fix the bug.
**Risk:** Low — bug fix is correctness improvement.

### 12. BitSet.Cardinality → BitOperations.PopCount
**File:** `Sharpen/BitSet.cs` (line 177)
Manual bit-twiddling for popcount. Hardware POPCNT is 10-100x faster.
**Fix:** `#if NET8_0_OR_GREATER` → `BitOperations.PopCount(data[i])`.
**Risk:** None.

### 13. MurmurHash boxing: add int overload
**File:** `Misc/MurmurHash.cs`
`Update(int hash, object value)` boxes ints. Called from LexerATNConfig.GetHashCode
with `passedThroughNonGreedyDecision ? 1 : 0`.
**Fix:** Add `Update(int hash, int value)` overload.
**Risk:** None — additive.

### 14. ArrayList.GetHashCode/Equals: enumerator allocation + boxing
**File:** `Misc/ArrayList.cs` (lines 26, 42-48)
Uses `GetEnumerator()` → allocates. `t.GetHashCode()` boxes value types.
**Fix:** Index-based for loops, `EqualityComparer<T>.Default`.
**Risk:** None.

### 15. Pair<T,U> → ValueTuple
**File:** `Atn/ParserATNSimulator.cs` (line 1478)
`new Pair<ATNConfigSet, ATNConfigSet>(...)` — heap allocation for a return value.
**Fix:** Return `(ATNConfigSet, ATNConfigSet)` ValueTuple.
**Risk:** Medium — Pair is public API. Add a parallel method returning ValueTuple.

### 16. PredictionMode: dictionary/BitSet allocations per conflict check
**File:** `Atn/PredictionMode.cs` (lines 786, 812, 818, 844)
`GetConflictingAltSubsets` allocates a Dictionary + BitSet per unique config.
`GetStateToAltMap` allocates another Dictionary + BitSets.
**Fix:** Pool these temporary collections or use thread-local reusable instances.
**Risk:** Low — all are method-local temporaries.

### 17. PredictionContext.MergeSingletons: small array allocations
**File:** `Atn/PredictionContext.cs` (lines 174-175)
`new int[2]` and `new PredictionContext[2]` per merge of singletons with different payloads.
**Fix:** On net8.0, use `stackalloc int[2]` with a span-accepting ArrayPredictionContext ctor
or pool the tiny arrays.
**Risk:** Low.

### 18. PredictionContext.CombineCommonParents: dict allocation per MergeArrays
**File:** `Atn/PredictionContext.cs` (lines 357-373)
New `Dictionary<PredictionContext, PredictionContext>` every MergeArrays call.
**Fix:** Pool or thread-local reusable dictionary with Clear().
**Risk:** Low.

## Tier 3 — Low Impact / Polish

### 19. DFA.states lock contention
**File:** `Atn/LexerATNSimulator.cs` (line 685)
`lock(p)` in AddDFAEdge — pure overhead for single-threaded lexing.
**Fix:** Use `Interlocked.CompareExchange` for idempotent edge assignment, or add
a single-threaded mode flag.
**Risk:** Medium — concurrency semantics.

### 20. DFAState hash code not cached eagerly
**File:** `Dfa/DFAState.cs`
Hash recomputed on every dictionary lookup via `configSet.GetHashCode()`.
**Fix:** Compute and cache in constructor once configSet is set.
**Risk:** Low.

### 21. Parser.Consume() micro-optimizations
**File:** `Parser.cs` (lines 724-758)
- Unnecessary cast `InputStream` to `ITokenStream` (line 729) — use `_input` directly
- Check `_parseListeners` separately from tree building
**Fix:** Conditional fast-path for common case (no listeners, no parse tree).
**Risk:** Low.

### 22. SemanticContext.AND/OR: typeof().GetHashCode() per call
**File:** `Atn/SemanticContext.cs` (lines 224, 342)
`typeof(SemanticContext.AND).GetHashCode()` does reflection work each time.
**Fix:** Cache in `static readonly int TypeHash = typeof(...).GetHashCode()`.
**Risk:** None.

### 23. BitSet.NextSetBit calls IsEmpty first
**File:** `Sharpen/BitSet.cs` (line 187)
Scans all data just to check emptiness before the real scan.
**Fix:** Remove the check — the loop naturally returns -1 for empty.
**Risk:** None.

### 24. trace_atn_sim not readonly
**File:** `Atn/ParserATNSimulator.cs` (line 244)
`static bool trace_atn_sim` — JIT cannot eliminate branches.
**Fix:** Make `static readonly bool`.
**Risk:** None (unless someone sets it at runtime — in which case use `#if`).

### 25. ATNConfigSet.ToString: hasSemanticContext.ToString().ToLower()
**File:** `Atn/ATNConfigSet.cs` (line 344)
Allocates strings for "True"→"true" conversion.
**Fix:** Ternary: `hasSemanticContext ? "true" : "false"`.
**Risk:** None.

### 26. IntervalSet.GetHashCode: enumerator allocation
**File:** `Misc/IntervalSet.cs` (line 578)
`foreach` over `IList<Interval>` allocates.
**Fix:** Index-based for loop.
**Risk:** None.

---

## What NOT to change (backward compat)

- `ITokenFactory.Create` signature (uses `Tuple<>`) — too many downstream implementors
- `Pair<T,U>` class — public API, deprecate but keep
- `ATNConfig` class → struct — too much downstream code treats it as reference type
- `ICharStream` interface — keep virtual dispatch, use type-check fast-path instead

## Recommended implementation order

**Phase 1 (zero-risk, high-value):**
Items 1, 3, 5, 9, 10, 12, 13, 14, 22, 23, 24, 25, 26

**Phase 2 (low-risk, high-value):**
Items 2, 4, 6, 7, 8, 11, 17, 18

**Phase 3 (medium-risk, cleanup):**
Items 15, 16, 19, 20, 21
