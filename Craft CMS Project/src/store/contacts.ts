import { defineStore } from 'pinia'
import { useStore } from '@/store'
import { ContactPerson } from '@/models/ContactPerson'
import { ContactPersonDetails } from '@/models/ContactPersonDetails'
import { useFetchContactPersonList } from '@/composables/api/useFetchContactPersonList'
import { useFetchContactPersonDetails } from '@/composables/api/useFetchContactPersonDetails'

interface ContactState {
  contacts: Array<ContactPerson> | null,
  contactDetails: ContactPersonDetails | null
}

let currExhId = ''

export const useContacts = defineStore('contacts', {
  state: (): ContactState => ({
    contacts: null,
    contactDetails: null
  }),

  actions: {
    async fetchContacts (forceLoad = false) {
      const store = useStore()

      if (currExhId !== store.$state.exhId || forceLoad) {
        currExhId = store.$state.exhId
        const { data, execute } = useFetchContactPersonList(store.$state.exhId, store.$state.langCode, false)
        await execute()
        if (data.value && data.value.length > 0) {
          this.contacts = data.value
        } else {
          this.contacts = []
        }
      }
    },
    async fetchContactDetails (contactId: string) {
      const store = useStore()

      const { data, execute } = useFetchContactPersonDetails(store.$state.exhId, contactId, store.$state.langCode, false)
      await execute()
      if (data.value) {
        this.contactDetails = data.value
      } else {
        this.contactDetails = null
      }
    }
  }
})
