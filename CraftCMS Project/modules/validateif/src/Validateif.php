<?php
/**
 * Validateif plugin for Craft CMS 4.x
 *
 * A super simple field type which allows you toggle existing field types.
 *
 * 
 */

namespace modules\validateif;

use modules\validateif\fields\ValidateifField;

use Craft;
use craft\services\Fields;
use craft\i18n\PhpMessageSource;
use craft\events\RegisterComponentTypesEvent;
use craft\events\RegisterTemplateRootsEvent;
use craft\web\View;

use yii\base\Event;


/**
 * Class Validateif
 *
 * @author    Refactory
 * @package   Validateif
 * @since     1.0.0
 *
 */
class Validateif extends \yii\base\Module
{
    public static $instance;

    public function __construct($id, $parent = null, array $config = [])
    {

      Craft::setAlias('@modules/validateif', $this->getBasePath());

      // Translation category
      $i18n = Craft::$app->getI18n();
      /** @noinspection UnSafeIsSetOverArrayInspection */
      if (!isset($i18n->translations[$id]) && !isset($i18n->translations[$id.'*'])) {
          $i18n->translations[$id] = [
              'class' => PhpMessageSource::class,
              'sourceLanguage' => 'en-US',
              'basePath' => '@modules/validateif/translations',
              'forceTranslation' => true,
              'allowOverrides' => true,
          ];
      }

        // Base template directory
        Event::on(View::class, View::EVENT_REGISTER_CP_TEMPLATE_ROOTS, function (RegisterTemplateRootsEvent $e) {
            if (is_dir($baseDir = $this->getBasePath().DIRECTORY_SEPARATOR.'templates')) {
                $e->roots[$this->id] = $baseDir;
            }
        });

        // Set this as the global instance of this module class
        static::setInstance($this);

        parent::__construct($id, $parent, $config);
    }

    public function init()
    {
        
        parent::init();
        
        self::$instance = $this;

        Event::on(
            Fields::class,
            Fields::EVENT_REGISTER_FIELD_TYPES,
            function (RegisterComponentTypesEvent $event) {
                $event->types[] = ValidateifField::class;
            }
        );

        Craft::info(
            'Validateif',
            __METHOD__
        );
    }

}
