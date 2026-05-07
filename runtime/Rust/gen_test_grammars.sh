#!/usr/bin/env bash
set -euo pipefail

TOOL_VERSION="4.13.3-DBT133"
ANTLR_PATH="../../../tool/target/antlr4-${TOOL_VERSION}-complete.jar"
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

declare -a GRAMMARS=(
    "VisitorBasic"
    "VisitorCalc"
    "CSV"
    "ReferenceToATN"
    "XMLLexer"
    "SimpleLR"
    "Labels"
#    "FHIRPath"
)

declare -a ADDITIONAL_ARGS=(
    "-visitor"
    "-visitor"
    "-visitor"
    ""
    ""
    ""
    "-stacker"
)

for i in "${!GRAMMARS[@]}"; do
    grammar="${GRAMMARS[$i]}"
    arg="${ADDITIONAL_ARGS[$i]}"
    file_name="${grammar}.g4"

    cmd=(java -cp "$ANTLR_PATH" org.antlr.v4.Tool -Dlanguage=Rust -o ../tests/gen "$file_name")
    if [[ -n "$arg" ]]; then
        cmd+=("$arg")
    fi

    echo "Generating: $grammar"
    (cd "$SCRIPT_DIR/grammars" && "${cmd[@]}")
done
