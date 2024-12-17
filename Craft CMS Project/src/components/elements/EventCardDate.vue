<template>
  <div class="event-card-date">
    <svg-icon :icon-name="SvgIconName.calendar" />
    <span>{{ dateFormatted || '&nbsp;' }}</span>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import { isToday, isTomorrow } from 'date-fns'
import SvgIcon, { SvgIconName } from '@/components/helpers/SvgIcon.vue'
import moment from 'moment-timezone'

export default defineComponent({
  name: 'EventCardDateComponent',
  components: {
    SvgIcon
  },
  props: {
    date: Date
  },
  data () {
    return {
      SvgIconName
    }
  },
  computed: {
    dateFormatted () {
      if (!this.date) {
        return ''
      }

      if (isToday(this.date)) {
        return this.$t('components_event_card_date_today')
      }
      if (isTomorrow(this.date)) {
        return this.$t('components_event_card_date_tomorrow')
      }

      return moment.utc(this.date.toISOString()).tz('Europe/Berlin').format('D. MMM. YY')
    }
  }
})
</script>

<style lang="scss" scoped>
.event-card-date {
  padding: 0 4px;

  .svg-icon {
    margin-right: 4px;
  }
}
</style>
