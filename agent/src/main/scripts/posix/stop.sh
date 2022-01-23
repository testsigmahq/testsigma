#!/bin/bash
set -x

ROOT_FOLDER="$(
  cd "$(dirname "$0")" || exit
  pwd -P
)"

cd "$ROOT_FOLDER" || exit 1

OS_TYPE=${OSTYPE:-"unknown"}
OS_NAME=`uname`

if [ -n "$ENV_TS_DATA_DIR" ]; then
  export TS_DATA_DIR="$ENV_TS_DATA_DIR"
else
  if [[ "$OS_TYPE" == "darwin"* ]]; then
    export TS_DATA_DIR="$HOME/Library/Application Support/Testsigma/Agent"
  elif [[ "$OS_TYPE" == "freebsd"* ]] || [[ "$OS_TYPE" == "linux-gnu" ]] || [[ "$OS_NAME" == "Linux" ]]; then
    export TS_DATA_DIR="$HOME/.testsigma/agent"
  elif [[ "$OS_TYPE" == "cygwin"* ]] || [[ "$OS_TYPE" == "msys"* ]] || [[ "$OS_TYPE" == "win32"* ]]; then
    echo "Unsupported OS (Windows)"
    exit 1
  else
    echo "Unsupported OS"
    exit 1
  fi
fi

TS_ROOT_DIR="$ROOT_FOLDER"
TS_AGENT_JAR_PATH="$ROOT_FOLDER"
MAIN_JAR_FILE="$ROOT_FOLDER/agent-launcher.jar"
LOGGING_LEVEL=INFO

while [ $# -gt 0 ]; do
  case "$1" in
    --TS_DATA_DIR=*)
      TS_DATA_DIR="${1#*=}"
      ;;
    --TS_ROOT_DIR=*)
      TS_ROOT_DIR="${1#*=}"
      ;;
    --TS_AGENT_JAR_PATH=*)
      TS_AGENT_JAR_PATH="${1#*=}"
      ;;
    --MAIN_JAR_FILE=*)
      MAIN_JAR_FILE="${1#*=}"
      ;;
    --debug*)
      LOGGING_LEVEL="DEBUG"
      ;;
    *)
      printf "***************************\n"
      printf "* Error: Invalid argument.*\n"
      printf "***************************\n"
      exit 1
  esac
  shift
done

"$TS_ROOT_DIR/jre/bin/java" -DTS_DATA_DIR="$TS_DATA_DIR" -DTS_ROOT_DIR="$TS_ROOT_DIR" -DTS_AGENT_JAR="$TS_AGENT_JAR_PATH" -Dlogging.level.="$LOGGING_LEVEL" -cp "$MAIN_JAR_FILE" com.testsigma.agent.launcher.Application stop
