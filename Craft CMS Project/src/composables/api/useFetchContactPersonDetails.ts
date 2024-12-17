import { ref } from 'vue'
import { ContactPersonDetails, ContactPersonDetailsResponse, createContactPersonDetails } from '@/models/ContactPersonDetails'
import { UseFetchReturn } from '@vueuse/core'
import { myCreateFetch } from '@/composables/api/myCreateFetch'
import { useStore } from '@/store'
import { config } from '../../../app.config'
import demoContactPersonDetails from '../../models/demo/contactPersonDetails.json'

export function useFetchContactPersonDetails (
  exhibitorId: string,
  contactPersonId: string,
  language: string,
  immediate = true
): UseFetchReturn<ContactPersonDetails> {
  const store = useStore()
  if (!config.useAPI) {
    return {
      isFetching: false,
      isFinished: true,
      error: null,
      data: ref(demoContactPersonDetails),
      execute: function () {
        return new Promise(resolve => {
          resolve('')
        })
      }
    } as unknown as UseFetchReturn<ContactPersonDetails>
  }

  const useMyFetch = myCreateFetch()
  let url = store.$state.urls.slices
  url = url.replace('{{exhId}}', exhibitorId).replace('{{language}}', language)
  url = url + `/contacts/${contactPersonId}?parentModule=profile&parentModule=stand`

  return useMyFetch(url, {
    immediate,
    initialData: createContactPersonDetails(),
    afterFetch (ctx) {
      ctx.data = createContactPersonDetails(ctx.data as ContactPersonDetailsResponse)
      return ctx
    }
  }).json()
}
