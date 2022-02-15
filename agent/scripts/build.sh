#!/bin/bash
set -x
set -e

WORKING_DIR="$( cd "$(dirname "$0")" ; pwd -P )"
EXPECTED_ARGS=2
E_BADARGS=65

VERSION=v1.0.0
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


echo "Generating complete agent builds with web and mobile"

sh "$WORKING_DIR/compile.sh"

sh "$WORKING_DIR/zip-build.sh" Mac mac
sh "$WORKING_DIR/zip-build.sh" Linux linux
sh "$WORKING_DIR/zip-build.sh" Windows windows

aws s3 cp $WORKING_DIR/../../TestsigmaAgent-Windows.zip s3://opensource-staging.testsigma.com/agent/$VERSION/TestsigmaAgent-Windows.zip --acl public-read
aws s3 cp $WORKING_DIR/../../TestsigmaAgent-Mac.zip s3://opensource-staging.testsigma.com/agent/$VERSION/TestsigmaAgent-Mac.zip --acl public-read
aws s3 cp $WORKING_DIR/../../TestsigmaAgent-Linux.zip s3://opensource-staging.testsigma.com/agent/$VERSION/TestsigmaAgent-Linux.zip --acl public-read

if [[ "$PUBLISH_TO_GIT" == "true"* ]]; then
	gh release upload $VERSION $WORKING_DIR/../../TestsigmaAgent-Windows.zip --clobber
	gh release upload $VERSION $WORKING_DIR/../../TestsigmaAgent-Mac.zip --clobber
	gh release upload $VERSION $WORKING_DIR/../../TestsigmaAgent-Linux.zip --clobber
fi

rm -f $WORKING_DIR/../../TestsigmaAgent-Windows.zip
rm -f $WORKING_DIR/../../TestsigmaAgent-Mac.zip
rm -f $WORKING_DIR/../../TestsigmaAgent-Linux.zip

exit 0
