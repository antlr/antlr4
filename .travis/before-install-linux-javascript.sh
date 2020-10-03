#!/bin/bash

set -euo pipefail

# use v14 and check
nvm install 14
nvm use 14
node --version
