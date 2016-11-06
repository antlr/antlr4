#!/bin/bash

set -euo pipefail

( go version ; go env ) || true
python --version
python3 --version
