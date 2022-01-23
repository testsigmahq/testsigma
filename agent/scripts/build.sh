#!/bin/bash
set -x
set -e

WORKING_DIR="$( cd "$(dirname "$0")" ; pwd -P )"
VERSION=$1

echo "Generating complete agent builds with web and mobile"

sh "$WORKING_DIR/compile.sh"

sh "$WORKING_DIR/zip-build.sh" Mac mac
sh "$WORKING_DIR/zip-build.sh" Linux linux
sh "$WORKING_DIR/zip-build.sh" Windows windows

cd ../osassets # Temporary...we can remove once we have published the the branch to a new repo.

gh release upload $VERSION $WORKING_DIR/../../TestsigmaAgent-Mac.zip --clobber
gh release upload $VERSION $WORKING_DIR/../../TestsigmaAgent-Windows.zip --clobber
gh release upload $VERSION $WORKING_DIR/../../TestsigmaAgent-Linux.zip --clobber

exit 0
