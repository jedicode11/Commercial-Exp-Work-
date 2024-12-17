<?php

namespace modules\finder\jobs;

use yii\base\ErrorException;
use yii\Log\Logger;

use Craft;
use craft\mail\Message;
use craft\helpers\App;
use GuzzleHttp\Client;
use GuzzleHttp\Psr7;
use GuzzleHttp\Exception\ClientException;

use modules\finder\helpers\DomainHelper;

class FinderJob extends \craft\queue\BaseJob
{
    private string $httpMethod;
    private $requestBody;
    private $url;
    private $associatedSiteHandle;

   public function __construct(string $httpMethod, $requestBody, string $associatedSiteHandle)
    {
      $this->url = App::env('FINDER_EVENTS_API_URL');
      $this->httpMethod = $httpMethod;
      $this->requestBody = $requestBody;
      $this->associatedSiteHandle = $associatedSiteHandle;

      parent::__construct();
    }

    public function execute($queue): void
    {
        $finderDomain = "";

        try {
          $finderDomain = DomainHelper::getFinderDomain($this->associatedSiteHandle);
        } catch (ErrorException $e) {
          throw $e;
        }

        if (!App::env('FINDER_EVENTS_API_URL')) {
          throw new ErrorException("Missing 'FINDER_EVENTS_API_URL' in .env");
        }

        if (!App::env('FINDER_API_KEY')) {
          throw new ErrorException("Missing 'FINDER_API_KEY' in .env");
        }

        if ($this->httpMethod === "PATCH") {
          $this->url = $this->url .  "/" . $this->requestBody["extId"];
        }

        $headers = [
          'X-Vis-Domain' => $finderDomain,
          'X-API-Key' => App::env('FINDER_API_KEY'),
          'Content-Type' => 'application/json'
        ];

        // POST request
        $client = new Client();
        try {
          $response = $client->request($this->httpMethod, $this->url, [
            'headers' => $headers,
            'json' => $this->requestBody
          ]);
          Craft::getLogger()->log(json_encode($this->requestBody), Logger::LEVEL_INFO);
        } catch (ClientException $e) {
          Craft::getLogger()->log(Psr7\Message::toString($e->getResponse()), Logger::LEVEL_ERROR);
          throw new ErrorException(Psr7\Message::toString($e->getResponse()));
        }
    }

    protected function defaultDescription(): string
    {
      return Craft::t('app', 'Process for sending changes');
    }
}
