<template>
  <div class="event-card-hourspan">
    <svg-icon :icon-name="SvgIconName.clock" />
    <span>{{ hourSpanFormatted || '&nbsp;' }}</span>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import SvgIcon, { SvgIconName } from '@/components/helpers/SvgIcon.vue'
import moment from 'moment-timezone'

export default defineComponent({
  name: 'EventCardHourSpanComponent',
  components: {
    SvgIcon
  },
  props: {
    from: Date,
    to: Date
  },
  data () {
    return {
      SvgIconName
    }
  },
  computed: {
    hourSpanFormatted () {
      if (!this.from || !this.to) {
        return ''
      }
      return 'CEST ' + this.formatDateTime(this.from) + ' - ' + this.formatDateTime(this.to)
      // return format(this.from, 'HH.mm') + ' - ' + format(this.to, 'HH.mm')
    }
  },
  methods: {
    formatDateTime (dateTime: Date) {
      return moment.utc(dateTime).tz('Europe/Berlin').format('HH.mm')
    }
  }
})
</script>

<style lang="scss" scoped>
.event-card-hourspan {
  padding: 0 4px;

  .svg-icon {
    margin-right: 4px;
  }
}
</style>
