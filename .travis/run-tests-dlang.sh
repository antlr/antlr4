#!/bin/bash

set -euo pipefail

mvn -q -Dtest=d.* test
