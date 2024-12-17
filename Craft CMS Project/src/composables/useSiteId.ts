import { storeToRefs } from 'pinia'
import { useLanguageStore } from './../store/language'
import { useSiteStore } from '@/store/site'

type SiteIdComposable = {
  setupAppSites: (siteId: string) => void;
  parseAppSites: (appSiteId: string) => string[];
  getSitesForGraphQl: () => string[];
}

export function useSiteId (): SiteIdComposable {
  const setupAppSites = (siteId = '') => {
    const siteStore = useSiteStore()
    const { setAppSiteId, setAppSites } = siteStore

    console.info('working with site Id:', siteId)
    setAppSiteId(siteId)

    const appSites = parseAppSites(siteId)
    setAppSites(appSites)
  }

  const parseAppSites = (appSiteId: string) => {
    let siteIds: string | string[]

    try {
      siteIds = JSON.parse(appSiteId)
    } catch {
      siteIds = appSiteId.split(',').map(siteId => siteId.trim())
    }

    return Array.isArray(siteIds) ? siteIds : [siteIds]
  }

  const getSitesForGraphQl = () => {
    const siteStore = useSiteStore()
    const { getAppSites } = siteStore
    const languageStore = useLanguageStore()
    const { currentLanguage } = storeToRefs(languageStore)

    const appendLang = (siteId: string) => {
      switch (currentLanguage.value) {
        case 'de':
          return siteId + 'De'
        default:
          return siteId + 'En'
      }
    }

    return getAppSites.map(appendLang)
  }

  return {
    setupAppSites,
    parseAppSites,
    getSitesForGraphQl
  }
}
