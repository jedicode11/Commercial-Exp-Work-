<template>
  <div class="slider slider-details">
    <div v-if="eventsList.length" class="slider-title">
      <h3 class="slider-details__sub_title"> {{ sliderTitle }} </h3>
      <p class="session-name"> {{ sessionName }} </p>
    </div>
    <div class="sliders-list-slider" :class="{ 'align-left': arrowVisibility(eventsList.length) }">
      <div :class="{ 'sliders-list-slider': true, 'align-left': arrowVisibility(eventsList.length) }" :style="eventsListStyles">
        <div class="sliders-list-slider-navigation-previous" v-if="arrowVisibility(eventsList.length)">
          <IconButton :text="t('components_generic_previous')" :icon-name="SvgIconName.arrowBack"
            v-touch:press="scrollLeftStart" v-touch:release="scrollStop" :isArrowInactive="isArrowLeftImage" @click="getScrollPosition('left')" />
        </div>

        <div :class="{ 'sliders-list': true, 'stele': widgetMode?.toLowerCase() === 'stele' }" :id="`container-${id}`">
          <EventSliderItem v-for="item in eventsList" :key="item.id" :event="item" @click="handleEventClick(item)"
            :isRegistered="true" @touchend="getScrollPosition('left') || getScrollPosition('right')" @wheel="getScrollPosition('left') || getScrollPosition('right')" />
        </div>

        <div class="sliders-list-slider-navigation-next" v-if="arrowVisibility(eventsList.length)">
          <IconButton :text="t('components_generic_next')" :icon-name="SvgIconName.arrowForward"
            v-touch:press="scrollRightStart" v-touch:release="scrollStop" :isArrowInactive="isArrowRightImage" @click="getScrollPosition('right')" />
        </div>

        <div class="sliders-list-slider-navigation" v-if="arrowVisibility(eventsList.length)">
          <IconButton :text="t('components_generic_previous')" :icon-name="SvgIconName.arrowBack"
            v-touch:press="scrollLeftStart" v-touch:release="scrollStop" :isArrowInactive="isArrowLeftImage" @click="getScrollPosition('left')" />
          <IconButton :text="t('components_generic_next')" :icon-name="SvgIconName.arrowForward"
            v-touch:press="scrollRightStart" v-touch:release="scrollStop" :isArrowInactive="isArrowRightImage" @click="getScrollPosition('right')" />
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType, inject, onMounted, onUpdated, ref, watchEffect } from 'vue'
import { SvgIconName } from '@/components/helpers/SvgIcon.vue'
import { useWidgetMode } from '@/composables/useWidgetMode'
import { EventModel } from '@/models/EventModel'
import router from '@/router'
import { useRouterStore } from '@/store/router'
import { uniqueId } from 'lodash-es'
import { useI18n } from 'vue-i18n'
import EventSliderItem from '@/components/modules/event-details/EventSliderItem.vue'
import IconButton from './IconButton.vue'

export default defineComponent({
  name: 'Slider',
  components: {
    EventSliderItem,
    IconButton
  },
  props: {
    eventsList: {
      type: Object as PropType<Array<EventModel>>,
      required: true
    },
    eventsListStyles: {
      type: Object as PropType<Record<string, any>>,
      required: false
    },
    sessionName: {
      type: String,
      required: false
    },
    eventsListByExhibitor: {
      type: String,
      required: false
    },
    sliderTitle: {
      type: String,
      required: true,
      default: ''
    }
  },
  setup (props) {
    const { isWidgetMode, navigateToEventDetailsFactory } = useWidgetMode()
    const widgetMode = inject<string | undefined>('app.widgetMode')

    const { t } = useI18n()

    const routerStore = useRouterStore()
    const { changePage } = routerStore

    // const eventsListByExhibitor = ref<Array<EventModel>>([])

    let parentUrl: string | undefined
    let parentEventFragment: string | undefined
    let parentTicketFragment: string | undefined
    let parentTicket: string | undefined

    const isMobile = ref(false)
    const maxScrollBar = ref(0)
    const isArrowLeftImage = ref(false)
    const isArrowRightImage = ref(false)

    if (isWidgetMode) {
      parentUrl = inject<string | undefined>('app.parentUrl')
      parentEventFragment = inject<string | undefined>('app.parentEventFragment')
      parentTicketFragment = inject<string | undefined>('app.parentTicketFragment')
      parentTicket = inject<string | undefined>('app.parentTicket')
      console.info('parent url event fragment:', parentEventFragment)
    }

    onMounted(() => {
      console.log('🚀 ~ Slider ~ onMounted:')
      const container = document.getElementById(`container-${id}`)
      if (container) {
        maxScrollBar.value = container.scrollWidth > container.clientWidth
          ? container.scrollWidth - container.clientWidth
          : container.scrollWidth
      }
      getScrollPosition('right')
      getScrollPosition('left')
    })

    onUpdated(() => {
      const container = document.getElementById(`container-${id}`)
      if (container) {
        maxScrollBar.value = container.scrollWidth > container.clientWidth
          ? container.scrollWidth - container.clientWidth
          : container.scrollWidth
      }
    })

    const handleEventClick = (item: EventModel) => {
      console.log('🚀 ~ Slider ~ handleEventClick ~ item', item)
      goToEventDetails(item.uri)
    }

    const goToEventDetails = (uri: string) => {
      console.log('🚀 ~ Slider ~ goToEventDetails ~ uri', uri)
      if (isWidgetMode) {
        if (parentUrl && parentEventFragment) {
          const navigateToEventDetails = navigateToEventDetailsFactory({ parentUrl, parentEventFragment, parentTicketFragment, parentTicket })
          navigateToEventDetails(`${uri}`)
        } else {
          changePage('EventDetails', { uri })
        }
      } else {
        router.push(`/details/${uri}`)
      }
    }

    const id = uniqueId()
    let scrollLeftTimer: number
    const scrollLeft = () => {
      const container = document.getElementById(`container-${id}`)
      if (container) {
        container.scrollLeft -= 5
      }
    }
    const scrollLeftStart = () => {
      if (scrollLeftTimer) window.clearInterval(scrollLeftTimer)
      scrollLeftTimer = window.setInterval(scrollLeft, 10)
      isArrowRightImage.value = false
    }
    const scrollLeftStop = () => {
      scrollLeftTimer && clearInterval(scrollLeftTimer)
    }

    let scrollRightTimer: number
    const scrollRight = () => {
      const container = document.getElementById(`container-${id}`)
      if (container) {
        container.scrollLeft += 5
      }
    }
    const scrollRightStart = () => {
      if (scrollRightTimer) window.clearInterval(scrollRightTimer)
      scrollRightTimer = window.setInterval(scrollRight, 10)
      isArrowLeftImage.value = false
    }

    const scrollRightStop = () => {
      scrollRightTimer && clearInterval(scrollRightTimer)
    }

    const scrollStop = () => {
      if (scrollLeftTimer) clearInterval(scrollLeftTimer)
      if (scrollRightTimer) clearInterval(scrollRightTimer)
    }

    const getScrollPosition = (position: string) => {
      const container = document.getElementById(`container-${id}`)

      if (!container) {
        return false
      }
      const isLeft = position === 'left'
      const isRight = position === 'right'
      const scrollLeft = Math.floor(container.scrollLeft)

      isArrowLeftImage.value = isLeft && container.scrollLeft === 0
      isArrowRightImage.value = isRight && scrollLeft >= (maxScrollBar.value - 5)
      return isArrowLeftImage.value || isArrowRightImage.value
    }

    const detectMobile = () => {
      if (window.matchMedia('(max-width: 481px)').matches) {
        isMobile.value = true
      } else {
        isMobile.value = false
      }
    }

    watchEffect(() => {
      detectMobile()
    })

    // const arrowVisibility = (eventListLength: number) => {
    //   let result = false
    //   if (isMobile.value) {
    //     result = eventListLength >= 3 ? true : false
    //   } else {
    //     result = eventListLength > 3 ? true : false
    //   }
    //   return result
    // }
    const arrowVisibility = (eventListLength: number) => {
      if (isMobile.value) {
        return eventListLength >= 3
      } else {
        return eventListLength > 3
      }
    }

    return {
      scrollStop,
      t,
      id,
      handleEventClick,
      goToEventDetails,
      scrollLeft,
      scrollLeftStart,
      scrollLeftStop,
      scrollRight,
      scrollRightStart,
      scrollRightStop,
      SvgIconName,
      isMobile,
      arrowVisibility,
      isArrowLeftImage,
      isArrowRightImage,
      getScrollPosition,
      widgetMode
    }
  }
})
</script>

<style lang="scss" scoped>
@import 'src/assets/styles/04-common/module-title';
.slider-details {
  &__sub_title {
    display: flex;
    gap: 3px;
    margin-top: 3px;
    @include finder-module-sub-title;
    margin-bottom: 10px;
    font-family: var(--finder-font-family-headline);
    font-size: 16px;
    line-height: 120%;
    font-weight: 400;
    color: var(--finder-color-dark);
  }
}
.slider {
  width: 100%;
  max-width: 925px;

  @media (max-width: 480px) {
    width: 100%;
    max-width: 430px;
  }
}
.slider-title {
  display: flex;
}
.session-name {
  display: flow-root;
  max-width: 53ch;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: bold;
  padding-left: 10px;

  @media (max-width: 480px) {
    display: flow-root;
    max-width: 11.8ch;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
.sliders-list {
  display: flex;
  flex-wrap: nowrap;
  list-style: none;
  margin: auto;
  overflow-x: auto;
  gap: 23px;
  // padding-bottom: 8px;
  padding-bottom: 5%;

  @media (max-width: 480px) {
    flex-direction: row;
    max-height: 70vh;
    gap: 6px;
    width: 100%;
  }

  /* For Internet Explorer and Edge */
  -ms-overflow-style: none;

  /* For Firefox */
  scrollbar-width: none;

  /* for Chrome, Safari, and Opera */
  &::-webkit-scrollbar {
    display: none;
    width: 0 !important;
  }
  & > * {
    min-width: 250px;
    width: 250px;
    flex-shrink: 0;

    &:first-child {
      margin-left: 0;
    }

    @media (max-width: 480px) {
      min-width: 132px;
      width: 132px;
    }
  }

  &.stele > * {
    margin-right: 18px;
  }
}

.sliders-list-slider {
  display: flex;
  margin-right: auto;
  overflow: hidden;

  &.align-left {
    justify-content: flex-start;

    .sliders-list {
      justify-content: flex-start;
    }
  }

  @media (max-width: 480px) {
    display: block;
  }
}

.sliders-list-slider-navigation {
  display: none;

  @media (max-width: 480px) {
    display: flex !important;
    flex-wrap: nowrap;
    justify-content: center;
  }
}

.sliders-list-slider-navigation-previous {
  margin-right: 20px;
  padding-top: 104px;

  @media (max-width: 480px) {
    display: none !important;
  }
}

.sliders-list-slider-navigation-next {
  margin-left: 20px;
  padding-top: 104px;

  @media (max-width: 480px) {
    display: none !important;
  }
}
</style>
