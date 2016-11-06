#!/bin/bash

set -euo pipefail

thisdir=$(dirname $(readlink -f "$0"))

"$thisdir/before_install-common.sh"
