<?php

namespace modules\uploadfeed\controllers;

use Craft;
use craft\web\Controller;
use craft\helpers\App;
use craft\helpers\UrlHelper;
use yii\web\HttpException;
use yii\web\UploadedFile;
use yii\web\Response;

use craft\feedme\Plugin;


class UploadController extends Controller
{
  protected int|bool|array $allowAnonymous = true;

  public function beforeAction($action): bool
  {
    // Disable protection for the action 'resolve-request'.
    // The actions must be in 'kebab-case'.
    if ($action->id === 'feed' || 'index') {
      $this->enableCsrfValidation = false;
    }

    return parent::beforeAction($action);
  }

  public function actionIndex(): Response
  {
    $variables['site'] = 'site1';
    return $this->renderTemplate('uploadfeed/index', $variables);
  }

  public function actionFeeds(): Response
  {
    $cpTrigger = \Craft::$app->config->general->cpTrigger ? \Craft::$app->config->general->cpTrigger . '/' : null;
    $feeds = Plugin::$plugin->feeds->getFeeds();

    $res = [];
    foreach ($feeds as &$feed) {
      $triggerUrl = str_replace($cpTrigger, '', UrlHelper::actionUrl('feed-me/feeds/run-task', [ 'direct' => true, 'feedId' => $feed->id, 'passkey' => $feed->passkey ]));
      array_push($res, array(
        "name" => $feed->name,
        "feedUrl" => $feed->feedUrl,
        "triggerUrl" => $triggerUrl)
      );
    }

    $breakpoin = '';

    return $this->asJson($res);
  }

  public function actionFeed()
  {
    $file = UploadedFile::getInstanceByName('file');
    $path = Craft::getAlias('@storage') . '/feed';
    $extension = $file->extension;

    if ($extension !== 'csv') {
      return $this->sendResponse(['error' => 'An error occurred. Only CSV files are allowed!']);
    }

    $this->upload($path, $file);
  }

  public function upload($path, $file)
  {
    if ($file->saveAs($path . '/event-feed.csv')) {
      return $this->sendResponse(['message' => 'The file has been successfully uploaded']);
    } else {
      return $this->sendResponse(['error' => 'An error occurred. The file was not uploaded!']);
    }
  }

  public function sendResponse( mixed $message) {

    return $this->asJson($message);
  }
}