#!/bin/bash

set -x
set -e

DOCKER_VERSION=$1
AGENT_TAG=$2
EXPECTED_ARGS=2
E_BADARGS=65
CURRENT_DIR="$(pwd -P)"

if [ $# -lt $EXPECTED_ARGS ]
then
  echo "Usage: $0 <version>"
  exit $E_BADARGS
fi

ROOT_FOLDER="$(
  cd "$(dirname "$0")" || exit
  cd ../..
  pwd -P
)"

cd "$ROOT_FOLDER" || exit 1

sh $ROOT_FOLDER/deploy/compile.sh --UI_BUILD_CONF=docker --LOCAL_AGENT_TAG=$AGENT_TAG

docker build -t server -f $ROOT_FOLDER/Dockerfile .

docker tag server:latest testsigmahq/server:$DOCKER_VERSION
docker push testsigmahq/server:$DOCKER_VERSION

cd $CURRENT_DIR