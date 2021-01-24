#!/bin/bash

set -euo pipefail

eval "$(sudo gimme 1.7.3)"
( go version ; go env ) || true
