import { ref } from 'vue'
import { UseFetchReturn } from '@vueuse/core'
import { myCreateFetch } from '@/composables/api/myCreateFetch'
import { config } from '../../../app.config'
import demoLanguages from '../../models/demo/languages.json'
import { useStore } from '@/store'

export interface Language {
  id: string,
  label: string
}

export interface MyadminLanguage extends Language {
  isFilled: boolean,
  isCurrentLang: boolean,
  title: string,
  link: string,
  previewUrl: string,
  editUrl: string
}

export function useFetchLanguages (
  userLanguage: string,
  immediate = true
): UseFetchReturn<Array<Record<string, unknown>>> {
  if (!config.useAPI) {
    return {
      isFetching: false,
      isFinished: true,
      error: null,
      data: ref(demoLanguages),
      execute: function () {
        return new Promise(resolve => {
          resolve('')
        })
      }
    } as unknown as UseFetchReturn<Array<Record<string, unknown>>>
  }
  const useMyFetch = myCreateFetch()
  let url = window.DIMEDIS.vis.myadmin.config.urls.lists
  url = url.replace('{{userLang}}', userLanguage).replace('{{listId}}', 'languages')

  // Filter the results and try to map certain properties
  return useMyFetch(url, {
    immediate: immediate,
    initialData: []
  }).json()
}

export function useFetchPageLanguages (
  immediate = true
): UseFetchReturn<Array<MyadminLanguage>> {
  const store = useStore()
  const useMyFetch = myCreateFetch()
  const editMode = store.$state.editMode
  let url = window.DIMEDIS.vis.myadmin.config.urls.pageLanguages
  if (editMode && store.$state.ticket) {
    url = url + `?ticket=${store.$state.ticket}`
  }

  // Filter the results and try to map certain properties
  return useMyFetch(url, {
    immediate: immediate,
    initialData: [],
    afterFetch (ctx) {
      return ctx
    }
  }).json()
}

export function useFetchTranslatePage (
  languageId: string,
  overwrite: boolean | undefined,
  immediate = true
): UseFetchReturn<Array<MyadminLanguage>> {
  const store = useStore()
  const useMyFetch = myCreateFetch()
  const editMode = store.$state.editMode
  let url = window.DIMEDIS.vis.myadmin.config.urls.translatePage
  if (editMode && store.$state.ticket) {
    url = url + `?langTo=${languageId}&overwrite=${overwrite}&ticket=${store.$state.ticket}`
  }

  // Filter the results and try to map certain properties
  return useMyFetch(url, {
    immediate: immediate,
    initialData: []
  }).json()
}
