# Kotoba target for ANTLR 4

Kotoba runtime libraries for ANTLR 4. Kotoba is a Clojure-shaped language
that compiles `.kotoba` source to WebAssembly. It cannot FFI the Java
runtime, so this tree is a native Kotoba implementation.

v1 is a **minimal runtime subset**. It matches the other `runtime/*` trees
in layout (Token, Interval, Vocabulary, CharStream, Lexer) and can scan a
vendored tiny lexer grammar. ATN/DFA, parse trees, and Java-tool codegen
are out of scope for this first PR.

See [doc/kotoba-target.md](../../doc/kotoba-target.md) for status and
follow-ups.

## Layout

```
runtime/Kotoba/
  src/antlr4/          Token, Interval, Vocabulary, CharStream, Lexer
  fixtures/Tiny.g4     vendored ATN-less lexer grammar
  test/                assembled self-checks (main returns 1 on success)
  scripts/test.sh      compile .kotoba -> wasm, then run fixtures
```

## Requirements

- [kotoba](https://github.com/kotoba-lang/kotoba) **v0.7.2**
- `python3` (JSON checks in `scripts/test.sh`)

```bash
# linux-amd64 example
curl -fsSL -o kotoba.tar.gz \
  https://github.com/kotoba-lang/kotoba/releases/download/v0.7.2/kotoba-linux-amd64.tar.gz
tar -xzf kotoba.tar.gz
export PATH="$PWD:$PATH"
```

## Test

```bash
./scripts/test.sh
```

The script concatenates the runtime fragments into one `antlr4.runtime`
module (Kotoba v0.7.2 has no in-component `require`), emits
`build/runtime_test.wasm`, and runs the same module via
`kotoba compile --target web --run`. `main` returns `1` when every
Token / Interval / Vocabulary / CharStream / Tiny-lexer check passes.

## v1 surface

| Type / op | Status |
|---|---|
| `CommonToken` | yes |
| `Interval` (inclusive `a..b`) | yes |
| Tiny `Vocabulary` names | yes |
| `CharStream` (`LA`, consume, seek, getText) | yes |
| ATN-less `Lexer` for `fixtures/Tiny.g4` | yes |
| ATN deserializer / DFA / parser | follow-up |
| `-Dlanguage=Kotoba` Java-tool codegen | follow-up |

Values are immutable. Stream and lexer operations return a new record.
The Tiny scanner is hand-written so the Java tool does not need a Kotoba
target class.
