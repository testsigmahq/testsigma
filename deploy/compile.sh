#!/bin/bash
set -x
set -e

UI_BUILD_CONF=dev
LOCAL_AGENT_TAG=latest

while [ $# -gt 0 ]; do
  case "$1" in
    --UI_BUILD_CONF=*)
      UI_BUILD_CONF="${1#*=}"
      ;;
    --LOCAL_AGENT_TAG=*)
      LOCAL_AGENT_TAG="${1#*=}"
      ;;
    *)
      printf "***************************\n"
      printf "* Error: Invalid argument.*\n"
      printf "***************************\n"
      exit 1
  esac
  shift
done

CURRENT_DIR="$(pwd -P)"

ROOT_FOLDER="$(
  cd "$(dirname "$0")" || exit
  cd ..
  pwd -P
)"

OS_TYPE=${OSTYPE:-"unknown"}


cd "$ROOT_FOLDER"

cd $ROOT_FOLDER/ui

export NODE_OPTIONS=--openssl-legacy-provider

npm install --legacy-peer-deps

$ROOT_FOLDER/ui/node_modules/.bin/ng build --configuration=$UI_BUILD_CONF

cd $ROOT_FOLDER/automator
mvn clean install

cd $ROOT_FOLDER/agent
mvn clean install

cd $ROOT_FOLDER/agent-launcher
mvn clean install

if [[ "$OS_TYPE" == "darwin"* ]]; then
  sed -i '' -e "s/local.agent.download.tag=latest/local.agent.download.tag=$LOCAL_AGENT_TAG/g" $ROOT_FOLDER/server/src/main/resources/application.properties
else
  sed -i  "s/local.agent.download.tag=latest/local.agent.download.tag=$LOCAL_AGENT_TAG/g" $ROOT_FOLDER/server/src/main/resources/application.properties
fi

cd $ROOT_FOLDER/server
mvn clean install

if [[ "$OS_TYPE" == "darwin"* ]]; then
  sed -i '' -e "s/local.agent.download.tag=$LOCAL_AGENT_TAG/local.agent.download.tag=latest/g" $ROOT_FOLDER/server/src/main/resources/application.properties
else
  sed -i "s/local.agent.download.tag=$LOCAL_AGENT_TAG/local.agent.download.tag=latest/g" $ROOT_FOLDER/server/src/main/resources/application.properties
fi

cd $CURRENT_DIR