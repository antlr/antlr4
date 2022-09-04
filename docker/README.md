# Docker Image for ANTLR4

This Docker image wraps current version of **ANTLR4** inclusive **Java runtime environment** so it can be executed as transparent command line tool even on machines without installed Java.

## Docker Image

The image uses the official [adoptopenjdk/openjdk11:alpine](https://hub.docker.com/r/adoptopenjdk/openjdk11/tags?page=1&name=alpine&ordering=-name) image
for building a distribution of ANTLR4 and [adoptopenjdk/openjdk11:alpine-jre](https://hub.docker.com/r/adoptopenjdk/openjdk11/tags?page=1&name=alpine-jre&ordering=-name) for runtime.

## Build

You can build docker image from source code locally. 

    git clone https://github.com/antlr/antlr4.git
    cd antlr4/docker
    docker build -t antlr/antlr4 --platfort linux/amd64　.    


## Run

For security reasons is **ANTLR4 Docker image** designed to run in the current folder only, so a container doesn't have any access to any other folders on a host system. Since this is a transparent call of Docker image from command line, where new files are generated, it is also a good idea to execute code inside a Docker as a non root user and match it to the host caller.

Calling a dockerized ANTLR4 image can look like this:

```shell
wget https://raw.githubusercontent.com/antlr/grammars-v4/master/json/JSON.g4
docker run --rm -u $(id -u ${USER}):$(id -g ${USER}) -v `pwd`:/work antlr/antlr4 -Dlanguage=Go JSON.g4
```

## Integration as alias

      alias antlr4='docker run -it -u $(id -u ${USER}):$(id -g ${USER}) -v $(pwd):/work antlr/antlr4 $@'


