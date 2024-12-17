# Validateif plugin for Craft 4

Supercharged text field validation...

### Requirements

This module requires Craft CMS 4.0.0 or later.

### Installation

To install the module, follow these steps:

1.  Place in the main project directory in modules folder. 

2.  Update the application config.
    We'll firstly need to tell Craft to load this module when the Craft application starts. This can be done by editing the project's config/app.php file:
    return [
      // ...
      'modules' => [
          // ...
          'validateif' => \modules\validateif\Validateif::class,
      ],
      'bootstrap' => [
          // ...
          'validateif'
      ],
    ];

3. Set up class autoloading.
   We need to tell Composer how to find the module’s classes by setting the autoload field in the project’s composer.json file:
   {
    // ...
    "autoload": {
        "psr-4": {
            "modules\\validateif\\": "modules/validateif/src/"
        }
    }
    // ...
}

4. Go to the project’s directory in a terminal, and run the following command:
   $ composer dump-autoload -a
   This will tell Composer to update its class autoloader script based on our new autoload mapping.

5. Regex for letters, numbers, dash and underscore - /^[a-zA-Z0-9-_]*$/


## Validateif Overview

This plugin adds a custom fieldtype which allows you to take control of validation on plain text fields. Validate using the following predefined strings or setup your own custom regex rules. Add custom placeholders and error messages.

1.  Email Address
2.  URL
3.  Phone Number
4.  IP Address
5.  IPv4 Address
6.  IPv6 Address
7.  Facebook Link
8.  Twitter Link
9.  LinkedIn Link
10. Instagram Link
11. Custom Regex Rule

## Using Validateif

Use just as you would any native Plain Text field:

    {{ entry.validateifFieldHandle }}

Based on validateit plugin.

git clone https://install:1znbDxWpV2J4rhzzztHP@git.devops.refactory.bg/dimedis/smart-fair-system/craft-cms-validate-input-field.git
