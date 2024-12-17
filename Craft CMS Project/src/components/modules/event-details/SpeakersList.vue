<template>
  <drag-scroll :isScrollable="isScrollable" tag="div" :class="{ 'speakers-list': true, stele: widgetMode?.toLowerCase() === 'stele'}">
    <Speaker v-for="item in speakersList" :key="item.id" :speaker="item" @click="handleSpeakerClick(item)" />
  </drag-scroll>
</template>

<script lang="ts">
import DragScroll from '@/components/helpers/DragScroll.vue'
import { SpeakerModel } from '@/models/SpeakerModel'
import { defineComponent, inject, onMounted, PropType, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import Speaker from '@/components/elements/Speaker.vue'
import router from '@/router'
import { useRouterStore } from '@/store/router'
import { useWidgetMode } from '@/composables/useWidgetMode'

export default defineComponent({
  name: 'SpeakersList',
  components: {
    Speaker,
    DragScroll
  },
  props: {
    speakersList: {
      type: Object as PropType<Array<SpeakerModel>>,
      required: true
    }
  },
  setup () {
    const { isWidgetMode, navigateToSpeakerDetailsFactory } = useWidgetMode()
    const widgetMode = inject<string | undefined>('app.widgetMode')

    const { t } = useI18n()

    const routerStore = useRouterStore()
    const { changePage } = routerStore

    let parentUrl: string | undefined
    let parentSpeakerFragment: string | undefined
    let parentTicketFragment: string | undefined
    let parentTicket: string | undefined
    if (isWidgetMode) {
      parentUrl = inject<string | undefined>('app.parentUrl')
      parentSpeakerFragment = inject<string | undefined>('app.parentSpeakerFragment')
      parentTicketFragment = inject<string | undefined>('app.parentTicketFragment')
      parentTicket = inject<string | undefined>('app.parentTicket')
      console.info('parent url speaker fragment:', parentSpeakerFragment)
    }
    onMounted(() => {
      console.log('🚀 ~ SpeakersList ~ onMounted:')
    })
    const handleSpeakerClick = (item: SpeakerModel) => {
      console.log('🚀 ~ file: SpeakersList.vue ~ line 35 ~ handleSpeakerClick ~ item', item)
      goToSpeakerDetails(item.uri)
    }
    const goToSpeakerDetails = (uri: string) => {
      console.log('🚀 ~ file: SpeakersList.vue ~ line 38 ~ goToSpeakerDetails ~ uri', uri)
      if (isWidgetMode) {
        if (parentUrl && parentSpeakerFragment) {
          const navigateToSpeakerDetails = navigateToSpeakerDetailsFactory({ parentUrl, parentSpeakerFragment, parentTicketFragment, parentTicket })
          navigateToSpeakerDetails(`${uri}`)
        } else {
          changePage('SpeakerDetails', { uri })
        }
      } else {
        router.push(`/speaker/${uri}`)
      }
    }

    const isScrollable = ref(false)
    const onResize = () => {
      isScrollable.value = (window.innerWidth > 481)
    }
    window.addEventListener('resize', onResize)
    window.removeEventListener('resize', onResize)

    return {
      t,
      handleSpeakerClick,
      goToSpeakerDetails,
      isScrollable,
      widgetMode
    }
  }
})
</script>

<style lang="scss" scoped>
.speakers-list {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
  list-style: none;
  overflow-x: auto;

  /* hide scrollbars */

  &::-webkit-scrollbar {
    display: none;
    width: 0 !important;
    height: 0;

    /* for Chrome, Safari, and Opera */
  }

  /* За Internet Explorer и Edge */
  -ms-overflow-style: none;

  /* За Firefox */
  scrollbar-width: none;

  & > * {
    --speaker-gap-width: 10px;
    flex: calc(50% - var(--speaker-gap-width));
    max-width: calc(50% - var(--speaker-gap-width));
    margin-bottom: 25px;
  }

  @media (max-width: 480px) {
    overflow: hidden;

    & > * {
      flex: 100%;
      max-width: 100%;
      width: 100%;
      align-items: flex-start;
      justify-content: flex-start;
      margin: 0;
      flex-flow: nowrap;
    }
  }

  &.stele > * {
    // margin: 0 35px 35px 0;
  }
}
</style>
