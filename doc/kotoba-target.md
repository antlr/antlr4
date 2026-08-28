# ANTLR4 Runtime for Kotoba

Kotoba is a Clojure-shaped language that compiles `.kotoba` source to
WebAssembly. The runtime lives at `runtime/Kotoba`. Kotoba cannot call
the Java runtime through FFI, so this library is a native port.

**v1 is a minimal subset.** A full ATN/DFA runtime and a Java-tool
codegen target (`-Dlanguage=Kotoba`) are larger than one first PR. This
tree lands the same public types the other runtimes start from, plus an
ATN-less lexer that matches a vendored tiny grammar.

## First steps

### 1. Install kotoba 0.7.2

Release assets: https://github.com/kotoba-lang/kotoba/releases/tag/v0.7.2

```bash
curl -fsSL -o kotoba-linux-amd64.tar.gz \
  https://github.com/kotoba-lang/kotoba/releases/download/v0.7.2/kotoba-linux-amd64.tar.gz
tar -xzf kotoba-linux-amd64.tar.gz
export PATH="$PWD:$PATH"
```

### 2. Build and test the runtime

```bash
cd runtime/Kotoba
./scripts/test.sh
```

That compiles the assembled module to `build/runtime_test.wasm` and runs
the fixture suite. Success is `main` returning `1`.

There is no `antlr4 -Dlanguage=Kotoba` generator yet. Hand-written
scanners use the records in `runtime/Kotoba/src/antlr4`.

## v1 API

Assembled namespace: `antlr4.runtime`.

- `CommonToken` — type, channel, start, stop, line, column, tokenIndex, text
- `Interval` — inclusive `a..b`, length, union, intersection, disjoint
- Tiny vocabulary — `tiny-literal-name` / `tiny-symbolic-name` / `tiny-display-name`
- `CharStream` — `from-string`, `stream-la`, `stream-consume`, `stream-seek`, `stream-get-text`
- `Lexer` / `next-token` — ATN-less scanner for [`runtime/Kotoba/fixtures/Tiny.g4`](../runtime/Kotoba/fixtures/Tiny.g4)

Kotoba values are immutable. `stream-consume` and `next-token` return
new records (`TokenResult` pairs a token with the updated lexer).

## Follow-ups

1. Java tool `KotobaTarget` + `Kotoba.stg` codegen (`-Dlanguage=Kotoba`)
2. ATN deserializer and `LexerATNSimulator`
3. Parser ATN simulator, DFA cache, prediction context
4. `CommonTokenStream`, parse trees, listeners, visitors
5. Error listeners and error strategy
6. `runtime-testsuite` descriptors and a hosted CI job
7. General `VocabularyImpl` over string tables (typed Wasm currently
   rejects untyped collection record fields)

## Notes

- Source files under `src/antlr4` are fragments. Kotoba 0.7.2 does not
  admit in-component `require`; `scripts/test.sh` concatenates them
  under one namespace.
- `kotoba compile --target wasm` is the portable artifact path.
  `kotoba compile --target web --run` is the fixture runner used here.
- This runtime does not rewrite the Java tool.
