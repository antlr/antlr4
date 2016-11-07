#!/bin/bash

set -euo pipefail

thisdir=$(dirname "$0")

brew update
brew install python3

"$thisdir/before_install-common.sh"
