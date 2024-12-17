import { config } from '../../../app.config'
import { useStore } from '@/store'

declare global {
  interface Window {
    DIMEDIS: any // eslint-disable-line
  }
}

export const getShowFavButton = (id: string): boolean => {
  const store = useStore()
  const favType = (id || '').split('=').shift() || ''
  const allowedDocTypes = ['profile'/* , 'prodinfo' */] // get 'allowedDocTypes' via config as soon as implemented (https://git.dimedis.de/vis/jarvis/-/merge_requests/520)
  const allowFav = allowedDocTypes.includes(favType)
  return allowFav && !store.$state.editMode && store.$state.myPlannerEnabled && (store.$state.userLoggedIn || store.$state.myPlannerCookieEnabled)
}
export const getFullList = (): Promise<Array<string>> => {
  if (window.DIMEDIS) {
    return window.DIMEDIS.$fn.myplannerController.getFullList()
  } else {
    if (!config.useAPI) {
      return Promise.resolve([])
    }
    console.warn('window.DIMEDIS is not available')
    return Promise.reject(new Error('window.DIMEDIS is not available'))
  }
}

export const getIsFavorite = (id: string): Promise<boolean> => {
  if (window.DIMEDIS) {
    return window.DIMEDIS.$fn.myplannerController.listContainsId(id)
  } else {
    if (!config.useAPI) {
      return Promise.resolve(false)
    }
    console.warn('window.DIMEDIS is not available')
    return Promise.reject(new Error('window.DIMEDIS is not available'))
  }
}

export async function addToFavorites (id: string): Promise<boolean> {
  if (window.DIMEDIS) {
    return window.DIMEDIS.$fn.myplannerController.addToList(id)
  } else {
    if (!config.useAPI) {
      return Promise.resolve(true)
    }
    console.warn('window.DIMEDIS is not available')
    return Promise.reject(new Error('window.DIMEDIS is not available'))
  }
}

export async function removeFromFavorites (id: string): Promise<boolean> {
  if (window.DIMEDIS) {
    return window.DIMEDIS.$fn.myplannerController.removeFromList(id)
  } else {
    if (!config.useAPI) {
      return Promise.resolve(true)
    }
    console.warn('window.DIMEDIS is not available')
    return Promise.reject(new Error('window.DIMEDIS is not available'))
  }
}
