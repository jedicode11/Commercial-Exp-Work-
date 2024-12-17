import { defineStore } from 'pinia'

export const useSiteStore = defineStore('site', {
  state: () => ({
    appSiteId: '',
    appSites: [] as string[]
  }),

  getters: {
    getAppSiteId: (state) => state.appSiteId,
    getAppSites: (state) => state.appSites
  },

  actions: {
    setAppSiteId (appSiteId: string) {
      this.appSiteId = appSiteId
    },
    setAppSites (appSites: string[]) {
      this.appSites = appSites
    }
  }
})
