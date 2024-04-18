#!/usr/bin/env bash

tsconfigFiles=(
  "tsconfig.node.commonjs.json"
)

failure=0

for tsconfig in "${tsconfigFiles[@]}"; do
  echo -n "$tsconfig "

  ./node_modules/.bin/tsc -p $tsconfig || { failure=1 ; echo "FAIL tsc: $tsconfig"; }
  result=$(node ./tsOutput/index.js)
  [[ $result == "OK" ]] && echo "OK"
  [[ $result != "OK" ]] && { failure=1 ; echo "FAIL loading runtime with config: $tsconfig"; }
  rm -rf ./tsOutput
done

exit $failure