import { UseFetchReturn } from '@vueuse/core'
// import { ProductListItemInterface } from '@/models/ProductListItemInterface'
// import Tag from '@/models/Tag'
import { myCreateFetch } from '@/composables/api/myCreateFetch'
import demoProductCategories from '@/models/demo/productCategories.json'
import { config } from '../../../app.config'
import { ref } from 'vue'
import {
  IdAndLabel, idAndLabelInterface,
  ProductCategory,
  ProductCategoryInterface
} from '@/models/ProductCategories'
import { useStore } from '@/store'

export function useFetchProductCategories (exhibitorId: string, language: string, immediate = true): UseFetchReturn<unknown> {
  const store = useStore()
  if (!config.useAPI) {
    return {
      isFetching: false,
      isFinished: true,
      error: null,
      data: ref(demoProductCategories),
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
  url = url + '/categories'

  // Filter the results and try to map certain properties
  return useMyFetch(url, {
    immediate: immediate,
    initialData: [] as unknown as ProductCategoryInterface[],
    afterFetch (ctx) {
      const categories = [] as unknown as ProductCategoryInterface[]
      ctx.data.forEach((element: ProductCategoryInterface) => {
        const productList = [] as idAndLabelInterface[]
        element.productList.forEach((list) => {
          productList.push(new IdAndLabel(list.id, list.label))
        })

        const hierarchy = [] as idAndLabelInterface[]
        element.hierarchy.forEach((list) => {
          hierarchy.push(new IdAndLabel(list.id, list.label))
        })

        const cat = new ProductCategory({
          id: element.id,
          label: element.label,
          catalogIndex: element.catalogIndex,
          productList: productList,
          hierarchy: hierarchy
        } as ProductCategoryInterface)
        categories.push(cat)
      })

      ctx.data = categories
      return ctx
    }
  }).json()
}
