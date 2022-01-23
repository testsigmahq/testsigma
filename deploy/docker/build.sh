#!/bin/bash

set -x
set -e

VERSION=$1
EXPECTED_ARGS=1
E_BADARGS=65
CURRENT_DIR="$(pwd -P)"

if [ $# -lt $EXPECTED_ARGS ]
then
  echo "Usage: $0 <version>"
  exit $E_BADARGS
fi

PUSH_LATEST=false

while [ $# -gt 0 ]; do
  case "$1" in
    --PUSH_LATEST=*)
      PUSH_LATEST="${1#*=}"
      ;;
    *)
  esac
  shift
done

ROOT_FOLDER="$(
  cd "$(dirname "$0")" || exit
  cd ../..
  pwd -P
)"

cd "$ROOT_FOLDER" || exit 1

sh $ROOT_FOLDER/deploy/compile.sh --UI_BUILD_CONF=docker

docker build -t server -f $ROOT_FOLDER/Dockerfile .

docker tag server:latest testsigmahq/server:$VERSION
docker push testsigmahq/server:$VERSION

if [[ "$PUSH_LATEST" == "true"* ]]; then
  echo "Pushing build with latest tag"
  docker tag server:latest testsigmahq/server:latest
  docker push testsigmahq/server:latest
else
  echo "Latest tag build not requested. So Skipping it..."
fi

cd $CURRENT_DIR