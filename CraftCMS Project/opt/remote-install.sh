#!/usr/bin/env bash
set -e

if [ -z $1 ];then
  echo "Build file missing as first and only parameter"
  false
fi
BUILD=/tmp/$1
ls -lah ${BUILD}
cd /var/www/ems.messe-duesseldorf.de/backend
tar xzf $BUILD
./craft install/check
curl -fvs localhost:8080/api/actions/app/health-check
cat version.txt
rm -f ${BUILD}