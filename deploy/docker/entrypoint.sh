#!/bin/bash

set -x

SSL_DIR="/etc/ssl/"
DATA_SSL="/opt/app/ts_data/ssl"

if [ ! -d "$SSL_DIR" ]; then
  mkdir $SSL_DIR
fi

rm -f $SSL_DIR/local_testsigmaos_com.key
rm -f $SSL_DIR/local_testsigmaos_com.pem

if [ -d "$DATA_SSL" ]; then
  cp $DATA_SSL/local_testsigmaos_com.key /etc/ssl/local_testsigmaos_com.key
  cp $DATA_SSL/local_testsigmaos_com.pem /etc/ssl/local_testsigmaos_com.pem
else
  wget -q -O $SSL_DIR/local_testsigmaos_com.key https://s3.amazonaws.com/public-assets.testsigma.com/os_certificates/local_testsigmaos_com.key
  wget -q -O $SSL_DIR/local_testsigmaos_com.pem https://s3.amazonaws.com/public-assets.testsigma.com/os_certificates/local_testsigmaos_com.pem
fi

echo "---------------------- Starting Nginx ----------------------"

/usr/sbin/nginx

/opt/app/start.sh --TS_DATA_DIR=$TS_DATA_DIR