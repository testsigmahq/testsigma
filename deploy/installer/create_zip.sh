#!/bin/bash
set -x
set -e

WORKING_DIR="$(pwd -P)"

ROOT_FOLDER="$(cd "$(dirname "$0")"; cd ../../ ; pwd -P)"

BUILD_OS_NAME=$1
OS_PATH_SUFFIX=$2
VERSION=$3

echo "Generating $OS_PATH_SUFFIX testsigma build"

BUILD_FILE_PREFIX=Testsigma
BUILD_FOLDER="$ROOT_FOLDER/$BUILD_FILE_PREFIX"
ZIP_FILE_NAME=$BUILD_FILE_PREFIX-$BUILD_OS_NAME-$VERSION.zip

rm -Rf "$ZIP_FILE_NAME"
rm -Rf "$BUILD_FOLDER"

mkdir "$BUILD_FOLDER"

########################################### Nginx Build ###########################################

cp -Rf $ROOT_FOLDER/deploy/installer/scripts/posix/start_nginx.sh "$BUILD_FOLDER/"
cp -Rf $ROOT_FOLDER/deploy/installer/scripts/posix/stop_nginx.sh "$BUILD_FOLDER/"
cp -Rf $ROOT_FOLDER/deploy/installer/scripts/windows/start_nginx.bat "$BUILD_FOLDER/"
cp -Rf $ROOT_FOLDER/deploy/installer/scripts/windows/stop_nginx.bat "$BUILD_FOLDER/"

chmod +x $BUILD_FOLDER/start_nginx.sh
chmod +x $BUILD_FOLDER/stop_nginx.sh

cp -Rf $HOME/.testsigma_os/$OS_PATH_SUFFIX/nginx "$BUILD_FOLDER/"

########################################### Testsigma UI Build ###########################################


UI_BUILD_FILE_PREFIX=TestsigmaUI
UI_BUILD_FOLDER="$BUILD_FOLDER/$UI_BUILD_FILE_PREFIX"
rm -Rf "$UI_BUILD_FOLDER"
mkdir "$UI_BUILD_FOLDER"
cp -Rf $WORKING_DIR/ui/dist/testsigma-angular/* "$UI_BUILD_FOLDER"


########################################### Testsigma Server Build ###########################################

SERVER_BUILD_FILE_PREFIX=TestsigmaServer
SERVER_BUILD_FOLDER="$BUILD_FOLDER/$SERVER_BUILD_FILE_PREFIX"

rm -Rf "$SERVER_BUILD_FOLDER"
mkdir "$SERVER_BUILD_FOLDER"
mkdir "$SERVER_BUILD_FOLDER/lib"
mkdir "$SERVER_BUILD_FOLDER/jre"

cp -Rf $ROOT_FOLDER/server/target/testsigma-server.jar "$SERVER_BUILD_FOLDER/"
cp -Rf $ROOT_FOLDER/server/target/lib/* "$SERVER_BUILD_FOLDER/lib/"
cp -Rf $ROOT_FOLDER/server/src/main/scripts/posix/start.sh "$SERVER_BUILD_FOLDER/"
cp -Rf $ROOT_FOLDER/server/src/main/scripts/windows/start.bat "$SERVER_BUILD_FOLDER/"
cp -Rf $ROOT_FOLDER/server/src/main/scripts/posix/stop.sh "$SERVER_BUILD_FOLDER/"
cp -Rf $ROOT_FOLDER/server/src/main/scripts/windows/stop.bat "$SERVER_BUILD_FOLDER/"
cp -Rf $HOME/.testsigma_os/$OS_PATH_SUFFIX/jre "$SERVER_BUILD_FOLDER"

if [[ "$OS_PATH_SUFFIX" == "windows"* ]]; then
  cp -Rf $HOME/.testsigma_os/windows/windows-kill.exe "$SERVER_BUILD_FOLDER/"
fi


########################################### Testsigma Agent Build ###########################################


AGENT_BUILD_FILE_PREFIX=TestsigmaAgent
AGENT_BUILD_FOLDER="$BUILD_FOLDER/$AGENT_BUILD_FILE_PREFIX"
rm -Rf "$AGENT_BUILD_FOLDER"
mkdir "$AGENT_BUILD_FOLDER"
mkdir "$AGENT_BUILD_FOLDER/lib"
mkdir "$AGENT_BUILD_FOLDER/jre"
mkdir "$AGENT_BUILD_FOLDER/android"
mkdir "$AGENT_BUILD_FOLDER/appium"
mkdir "$AGENT_BUILD_FOLDER/ios"

cp -Rf $ROOT_FOLDER/agent-launcher/target/agent-launcher.jar "$AGENT_BUILD_FOLDER/"
cp -Rf $ROOT_FOLDER/agent/target/agent.jar "$AGENT_BUILD_FOLDER/"
cp -Rf $ROOT_FOLDER/agent/target/lib/* "$AGENT_BUILD_FOLDER/lib/"
cp -Rf $ROOT_FOLDER/agent/src/main/scripts/windows/start.bat "$AGENT_BUILD_FOLDER/"
cp -Rf $ROOT_FOLDER/agent/src/main/scripts/windows/stop.bat "$AGENT_BUILD_FOLDER/"
cp -Rf $ROOT_FOLDER/agent/src/main/scripts/posix/start.sh "$AGENT_BUILD_FOLDER/"
cp -Rf $ROOT_FOLDER/agent/src/main/scripts/posix/stop.sh "$AGENT_BUILD_FOLDER/"

if [[ "$OS_PATH_SUFFIX" == "windows"* ]]; then
  cp -Rf $HOME/.testsigma_os/windows/windows-kill.exe "$AGENT_BUILD_FOLDER/"
fi
cp -Rf $HOME/.testsigma_os/$OS_PATH_SUFFIX/jre "$AGENT_BUILD_FOLDER"

chmod +x "$AGENT_BUILD_FOLDER/start.sh"
chmod +x "$AGENT_BUILD_FOLDER/stop.sh"
chmod -R +xw "$AGENT_BUILD_FOLDER/jre"

cp -Rf $HOME/.testsigma_os/$OS_PATH_SUFFIX/android "$AGENT_BUILD_FOLDER"
cp -Rf $HOME/.testsigma_os/$OS_PATH_SUFFIX/ios "$AGENT_BUILD_FOLDER"
cp -Rf $HOME/.testsigma_os/$OS_PATH_SUFFIX/appium "$AGENT_BUILD_FOLDER"

chmod -R +xw "$AGENT_BUILD_FOLDER/appium"
chmod -R +xw "$AGENT_BUILD_FOLDER/android"
chmod -R +xw "$AGENT_BUILD_FOLDER/ios"


zip -r "$ZIP_FILE_NAME" "$BUILD_FILE_PREFIX"

rm -Rf "$BUILD_FOLDER"

echo "Finished creating $OS_PATH_SUFFIX testsigma build"
