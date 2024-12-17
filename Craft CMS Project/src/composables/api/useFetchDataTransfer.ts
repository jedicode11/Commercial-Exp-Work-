import { ref } from 'vue'
import { UseFetchReturn } from '@vueuse/core'
import { useStore } from '@/store'
import { myCreateFetch } from '@/composables/api/myCreateFetch'
import { config } from '../../../app.config'
import demoDataTransfer from '../../models/demo/dataTransfer.json'

export interface TransferSection {
  sections: Array<string>
  transferFrom: string
  populated?: boolean
}

export function useFetchDataTransfer (
  section: string | undefined,
  immediate = true
): UseFetchReturn<Array<TransferSection> | TransferSection> {
  const store = useStore()
  if (!config.useAPI) {
    return {
      isFetching: false,
      isFinished: true,
      error: null,
      data: ref(demoDataTransfer),
      execute: function () {
        return new Promise(resolve => {
          resolve('')
        })
      }
    } as unknown as UseFetchReturn<Array<TransferSection>>
  }
  const useMyFetch = myCreateFetch()
  const editMode = store.$state.editMode
  let url = window.DIMEDIS.vis.myadmin.config.urls.dataTransfer
  // TODO change to {{sectionPath}} and remember to change the jsconfig.ftl in JarVis template also.
  url = url.replace('{{sectionPath|pathParam}}', section ? '/' + section : '')
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

export function useFetchStartSectionTransfer (
  section: string,
  immediate = true
): UseFetchReturn<Record<string, unknown>> {
  const store = useStore()
  const useMyFetch = myCreateFetch()
  const editMode = store.$state.editMode
  let url = window.DIMEDIS.vis.myadmin.config.urls.dataTransfer
  // TODO change to {{sectionPath}} and remember to change the jsconfig.ftl in JarVis template also.
  url = url.replace('{{sectionPath|pathParam}}', '/' + section)
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
  }).put().json()
}
