import { UseFetchReturn } from '@vueuse/core'
import { myCreateFetch } from '@/composables/api/myCreateFetch'
import { config } from '../../../app.config'
import { ref } from 'vue'

export function useFetchTranslations (language : string, immediate = true, url: string): UseFetchReturn<unknown> {
  if (!config.useAPI) {
    return {
      isFetching: false,
      isFinished: true,
      error: null,
      data: ref({}),
      execute: function () {
        return new Promise(resolve => {
          resolve('')
        })
      }
    } as unknown as UseFetchReturn<unknown>
  }
  const useMyFetch = myCreateFetch()

  // Filter the results and try to map certain properties
  return useMyFetch(url, {
    immediate: immediate,
    initialData: {}
  }).json()
}
