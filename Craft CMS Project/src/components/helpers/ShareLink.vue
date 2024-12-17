<template>
  <a
    v-if="showShareLink"
    :href="mailtoUrl"
    target="_blank" rel="noopener"
    @click="onClick"
  >
    <slot></slot>
  </a>
</template>

<script lang="ts">
import { defineComponent, toRef, computed } from 'vue'
import { useStore } from '@/store'

export default defineComponent({
  name: 'ShareLink',
  props: {
    url: {
      type: String,
      required: true
    },
    title: {
      type: String,
      required: false,
      default: ''
    },
    text: {
      type: String,
      required: false,
      default: ''
    }
  },
  setup (props) {
    const store = useStore()
    const url = toRef(props, 'url')
    const title = toRef(props, 'title')
    const text = toRef(props, 'text')
    const isEditMode = store.$state.editMode
    const isKioskMode = store.$state.kioskMode

    const mailtoUrl = computed(() => {
      const body = text.value !== '' ? `${text.value}\r\n\r\n${url.value}` : url.value
      return `mailto:?subject=${encodeURIComponent(title.value)}&body=${encodeURIComponent(body)}`
    })

    const showShareLink = computed(() => {
      return !isEditMode && !isKioskMode
    })

    const onClick = (e: Event): void => {
      if (navigator.share) {
        e.preventDefault()

        navigator.share({
          title: title.value, // Attention! Title will not be used in some cases, e.g. iMessage
          text: text.value,
          url: url.value
        })
          // .then(() => console.log('Successfully shared'))
          .catch((error) => console.error('Error while sharing', error))
      }
    }

    return {
      mailtoUrl,
      showShareLink,
      onClick
    }
  }
})
</script>

<style lang="scss" scoped>
a {
  display: inline-block;
}
</style>
