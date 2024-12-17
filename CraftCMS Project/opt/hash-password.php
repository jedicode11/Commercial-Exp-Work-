<?php
// Assuming Yii2's security component is autoloadable or you're doing this within a CraftCMS plugin/controller
require 'vendor/autoload.php';

$security = new \yii\base\Security();
$hash = $security->generatePasswordHash($argv[1]);

echo $hash."\n";
