#!/bin/bash

set -x
set -e

WORKING_DIR="$( cd "$(dirname "$0")" ; pwd -P )"

cd $WORKING_DIR/nginx/
$WORKING_DIR/nginx/nginx -s quit
