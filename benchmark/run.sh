#!/bin/sh -e

# Script to build and run ANTLR 4 microbenchmarks.

mvn clean install
java -jar target/microbenchmarks.jar
