import { defineStore } from 'pinia'

export const useRouterStore = defineStore('router', {
  state: () => ({
    page: 'EventsView',
    params: undefined as any,
    history: [] as unknown as { page: string, params?: any }[]
  }),

  getters: {
    currentPage: (state) => state.page,
    currentParams: (state) => state.params,
    hasHistory: (state) => state.history.length > 1
  },

  actions: {
    changePage (page: string, params?: any) {
      this.history.push({ page: this.page, params: this.params })
      this.page = page
      this.params = params
    },
    goBack () {
      const history = this.history.pop()
      console.log('🚀 ~ goBack ~ history:', history)
      if (history) {
        const { page, params } = history
        this.page = page
        this.params = params
      }
    }
  }
})
