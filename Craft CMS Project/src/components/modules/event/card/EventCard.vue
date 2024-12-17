<template>
  <div class="event-card">
    <div class="event-card__top-bar">
      <EventCardDate v-if="date" :date="date" />
      <EventCardHourSpan v-if="from && to" :from="from" :to="to" />
      <div class="spacer"></div>
    </div>
    <div class="event-card__text">
      <p class="title" @click="$event.preventDefault()">{{ event.title || '&nbsp;' }}</p>
      <p class="description" @click="$event.preventDefault()">{{ event.exhibitor[0].title || '&nbsp;' }}</p>
    </div>
    <div class="event-card__image">
      <img :src="event.featureImage[0] && event.featureImage[0].url || '/images/image-placeholder.jpg'" />
      <div class="event-card__image__chips">
        <Chip v-if="event.eventPlace === 'onSite'" :icon="SvgIconName.locationOn" :text="$t('components_event_chip_onsite')" />
        <Chip v-if="event.eventPlace === 'remote'" :icon="SvgIconName.headphones" :text="$t('components_event_chip_remote')" />
        <Chip v-if="event.eventPlace === 'webSession'" :icon="SvgIconName.webSession" :text="$t('components_event_chip_websession')" />
        <Chip v-if="event.eventChargeable.length && event.eventChargeable.includes('chargeable')" :icon="SvgIconName.euroCurrency" :text="$t('components_event_chip_chargeable')" />
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue'
import Chip from '@/components/elements/Chip.vue'
import EventCardDate from '@/components/elements/EventCardDate.vue'
import EventCardHourSpan from '@/components/elements/EventCardHourSpan.vue'
import { EventLiteModel } from '@/models/EventLiteModel'
import { SvgIconName } from '@/components/helpers/SvgIcon.vue'
import { convertDatetimeToDate } from '@/utils/date'

export default defineComponent({
  name: 'EventCard',
  components: {
    Chip,
    EventCardDate,
    EventCardHourSpan
  },
  props: {
    event: {
      type: Object as PropType<EventLiteModel>,
      required: true
    },
    isRegistered: Boolean
  },
  data () {
    return {
      SvgIconName,
      convertDatetimeToDate
    }
  },
  computed: {
    date (): Date | null {
      return this.event.startEventDatetime ? new Date(this.event.startEventDatetime) : null
    },
    from (): Date | null {
      return this.event.startEventDatetime ? new Date(this.event.startEventDatetime) : null
    },
    to (): Date | null {
      return this.event.endEventDatetime ? new Date(this.event.endEventDatetime) : null
    }
  },
})
</script>

<style lang="scss" scoped>
.event-card {
  border-radius: 6px;
  overflow: hidden;
  box-shadow: rgba(149, 157, 165, 0.4) 0px 8px 24px;
  transition: all 0.2s;
  margin: 24px 0;

  &:hover {
    box-shadow: rgba(149, 157, 165, 0.6) 0px 4px 18px;
  }

  &__top-bar {
    display: flex;

    & > * {
      display: flex;
      align-items: center;
    }

    .spacer {
      flex-grow: 1;
    }
  }

  &__text {
    padding: 8px;
    background-color: #ffffff;

    & > p {
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      margin-bottom: 0;
    }

    a {
      text-decoration: none;
    }

    .title {
      font-weight: bold;
    }
  }

  &__image {
    position: relative;
    padding-top: 56.25%;
    overflow: hidden;

    img {
      display: block;
      width: 100%;
      height: auto;
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
    }

    &__chips {
      position: absolute;
      bottom: 8px;
      left: 8px;
      display: flex;
      gap: 6px;
    }
  }
}
</style>
