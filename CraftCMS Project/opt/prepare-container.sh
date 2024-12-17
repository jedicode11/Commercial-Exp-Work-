#!/usr/bin/env bash

# Install some dependencies and helpers
echo "🚚 Installing required debian packages ... "
apt-get update >/dev/null && apt-get --no-install-recommends -y install default-mysql-client iputils-ping libicu-dev libzip-dev libpng-dev >/dev/null

echo "🚚 Installing required PHP extensions ... "
docker-php-ext-install -j$(nproc) intl >/dev/null
docker-php-ext-install -j$(nproc) pdo_mysql >/dev/null
docker-php-ext-install -j$(nproc) bcmath >/dev/null
docker-php-ext-install -j$(nproc) zip >/dev/null
docker-php-ext-install -j$(nproc) gd >/dev/null
php -m|grep -i -E "(zip|bcmath|pdo|intl)"

echo "🚚 Installing PHP Composer ..."
php -r "copy('https://getcomposer.org/installer', 'composer-setup.php');"
php -r "if (hash_file('sha384', 'composer-setup.php') === 'dac665fdc30fdd8ec78b38b9800061b4150413ff2e3b6f88543c636f7cd84f6db9189d43a81e5503cda447da73c7e5b6') { echo 'Installer verified'; } else { echo 'Installer corrupt'; unlink('composer-setup.php'); } echo PHP_EOL;"
php composer-setup.php
php -r "unlink('composer-setup.php');"
mv composer.phar /usr/bin/composer
composer --version