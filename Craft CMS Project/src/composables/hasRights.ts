
import { useStore } from '@/store'

/**
 * Check the current users permissions
 * @param stringPath {string} e.g. 'products.add'
 * @returns boolean
 */
export const hasRights = (stringPath: string): boolean => {
  const store = useStore()
  return store.hasRightsFunction(stringPath)
}
