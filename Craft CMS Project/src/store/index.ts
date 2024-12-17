import { defineStore } from 'pinia'
import { get as _get } from 'lodash-es'
import { PageLanguage, PageSectionAnchor } from '@/models/base'

export interface stateInterface {
  'context': string,
  'apiBaseUrl': string,
  'visDomain': string,
  'visValidation': string,
  'langCode': string,
  'userLoggedIn': boolean,
  'ticket': string,
  'editMode': boolean,
  'kioskMode': boolean,
  'hallMapType': string,
  'hallMapUrl': string,
  'myPlannerEnabled': boolean,
  'myPlannerCookieEnabled': boolean,
  'getInTouchEnabled': boolean,
  'exhId': string,
  'exhSeoId': string,
  'rights':
  {
    'news':
    {
      'add': boolean,
      'edit': boolean,
    },
    'view':
    {
      'getInTouch': boolean,
      'history': boolean,
      'dashboard': boolean,
    },
    'activities':
    {
      'add': boolean,
      'edit': boolean
    },
    'profile':
    {
      'address': boolean
      'anonyms': boolean,
      'businessData': boolean,
      'contactData': boolean,
      'getInTouch': boolean,
      'linkVideos': boolean,
      'pdfs': boolean,
      'premium': boolean,
      'socialMedia': boolean,
      'synonyms': boolean,
      'tags': boolean,
      'uploadImages': boolean,
      'uploadVideos': boolean,
    },
    'exhCategories':
    {
      'edit': boolean,
    },
    'categories':
    {
      'edit': boolean,
    },
    'portrait':
    {
      'edit': boolean,
      'text': boolean,
    },
    'contacts':
    {
      'add': boolean,
      'edit': boolean,
    },
    'products':
    {
      'add': boolean,
      'prod13': boolean,
      'prod6': boolean,
      'prod12': boolean,
      'prod5': boolean,
      'images': boolean,
      'prod4': boolean,
      'prod14': boolean,
      'prod2': boolean,
      'edit': boolean,
      'uploadVideos': boolean,
      'title': boolean,
      'prod11': boolean,
      'prod10': boolean,
      'linkVideos': boolean,
      'pdfs': boolean,
      'text1': boolean,
      'text2': boolean,
      'links': boolean,
      'categories': boolean,
      'text': boolean,
      'prod9': boolean,
      'prod8': boolean,
      'prod7': boolean,
    }
  },
  'pageSections': Array<PageSectionAnchor>,
  'pageLanguages': Array<PageLanguage>,
  'urls':
  {
    'translations': string,
    'slices': string,
    'exhProfiles': string,
    'exhProfilesSeo': string,
    'exhProfileEditMode': string,
    'exhDashboard': string,
    'exhHistory': string,
    'bookingLinkLogo': string,
    'bookingLinkCategories': string,
    'exhHallMapLink': string,
    'exhProfilesRecommendation': string,
    'exhProfilesSeoRecommendation': string
  }
}

const defaultState = {
  context: '',
  apiBaseUrl: '',
  visDomain: '',
  visValidation: '',
  langCode: 'de',
  userLoggedIn: false,
  ticket: '',
  editMode: false,
  kioskMode: false,
  hallMapType: 'none',
  hallMapUrl: '',
  myPlannerEnabled: false,
  myPlannerCookieEnabled: false,
  getInTouchEnabled: false,
  exhId: '',
  exhSeoId: '',
  rights:
  {
    news:
    {
      add: false,
      edit: false
    },
    view:
    {
      getInTouch: false,
      history: false,
      dashboard: false
    },
    activities:
    {
      add: false,
      edit: false
    },
    profile:
    {
      address: false,
      anonyms: false,
      businessData: false,
      contactData: false,
      getInTouch: false,
      linkVideos: false,
      pdfs: false,
      premium: false,
      socialMedia: false,
      synonyms: false,
      tags: false,
      uploadImages: false,
      uploadVideos: false
    },
    exhCategories:
    {
      edit: false
    },
    categories:
    {
      edit: false
    },
    portrait:
    {
      edit: false,
      text: false
    },
    contacts:
    {
      add: false,
      edit: false
    },
    products:
    {
      add: false,
      prod13: false,
      prod6: false,
      prod12: false,
      prod5: false,
      images: false,
      prod4: false,
      prod14: false,
      prod2: false,
      edit: false,
      uploadVideos: false,
      title: false,
      prod11: false,
      prod10: false,
      linkVideos: false,
      pdfs: false,
      text1: false,
      text2: false,
      links: false,
      categories: false,
      text: false,
      prod9: false,
      prod8: false,
      prod7: false
    }
  },
  pageSections: [] as Array<PageSectionAnchor>,
  pageLanguages: [] as Array<PageLanguage>,
  urls:
  {
    translations: '',
    slices: '',
    exhProfiles: '',
    exhProfilesSeo: '',
    exhProfileEditMode: '',
    exhDashboard: '',
    exhHistory: '',
    bookingLinkLogo: '',
    bookingLinkCategories: '',
    exhHallMapLink: '',
    exhProfilesRecommendation: '',
    exhProfilesSeoRecommendation: ''

  }
} as stateInterface

export const testState = {
  context: '/index',
  apiBaseUrl: '',
  visDomain: 'https://prowein-finder-dev.dimedis.de',
  visValidation: '',
  langCode: 'de',
  userLoggedIn: true,
  ticket: '28977061392165',
  editMode: false,
  kioskMode: false,
  hallMapType: 'none',
  hallMapUrl: '',
  myPlannerEnabled: true,
  myPlannerCookieEnabled: true,
  getInTouchEnabled: true,
  exhId: 'prowein2020.2638475', // ganz gut
  // exhId: 'prowein2020.2648063', // premium
  // exhId: 'prowein2020.2650754', // hat zu große Bilder für die Kacheln
  // exhId: 'prowein2020.2643314',
  // exhId: 'prowein2020.2647804', // hat 127 Sub-Aussteller
  // exhId: 'prowein2020.2632103',
  // exhId: 'prowein2020.2651408',
  // exhId: 'prowein2020.2645282', // altes schlößchen
  exhSeoId: 'TestSeoId123',
  rights:
  {
    news:
    {
      add: true,
      edit: true
    },
    view:
    {
      getInTouch: true,
      history: true,
      dashboard: true
    },
    activities:
    {
      add: true,
      edit: true
    },
    profile:
    {
      address: true,
      anonyms: true,
      businessData: true,
      contactData: true,
      getInTouch: true,
      linkVideos: true,
      pdfs: true,
      premium: true,
      socialMedia: true,
      synonyms: true,
      tags: true,
      uploadImages: false,
      uploadVideos: false
    },
    exhCategories:
    {
      edit: true
    },
    categories:
    {
      edit: true
    },
    portrait:
    {
      edit: true,
      text: true
    },
    contacts:
    {
      add: true,
      edit: true
    },
    products:
    {
      add: true,
      prod13: true,
      prod6: true,
      prod12: true,
      prod5: true,
      images: true,
      prod4: true,
      prod14: true,
      prod2: true,
      edit: true,
      uploadVideos: true,
      title: true,
      prod11: true,
      prod10: true,
      linkVideos: true,
      pdfs: true,
      text1: true,
      text2: true,
      links: true,
      categories: true,
      text: true,
      prod9: true,
      prod8: true,
      prod7: true
    }
  },
  pageSections: [
    { id: 'profile', title: 'Profil', selector: '#' },
    { id: 'trademarks', title: 'Marken', selector: '#' },
    { id: 'products', title: 'Produkte', selector: '#' },
    { id: 'stand', title: 'Stand', selector: '#' },
    { id: 'appointments', title: 'Events', selector: '#' },
    { id: 'news', title: 'Firmennews', selector: '#' }
  ] as Array<PageSectionAnchor>,
  pageLanguages: [
    { id: 'de', title: 'Deutsch', isCurrentLang: true, link: '#deutsch' },
    { id: 'en', title: 'English', isCurrentLang: false, link: '#english' }
  ] as Array<PageLanguage>,
  urls:
  {
    translations: '/vis-api/vis/v1/de/files/translation.json?type=components',
    slices: '/vis-api/vis/v1/{{language}}/exhibitors/{{exhId}}/slices',
    exhProfiles: '/vis/v1/de/exhibitors/{{exhId}}?oid=29556&lang=1',
    exhProfilesSeo: '/vis/v1/de/exhprofiles/{{exhSeoId}}?oid=29556&lang=1',
    exhProfileEditMode: '',
    exhDashboard: '/vis_dashboard?stand_id={{exhId}}&exh_select={{exhId}}&ticket=12456789',
    exhHistory: '/vis/v1/de/exhibitors/prowein2020.2638475/history?_cmsparams&_edit=true',
    bookingLinkLogo: '/oos_link_logo?ticket=12456789',
    bookingLinkCategories: '/oos_link_category?ticket=12456789',
    exhHallMapLink: '',
    exhProfilesRecommendation: '/vis/v1/de/exhibitors/{{exhId}}',
    exhProfilesSeoRecommendation: '/vis/v1/de/exhprofiles/{{exhSeoId}}'
  }
} as stateInterface

/*
const changeLocale = (locale: string) => {
  if (locale == null) {
    return
  }

  i18n.global.locale = locale
  moment.locale(locale)
}

changeLocale(initialState.locale)
*/

export const useStore = defineStore('main', {
  state: () => (defaultState),
  getters: {
    getExhibitorProfileUrl (state) {
      return (exhibitorId: string, exhSeoId: string) => {
        if (exhSeoId !== '') {
          return state.urls.exhProfilesSeo.replace('{{exhSeoId}}', exhSeoId)
        } else {
          return state.urls.exhProfiles.replace('{{exhId}}', exhibitorId)
        }
      }
    },
    getExhibitorProfileRecommendationUrl (state) {
      return (exhibitorId: string) => {
        return window.location.origin + state.urls.exhProfilesRecommendation.replace('{{exhId}}', exhibitorId)
      }
    },
    // this function uses stringPath (i.e. 'rights.a.b.c') pattern to check the current users permissions
    // this won't be cached like other getters and returns a function you have to call with the stringPath param instead
    hasRightsFunction: (state) => {
      return (stringPath: string) => {
        return _get(state.rights, stringPath, false)
      }
    },
    prerenderMode: () => {
      // returns true if rendered by Headless Chrome of Prerender service
      // expected UA String: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) HeadlessChrome/93.0.4577.82 Safari/537.36 Prerender (+https://github.com/prerender/prerender)
      return navigator.userAgent.match(/Prerender/i) != null
    }
  },
  actions: {
    // changeLocale (locale: string) {
    //   this.locale = locale
    //   changeLocale(locale)
    // }
  }
})

export function setInitialState (initialState: stateInterface): void {
  const store = useStore()
  store.$patch(initialState)
}
