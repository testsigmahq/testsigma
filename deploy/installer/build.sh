#!/bin/bash
set -x
set -e

VERSION=$1

WORKING_DIR="$(pwd -P)"

ROOT_FOLDER="$(cd "$(dirname "$0")"; cd ../../ ; pwd -P)"

cd "$ROOT_FOLDER"

echo "Generating complete agent builds with web and mobile"

sh "$ROOT_FOLDER/deploy/compile.sh" --UI_BUILD_CONF=docker --LOCAL_AGENT_TAG=$VERSION

sh "$ROOT_FOLDER/deploy/installer/create_zip.sh" Windows windows $VERSION
sh "$ROOT_FOLDER/deploy/installer/create_zip.sh" Mac mac $VERSION
sh "$ROOT_FOLDER/deploy/installer/create_zip.sh" Linux linux $VERSION

gh release upload $VERSION $ROOT_FOLDER/Testsigma-Windows-$VERSION.zip --clobber
gh release upload $VERSION $ROOT_FOLDER/Testsigma-Mac-$VERSION.zip --clobber
gh release upload $VERSION $ROOT_FOLDER/Testsigma-Linux-$VERSION.zip --clobber

cd $WORKING_DIR