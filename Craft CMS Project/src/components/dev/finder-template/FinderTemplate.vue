<template>
  <link rel="stylesheet" href="/finder-template/md_prowein.css">
  <link rel="stylesheet" href="/finder-template/cms-style-overrides.css">
  <header class="metabar-row">
    <div class="inner-wrap inner-wrap--padding">
      <div class="grid page">
        <div class="grid__item portable-one-whole">
          <a class="toggle-nav-btn" id="top" href="#nav-on">Menu</a>
          <div class="metabar" x-dim-metabar>
            <ul class="nav metabar__nav">
              <li v-if="username">
                <a href="#" class="metabar-link-faq">{{ 'Hi ' + username }}</a>
              </li>
              <li>
                <a href="#" class="metabar-link-faq">Hilfe</a>
              </li>
              <li v-if="!isAuthenticated" class="position--relative">
                <a href="#" class="toggle-trigger2" @click="$event.preventDefault(); login()">
                  <i class="fa fa-user font-size-125"></i>
                  <span><strong>Login</strong></span>
                </a>
              </li>
              <li v-if="isAuthenticated" class="position--relative">
                <a href="#" class="toggle-trigger2" @click="$event.preventDefault(); logout()">
                  <i class="fa fa-user font-size-125"></i>
                  <span><strong>Logout</strong></span>
                </a>
              </li>
              <li>
                <a href="#" class="toggle-trigger2">
                  <i class="fa fa-search font-size-125"></i>
                  <span> Suche </span>
                </a>
              </li>
              <li class="position--relative">
                <a href="#" class="toggle-trigger2" @click="$event.preventDefault(); isSelectLanguageDropdownActive = !isSelectLanguageDropdownActive">
                  <i class="fa fa- fa-globe font-size-125"></i>
                  <span>{{ t('components_generic_current_language') }}</span>
                </a>
                <ul v-if="isSelectLanguageDropdownActive" class="toggle-box actionbar__dropdown actionbar__dropdown--language active" style="display: block;">
                  <li>
                    <a href="#" @click="$event.preventDefault(); i18n.global.locale = 'de'; isSelectLanguageDropdownActive = false">Deutsch</a>
                  </li>
                  <li>
                    <a href="#" @click="$event.preventDefault(); i18n.global.locale = 'en'; isSelectLanguageDropdownActive = false">English</a>
                  </li>
              </ul>
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  </header>
  <div id="site-menu" class="vis-nav-side droppable-nav" data-levelcounter="1">
    <div id="site-menu-close" class="btn btn-link container-maxW text-left">
      <i class="fa fa-times-circle"></i>
      schließen
    </div>
    <div
      id="site-menu-back"
      class="vis-nav-side-back-button btn btn-link container-maxW text-left"
    >
      <i class="fa fa-chevron-circle-left"></i>
      zurück
    </div>
  </div>
  <div id="site-wrapper" class="vis-page-wrapper page-wrapper">
    <div class="header-row inner-wrap position--relative">
      <div class="page advert-billboard"></div>
    </div>
    <header>
      <div>
        <div class="inner-wrap inner-wrap--padding position--relative">
          <div class="grid page advert-row-expandable">
            <div class="grid__item header">
              <a href="#">
                <picture>
                  <source
                    srcset="/finder-template/Header_768x192.png"
                    media="(min-width: 768px) and (max-width: 949px)"
                  />
                  <source
                    srcset="/finder-template/Header_945x192.png"
                    media="(min-width: 950px)"
                  />
                  <img src="/finder-template/Header_608x192.png"/>
                </picture>
              </a>
            </div>
          </div>
          <div class="advert-col"></div>
        </div>
      </div>
    </header>
    <div class="nav-row cf visuallyhidden--portable">
      <div class="inner-wrap inner-wrap--padding">
        <div id="mainNavWrapper" class="mainNavWrapper pull-left page">
          <nav id="nav_main" class="vis-nav-main pull-left one-whole no-js">
            <ul class="list-unstyled pull-left one-whole flush">
              <li class="vis-nav-lvl-1 level_1 active">
                <a
                  class="entry has-children"
                  title="Home"
                  href="#"
                  data-link-active-level="level_1"
                  data-link-level="level_2"
                >
                  <i class="md md-home navi-icon-position"></i>
                  <span class="visuallyhidden">Home</span>
                </a>
                <div class="level_2_wrapper">
                  <div class="inner-wrap">
                    <div class="page page--padding">
                      <ul class="list-unstyled level_2 flush">
                        <li class="level_2">
                          <div class="col-sm-3">
                            <div class="mbm">
                              <a
                                class="entry"
                                title="Home"
                                href="/"
                                data-link-active-level="level_2"
                                data-link-level="level_3"
                              >
                                Home
                              </a>
                            </div>
                          </div>
                          <div class="col-sm-3">
                            <div class="mbm">
                              <a
                                class="entry"
                                title="Strukturdaten ProWein 2019"
                                href="#"
                                data-link-active-level="level_2"
                                data-link-level="level_3"
                              >
                                Strukturdaten ProWein 2019
                              </a>
                            </div>
                          </div>
                          <div class="col-sm-3">
                            <div class="mbm">
                              <a
                                class="entry has-children"
                                title="Das war die ProWein 2019"
                                href="#"
                                data-link-active-level="level_2"
                                data-link-level="level_3"
                              >
                                Das war die ProWein 2019
                              </a>
                              <ul class="list-unstyled level_3 flush">
                                <li class="level_3">
                                  <a
                                    class="entry"
                                    title="Rückblick ProWein 2019"
                                    href="#"
                                    data-link-active-level="level_3"
                                    data-link-level="level_4"
                                  >
                                    Rückblick ProWein 2019
                                  </a>
                                </li>
                                <li class="level_3">
                                  <a
                                    class="entry"
                                    title="ProWein 2019 Live"
                                    href="#"
                                    data-link-active-level="level_3"
                                    data-link-level="level_4"
                                  >
                                    ProWein 2019 Live
                                  </a>
                                </li>
                              </ul>
                            </div>
                          </div>
                          <div class="col-sm-3">
                            <div class="mbm">
                              <a
                                class="entry"
                                title="ProWein@Social Media"
                                href="#"
                                data-link-active-level="level_2"
                                data-link-level="level_3"
                              >
                                ProWein@Social Media
                              </a>
                            </div>
                          </div>
                        </li>
                      </ul>
                    </div>
                  </div>
                </div>
              </li>
              <li class="vis-nav-lvl-1 level_1 active">
                <a
                  class="entry"
                  title="same but different"
                  href="#"
                  data-link-active-level="level_1"
                  data-link-level="level_2"
                >
                  <span> same but different </span>
                </a>
              </li>
              <li class="vis-nav-lvl-1 level_1 active">
                <a
                  class="entry has-children"
                  title="Magazin"
                  href="#"
                  data-link-active-level="level_1"
                  data-link-level="level_2"
                >
                  <span> Magazin </span>
                </a>
              </li>
              <li class="vis-nav-lvl-1 level_1 active">
                <a
                  class="entry has-children"
                  title="Aussteller & Produkte"
                  href="#"
                  data-link-active-level="level_1"
                  data-link-level="level_2"
                >
                  <span> Aussteller & Produkte </span>
                </a>
              </li>
              <li class="vis-nav-lvl-1 level_1 active">
                <a
                  class="entry has-children"
                  title="Für Besucher"
                  href="#"
                  data-link-active-level="level_1"
                  data-link-level="level_2"
                >
                  <span> Für Besucher </span>
                </a>
              </li>
              <li class="vis-nav-lvl-1 level_1 active">
                <a
                  class="entry has-children"
                  title="Für Aussteller"
                  href="#"
                  data-link-active-level="level_1"
                  data-link-level="level_2"
                >
                  <span> Für Aussteller </span>
                </a>
              </li>
              <li class="vis-nav-lvl-1 level_1 active">
                <a
                  class="entry has-children"
                  title="Für Presse"
                  href="#"
                  data-link-active-level="level_1"
                  data-link-level="level_2"
                >
                  <span> Für Presse </span>
                </a>
              </li>
              <li class="vis-nav-lvl-1 level_1 active">
                <a
                  class="entry has-children"
                  title="Kontakt & Anreise"
                  href="#"
                  data-link-active-level="level_1"
                  data-link-level="level_2"
                >
                  <span> Kontakt & Anreise </span>
                </a>
              </li>
              <li class="vis-nav-lvl-1 level_1 active">
                <a
                  class="entry"
                  title="ProWein World"
                  href="#"
                  data-link-active-level="level_1"
                  data-link-level="level_2"
                >
                  <span> ProWein World </span>
                </a>
              </li>
            </ul>
          </nav>
        </div>
      </div>
    </div>
    <div class="content-block push-double--bottom finder-normalize">
      <div :class="[{'finder-content-wrapper push-double--top': addWrapper}]">
        <div :class="[{'finder-content-wrapper__inner': addWrapper}]">
          <slot></slot>
        </div>
      </div>
    </div>
    <footer class="footer">
      <!-- menu -->
      <div class="footer-row footer-row--menu">
        <div class="inner-wrap inner-wrap--padding">
          <div class="page">
            <ul class="footer__menu">
              <li class="footer__menu__item footer__menu__item--links">
                <div class="footer__menu__item__title">Messeplanung</div>
                <ul class="footer__submenu">
                  <li
                    class="footer__submenu__item"
                    title="Als Besucher registrieren"
                  >
                    <a
                      href="#"
                      class="footer__submenu__link footer__submenu__link--has-icon"
                    >
                      <i class="footer__submenu__link__icon fa fa-ticket"></i>
                      <div class="footer__submenu__link__label">
                        Als Besucher registrieren
                      </div>
                    </a>
                  </li>
                  <li
                    class="footer__submenu__item"
                    title="Aussteller - Produkte - News"
                  >
                    <a
                      href="#"
                      class="footer__submenu__link footer__submenu__link--has-icon"
                    >
                      <i class="footer__submenu__link__icon fa fa-search"></i>
                      <div class="footer__submenu__link__label">
                        Aussteller - Produkte - News
                      </div>
                    </a>
                  </li>
                  <li class="footer__submenu__item" title="Anreise">
                    <a
                      href="#"
                      class="footer__submenu__link footer__submenu__link--has-icon"
                    >
                      <i class="footer__submenu__link__icon fa fa-plane"></i>
                      <div class="footer__submenu__link__label">Anreise</div>
                    </a>
                  </li>
                </ul>
              </li>
              <li
                class="footer__menu__item footer__menu__item--top-pages"
                data-footer-togglebox=""
              >
                <div
                  class="footer__menu__item__title"
                  data-footer-togglebox-trigger=""
                >
                  Wichtige Links
                  <i
                    class="footer__menu__item__togglebox-indicator"
                    data-footer-togglebox-indicator=""
                  ></i>
                </div>
                <ul class="footer__submenu" data-footer-togglebox-content="">
                  <li class="footer__submenu__item">
                    <a href="#" class="footer__submenu__link">Geländeplan</a>
                  </li>
                  <li class="footer__submenu__item">
                    <a href="#" class="footer__submenu__link">Öffnungszeiten</a>
                  </li>
                  <li class="footer__submenu__item">
                    <a href="#" class="footer__submenu__link">Logo-Download</a>
                  </li>
                </ul>
              </li>
              <li
                class="footer__menu__item footer__menu__item--fairs"
                data-footer-togglebox=""
              >
                <div
                  class="
                    footer__menu__item__title
                    visuallyhidden--palm visuallyhidden--lap
                  "
                >
                  Weitere Online-Angebote der Messe-Düsseldorf GmbH aus dem
                  Kompetenzfeld Handel, Handwerk und Dienstleistungen:
                </div>
                <div
                  class="footer__menu__item__title visuallyhidden--desk"
                  data-footer-togglebox-trigger=""
                >
                  Weitere Messen
                  <i
                    class="footer__menu__item__togglebox-indicator"
                    data-footer-togglebox-indicator=""
                  ></i>
                </div>
                <ul
                  class="footer__submenu footer__submenu--cols"
                  data-footer-togglebox-content=""
                >
                  <li class="footer__submenu__item">
                    <a href="#" class="footer__submenu__link">ProWein World</a>
                  </li>
                  <li class="footer__submenu__item">
                    <a href="#" class="footer__submenu__link">EuroCIS</a>
                  </li>
                  <li class="footer__submenu__item">
                    <a href="#" class="footer__submenu__link">ProWine Shanghai</a>
                  </li>
                  <li class="footer__submenu__item">
                    <a href="#" class="footer__submenu__link">EuroShop</a>
                  </li>
                  <li class="footer__submenu__item">
                    <a href="#" class="footer__submenu__link">ProWine Hong Kong</a>
                  </li>
                  <li class="footer__submenu__item">
                    <a href="#" class="footer__submenu__link"
                    >Global Competence Retail</a
                    >
                  </li>
                  <li class="footer__submenu__item">
                    <a href="#" class="footer__submenu__link">ProWine Singapore</a>
                  </li>
                </ul>
              </li>
            </ul>
          </div>
        </div>
      </div>
      <!-- // menu -->
      <!-- logos -->
      <div class="footer-row footer-row--logos">
        <div class="inner-wrap inner-wrap--padding">
          <div class="page">
            <ul class="footer__logo-list">
              <li class="footer__logo-list__item footer__logo-list__item--main">
                <a title="Messe D&uuml;sseldorf" href="#">
                  <img
                    class="footer__logo-img"
                    src="/finder-template/logo_md_mobile.svg"
                    alt="Messe D&uuml;sseldorf"
                  />
                </a>
              </li>
              <li class="footer__logo-list__item footer__logo-list__item--d-cse">
                <a title="D&uuml;sseldorf Congress" href="#">
                  <img
                    class="footer__logo-img"
                    src="/finder-template/logo_ddc.png"
                    alt="D&uuml;sseldorf Congress"
                  />
                </a>
              </li>
            </ul>
          </div>
        </div>
      </div>
      <!-- // logos -->
      <!-- copyright -->
      <div class="footer-row footer-row--static-links">
        <div class="inner-wrap inner-wrap--padding">
          <div class="page">
            <div class="footer__static-links-wrapper">
              <ul class="footer__static-links">
                <li class="footer__static-links__item">
                  <a
                    class="footer__static-links__item__link"
                    href="#"
                    title="Messe D&uuml;sseldorf"
                  >
                    &copy; Messe D&uuml;sseldorf GmbH
                  </a>
                </li>
                <li class="footer__static-links__item">
                  <a class="footer__static-links__item__link" href="#">
                    Datenschutz
                  </a>
                </li>
                <li class="footer__static-links__item">
                  <a class="footer__static-links__item__link" href="#">
                    Impressum
                  </a>
                </li>
                <li class="footer__static-links__item">
                  <a class="footer__static-links__item__link" href="#"> Kontakt </a>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>
      <!-- // copyright -->
    </footer>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, watchEffect } from 'vue'
import { useI18n } from 'vue-i18n'
// import i18n from '@/i18n'
import createI18n from '@/i18n'
import { useKeycloak } from '@baloise/vue-keycloak'

export default defineComponent({
  name: 'FinderTemplate',
  props: {
    addWrapper: {
      type: Boolean,
      required: false,
      default: true
    }
  },
  setup () {
    const { t } = useI18n() // use as global scope
    const i18n = createI18n()
    const {
      keycloak, // keycloak is the instance of the keycloak-js adapter.
      isAuthenticated, // Ref<boolean> If true the user is authenticated.
      username, // Ref<string>  username the name of our user.
      isPending, // Ref<boolean>    If true the authentication request is still pending.
      token // Ref<string>  token is the raw value of the JWT token.
    } = useKeycloak()

    const isSelectLanguageDropdownActive = ref<boolean>(false)

    const logout = () => {
      const basePath = window.location.origin.toString()
      const logoutOptions = { redirectUri: basePath }
      keycloak.logout(logoutOptions)
    }

    const login = () => {
      const basePath = window.location.toString()
      const logoutOptions = { redirectUri: basePath }
      keycloak.login(logoutOptions)
    }

    watchEffect(() => console.log('isPending', isPending.value))
    watchEffect(() => console.log('token', token.value))

    return {
      t,
      isSelectLanguageDropdownActive,
      i18n,
      isAuthenticated,
      login,
      logout,
      username
    }
  }
})
</script>
