#!/usr/bin/env bash
# Compile the Kotoba ANTLR runtime to wasm and run fixtures.
# Requires kotoba 0.7.2 (https://github.com/kotoba-lang/kotoba/releases/tag/v0.7.2).
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
KOTOBA="${KOTOBA:-kotoba}"
BUILD="$ROOT/build"
ASSEMBLED="$BUILD/antlr4.runtime.kotoba"

if ! command -v "$KOTOBA" >/dev/null 2>&1; then
  echo "kotoba CLI not found; install v0.7.2 and put it on PATH" >&2
  exit 1
fi

mkdir -p "$BUILD"

{
  echo '(ns antlr4.runtime (:export [main]))'
  cat "$ROOT/src/antlr4/token.kotoba"
  cat "$ROOT/src/antlr4/interval.kotoba"
  cat "$ROOT/src/antlr4/vocabulary.kotoba"
  cat "$ROOT/src/antlr4/char_stream.kotoba"
  cat "$ROOT/src/antlr4/lexer.kotoba"
  cat "$ROOT/src/antlr4/tiny_lexer.kotoba"
  cat "$ROOT/test/runtime_test.kotoba"
} > "$ASSEMBLED"

echo "== compile wasm =="
set +e
"$KOTOBA" compile "$ASSEMBLED" --target wasm --output "$BUILD/runtime_test.wasm" --json \
  > "$BUILD/wasm.json"
set -e
if [ ! -s "$BUILD/wasm.json" ]; then
  echo "kotoba compile produced no JSON" >&2
  exit 1
fi
python3 - "$BUILD/wasm.json" "$BUILD/runtime_test.wasm" <<'PY'
import json, sys
payload = json.load(open(sys.argv[1]))
if not payload.get("kotoba.cli/ok?"):
    sys.stderr.write("wasm compile failed: %s\n" % payload.get("kotoba.cli/message"))
    sys.exit(1)
if payload.get("kotoba.cli/code") != "emitted":
    sys.stderr.write("expected emitted, got %s\n" % payload.get("kotoba.cli/code"))
    sys.exit(1)
data = open(sys.argv[2], "rb").read()
if data[:4] != b"\x00asm":
    sys.stderr.write("output is not a wasm module\n")
    sys.exit(1)
print("wasm bytes", len(data))
PY

echo "== run fixtures (web) =="
set +e
"$KOTOBA" compile "$ASSEMBLED" --target web --run --json --output "$BUILD/antlr4.runtime.mjs" \
  > "$BUILD/run.json"
set -e
if [ ! -s "$BUILD/run.json" ]; then
  echo "kotoba compile --run produced no JSON" >&2
  exit 1
fi
python3 - "$BUILD/run.json" <<'PY'
import json, sys
payload = json.load(open(sys.argv[1]))
data = payload.get("kotoba.cli/data") or {}
print("ok", payload.get("kotoba.cli/ok?"), "code", payload.get("kotoba.cli/code"),
      "result", data.get("result"))
if not payload.get("kotoba.cli/ok?"):
    err = data.get("error") or payload.get("kotoba.cli/message") or ""
    err = str(err)
    sys.stderr.write((err if len(err) < 500 else err[-500:]) + "\n")
    sys.exit(1)
if data.get("result") != 1:
    sys.stderr.write("fixture main returned %r, expected 1\n" % (data.get("result"),))
    sys.exit(1)
print("fixtures passed")
PY
