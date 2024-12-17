import { UseFetchReturn } from '@vueuse/core'
import { myCreateFetch } from '@/composables/api/myCreateFetch'
import demoProducts from '@/models/demo/productDetails.json'
import { config } from '../../../app.config'
import { ref } from 'vue'
import { useStore } from '@/store'

export function useFetchProduct (exhibitorId: string, productId: string, language: string, immediate = true): UseFetchReturn<unknown> {
  const store = useStore()
  if (!config.useAPI) {
    return {
      isFetching: false,
      isFinished: true,
      error: null,
      data: ref(demoProducts),
      execute: function () {
        return new Promise(resolve => {
          resolve('')
        })
      }
    } as unknown as UseFetchReturn<unknown>
  }
  const useMyFetch = myCreateFetch()
  let url = store.$state.urls.slices
  url = url.replace('{{exhId}}', exhibitorId).replace('{{language}}', language)
  url = url + `/products/${productId}`

  // Filter the results and try to map certain properties
  return useMyFetch(url, {
    immediate: immediate,
    afterFetch (ctx) {
      // ctx.data = { jens: 1 }
      //   const returnData = [] as ProductListItem[]
      //   // eslint-disable-next-line
      //   ctx.data.forEach((element : any) => {
      //     const newElement = element as ProductListItem
      //     if (element.categories) {
      //       newElement.tags = element.categories.map((category : string) => {
      //         return { label: category } as Tag
      //       })
      //     }
      //     returnData.push(element as ProductListItem)
      //   })
      //
      //   ctx.data = returnData
      return ctx
    }
  }).json()
}
