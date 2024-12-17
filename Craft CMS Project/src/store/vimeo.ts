import { defineStore } from 'pinia'
import { get as _get } from 'lodash-es'
import { useSessionStorage } from '@vueuse/core'
import { Ref } from 'vue'
import { VimeoOEmbedData } from '@/models/VimeoOEmbedData'

interface VimeoOEmbedState {
  embedDataStorage: Ref<Record<string, VimeoOEmbedData>>
}

export const useVimeoStore = defineStore('vimeo', {
  state: (): VimeoOEmbedState => ({
    embedDataStorage: useSessionStorage('finder/vimeo/embedData', {} as Record<string, VimeoOEmbedData>)
  }),

  getters: {
    getCachedVimeoOEmbedData: (state) => {
      return (videoId: string): VimeoOEmbedData | null => {
        return _get(state.embedDataStorage, videoId, null) as VimeoOEmbedData || null
      }
    }
  },

  actions: {
    setVideoOEmbedData (videoId: string, oEmbedData: VimeoOEmbedData) {
      this.embedDataStorage[videoId] = oEmbedData
    },
    removeCachedVimeoOEmbedData (videoId: string) {
      delete this.embedDataStorage[videoId]
    }
  }
})
