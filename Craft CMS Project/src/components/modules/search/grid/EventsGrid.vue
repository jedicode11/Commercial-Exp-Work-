<template>
  <div tag="div" :class="widgetMode?.toLowerCase() === 'stele' ? 'events-grid-style' : 'events-grid'">
  <!-- <drag-scroll tag="div" class="events-grid"> -->
    <!-- <EventCard v-for="item in eventsList" :key="item.id" :event="item" @click="handleEventClick(item)" :isRegistered="true" /> -->
    <EventGridItem v-for="item in eventsList" :key="item.id" :event="item" @click="handleEventClick(item)" :isRegistered="true" />
  <!-- </drag-scroll> -->
  </div>
</template>

<script lang="ts">
import { EventModel } from '@/models/EventModel'
import { defineComponent, inject, PropType } from 'vue'
import { useI18n } from 'vue-i18n'
import EventGridItem from '../grid-item/EventGridItem.vue'
import router from '@/router'
import { useRouterStore } from '@/store/router'
import { useWidgetMode } from '@/composables/useWidgetMode'

export default defineComponent({
  name: 'EventsGrid',
  components: {
    EventGridItem
  },
  props: {
    eventsList: {
      type: Object as PropType<Array<EventModel>>,
      required: true
    }
  },
  // setup (props, { emit }) {
  setup () {
    const { isWidgetMode, navigateToEventDetailsFactory, navigateToSessionDetailsFactory } = useWidgetMode()

    // const store = useStore()
    const { t } = useI18n()

    const routerStore = useRouterStore()
    const { changePage } = routerStore

    let parentUrl: string | undefined
    let parentEventFragment: string | undefined
    let parentSessionFragment: string | undefined
    let parentTicketFragment: string | undefined
    let parentTicket: string | undefined
    let widgetMode: string | undefined
    if (isWidgetMode) {
      widgetMode = inject<string | undefined>('app.widgetMode')
      parentUrl = inject<string | undefined>('app.parentUrl')
      parentEventFragment = inject<string | undefined>('app.parentEventFragment')
      parentSessionFragment = inject<string | undefined>('app.parentSessionFragment')
      parentTicketFragment = inject<string | undefined>('app.parentTicketFragment')
      parentTicket = inject<string | undefined>('app.parentTicket')
      console.info('parent url event fragment:', parentEventFragment)
    }

    // const onAppointmentSelection = (appointmentId: string) => {
    //   emit('AppointmentSelection', appointmentId)
    // }
    // const handleEventClick = (item: EventLiteModel) => {
    const handleEventClick = (item: EventModel) => {
      console.log('🚀 ~ file: EventsGrid.vue ~ line 57 ~ handleEventClick ~ item', item)
      if (item.sectionHandle === 'session') {
        goToSessionDetails(item.uri)
      } else {
        goToEventDetails(item.uri)
      }
    }
    const goToSessionDetails = (uri: string) => {
      console.log('🚀 ~ file: EventsGrid.vue ~ line 38 ~ goToSessionDetails ~ uri', uri)
      if (isWidgetMode) {
        if (parentUrl && parentSessionFragment) {
          const navigateToSessionDetails = navigateToSessionDetailsFactory({ parentUrl, parentSessionFragment, parentTicketFragment, parentTicket })
          navigateToSessionDetails(`${uri}`)
        } else {
          changePage('SessionDetails', { uri })
        }
      } else {
        router.push(`/session/${uri}`)
      }
    }

    const goToEventDetails = (uri: string) => {
      console.log('🚀 ~ file: EventsGrid.vue ~ line 78 ~ goToEventDetails ~ uri', uri)
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

    return {
      t,
      handleEventClick,
      goToEventDetails,
      widgetMode
    }
  }
})
</script>

<style lang="scss" scoped>
.events-grid {
  display: flex;
  flex-wrap: wrap;
  margin: 0;

  /* hide scrollbars */
  scrollbar-width: none; /* for Firefox */
  -ms-overflow-style: none; /* for Internet Explorer, Edge */
  &::-webkit-scrollbar {
    display: none;
    width: 0 !important; /* for Chrome, Safari, and Opera */
  }

  @media (max-width: 480px) {
    display: block;
    flex-direction: column;
    align-items: center; // Centered vertically mobile
    justify-content: center;
    overflow-y: scroll;
    padding-bottom: 10%;
    gap: 24px;
  }

  gap: 48px 18px;

  & > * {
    @media (max-width: 480px) {
      flex: 1;
      min-width: calc(100% - 2 * 18px / 3);
      // width: calc(100% - 2 * 18px / 3);
      width: 100%;
      margin-bottom: 10px;
    }
    min-width: calc(33.333% - 2 * 18px / 3);
    width: calc(33.333% - 2 * 18px / 3);
    margin-bottom: 10px;
  }
}

.events-grid-style {
  display: flex;
  flex-wrap: wrap;
  margin: 0;

  /* hide scrollbars */
  scrollbar-width: none; /* for Firefox */
  -ms-overflow-style: none; /* for Internet Explorer, Edge */
  &::-webkit-scrollbar {
    display: none;
    width: 0 !important; /* for Chrome, Safari, and Opera */
  }

  & > * {
    min-width: calc(33.333% - 2 * 18px / 3);
    width: calc(33.333%);
    padding: 7px;
  }
}
</style>
