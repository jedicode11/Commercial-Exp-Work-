<template>
  <div class="main-wrapper">
    <component :is="currentPage" />
  </div>
</template>

<script lang="ts">
import { defineComponent, inject, onMounted, watch, WritableComputedRef } from 'vue'
import {
  EventsView,
  EventDetails,
  SearchView,
  SpeakerDetails,
  Slider,
  SessionDetails,
  SpeakerOverview
} from './components'

import { useRouterStore } from './store/router'
import { useLoadMatomoScript } from './composables/useLoadMatomoScript'
import { storeToRefs } from 'pinia'
import { useLanguageStore } from './store/language'
import { globalLocationStore } from './store/location'
import { useI18n } from 'vue-i18n'
import { useSiteId } from './composables/useSiteId'
import { StartPage } from '@/enums/script-params'
import { ScriptParamsService, sps } from './utils/scriptParamService'
import { useLazyQuery } from '@vue/apollo-composable'
import { GET_GLOBAL_LOCATIONS } from '@/graphql/queries/getGlobalLocations'

export default defineComponent({
  name: 'App',
  components: {
    EventsView,
    SearchView,
    EventDetails,
    SpeakerDetails,
    SessionDetails,
    Slider,
    SpeakerOverview,
  },
  setup() {
    useLoadMatomoScript()
    const i18n = useI18n()

    const routerStore = useRouterStore()
    const { currentPage } = storeToRefs(routerStore)
    const { changePage } = routerStore
    const gls = globalLocationStore()
    const { setGlobalLocations } = gls

    const languageStore = useLanguageStore()
    const { setLanguage } = languageStore

    const origin = inject<string | undefined>('app.origin')
    const initLang = inject<string>('app.initLang') ?? process.env.VUE_APP_I18N_LOCALE ?? 'en'
    console.info('initial language:', initLang)
    setLanguage(initLang)
    console.log('i18n', i18n)
    i18n.locale = initLang as unknown as WritableComputedRef<any>
    console.log('🚀 ~ setup ~ i18n.locale:', i18n.locale)

    const { setupAppSites, getSitesForGraphQl } = useSiteId()
    setupAppSites(inject('app.siteId') ?? (process.env.VUE_APP_SITE!))

    

    /*
     * parentUrl
     * parentTicketFragment
     * parentEventFragment
     * parentSpeakerFragment
     * parentSessionFragment
     */

    const parentUrl = inject<string | undefined>('app.parentUrl')
    let parentEventFragment
    let parentSpeakersFragment
    let parentSpeakerFragment
    let parentSessionFragment
    let parentFilterFragment
    let parentFilterDateFragment
    let parentFilterCurrentEventsFragment
    let parentFilterTagFragment
    let parentFilterTimeOfDayFragment
    let parentFilterSpeakerFragment
    let parentFilterLangFragment
    let parentFilterForumFragment
    
    let scriptParamsService

    if (parentUrl) {
      console.info('parent URL defined:', parentUrl, 'operating widget in redirect mode')
      parentEventFragment = inject<string | undefined>('app.parentEventFragment')
      parentSpeakersFragment = inject<string | undefined>('app.parentSpeakersFragment')
      parentFilterTagFragment = inject<string | undefined>('app.parentFilterTagFragment')
      parentSpeakerFragment = inject<string | undefined>('app.parentSpeakerFragment')
      parentSessionFragment = inject<string | undefined>('app.parentSessionFragment')
      parentFilterFragment = inject<string | undefined>('app.parentFilterFragment')
      parentFilterDateFragment = inject<string | undefined>('app.parentFilterDateFragment')
      parentFilterCurrentEventsFragment = inject<string |  undefined>('app.parentFilterCurrentEventsFragment')
      parentFilterTimeOfDayFragment = inject<string | undefined>('app.parentFilterTimeOfDayFragment')
      parentFilterSpeakerFragment = inject<string | undefined>('app.parentFilterSpeakerFragment')
      parentFilterLangFragment = inject<string | undefined>('app.parentFilterLangFragment')
      parentFilterForumFragment = inject<string | undefined>('app.parentFilterForumFragment')
      parentFilterTagFragment = inject<string | undefined>('app.parentFilterTagFragment')
      scriptParamsService = inject<ScriptParamsService | undefined>(sps)
    } else {
      console.info('parent URL not defined.', 'operating widget in internal routing mode')
    }

    const prepareUrlQueryParam = (fragment: string, value?: string) => {
      return fragment.replace(/[?&]/, '').replace(/{.*?}/, value ?? '')
    }

    const prepareUrlQueryParamMatch = (fragment: string, regexp?: string) => {
      return new RegExp(fragment.replace(/[?&]/, '').replace(/{.*?}/, (regexp ?? '')), 'u')
    }

    type StartPageParams = {
      id?: string
      uri?: string
      forums?: string
      speaker?: string
      langs?: string
      date?: string
      tags?: string
      currentEvents?: boolean
      timeOfDay?: string | undefined
    }

    // let startPage = inject<string>('app.startPage')
    let startPage = scriptParamsService?.startPage
    let startPageParams: StartPageParams = {}

    if (parentUrl) {
      console.info('trying to guess entry point...')
      const location = window.location
      console.log('🚀 ~ setup ~ location:', location)
      const href = decodeURI(location.href)

      if (parentEventFragment && href.includes(prepareUrlQueryParam(parentEventFragment))) {
        console.info('- event fragment found, elaborating event uri')
        const matches = href.match(prepareUrlQueryParamMatch(parentEventFragment, '([\\d\\p{L}-]*)[/&]?'))
        if (matches?.[1]) {
          console.info('- event id matched in fragment:', matches[1], 'using Event start page')
          startPage = StartPage.EVENT_DETAIL
          startPageParams.id = matches[1]
        } else {
          console.info('- no event id matched in fragment, using default start page')
          startPage || StartPage.EVENTS_VIEW
        }
      } else if (parentSpeakersFragment && href.includes(prepareUrlQueryParam(parentSpeakersFragment))) {
        console.info('- speakers fragment found, elaborating speaker uri')
        const matches = href.match(prepareUrlQueryParamMatch(parentSpeakersFragment, '([\\d\\p{L}-]*)[/&]?'))
        if (matches?.[1]) {
          console.info('- event id matched in fragment:', matches[1], 'using Speaker start page')
          startPage = StartPage.SPEAKERS_LIST  // 'SpeakersList'
          startPageParams.id = matches[1]
        } else {
          console.info('- no event id matched in fragment, using default start page')
          startPage || StartPage.EVENTS_VIEW
        }
      } else if (parentSpeakerFragment && href.includes(prepareUrlQueryParam(parentSpeakerFragment))) {
        console.info('- speaker fragment found, elaborating speaker uri')
        const matches = href.match(prepareUrlQueryParamMatch(parentSpeakerFragment, '([\\d\\p{L}-]*)[/&]?'))
        if (matches?.[1]) {
          console.info('- speaker uri matched in fragment:', matches[1], 'using Speaker start page')
          startPage = StartPage.SPEAKER_DETAIL  // 'SpeakerDetails'
          startPageParams.id = matches[1]
        } else {
          console.info('- no event id matched in fragment, using default start page')
          startPage || StartPage.EVENTS_VIEW
        }
      } else if (parentSessionFragment && href.includes(prepareUrlQueryParam(parentSessionFragment))) {
        console.info('- session fragment found, elaborating session uri')
        const matches = href.match(prepareUrlQueryParamMatch(parentSessionFragment, '([\\d\\p{L}-]*)[/&]?'))
        if (matches?.[1]) {
          console.info('- session uri matched in fragment:', matches[1], 'using Session start page')
          startPage = StartPage.SESSION_DETAIL  // 'SessionDetails'
          startPageParams.uri = matches[1]
        } else {
          console.info('- no session uri matched in fragment, using default start page')
          startPage = startPage || StartPage.EVENTS_VIEW
        }
      } else if (parentFilterFragment && href.includes(prepareUrlQueryParam(parentFilterFragment))) {
        console.info('- filter fragment found, elaborating session uri')
        startPage = StartPage.SEARCH_VIEW  // 'SearchView'

        if (parentFilterDateFragment) {
          const matchesDateParams = href.match(prepareUrlQueryParamMatch(parentFilterDateFragment, '([\\d\\p{L}.]*)[/&]?'))
          console.info('- filter uri matched date in fragment:', matchesDateParams?.[1], 'using Search start page')
          startPageParams.date = matchesDateParams?.[1]

          if (parentFilterTimeOfDayFragment) {
            const matchesTimeOfDayParams = href.match(prepareUrlQueryParamMatch(parentFilterTimeOfDayFragment, '(morning|afternoon)'))
            console.info('- filter uri matched time of day in fragment:', matchesTimeOfDayParams?.[1], 'using Search start page')
            startPageParams.timeOfDay = matchesTimeOfDayParams?.[1] === 'morning' || 'afternoon' ? matchesTimeOfDayParams?.[1] : undefined
          }
        }
        if (parentFilterSpeakerFragment) {
          const matchesSpeakerParams = href.match(prepareUrlQueryParamMatch(parentFilterSpeakerFragment, '([\\d\\p{L}_,-]*)[/&]?'))
          console.info('- filter uri matched speaker in fragment:', matchesSpeakerParams?.[1], 'using Search start page')
          startPageParams.speaker = matchesSpeakerParams?.[1]
        }
        if (parentFilterLangFragment) {
          const matchesLangParams = href.match(prepareUrlQueryParamMatch(parentFilterLangFragment, '([\\d\\p{L},-]*)[/&]?'))
          console.info('- filter uri matched lang in fragment:', matchesLangParams?.[1], 'using Search start page')
          startPageParams.langs = matchesLangParams?.[1]
        }
        if (parentFilterForumFragment) {
          const matchesForumsParams = href.match(prepareUrlQueryParamMatch(parentFilterForumFragment, '([\\d\\p{L},-]*)[/&]?'))
          console.info('- filter uri matched forum in fragment:', matchesForumsParams?.[1], 'using Search start page')
          startPageParams.forums = matchesForumsParams?.[1]
        }
        if (parentFilterCurrentEventsFragment) {
          const matchesCurrentEventsParams = href.match(prepareUrlQueryParamMatch(parentFilterCurrentEventsFragment, '([\\d\\p{L}_,-]*)[/&]?'))
          console.info('- filter matched current event in fragment:', matchesCurrentEventsParams?.[1], 'using Search start page')
          startPageParams.currentEvents = matchesCurrentEventsParams?.[1] === 'true' ? true : false
        }
        if (parentFilterTagFragment) {
          const matchesTagParams = href.match(prepareUrlQueryParamMatch(parentFilterTagFragment, '([\\d\\p{L},-]*)[/&]?'))
          console.info('- filter matched tag in fragment:', matchesTagParams?.[1], 'using Search start page')
          startPageParams.tags = matchesTagParams?.[1]
        }

      } else {
        console.info('- no fragment found, using default start page')
        startPage = startPage || StartPage.EVENTS_VIEW
      }
    } else if (startPage) {
      console.info('start page defined:', startPage)
    } else {
      console.info('no start page defined, using default')
    }

    if (startPage) {
      changePage(startPage, startPageParams)
    }

    const organizerBookmarkAddUrl = inject<string>('app.organizerBookmarkAddUrl')
    if (!organizerBookmarkAddUrl) {
      console.warn('No organizer bookmark add url provided. Bookmarking will be disabled.')
    }

    const loadIcons = () => {
      var ajax = new XMLHttpRequest();
      ajax.open("GET", `${origin}/img/icons.svg`, true);
      ajax.responseType = "document";
      ajax.onload = function (e) {
        if (ajax.responseXML) {
          document.body.insertBefore(ajax.responseXML.documentElement, document.body.childNodes[0]);
        }
      }
      ajax.send();
    }

    const { result, load } = useLazyQuery(GET_GLOBAL_LOCATIONS)

    onMounted(() => {
      loadIcons()
      load(undefined, { site: getSitesForGraphQl() })
    })

    watch(result, val => {
      setGlobalLocations(val.globalSets)
    })

    return {
      currentPage
    }
  }
})
</script>

<style lang="scss">
  .main-wrapper {
    @import '../node_modules/bootstrap/dist/css/bootstrap.min';
  }

  .clickable {
    cursor: pointer;
  }
</style>