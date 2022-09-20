#!/bin/bash
set -x
set -e

BUILD_OS_NAME=$1
OS_PATH_SUFFIX=$2
BUILD_FILE_PREFIX=TestsigmaAgent

BUILD_FOLDER="$(pwd)/$BUILD_FILE_PREFIX"
ZIP_FILE_NAME=$BUILD_FILE_PREFIX-$BUILD_OS_NAME.zip

echo "Generating $OS_PATH_SUFFIX hybrid agent build"

rm -Rf "$BUILD_FOLDER"
rm -Rf "$ZIP_FILE_NAME"

mkdir "$BUILD_FOLDER"
mkdir "$BUILD_FOLDER/lib"
mkdir "$BUILD_FOLDER/jre"
mkdir "$BUILD_FOLDER/android"
mkdir "$BUILD_FOLDER/appium"
mkdir "$BUILD_FOLDER/ios"

cp -Rf agent-launcher/target/agent-launcher.jar "$BUILD_FOLDER/"
cp -Rf agent/target/agent.jar "$BUILD_FOLDER/"
cp -Rf agent/target/lib/* "$BUILD_FOLDER/lib/"

cp -Rf agent/src/main/scripts/windows/start.bat "$BUILD_FOLDER/"
cp -Rf agent/src/main/scripts/windows/stop.bat "$BUILD_FOLDER/"
cp -Rf agent/src/main/scripts/posix/start.sh "$BUILD_FOLDER/"
cp -Rf agent/src/main/scripts/posix/stop.sh "$BUILD_FOLDER/"

if [[ "$OS_PATH_SUFFIX" == "windows"* ]]; then
  cp -Rf $HOME/.testsigma_os/windows/windows-kill.exe "$BUILD_FOLDER/"
fi
cp -Rf $HOME/.testsigma_os/$OS_PATH_SUFFIX/jre "$BUILD_FOLDER"

chmod +x "$BUILD_FOLDER/start.sh"
chmod +x "$BUILD_FOLDER/stop.sh"
chmod -R +xw "$BUILD_FOLDER/jre"

cp -Rf $HOME/.testsigma_os/$OS_PATH_SUFFIX/android "$BUILD_FOLDER"
cp -Rf $HOME/.testsigma_os/$OS_PATH_SUFFIX/ios "$BUILD_FOLDER"
cp -Rf $HOME/.testsigma_os/$OS_PATH_SUFFIX/appium "$BUILD_FOLDER"

chmod -R +xw "$BUILD_FOLDER/appium"
chmod -R +xw "$BUILD_FOLDER/android"
chmod -R +xw "$BUILD_FOLDER/ios"

echo "Finished creating $OS_PATH_SUFFIX hybrid agent build"

zip -r "$ZIP_FILE_NAME" "$BUILD_FILE_PREFIX"
rm -Rf "$BUILD_FOLDER"
echo "Finished zipping $OS_PATH_SUFFIX hybrid agent"

exit 0
