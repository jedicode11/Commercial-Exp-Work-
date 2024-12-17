import '@/assets/styles/main.scss'
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { dragscrollNext } from 'vue-dragscroll'
import smoothscroll from 'smoothscroll-polyfill'
import App from './App.vue'
import { setInitialState, stateInterface, testState, useStore } from './store'
import { loadTranslationsFromApi } from '@/utils/i18n'
import createI18n from './i18n'
import { TrackingAttribute, TrackingClick } from '@/directives/Tracking'
import { ModalContainerPlugin } from '@/plugins/modalContainerPlugin'

import 'bootstrap/dist/css/bootstrap.min.css'
import { apolloClient, DefaultApolloClient } from '@/graphql/apollo'
import 'v-calendar/dist/style.css'
import VCalendar from 'v-calendar'

import router from '@/router'
import Vue3TouchEvents from 'vue3-touch-events'

import 'bootstrap/dist/js/bootstrap.js'
import '@/assets/styles/custom.scss'

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
if (initialState === null) {
  initialState = {} as stateInterface
}

// function getQueryParams () {
//   const urlParams = new URLSearchParams(window.location.search)
//   return {
//     xCraftLivePreview: urlParams.get('x-craft-live-preview'),
//     token: urlParams.get('token')
//   }
// }

// const queryParams = getQueryParams()

if (document.getElementById('app')) {
  const i18n = createI18n()
  const thisApp = createApp(App)
    .use(i18n)
    .use(pinia)
    .use(router)
    .use(ModalContainerPlugin)
    .use(Vue3TouchEvents)
    .directive('dragscroll', dragscrollNext)
    .directive('tracking-attr', TrackingAttribute)
    .directive('tracking-click', TrackingClick)
    .provide(DefaultApolloClient, apolloClient)
  setInitialState(testState)
  loadTrans()
  thisApp.use(VCalendar, {})
  thisApp.mount('#app')
} else {
  console.error('selector "#app" was not found in HTML, aborting widget initialization')
  const urlParams = new URLSearchParams(window.location.search)
  const emsDebug = urlParams.get('ems_debug')
  if (emsDebug) {
    console.debug('body:', JSON.stringify(document.documentElement.outerHTML))
  }
}
