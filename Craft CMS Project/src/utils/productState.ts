import { ProductListItemInterface } from '@/models/ProductListItemInterface'
import { useStore } from '@/store'

interface ProductState {
  hasCategories: boolean
  hasDownloads: boolean
  hasInfos: boolean
  hasLinks: boolean
}

export function getProductState (product: ProductListItemInterface | null): ProductState {
  const store = useStore()
  const isKioskMode = store.$state.kioskMode

  let productState = {
    hasCategories: false,
    hasDownloads: false,
    hasInfos: false,
    hasLinks: false
  }

  if (product) {
    const infos = Object.values(product.additionalDetails)
    const categories = product.categories
    const linkItems = product.links
    const downloadItems = product.pdfs

    productState = {
      hasCategories: categories && categories.length > 0,
      hasDownloads: !isKioskMode && downloadItems && downloadItems.length > 0,
      hasInfos: infos && infos.length > 0,
      hasLinks: linkItems && linkItems.length > 0
    }
  }

  return productState
}
