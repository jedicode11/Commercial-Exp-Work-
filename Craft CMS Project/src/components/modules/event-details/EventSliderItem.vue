<template>
  <div class="event-slider-item">
    <div class="event-slider-item__image">
      <img :src="event?.featureImage[0] && event?.featureImage[0].url || '/images/image-placeholder.jpg'" :alt="event?.featureImage[0] && event?.featureImage[0].alt || ''" :title="event?.featureImage[0] && event?.featureImage[0].alt || ''"/>
      <div class="event-slider-item__image__overlay"></div>
      <div class="event-slider-item__image__datetime">
        <div class="event-slider-item__image__datetime__date">{{ dateFormatted || '&nbsp;' }}</div>
        <div class="event-slider-item__image__datetime__hourspan">{{ houtSpanFormatted || '&nbsp;' }}</div>
      </div>

      <div v-if="event.eventLanguage.map(lang => lang.abbreviated).length" class="event-slider-item__image__language">
        <cta-button class="stream-cta not-allowed" isSmall :title="event.eventLanguage">{{ event.eventLanguage.map(lang => lang.abbreviated).join(' | ') }}</cta-button>
      </div>

    </div>
    <div class="event-slider-item__text">
      <p class="event-slider-item__text__categories" @click="$event.stopPropagation()">{{ event.eventCategories.map(c => c.title).join(', ') }}</p>
      <p class="event-slider-item__text__title" @click="$event.preventDefault()">{{ event.title }}</p>
      <p v-if="scriptParamsService?.locationHallPlanActivated" class="event-slider-item__text__location" :title="useLocatio.getLocationTitle" @click="$event.preventDefault(); $event.stopPropagation()">{{ useLocatio.getLocationTitle }}</p>
      <p v-else class="event-slider-item__text__location" :title="event.eventLocation" @click="$event.preventDefault(); $event.stopPropagation()">{{ event.eventLocation }}</p>
    </div>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, PropType, ref, watch, inject} from 'vue'
import { useI18n } from 'vue-i18n'
import { EventModel } from '@/models/EventModel'
import { SvgIconName } from '@/components/helpers/SvgIcon.vue'
import { convertDatetimeToDate } from '@/utils/date'
import { isToday, isTomorrow, format } from 'date-fns'
import { useLocation } from '@/composables/useLocation'
import { ScriptParamsService, sps } from '@/utils/scriptParamService'
import CtaButton from '@/components/elements/CtaButton.vue'
import moment from 'moment'

export default defineComponent({
  name: 'EventSliderItem',
  components: {
    CtaButton
  },
  props: {
    event: {
      type: Object as PropType<EventModel>,
      required: true
    },
    isRegistered: Boolean
  },
  setup (props) {
    const i18n = useI18n()
    const t = i18n.t
    const scriptParamsService = inject<ScriptParamsService | undefined>(sps)
    
    const e = ref<any>(props.event)
    const rs = ref<any>(props.event.relatedSession)
    // const sql = ref<any>(props.event.siteGlobalLocation)

    const useLocatio = ref<any>(useLocation(e, rs))
    
    watch(() => props.event, newVal => {
      e.value = props.event
      rs.value = props.event.relatedSession
      // sql.value = props.event.siteGlobalLocation
    })

    const date = computed((): Date | null => {
      // return new Date(+this.event.startEventDatetime * 1000)
      return props.event.startEventDatetime ? new Date(props.event.startEventDatetime) : null
    })

    const from = computed((): Date | null => {
      // return new Date(+this.event.startEventDatetime * 1000)
      return props.event.startEventDatetime ? new Date(props.event.startEventDatetime) : null
    })

    const to = computed((): Date | null => {
      // return new Date(+props.event.endEventDatetime * 1000)
      return props.event.endEventDatetime ? new Date(props.event.endEventDatetime) : null
    })

    const dateFormatted = computed(() => {
      if (!date) {
        return ''
      }

      if (isToday((date.value as Date))) {
        return t('components_event_card_date_today')
      }
      if (isTomorrow((date.value as Date))) {
        return t('components_event_card_date_tomorrow')
      }

      return format((date.value as Date), 'd. MMM')
    })

    const houtSpanFormatted = computed(() => {
      if (!from || !to) {
        return ''
      }

      return 'CEST ' + formatDateTime((from.value as Date)) + ' - ' + formatDateTime((to.value as Date))
    })

    const formatDateTime = (dateTime: Date) => {
      return moment.utc(dateTime).tz('Europe/Berlin').format('HH.mm')
    }

    return {
      SvgIconName,
      convertDatetimeToDate,
      dateFormatted,
      houtSpanFormatted,
      useLocatio,
      scriptParamsService
    }
  },

})
</script>

<style lang="scss" scoped>
.event-slider-item {
  .event-slider-item__image {
    position: relative;
    overflow: hidden;
    margin-bottom: 12px;
    padding-top: 56.25%; /* 16:9 */
    padding-top: 66.6667%; /* 4:3 */ // Expected "66.666666666667" to be "66.6667"  number-max-precision
    padding-top: 100%;

    border: 1px solid rgba(24, 24, 24, 0.25);
    border-radius: 9px;

    img {
      position: absolute;
      top: 0;

      display: block;
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
    .event-slider-item__image__overlay {
      box-sizing: border-box;
      cursor: pointer;
      position: absolute;
      left: 0%;
      right: 0%;
      top: 0%;
      bottom: 0%;
      background: linear-gradient(180deg, rgba(0, 0, 0, 0) 0%, rgba(0, 0, 0, 0.7) 100%);
    }

    .event-slider-item__image__datetime {
      position: absolute;
      bottom: 10px;
      left: 10px;
      color: #ffffff;

      .event-slider-item__image__datetime__date {
        font-size: 26px;
        font-weight: 600;
      }
    }

    .event-slider-item__image__language {
      position: absolute;
      bottom: 0;
      right: 0;
      font-size: 26px;
      font-weight: 600;
      @media (max-width: 480px) {
        display: block;
        position: absolute;
        top: 10px;
        right: 0px;
        font-size: 26px;
        font-weight: 600;
      }
    }

    .stream-cta {
      background-color: rgba(24, 24, 24, 0.2);
      color: #ffffff;
      position: relative;
      top: -10px;
      left: -10px;
      padding-right: 100;
      text-overflow: ellipsis;
      max-width: 15ch;
    }
    .not-allowed {
      pointer-events: none;
    }
  }

  .event-slider-item__text {
    overflow: hidden;

    & > p {
      margin-bottom: 0;
      height: auto;
      width: 100%;
    }

    .event-slider-item__text__categories {
      width: 255px;
      height: 14px;

      /* Special Styles/Overline Small Regular */
      font-family: 'Source Sans Pro', sans-serif;
      font-style: normal;
      font-weight: 400;
      font-size: 12px;
      line-height: 120%; /* or 14px */
      text-transform: uppercase;

      /* Colors/Dark - 50% */
      color: rgba(24, 24, 24, 0.5);

      /* Inside auto layout */
      flex: none;
      order: 0;
      align-self: stretch;
      flex-grow: 0;
    }

    .event-slider-item__text__title {
      width: 255px;
      cursor: pointer;

      /* Headlines/Headline Bold - 16 */
      font-family: 'Source Sans Pro', sans-serif;
      font-style: normal;
      font-weight: 700;
      font-size: 16px;
      line-height: 120%; /* or 19px */

      /* Colors/Dark */
      color: #181818;

      /* Inside auto layout */
      flex: none;
      order: 1;
      align-self: stretch;
      flex-grow: 0;

      /* restrict to two lines with ellipsis */
      display: block;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      text-overflow: ellipsis;
    }

    .event-slider-item__text__location {
      width: 255px;
      height: 14px;
      @media (max-width: 480px) {
        height: 50px;
      }

      font-family: 'Source Sans Pro', sans-serif;
      font-style: normal;
      font-weight: 400;
      font-size: 12px;
      line-height: 120%; /* or 14px */
      text-transform: uppercase;

      /* Colors/Dark - 50% */
      color: rgba(24, 24, 24, 0.5);
      flex: none;
      order: 0;
      align-self: stretch;
      flex-grow: 0;
    }
  }
}
</style>
