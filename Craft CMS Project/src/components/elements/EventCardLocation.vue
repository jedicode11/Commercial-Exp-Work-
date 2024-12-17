<template>
  <div class="event-card-location">
    <svg-icon :icon-name="SvgIconName.locationOn" />
    <span :title="location">{{ locationFormatted || '&nbsp;' }}</span>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import SvgIcon, { SvgIconName } from '@/components/helpers/SvgIcon.vue'

export default defineComponent({
  name: 'EventCardLocation',
  components: {
    SvgIcon
  },
  props: {
    location: String
  },
  data () {
    return {
      SvgIconName
    }
  },
  computed: {
    locationFormatted () {
      if (!this.location) {
        return ''
      }

      return this.location.length > 30 ? this.location.substring(0, 30) + '...' : this.location
    }
  }
})
</script>

<style lang="scss" scoped>
.event-card-location {
  padding: 0 4px;

  .svg-icon {
    margin-right: 4px;
  }
}
</style>
