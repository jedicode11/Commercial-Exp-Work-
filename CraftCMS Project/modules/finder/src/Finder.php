<?php
/**
 * Finder plugin for Craft CMS 4.x
 *
 * This module provides a connection to the Finder API to update and save the Events in EMS.
 *
 *
 */

namespace modules\finder;

// use modules\finder\fields\FinderField;

use modules\finder\helpers\FileLog;
use modules\finder\jobs\FinderJob;

use Craft;
use craft\base\Element;
use craft\elements\Entry;
use craft\events\ModelEvent;
use craft\helpers\ElementHelper;
use craft\helpers\Queue;
use craft\helpers\App;

use craft\services\Fields;
use craft\i18n\PhpMessageSource;
use craft\events\RegisterComponentTypesEvent;
use craft\events\RegisterTemplateRootsEvent;
use craft\web\View;

use yii\Log\Logger;

use yii\base\Event;


/**
 * Class Finder
 *
 * @author    Refactory - V.Todorov
 * @package   Finder
 * @since     1.0.0
 *
 */
class Finder extends \yii\base\Module
{
    public static $instance;

    public function __construct($id, $parent = null, array $config = [])
    {

      Craft::setAlias('@modules/finder', $this->getBasePath());

      // Translation category
      $i18n = Craft::$app->getI18n();

        // Set this as the global instance of this module class
        static::setInstance($this);

        parent::__construct($id, $parent, $config);
    }

    public function init()
    {

        parent::init();

        self::$instance = $this;

        $this->installEventListeners();
        // FileLog::create($this::LOG_FILE_NAME, 'modules\finder\*');

        Craft::info(
            'Finder',
            __METHOD__
        );
    }

    protected function installEventListeners()
    {
        // Handler: ClearCaches::EVENT_REGISTER_CACHE_OPTIONS
        Event::on(
            Entry::class,
            Element::EVENT_AFTER_PROPAGATE,
            function(ModelEvent $event) {
                /* @var Entry $entry */
                $this->handleEventAfterSave($event);
            }
        );
    }

    protected function handleEventAfterSave(ModelEvent $event) {
        $entry = $event->sender;
        $section = $entry->section;

        if (ElementHelper::isDraftOrRevision($entry)
        || $section->handle !== 'event'
            ) {
            // don’t do anything with drafts or revisions
            return;
        }

        if ($entry->firstSave) {
            $this->finderSender($entry, 'POST');
            return;
        }

        $this->finderSender($entry, 'PATCH');
    }

  protected function finderSender($entry, string $httpMethod) {

    $associatedSiteHandle = $entry->getSite()->handle;
    $supportedSites = $entry->getSupportedSites();

	  $title = [];
	  $text = [];
	  $exhibitorFinderId = '';

	  $fieldColumn = $entry->getFieldValues();
	  if ($fieldColumn["exhibitor"]->one()) {
	  	$exhibitorFinderId = $fieldColumn["exhibitor"]->one()->getFieldValues()["exhibitorFinderId"];
	  }

    $imageUrl = $fieldColumn["featureImage"]->one()->url;
    $productCategories = $fieldColumn["productCategory"]->all();

    foreach ($supportedSites as $site) {
	 if (Entry::find()->id($entry->id)->siteId($site['siteId'])->one()) {
        	 $language = Entry::find()->id($entry->id)->siteId($site['siteId'])->one()->language;
	 	 $currentTitle = Entry::find()->id($entry->id)->siteId($site['siteId'])->one()->title;
		 $currentText = Entry::find()->id($entry->id)->siteId($site['siteId'])->one()->getFieldValues()["eventSummary"];

		 $title[$language] = $currentTitle;
		 $text[$language] = $currentText;
	 }

    }

        $link = $entry->url;

        $prodList = [];
	foreach ($productCategories as $productCategory) {
		array_push($prodList, $productCategory["productCategoryFinderId"]);
        }

        $event = array (
          "extId" => $entry->uid,
          "title" => $title,
          "location" => $fieldColumn["eventLocation"],
          "exhId" => $exhibitorFinderId,
//          "fairId" => $fieldColumn["fairId"],
          "image" => $imageUrl,
          "date" => $fieldColumn["timeStart"]->setTimezone(new \DateTimeZone("UTC"))->format('Y-m-d\\TH:i:s'),
          "dateUntil" => $fieldColumn["timeEnd"]->setTimezone(new \DateTimeZone("UTC"))->format('Y-m-d\\TH:i:s'),
          "text" => $text,
          "link" => $link,
        );

        if (!empty($prodList)) {
          $event["prodList"] = $prodList;
	}

	Queue::push(new FinderJob($httpMethod, $event, $associatedSiteHandle));

    }
}
