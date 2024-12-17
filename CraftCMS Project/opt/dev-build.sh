#!/usr/bin/env bash
set -e

pwd
ls -la

echo "🚚 Installing dependencies with PHP composer ... "
composer install

echo "🚚 Creating a dev build ..."
VERSION=${CI_PROJECT_NAMESPACE}-${CI_PROJECT_NAME}-${CI_PROJECT_ID}-${CI_PIPELINE_ID}
BUILD=${VERSION}.tar.gz
BUILD_FILE=/tmp/${BUILD}
echo "$(date) -- $VERSION" > version.txt
mv .env.dev .env
sed -i "s/CRAFT_DB_PASSWORD=.*/CRAFT_DB_PASSWORD=$MYSQL_PASSWD/g" .env
tar --exclude=.git* --exclude=opt --exclude=composer* -czf $BUILD_FILE .
ls -lah $BUILD_FILE

echo "🚚 Copying build file to remote server ..."
DEBIAN_FRONTEND=noninteractive apt-get --no-install-recommends -y install openssh-client
eval $(ssh-agent)
echo $SSH_PRIV_KEY_TEST_SERVER|base64 -d|ssh-add -
test -e ~/.ssh || mkdir ~/.ssh
echo $KNOWN_HOSTS > ~/.ssh/known_hosts
cat ~/.ssh/known_hosts
scp $BUILD_FILE $SSH_USER@$TEST_SERVER:/tmp/

echo "🚚 Unpacking build file on remote server ..."
ssh $SSH_USER@$TEST_SERVER bash -s -- $BUILD < ./opt/remote-install.sh