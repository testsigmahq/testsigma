#!/bin/bash
set -x
set -e

CURRENT_DIR="$(pwd -P)"

ROOT_FOLDER="$(
  cd "$(dirname "$0")" || exit
  cd ..
  pwd -P
)"

cd "$ROOT_FOLDER"

cd $ROOT_FOLDER/automator
mvn clean

cd $ROOT_FOLDER/agent
mvn clean

cd $ROOT_FOLDER/agent-launcher
mvn clean

cd $ROOT_FOLDER/server
mvn clean

cd $CURRENT_DIR
