import { UseFetchReturn } from '@vueuse/core'
import { ProductListItem, ProductListItemInterface } from '@/models/ProductListItemInterface'
import Tag from '@/models/Tag'
import { myCreateFetch } from '@/composables/api/myCreateFetch'
import demoProducts from '@/models/demo/products.json'
import { config } from '../../../app.config'
import { ref } from 'vue'
import { useStore } from '@/store'

export function useFetchProductList (exhibitorId: string, language: string, immediate = true): UseFetchReturn<unknown> {
  const store = useStore()
  if (!config.useAPI) {
    return {
      isFetching: false,
      isFinished: true,
      error: null,
      data: ref(demoProducts.concat((demoProducts))),
      execute: function () {
        return new Promise(resolve => {
          resolve('')
        })
      }
    } as unknown as UseFetchReturn<unknown>
  }
  const useMyFetch = myCreateFetch()
  const editMode = store.$state.editMode
  let url = store.$state.urls.slices
  url = url.replace('{{exhId}}', exhibitorId).replace('{{language}}', language)
  url = url + '/products'
  if (editMode && store.$state.ticket) {
    url = url + `?ticket=${store.$state.ticket}`
  }

  // Filter the results and try to map certain properties
  return useMyFetch(url, {
    immediate: immediate,
    initialData: [],
    afterFetch (ctx) {
      const returnData = [] as ProductListItemInterface[]
      // eslint-disable-next-line
      ctx.data.forEach((element: any) => {
        const newElement = element as ProductListItemInterface
        if (element.categories) {
          newElement.tags = element.categories.map((category: string) => {
            return { label: category } as Tag
          })
        }
        returnData.push(new ProductListItem(element as ProductListItemInterface))
      })

      ctx.data = returnData
      return ctx
    }
  }).json()
}
