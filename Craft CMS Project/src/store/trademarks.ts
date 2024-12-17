import { defineStore } from 'pinia'
import { useStore } from '@/store'
import { useFetchTrademarks } from '@/composables/api/useFetchTrademarks'
import { Trademark } from '@/models/Trademark'

export interface TrademarksState {
  trademarksList: Array<Trademark>
}

let currExhId = ''

export const useTrademarks = defineStore('trademarks', {
  state: (): TrademarksState => ({
    trademarksList: []
  }),

  actions: {
    async fetchTrademarksList (forceLoad = false) {
      const store = useStore()

      if (currExhId !== store.$state.exhId || forceLoad) {
        currExhId = store.$state.exhId
        const { data, execute } = useFetchTrademarks(store.$state.exhId, store.$state.langCode, false)
        await execute()
        if (data.value) {
          this.trademarksList = data.value
        }
      }
    }
  }
})
