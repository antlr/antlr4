#!/bin/bash

# only test swift as we develop on os x so likely well tested and its dog slow on travis
mvn -Dtest=swift.* test
