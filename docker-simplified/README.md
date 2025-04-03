# Simplified Docker

Similar to the `docker` directory, but with a simplified and more universally runnable build.

## Why would I want this?

Good question! Docker contains a convenient cross platform way to manage dependencies that, at least for the most part "just works".

You might want to use this if you don't have/don't want Java on your system, or if you're having issues with the Java versions etc...

This was created because the Java installation on my ARM OSx Macbook Air was being a bit painful, and this (at least with  my skills) was easier.

Compared to the full Docker image, the build is drastically faster and the base image was chosen to support more architectures (eg. OSx on ARM as in my case)

## Build

There are no dependencies other than Docker itself

`cd` here: `cd docker-simplified`

Build and tag this image: `docker build --tag antlr/antlr4 .`

## Usage

`./antlr4` is a simple shell script that will pass the arguments to the `antlr4` command in the container, using the CWD as the directory passed to antlr4.

Should be pretty transparent compared to a native `antlr4` install
