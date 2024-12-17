import { ref } from 'vue'
import { UseFetchReturn } from '@vueuse/core'
import { myCreateFetch } from '@/composables/api/myCreateFetch'
import { useStore } from '@/store'
import { config } from '../../../app.config'
import { Trademark } from '@/models/Trademark'
import demoTrademarks from '@/models/demo/trademarks.json'

export function useFetchTrademarks (
  exhibitorId: string,
  language: string,
  immediate = true
): UseFetchReturn<Array<Trademark>> {
  const store = useStore()
  if (!config.useAPI) {
    return {
      isFetching: false,
      isFinished: true,
      error: null,
      data: ref(demoTrademarks),
      execute: function () {
        return new Promise(resolve => {
          resolve('')
        })
      }
    } as unknown as UseFetchReturn<Array<Trademark>>
  }
  const useMyFetch = myCreateFetch()
  let url = store.$state.urls.slices
  url = url.replace('{{exhId}}', exhibitorId).replace('{{language}}', language)
  url = url + '/trademarks'

  return useMyFetch(url, {
    immediate: immediate
  }).json()
}
