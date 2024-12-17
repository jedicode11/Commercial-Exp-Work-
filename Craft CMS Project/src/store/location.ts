import { defineStore } from 'pinia'
import { GlobalLocationModel } from '@/models/GlobalLocationModel'

export const globalLocationStore = defineStore('globalLocations', {
  state: () => ({
    globalLocations: [] as GlobalLocationModel[],
  }),

  getters: {
    getGlobalLocations: (state) => state.globalLocations,
  },

  actions: {
    setGlobalLocations (globalLocation: GlobalLocationModel[]) {
        this.globalLocations = globalLocation
    },
    getGlobalLocationBySiteId (siteId: number): GlobalLocationModel | undefined {
      return this.globalLocations.find(gl => gl.siteId === siteId)
    }
  }
})
