import { defineStore } from 'pinia'
import {
  addToFavorites,
  removeFromFavorites,
  getFullList
} from '@/composables/vis-modules/myPlanner'

interface MyPlannerState {
  favoriteList: Array<string>
}

export const useMyPlanner = defineStore('myplanner', {
  state: (): MyPlannerState => ({
    favoriteList: []
  }),

  getters: {
    isFavorite: (state) => {
      return (profileId: string) => state.favoriteList.indexOf(profileId) !== -1
    }
  },

  actions: {
    async fetchFavoriteList () {
      let fullList: Array<string> = []
      try {
        fullList = await getFullList()
      } catch (e) {
        console.warn('Error fetching Fav List')
        fullList = []
      }
      this.$patch({ favoriteList: fullList })
    },
    async addToFavorites (id: string) {
      await addToFavorites(id)
      await this.fetchFavoriteList()
    },
    async removeFromFavorites (id: string) {
      await removeFromFavorites(id)
      await this.fetchFavoriteList()
    }
  }
})
