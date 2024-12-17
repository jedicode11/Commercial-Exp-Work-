<template>
  <loading
    :active="loading"
    :can-cancel="true"
    :is-full-page="true"
    loader="Dots"
    :color="color"
    :width="140"
  />
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import Loading from 'vue-loading-overlay'
import 'vue-loading-overlay/dist/css/index.css'

export default defineComponent({
  name: 'LoaderComponent',
  components: {
    Loading
  },
  props: {
    color: {
      type: String,
      default: process.env.VUE_APP_COLOR_PRIMARY
    },
    loading: {
      type: Boolean,
      required: false,
      default: false
    }
  }
})
</script>
