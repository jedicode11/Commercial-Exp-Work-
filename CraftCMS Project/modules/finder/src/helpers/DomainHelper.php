<?php

namespace modules\finder\helpers;

use yii\base\ErrorException;
use Craft;

class DomainHelper
{
  public static function getFinderDomain($siteHandle) {
    // Check site is existing
    $site = Craft::$app->getSites()->getSiteByHandle($siteHandle);
    if(!$site) {
      throw new ErrorException(sprintf('site with handle "%s" does not exist', $siteHandle));
    }

    $globalSets = \craft\elements\GlobalSet::find()
    ->site($siteHandle)
    ->handle('finderDomain')
    ->one();

    if(!$globalSets) {
      throw new ErrorException(sprintf('Global with handle finderDomain does not exist'));
    }

    $fieldValues = $globalSets->fieldValues["finderDomain"];

    if(!$fieldValues) {
	 throw new ErrorException(sprintf('Field with handle finderDomain does not exist or empty'));
    }

    return $fieldValues;
  }
}
