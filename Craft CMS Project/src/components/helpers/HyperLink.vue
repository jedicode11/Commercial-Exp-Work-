<template>
  <div v-if="isEmpty">
    <slot></slot>
  </div>
  <a v-else-if="isKioskMode" style="pointer-events: none;">
    <slot></slot>
  </a>
  <a v-else-if="isDownload" :href="filteredUri" download @click="onClick">
    <slot></slot>
  </a>
  <a v-else-if="isRelative" :href="filteredUri" @click="onClick">
    <slot></slot>
  </a>
  <a v-else-if="!isRelative" :href="filteredUri" target="_blank" rel="noopener" @click="onClick">
    <slot></slot>
  </a>
</template>

<script lang="ts">
import { defineComponent, toRef, computed } from 'vue'
import { useStore } from '@/store'

export default defineComponent({
  name: 'HyperLink',
  props: {
    /** URI or path to link to */
    uri: { type: String, required: true },

    /** Optional base url, e.g. 'https://hypercode.de/' */
    baseUrl: { type: String, required: false, default: '' },

    /** Optional flag for downloads */
    isDownload: { type: Boolean, required: false, default: false }
  },
  setup (props) {
    const store = useStore()
    const uri = toRef(props, 'uri')
    const isKioskMode = store.$state.kioskMode

    const filteredUri = computed(() => {
      let newUri = uri.value
      if (newUri.indexOf(props.baseUrl) === 0) {
        newUri = newUri.replace(props.baseUrl, '')
      }
      return newUri
    })

    const isEmpty = computed(() => {
      return uri.value === '' || uri.value == null
    })

    const isRelative = computed(() => {
      return !(
        filteredUri.value.indexOf('http://') === 0 ||
        filteredUri.value.indexOf('https://') === 0 ||
        filteredUri.value.indexOf('mailto:') === 0
      )
    })

    const onClick = (e: Event) => {
      if (store.$state.kioskMode) {
        e.preventDefault()
      }
      e.stopPropagation()
    }

    return {
      filteredUri,
      isEmpty,
      isKioskMode,
      isRelative,
      onClick
    }
  }
})
</script>
