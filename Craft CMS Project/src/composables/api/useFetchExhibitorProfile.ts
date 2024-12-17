import { UseFetchReturn } from '@vueuse/core'
import { myCreateFetch } from '@/composables/api/myCreateFetch'
import demoProfile from './../../models/demo/profile.json'
import { config } from '../../../app.config'
import { ref } from 'vue'
import { createProfile, Profile } from '@/models/Profile'
import { useStore } from '@/store'

export function useFetchExhibitorProfile (exhibitorId: string, language: string, immediate = true): UseFetchReturn<Profile> {
  const store = useStore()
  if (!config.useAPI) {
    return {
      isFetching: false,
      isFinished: true,
      error: null,
      data: ref(demoProfile),
      execute: function () {
        return new Promise(resolve => {
          resolve('')
        })
      }
    } as unknown as UseFetchReturn<Profile>
  }
  const useMyFetch = myCreateFetch()
  const editMode = store.$state.editMode
  let url = store.$state.urls.slices
  url = url.replace('{{exhId}}', exhibitorId).replace('{{language}}', language)
  url = url + '/profile'
  if (editMode && store.$state.ticket) {
    url = url + `?ticket=${store.$state.ticket}`
  }

  // Filter the results and try to map certain properties
  return useMyFetch(url, {
    immediate: immediate,
    initialData: createProfile({}),
    afterFetch (ctx) {
      // eslint-disable-next-line
      ctx.data = createProfile(ctx.data)
      return ctx
    }
  }).json()
}
