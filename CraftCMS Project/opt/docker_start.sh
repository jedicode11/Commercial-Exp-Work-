#!/bin/bash
set -e

cd app
bash ./opt/prepare-container.sh
ls -la
. .env
echo "Testing DB connection from .env ..."
ping -c 2 $CRAFT_DB_SERVER
if mysql -h $CRAFT_DB_SERVER -u $CRAFT_DB_USER -p$CRAFT_DB_PASSWORD $CRAFT_DB_DATABASE -e "show tables";then
  echo "👍 AWESOME. Database connection successfully tested!"
fi

echo "##########################################################################"
echo "                         Starting CraftCMS"
echo "##########################################################################"
composer install
./craft serve 0.0.0.0:9090