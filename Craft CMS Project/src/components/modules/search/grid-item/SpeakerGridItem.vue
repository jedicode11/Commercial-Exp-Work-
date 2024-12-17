<template>
  <div class="speaker-grid-item" @click="goToLink()">
    <div class="speaker-grid-item__image">
      <img v-if="speaker.speakerAvatar?.length" :src="speaker.speakerAvatar[0]?.url" :alt="speaker.speakerAvatar[0]?.alt"
        :title="speaker.speakerAvatar[0]?.alt" />
      <img v-else :src="hostname + 'img/no_image_icon.png'" />
    </div>
    <div class="speaker-grid-item__image__language">
    </div>

    <div class="speaker-grid-item__text">
      <p class="speaker-grid-item__text__categories">{{ }}</p>
      <p class="speaker-grid-item__text__company" @click="$event.preventDefault(); $event.stopPropagation()">{{ speaker.company }}</p>
      <p class="speaker-grid-item__text__speakerName" @click="$event.preventDefault()">{{ speaker.speakerTitle ? speaker.speakerTitle: ' ' }} {{ speaker.speakerName }}</p>
      <p class="speaker-grid-item__text__titleJob" @click="$event.preventDefault(); $event.stopPropagation()">{{ speaker.speakerJobTitle }}</p>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, computed, PropType, inject, toRef } from 'vue'
import { useWidgetMode } from '@/composables/useWidgetMode'
import { SvgIconName } from '@/components/helpers/SvgIcon.vue'
import { convertDatetimeToDate } from '@/utils/date'
import { SpeakerModel } from '@/models/SpeakerModel'
import router from '@/router'
import { useRouterStore } from '@/store/router'

export default defineComponent({
  name: 'SpeakerGridItem',
  props: {
    speaker: {
      type: Object as PropType<SpeakerModel>,
      required: true
    },

    isRegistered: Boolean
  },
  setup (props) {
    const { isWidgetMode, navigateToSpeakerDetailsFactory } = useWidgetMode()
    const routerStore = useRouterStore()
    const { changePage } = routerStore

    const origin = inject('app.origin')

    const speaker = toRef(props, 'speaker')

    let parentUrl: string | undefined
    let parentSpeakerFragment: string | undefined
    let parentTicketFragment: string | undefined
    let parentTicket: string | undefined

    if (isWidgetMode) {
      parentUrl = inject<string | undefined>('app.parentUrl')
      parentSpeakerFragment = inject<string | undefined>('app.parentSpeakerFragment')
      parentTicketFragment = inject<string | undefined>('app.parentTicketFragment')
      parentTicket = inject<string | undefined>('app.parentTicket')
    }

    const goToLink = () => {
      if (isWidgetMode) {
        if (parentUrl && parentSpeakerFragment) {
          const navigateToSpeakerDetails = navigateToSpeakerDetailsFactory({ parentUrl, parentSpeakerFragment, parentTicketFragment, parentTicket })
          navigateToSpeakerDetails(`${speaker.value.uri}`)
        } else {
          changePage('SpeakerDetails', { uri: speaker.value.uri })
        }
      } else {
        router.push(`/speaker/${speaker.value.uri}`)
      }
    }

    const hostname = computed(() => {
      if (isWidgetMode) {
        return origin + '/'
      }

      return '/'
    })

    return {
      SvgIconName,
      convertDatetimeToDate,
      goToLink,
      hostname
    }
  }
})
</script>

<style lang="scss" scoped>
.speaker-grid-item {

  @media (max-width: 480px) {
    margin-bottom: 10px;
  }

  &__image {
    position: relative;
    overflow: hidden;
    margin-bottom: 5px;
    padding-top: 56.25%;
    padding-top: 66.6667%;
    padding-top: 100%;

    border: 1px solid rgba(24, 24, 24, 0.25);
    border-radius: 7px;

    img {
      position: absolute;
      top: 0;

      display: block;
      width: 100%;
      height: 100%;
      object-fit: cover;
      cursor: pointer;
    }

    &__overlay {
      box-sizing: border-box;
      cursor: pointer;
      position: absolute;
      left: 0%;
      right: 0%;
      top: 0%;
      bottom: 0%;
      background: linear-gradient(180deg, rgba(0, 0, 0, 0) 0%, rgba(0, 0, 0, 0.7) 100%);
    }

    &__datetime {
      position: absolute;
      bottom: 10px;
      left: 10px;
      color: #ffffff;

      &__date {
        font-size: 26px;
        font-weight: 600;
      }

      &__hourspan {
        font-weight: 300;
      }
    }

    &__language {
      position: absolute;
      bottom: 0;
      right: 0;
    }

    .stream-cta {
      background-color: rgba(24, 24, 24, 0.2);
      color: #ffffff;
      position: relative;
      top: -10px;
      left: -10px;
      padding-right: 100;
    }
    .not-allowed {
      pointer-events: none;
    }
  }

  &__text {
    overflow: hidden;

    & > p {
      margin-bottom: 0;
      height: auto;
      width: 100%;
    }

    &__categories {
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

    &__speakerName {
      margin-top: 5px;
      width: 255px;

      // height: 38px;
      cursor: pointer;

      /* Headlines/Headline Bold - 16 */
      font-family: 'Source Sans Pro', sans-serif;
      font-size: 16px;
      font-weight: 700;
      line-height: 19px;

      /* Colors/Dark */
      color: #181818;

      /* Inside auto layout */
      flex: none;
      order: 1;
      align-self: stretch;
      flex-grow: 0;

      display: block;
      display: -webkit-box;
      -webkit-box-orient: vertical;
      text-overflow: ellipsis;
    }

    &__titleJob {
      margin-top: 5px;
      width: 255px;
      height: 14px;
      @media (max-width: 480px) {
        height: 50px;
      }
      font-family: 'Source Sans Pro', sans-serif;
      font-style: normal;
      font-size: 16px;
      line-height: 19px;
      color: rgba(24, 24, 24, .5);
      font-weight: 400;
      flex: none;
      order: 0;
      align-self: stretch;
      flex-grow: 0;
    }
    &__description {
      width: 255px;
      height: 38px;

      /* Headlines/Headline Regular - 16 */
      font-family: 'Source Sans Pro', sans-serif;
      font-style: normal;
      font-weight: 400;
      font-size: 16px;
      line-height: 120%; /* or 19px */

      /* Colors/02-black */
      color: #181818;

      /* Inside auto layout */
      flex: none;
      order: 2;
      align-self: stretch;
      flex-grow: 0;

      /* restrict to two lines with ellipsis */
      display: block;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    &__company {

      margin-top: 0px;
      width: 255px;
      height: 14px;
      @media (max-width: 480px) {
        height: 50px;
      }

      /* Special Styles/Overline Small Regular */
      font-family: 'Source Sans Pro', sans-serif;
      font-style: normal;
      font-size: 12px;
      font-weight: 400;
      line-height: 120%;
      text-transform: uppercase;
      color: rgba(24, 24, 24, 0.5);

      flex: none;
      order: 0;
      align-self: stretch;
      flex-grow: 0;
    }
  }
}
</style>
