<template>
  <component
    :is="tag"
    v-dragscroll.x="isScrollable"
    @dragscrollstart="start"
    @dragscrollend="end"
    @click.capture="click"
  >
    <slot />
  </component>
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'DragScroll',
  props: {
    tag: {
      type: String,
      default: 'div'
    },
    isScrollable: {
      type: Boolean,
      default: true
    }
  },
  setup () {
    // Avoid emitting click event scrolldragging
    let dragging = false
    let timer: number | undefined

    const start = () => {
      timer = window.setTimeout(() => (dragging = true), 100)
    }

    const end = () => {
      clearTimeout(timer)
      setTimeout(() => (dragging = false))
    }

    const click = (event: Event) => {
      if (dragging) {
        event.stopPropagation()
      }
    }

    return {
      start,
      end,
      click
    }
  }
})
</script>
