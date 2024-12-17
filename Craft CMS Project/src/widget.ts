import '@/assets/styles/main.scss'
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { dragscrollNext } from 'vue-dragscroll'
import smoothscroll from 'smoothscroll-polyfill'
import Widget from './Widget.vue'
import { setInitialState, stateInterface, testState, useStore } from './store'
import { loadTranslationsFromApi } from '@/utils/i18n'
import createI18n from './i18n'
import { TrackingAttribute, TrackingClick } from '@/directives/Tracking'
import { ModalContainerPlugin } from '@/plugins/modalContainerPlugin'
import { SnackbarPlugin } from '@/plugins/snackbarPlugin'

import 'v-calendar/dist/style.css'
import VCalendar from 'v-calendar'

import 'gitart-vue-dialog/dist/style.css'
import { GDialog } from 'gitart-vue-dialog'
import { ApolloClient, createHttpLink, InMemoryCache } from '@apollo/client/core'
import { DefaultApolloClient } from '@vue/apollo-composable'
import Vue3TouchEvents from 'vue3-touch-events'

import 'bootstrap/dist/js/bootstrap.js'

import '@/assets/styles/custom.scss'
import { ScriptParamsService, sps } from './utils/scriptParamService'

smoothscroll.polyfill()

let initialState = null
const pinia = createPinia()

let translationsLoaded = false
function loadTrans ():void {
  const store = useStore()
  if (translationsLoaded) { return }
  const url = store.$state.urls.translations
  if (url) {
    loadTranslationsFromApi(store.$state.langCode, url)
    translationsLoaded = true
  }
}

const finderBaseConfigElement = document.getElementById('finder-base-config')
if (finderBaseConfigElement) {
  const baseConfigData = finderBaseConfigElement.textContent
  if (baseConfigData) {
    initialState = JSON.parse(baseConfigData)
  }
}
// Fallback
if (initialState === null) {
  initialState = {} as stateInterface
}

// ----- Get app Parameters

function getSyncScriptParams () {
  const currentScript = document.currentScript as HTMLOrSVGScriptElement
  return {
    scriptElement: currentScript,

    selector: currentScript.getAttribute('data-selector'),
    siteId: currentScript.getAttribute('data-site-id'),
    lang: currentScript.getAttribute('data-lang'),
    backendUri: currentScript.getAttribute('data-backend-uri'),
    authBearer: currentScript.getAttribute('data-auth-bearer'),
    startPage: currentScript.getAttribute('data-start-page'),
    pageSegment: currentScript.getAttribute('data-page-segment'),
    searchPhrase: currentScript.getAttribute('data-search-phrase'),
    searchProductCategories: currentScript.getAttribute('data-search-product-categories'),
    searchDate: currentScript.getAttribute('data-search-date'),
    searchSpeaker: currentScript.getAttribute('data-search-speaker'),
    searchForums: currentScript.getAttribute('data-search-forum'),
    permittedForums: currentScript.getAttribute('data-search-permitted-forums'),
    forumBasedOnEntry: currentScript.getAttribute('data-search-forum-based-on-entry'),
    searchResultOrder: currentScript.getAttribute('data-search-result-order'),
    detailsId: currentScript.getAttribute('data-details-id'),

    searchFieldIsHide: currentScript.getAttribute('data-search-field-is-hide'),
    speakerFilterIsHide: currentScript.getAttribute('data-search-speaker-is-hide'),
    tagFilterIsHide: currentScript.getAttribute('data-search-tag-is-hide'),
    widgetMode: currentScript.getAttribute('data-widget-mode'),

    tagCategoryParentId: currentScript.getAttribute('data-tag-category-perant-id'),

    eventAllSearchParams: currentScript.getAttribute('data-event-all-search-parameter'),

    querySearchSections: currentScript.getAttribute('data-query-search-sections'),

    parentUrl: currentScript.getAttribute('data-parent-url'),
    parentTicketFragment: currentScript.getAttribute('data-parent-ticket-fragment'),
    parentTicket: currentScript.getAttribute('data-parent-ticket'),
    parentEventFragment: currentScript.getAttribute('data-parent-event-fragment'),
    parentSpeakersFragment: currentScript.getAttribute('data-parent-speakers-fragment'),
    parentTagFragment: currentScript.getAttribute('data-parent-filter-tag-fragment'),
    parentSpeakerFragment: currentScript.getAttribute('data-parent-speaker-fragment'),
    parentSessionFragment: currentScript.getAttribute('data-parent-session-fragment'),
    parentFilterFragment: currentScript.getAttribute('data-parent-filter-fragment'),
    parentFilterDateFragment: currentScript.getAttribute('data-parent-filter-date-fragment'),
    parentFilterCurrentEventsFragment: currentScript.getAttribute('data-parent-filter-current-events-fragment'),
    parentFilterTimeOfDayFragment: currentScript.getAttribute('data-parent-filter-time-of-day-fragment'),
    parentFilterSpeakerFragment: currentScript.getAttribute('data-parent-filter-speaker-fragment'),
    parentFilterLangFragment: currentScript.getAttribute('data-parent-filter-lang-fragment'),
    parentFilterForumFragment: currentScript.getAttribute('data-parent-filter-forum-fragment'),
    parentFilterTagFragment: currentScript.getAttribute('data-parent-filter-tag-fragment'),

    websessionAddUrl: currentScript.getAttribute('data-websession-add-url'),
    websessionGetUrl: currentScript.getAttribute('data-websession-get-url'),

    organizerBookmarkAddUrl: currentScript.getAttribute('data-organizer-bookmark-add-url'),
    organizerBookmarksGetUrl: currentScript.getAttribute('data-organizer-bookmarks-get-url'),
    organizerBookmarkDeleteUrl: currentScript.getAttribute('data-organizer-bookmark-delete-url'),
    organizerTicket: currentScript.getAttribute('data-organizer-ticket'),
    organizerHeaders: currentScript.getAttribute('data-organizer-headers'),

    locationHallPlanUrl: currentScript.getAttribute('data-location-hall-plan-url'),
    locationHallPlanActivated: currentScript.getAttribute('data-location-hall-plan-activated'),

    limit: currentScript.getAttribute('data-limit') || process.env.VUE_APP_WIDGET_LIMIT || 30,
    offset: currentScript.getAttribute('data-offset') || process.env.VUE_APP_WIDGET_OFFSET || 20,

    src: currentScript.getAttribute('src')
  }
}

const scriptParams = getSyncScriptParams()

function getQueryParams () {
  const urlParams = new URLSearchParams(window.location.search)
  return {
    xCraftLivePreview: urlParams.get('x-craft-live-preview'),
    token: urlParams.get('token')
  }
}

const queryParams = getQueryParams()

const apolloURL = new URL(scriptParams.backendUri || process.env.VUE_APP_BACKEND_URI!)
queryParams.xCraftLivePreview && apolloURL.searchParams.set('x-craft-live-preview', queryParams.xCraftLivePreview)
queryParams.token && apolloURL.searchParams.set('token', queryParams.token)

// HTTP connection to the API
const httpLink = createHttpLink({
  // You should use an absolute URL here
  uri: apolloURL.href,
  headers: {
    Authorization: `Bearer ${scriptParams.authBearer || process.env.VUE_APP_BACKEND_AUTH_BEARER}`,
    'Content-Type': 'application/json'
  }
})

// Cache implementation
const cache = new InMemoryCache()

// Create the apollo client
const apolloClient = new ApolloClient({
  link: httpLink,
  cache
})

const scriptParamsService = new ScriptParamsService(scriptParams)
if (scriptParamsService.hasError) {
  scriptParamsService.getErrors.forEach(error => {
    console.error(error.message)
  })
}

const selector = scriptParams.selector ?? '#app'

if (document.querySelector(selector)) {
  const i18n = createI18n(scriptParams.lang ?? undefined)
  const thisApp = createApp(Widget)
    .use(i18n)
    .use(pinia)
    .use(ModalContainerPlugin)
    .use(SnackbarPlugin)
    .use(Vue3TouchEvents)
    .directive('dragscroll', dragscrollNext)
    .directive('tracking-attr', TrackingAttribute)
    .directive('tracking-click', TrackingClick)
    .provide('app.siteId', scriptParams.siteId)
    .provide('app.initLang', scriptParams.lang)
    .provide('app.backendUri', scriptParams.backendUri || process.env.VUE_APP_BACKEND_URI)
    .provide('app.startPage', scriptParams.startPage || process.env.VUE_APP_WIDGET_START_PAGE__TEST)
    .provide('app.pageSegment', scriptParams.pageSegment || process.env.VUE_APP_WIDGET_PAGE_SEGMENT__TEST)
    .provide('app.detailsId', scriptParams.detailsId || process.env.VUE_APP_WIDGET_DETAILS_ID__TEST)
    .provide('app.searchPhrase', scriptParams.searchPhrase || process.env.VUE_APP_WIDGET_SEARCH_PHRASE__TEST)
    .provide('app.searchProductCategories', scriptParams.searchProductCategories || process.env.VUE_APP_WIDGET_SEARCH_PRODUCT_CATEGORIES__TEST)
    .provide('app.searchDate', scriptParams.searchDate || process.env.VUE_APP_WIDGET_SEARCH_DATE__TEST)
    .provide('app.searchSpeaker', scriptParams.searchSpeaker || process.env.VUE_APP_WIDGET_SEARCH_SPEAKER__TEST)
    .provide('app.searchForums', scriptParams.searchForums || process.env.VUE_APP_WIDGET_SEARCH_FORUMS__TEST)
    .provide('app.permittedForums', scriptParams.permittedForums || process.env.VUE_APP_WIDGET_PERMITTED_FORUMS__TEST)
    .provide('app.forumBasedOnEntry', scriptParams.forumBasedOnEntry || process.env.VUE_APP_WIDGET_FORUM_BASED_ON_ENTRY__TEST)
    .provide('app.tagCategoryParentId', scriptParams.tagCategoryParentId || process.env.VUE_APP_TAG_CATEGORY_PARENT_ID__TEST)
    .provide('app.searchFieldIsHide', scriptParams.searchFieldIsHide || process.env.VUE_APP_WIDGET_SEARCH_FIELD_IS_HIDE__TEST)
    .provide('app.speakerFilterIsHide', scriptParams.speakerFilterIsHide || process.env.VUE_APP_WIDGET_SPEAKER_FILTER_IS_HIDE__TEST)
    .provide('app.searchResultOrder', scriptParams.searchResultOrder || process.env.VUE_APP_WIDGET_SEARCH_RESULT_ORDER__TEST)
    .provide('app.tagFilterIsHide', scriptParams.tagFilterIsHide || process.env.VUE_APP_WIDGET_TAG_FILTER_IS_HIDE__TEST)
    .provide('app.widgetMode', scriptParams.widgetMode || process.env.VUE_APP_WIDGET_MODE__TEST)
    .provide('app.parentUrl', scriptParams.parentUrl || process.env.VUE_APP_WIDGET_PARENT_URL__TEST)
    .provide('app.parentTicketFragment', scriptParams.parentTicketFragment || process.env.VUE_APP_WIDGET_PARENT_TICKET_FRAGMENT__TEST)
    .provide('app.parentTicket', scriptParams.parentTicket || process.env.VUE_APP_WIDGET_PARENT_TICKET__TEST)
    .provide('app.parentEventFragment', scriptParams.parentEventFragment || process.env.VUE_APP_WIDGET_PARENT_EVENT_FRAGMENT__TEST)
    .provide('app.parentSpeakerFragment', scriptParams.parentSpeakerFragment || process.env.VUE_APP_WIDGET_PARENT_SPEAKER_FRAGMENT__TEST)
    .provide('app.parentSpeakersFragment', scriptParams.parentSpeakersFragment || process.env.VUE_APP_WIDGET_PARENT_SPEAKERS_FRAGMENT__TEST)
    .provide('app.parentTagFragment', scriptParams.parentTagFragment || process.env.VUE_APP_WIDGET_PARENT_TAG_FRAGMENT__TEST)
    .provide('app.parentFilterFragment', scriptParams.parentFilterFragment || process.env.VUE_APP_WIDGET_PARENT_FILTER_FRAGMENT__TEST)
    .provide('app.parentFilterDateFragment', scriptParams.parentFilterDateFragment || process.env.VUE_APP_WIDGET_PARENT_FILTER_DATE_FRAGMENT__TEST)
    .provide('app.parentFilterCurrentEventsFragment', scriptParams.parentFilterCurrentEventsFragment || process.env.VUE_APP_WIDGET_PARENT_FILTER_CURRENT_EVENTS_FRAGMENT__TEST)
    .provide('app.parentFilterTimeOfDayFragment', scriptParams.parentFilterTimeOfDayFragment || process.env.VUE_APP_WIDGET_PARENT_FILTER_TIME_OF_DAY_FRAGMENT__TEST)
    .provide('app.parentFilterSpeakerFragment', scriptParams.parentFilterSpeakerFragment || process.env.VUE_APP_WIDGET_PARENT_FILTER_SPEAKER_FRAGMENT__TEST)
    .provide('app.parentFilterLangFragment', scriptParams.parentFilterLangFragment || process.env.VUE_APP_WIDGET_PARENT_FILTER_LANG_FRAGMENT__TEST)
    .provide('app.parentFilterForumFragment', scriptParams.parentFilterForumFragment || process.env.VUE_APP_WIDGET_PARENT_FILTER_FORUM_FRAGMENT__TEST)
    .provide('app.parentFilterTagFragment', scriptParams.parentFilterTagFragment || process.env.VUE_APP_WIDGET_PARENT_FILTER_TAG_FRAGMENT__TEST)
    .provide('app.parentSessionFragment', scriptParams.parentSessionFragment || process.env.VUE_APP_WIDGET_PARENT_SESSION_FRAGMENT__TEST)
    .provide('app.websessionAddUrl', scriptParams.websessionAddUrl)
    .provide('app.websessionGetUrl', scriptParams.websessionGetUrl)
    .provide('app.organizerBookmarkAddUrl', scriptParams.organizerBookmarkAddUrl || process.env.VUE_APP_WIDGET_ORGANIZER_BOOKMARK_ADD_URL)
    .provide('app.organizerBookmarksGetUrl', scriptParams.organizerBookmarksGetUrl || process.env.VUE_APP_WIDGET_ORGANIZER_BOOKMARKS_GET_URL)
    .provide('app.organizerBookmarkDeleteUrl', scriptParams.organizerBookmarkDeleteUrl || process.env.VUE_APP_WIDGET_ORGANIZER_BOOKMARK_DELETE_URL)
    .provide('app.organizerTicket', scriptParams.organizerTicket || process.env.VUE_APP_WIDGET_ORGANIZER_TICKET__TEST)
    .provide('app.origin', scriptParams.src ? new URL(scriptParams.src).origin : '')
    .provide('app.organizerHeaders', scriptParams.organizerHeaders || process.env.VUE_APP_WIDGET_ORGANIZER_HEADERS__TEST)
    .provide('app.querySearchSections', scriptParams.querySearchSections || process.env.VUE_APP_WIDGET_QUERY_SEARCH_SECTIONS__TEST)
    .provide('app.eventAllSearchParams', scriptParams.eventAllSearchParams)
    .provide('app.locationHallPlanUrl', scriptParams.locationHallPlanUrl || process.env.VUE_APP_WIDGET_LOCATION_HALL_PLAN_URL__TEST)
    .provide('app.locationHallPlanActivated', scriptParams.locationHallPlanActivated || process.env.VUE_APP_WIDGET_LOCATION_HALL_PLAN_ACTIVATED__TEST)
    .provide('app.limit', scriptParams.limit || process.env.VUE_APP_WIDGET_LIMIT)
    .provide('app.offset', scriptParams.offset || process.env.VUE_APP_WIDGET_OFFSET)
    .provide(DefaultApolloClient, apolloClient)

    .provide(sps, scriptParamsService)
  setInitialState(testState) // set store's test state explicitly
  loadTrans()
  thisApp.use(VCalendar, {})
  thisApp.component('GDialog', GDialog)
  thisApp.mount(selector)
} else {
  console.error('selector ' + selector + ' was not found in HTML, aborting widget initialization')
  const urlParams = new URLSearchParams(window.location.search)
  const emsDebug = urlParams.get('ems_debug')
  if (emsDebug) {
    console.debug('body:', JSON.stringify(document.documentElement.outerHTML))
  }
}
