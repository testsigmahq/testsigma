#!/bin/bash

set -x
set -e

EXPECTED_ARGS=3
E_BADARGS=65
CURRENT_DIR="$(pwd -P)"


DOCKER_VERSION=v1.1.0
AGENT_TAG=v1.1.0
IMAGE_NAME=server

if [ $# -lt $EXPECTED_ARGS ]
then
  echo "Usage: $0 <version>"
  exit $E_BADARGS
fi

while [ $# -gt 0 ]; do
  case "$1" in
    --DOCKER_VERSION=*)
      DOCKER_VERSION="${1#*=}"
      ;;
    --AGENT_TAG=*)
      AGENT_TAG="${1#*=}"
      ;;
    --IMAGE_NAME=*)
      IMAGE_NAME="${1#*=}"
      ;;
    *)
      printf "***************************\n"
      printf "* Error: Invalid argument.*\n"
      printf "***************************\n"
      exit 1
  esac
  shift
done

ROOT_FOLDER="$(
  cd "$(dirname "$0")" || exit
  cd ../..
  pwd -P
)"

sed -i -E "s/agent.version=[0-9]+.[0-9].+[0-9]+/agent.version=$DOCKER_VERSION/g" agent/src/main/resources/agent.properties
sed -i -E "s/currentAgentVersion = \"[0-9]+.[0-9].+[0-9]+\"/currentAgentVersion = \"$DOCKER_VERSION\"/g" server/src/main/java/com/testsigma/dto/AgentDTO.java

cd "$ROOT_FOLDER" || exit 1

sh $ROOT_FOLDER/deploy/compile.sh --UI_BUILD_CONF=docker --LOCAL_AGENT_TAG=$AGENT_TAG

echo "Starting build..."
docker buildx build --platform linux/amd64,linux/arm64/v8 -t testsigmahq/$IMAGE_NAME:$DOCKER_VERSION -f $ROOT_FOLDER/Dockerfile --push .

#docker tag server:latest testsigmahq/$IMAGE_NAME:$DOCKER_VERSION
#docker push testsigmahq/$IMAGE_NAME:$DOCKER_VERSION

cd $CURRENT_DIR