import { ref } from 'vue'
import { ContactPerson } from '@/models/ContactPerson'
import { UseFetchReturn } from '@vueuse/core'
import { myCreateFetch } from '@/composables/api/myCreateFetch'
import { useStore } from '@/store'
import { config } from '../../../app.config'
import demoContactPersons from '../../models/demo/contactPersons.json'

export enum ParentModule {
  profile = 'profile',
  stand = 'stand'
}

export function useFetchContactPersonList (exhibitorId: string, language: string, immediate = true): UseFetchReturn<Array<ContactPerson>> {
  const store = useStore()
  if (!config.useAPI) {
    return {
      isFetching: false,
      isFinished: true,
      error: null,
      data: ref(demoContactPersons),
      execute: function () {
        return new Promise(resolve => {
          resolve('')
        })
      }
    } as unknown as UseFetchReturn<Array<ContactPerson>>
  }

  const useMyFetch = myCreateFetch()
  const editMode = store.$state.editMode
  let url = store.$state.urls.slices
  url = url.replace('{{exhId}}', exhibitorId).replace('{{language}}', language)
  url = url + '/contacts?parentModule=profile&parentModule=stand'
  if (editMode && store.$state.ticket) {
    url = url + `&ticket=${store.$state.ticket}`
  }

  return useMyFetch(url, {
    immediate: immediate,
    initialData: [],
    afterFetch (ctx) {
      return ctx
    }
  }).json()
}
