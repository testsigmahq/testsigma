#!/bin/bash
set -x
set -e

echo "Starting compilation of local agent source"

cd automator
mvn clean install

cd ../agent
mvn clean install

cd ../agent-launcher
mvn clean install

cd ..

echo "Completed compilation of local agent source"

exit 0
