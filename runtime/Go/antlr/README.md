# Antlr4 Go runtime

The intention of this repository is to make it easier to get up a running with Antlr4 in Go.
This is Go gettable and contains a build Antlr4 jar file, an example grammar, Go test file and of course the runtime source.
The test file contain a go:generate comment, so the go tool can be used to call the antlr4 tool.

## Getting started

```
mkdir antlr4test
cd antlr4test
mkdir src
export GOPATH=`pwd`
go get github.com/wxio/antlr4-go
go get github.com/wxio/antlr4-go-examples
cd src/github.com/wxio/antlr4-go-examples
go generate ./...
go test ./...
```

The `go generate` command read the source and executes commands specified by in `//go:generate xxx` lines.
The example has such a line, which calls the Antlr4 tool and then `sed` to replace the import statement in the generated code.

## Issues
This only works on OSX as the `sed` for OSX is different.
To get this to work in Linux or Windows, change the sed command or edit the creating and modifying a generate file (eg. gen_linux.go).

## Notes

Go runtime package for Antlr4. Also contains Antlr4 jar file and examples.

This is simply a split of the Go runtime from the main github repository.
See https://help.github.com/articles/splitting-a-subfolder-out-into-a-new-repository/ regarding split a repo

## Building Antlr4

```
# install maven
sudo apt install maven
# choose appropriate java version (worked with jdk 1.8)
sudo update-alternatives --config java

git clone git@github.com:wxio/antlr4.git
cd antlr4
# change snapshot version
# todo something like find . -name "pom.xml" | xargs -i sed -e 's!4.7.3-SNAPSHOT!4.7.4-SNAPSHOT!' {}
mvn install -DskipTests

# in antlr4-go
cd ../antlr4-go/v4
rm lib/wxio/*
cp ../../antlr4/tool/target/antlr4-4.7.3-SNAPSHOT* lib/wxio/
# git tag and push
```
