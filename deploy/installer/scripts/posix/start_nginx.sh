#!/bin/bash
set -x
set -e

WORKING_DIR="$( cd "$(dirname "$0")" ; pwd -P )"
OS_TYPE=${OSTYPE:-"unknown"}

if [ ! -d "$WORKING_DIR/nginx/ssl" ]
then
  mkdir $WORKING_DIR/nginx/ssl
fi

if [ ! -d "$WORKING_DIR/nginx/logs" ]
then
  mkdir $WORKING_DIR/nginx/logs
fi

touch $WORKING_DIR/nginx/logs/error.log
chmod 777 $WORKING_DIR/nginx/logs/error.log

curl -s -o $WORKING_DIR/nginx/ssl/local_testsigmaos_com.key https://s3.amazonaws.com/public-assets.testsigma.com/os_certificates/local_testsigmaos_com.key
curl -s -o $WORKING_DIR/nginx/ssl/local_testsigmaos_com.pem https://s3.amazonaws.com/public-assets.testsigma.com/os_certificates/local_testsigmaos_com.pem

cd $WORKING_DIR/nginx

if [[ "$OS_TYPE" == "darwin"* ]]; then
  xattr -r -d com.apple.quarantine ./
fi

$WORKING_DIR/nginx/nginx -c "$WORKING_DIR/nginx/nginx.conf" -g 'daemon off;'
