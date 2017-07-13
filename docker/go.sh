#!/bin/bash
set -e

echo dirname $(readlink -f $BASH_SOURCE);
DOCKER_DIR=`dirname $(readlink -f $BASH_SOURCE)`;
ANTLR_DIR=`readlink -f "$DOCKER_DIR/.."`;

IMAGE_NAME="antlr4/basic";

pushd $DOCKER_DIR > /dev/null;

docker build --tag "${IMAGE_NAME}" . ;

# Create reusable named volume for Maven stuff, so that we aren't always
# redownloading POMs and plugins for each new container.
docker volume create --name "m2-repository";

# Run a temporary container that will be cleaned up afterwards.
docker run -i --tty \
    --rm \
    --name "antlr4-development" \
    --volume "${ANTLR_DIR}:/opt/project" \
    --volume "m2-repository:/root/.m2/repository" \
    -p 127.0.0.1:5005:5005 \
    --hostname="antlr4" \
    "${IMAGE_NAME}";

popd > /dev/null;
