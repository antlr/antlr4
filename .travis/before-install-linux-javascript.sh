#!/bin/bash

set -euo pipefail

sudo apt-get update -qq
sudo n 14
node --version
