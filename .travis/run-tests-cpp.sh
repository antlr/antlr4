#!/bin/bash

mvn -q -Dtest=cpp.* test # timeout due to no output for 10 min on travis if in parallel
