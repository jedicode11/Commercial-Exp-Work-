<?php
namespace modules\uploadfeed\assets;

use craft\web\AssetBundle;

class UploaderAsset extends AssetBundle
{
    // Public Methods
    // =========================================================================

    public function init(): void
    {
        $this->sourcePath = '@uploader/assets';

        $this->js = [
            'js/site.js',
            'js/app.js',
            'js/chunk-vendors.js'

        ];

        $this->css = [
          'css/site.css',
          'css/app.css',
          'css/chunk-vendors.css'
        ];

        parent::init();
    }
}