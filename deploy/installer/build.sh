#!/bin/bash
set -x
set -e

WORKING_DIR="$(pwd -P)"
EXPECTED_ARGS=2
E_BADARGS=65

VERSION=v1.1.0
PUBLISH_TO_GIT=true


while [ $# -gt 0 ]; do
  case "$1" in
    --VERSION=*)
      VERSION="${1#*=}"
      ;;
    --PUBLISH_TO_GIT=*)
      PUBLISH_TO_GIT="${1#*=}"
      ;;
    *)
      printf "***************************\n"
      printf "* Error: Invalid argument.*\n"
      printf "***************************\n"
      exit 1
  esac
  shift
done

if [ $# -lt $EXPECTED_ARGS ]
then
  echo "Usage: $0 <version>"
  exit $E_BADARGS
fi


ROOT_FOLDER="$(cd "$(dirname "$0")"; cd ../../ ; pwd -P)"

cd "$ROOT_FOLDER"

echo "Generating complete agent builds with web and mobile"

sh "$ROOT_FOLDER/deploy/compile.sh" --UI_BUILD_CONF=docker --LOCAL_AGENT_TAG=$VERSION

sh "$ROOT_FOLDER/deploy/installer/create_zip.sh" Windows windows $VERSION
sh "$ROOT_FOLDER/deploy/installer/create_zip.sh" Mac mac $VERSION
sh "$ROOT_FOLDER/deploy/installer/create_zip.sh" Linux linux $VERSION

aws s3 cp $ROOT_FOLDER/Testsigma-Windows-$VERSION.zip s3://opensource-staging.testsigma.com/server/$VERSION/Testsigma-Windows-$VERSION.zip --acl public-read
aws s3 cp $ROOT_FOLDER/Testsigma-Mac-$VERSION.zip s3://opensource-staging.testsigma.com/server/$VERSION/Testsigma-Mac-$VERSION.zip --acl public-read
aws s3 cp $ROOT_FOLDER/Testsigma-Linux-$VERSION.zip s3://opensource-staging.testsigma.com/server/$VERSION/Testsigma-Linux-$VERSION.zip --acl public-read

if [[ "$PUBLISH_TO_GIT" == "true"* ]]; then
	gh release upload $VERSION $ROOT_FOLDER/Testsigma-Windows-$VERSION.zip --clobber
	gh release upload $VERSION $ROOT_FOLDER/Testsigma-Mac-$VERSION.zip --clobber
	gh release upload $VERSION $ROOT_FOLDER/Testsigma-Linux-$VERSION.zip --clobber
fi

rm -f $ROOT_FOLDER/Testsigma-Windows-$VERSION.zip
rm -f $ROOT_FOLDER/Testsigma-Mac-$VERSION.zip
rm -f $ROOT_FOLDER/Testsigma-Linux-$VERSION.zip


cd $WORKING_DIR