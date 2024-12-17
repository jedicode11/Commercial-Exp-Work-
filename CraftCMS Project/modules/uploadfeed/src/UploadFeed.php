<?php
/**
 * Finder Api module for Craft CMS 4.x
 *
 *
 *
 *
 */

namespace modules\uploadfeed;

use Craft;
use craft\events\RegisterUrlRulesEvent;
use craft\events\RegisterCpNavItemsEvent;
use craft\events\RegisterTemplateRootsEvent;
use craft\web\View;
use craft\web\twig\variables\Cp;
use craft\web\UrlManager;
use craft\helpers\UrlHelper;
use yii\base\Event;


/**
 * Class Finder
 *
 * @author    Refactory - V.Todorov
 * @package   Finder Api
 * @since     1.0.0
 *
 */
class UploadFeed extends \yii\base\Module
{
    public static $instance;

    public function __construct($id, $parent = null, array $config = [])
    {
      // Set this as the global instance of this module class
      static::setInstance($this);
      parent::__construct($id, $parent, $config);
    }

    public function init()
    {
        parent::init();

        Craft::setAlias('@uploader', __DIR__);
        $path = Craft::getAlias('@storage') . '/feed';

        if (!file_exists($path)) {
          mkdir($path, 0777, true);
        }

        Craft::setAlias('@feed/folder', $path);

        Event::on(UrlManager::class, UrlManager::EVENT_REGISTER_CP_URL_RULES,
          function(RegisterUrlRulesEvent $event) {
            $event->rules = array_merge($event->rules, [
              'uploader/upload' => 'uploadfeed/upload/index',
              'uploader/feeds' => 'uploadfeed/upload/feeds',
              'POST uploader/feed' => 'uploadfeed/upload/feed',
            ]);
          }
        );

        Event::on(Cp::class, Cp::EVENT_REGISTER_CP_NAV_ITEMS,
          function(RegisterCpNavItemsEvent $event) {
            $event->navItems[] = [
              'url' => UrlHelper::cpUrl('uploader/upload'),
              'label' => 'Uploader',
              'icon' => '@uploader/upload.svg',
            ];
          }
        );

         // Register template roots to resolve our templates correctly
        Event::on(View::class, View::EVENT_REGISTER_CP_TEMPLATE_ROOTS,
          function(RegisterTemplateRootsEvent $event) {
            $event->roots[$this->id] = $this->getBasePath() . DIRECTORY_SEPARATOR . 'templates';
        });

        Craft::info(
          'UploadFeed',
          __METHOD__
        );
    }
}
