import { defineStore } from 'pinia'
import { useStore } from '@/store'
import { createProfile, Profile } from '@/models/Profile'
import { useFetchExhibitorProfile } from '@/composables/api/useFetchExhibitorProfile'

interface ProfileState {
  profile: Profile
}

let currExhId = ''

export const useProfile = defineStore('profile', {
  state: (): ProfileState => ({
    profile: createProfile()
  }),

  actions: {
    async fetchProfile (forceLoad = false) {
      const store = useStore()

      if (currExhId !== store.$state.exhId || forceLoad) {
        currExhId = store.$state.exhId
        const { data, execute } = useFetchExhibitorProfile(store.$state.exhId, store.$state.langCode, false)
        await execute()
        if (data.value) {
          this.profile = data.value
        }
      }
    }
  }
})
