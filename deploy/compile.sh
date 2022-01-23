#!/bin/bash
set -x
set -e

UI_BUILD_CONF=dev

while [ $# -gt 0 ]; do
  case "$1" in
    --UI_BUILD_CONF=*)
      UI_BUILD_CONF="${1#*=}"
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

cd "$ROOT_FOLDER"

cd $ROOT_FOLDER/ui

npm install

$ROOT_FOLDER/ui/node_modules/.bin/ng build --configuration=$UI_BUILD_CONF

cd $ROOT_FOLDER/automator
mvn clean install

cd $ROOT_FOLDER/agent
mvn clean install

cd $ROOT_FOLDER/agent-launcher
mvn clean install

cd $ROOT_FOLDER/server
mvn clean install

cd $CURRENT_DIR