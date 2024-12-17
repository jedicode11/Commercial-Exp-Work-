import { onMounted, Ref, ref, watch } from 'vue'
import { useVimeoStore } from '@/store/vimeo'
import { useVideoThumbChangeListener } from '@/composables/vis-modules/myAdmin'
import { createEmptyVimeoOEmbed, VimeoOEmbedData } from '@/models/VimeoOEmbedData'
import { useFetchVimeoOEmbed } from '@/composables/api/useFetchVimeoOEmbed'

export interface UseVimeoEmbedReturn {
  embedData: Ref<VimeoOEmbedData | null>,
  isFetching: Ref<boolean>
  updateEmbedData: () => void
}

export function useVimeoEmbed (videoId: Ref<string>, enabled: Ref<boolean>): UseVimeoEmbedReturn {
  const store = useVimeoStore()
  const embedData: Ref<VimeoOEmbedData> = ref(createEmptyVimeoOEmbed())
  const isFetching = ref(false)

  const updateEmbedData = async (clearCache = true) => {
    const cachedValue = store.getCachedVimeoOEmbedData(videoId.value)
    if (enabled.value && videoId.value && (!cachedValue || clearCache)) {
      isFetching.value = true
      if (videoId.value) {
        try {
          const { data } = await useFetchVimeoOEmbed(videoId.value).json()
          embedData.value = data.value
          if (data.value) {
            store.setVideoOEmbedData(videoId.value, embedData.value)
          }
        } catch (e) {
        }
        isFetching.value = false
      }
    } else if (cachedValue) {
      embedData.value = cachedValue
    }
  }

  watch(videoId, () => {
    updateEmbedData(false)
  })

  watch(enabled, () => {
    updateEmbedData(false)
  })

  onMounted(() => {
    updateEmbedData(false)
  })

  useVideoThumbChangeListener((id) => {
    store.removeCachedVimeoOEmbedData(id)
    if (id === videoId.value && enabled.value) {
      updateEmbedData(true)
    }
  })

  return {
    embedData,
    isFetching,
    updateEmbedData
  }
}
