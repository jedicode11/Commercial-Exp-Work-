<template>
  <Loader :loading="loading.value" />
  <section class="event-details finder-normalize finder-content-wrapper">

    <div class="finder-content-wrapper__inner">
      <div class="event-details__section">
        <BackButton v-if="widgetMode?.toLowerCase() !== 'stele'" :icon-name="SvgIconName.arrowCircleLeft" />
      </div>
    </div>

    <div class="finder-content-wrapper__inner" v-if="event">
      <div class="event-details__section event-details__summary">
        <div class="event-details__summary__media">
          <div class="event-details__summary__media__images">
            <img :src="event.featureImage[0]?.url ?? '/images/image-placeholder.jpg'"
              :alt="event.featureImage[0]?.alt ?? ''" :title="event.featureImage[0]?.alt ?? ''" />
            <div class="event-details__summary__media__images__overlay"></div>
            <div class="event-details__summary__media__images__details">
              <EventCardDate class="event-card-date" v-if="date !== null && date !== undefined" :date="date" />
              <EventCardHourSpan class="event-card-hourspan"
                v-if="from !== null && to !== null && from !== undefined && to !== undefined" :from="from" :to="to" />
              <div v-if="locationActivator()">
                <EventCardLocation class="event-card-location"
                v-if="getLocationHallPlan.length"
                :location="getLocationTitle"
                @click="goToLocationLink()"/>
              </div>
              <div v-else>
                <EventCardLocation class="event-card-location old-location"
                v-if="event.eventLocation !== null && event.eventLocation !== undefined"
                :location="event.eventLocation" />
              </div>
              <EventCardLanguage class="event-card-language"
                v-if="event.eventLanguage !== null && event.eventLanguage !== undefined"
                :language="event.eventLanguage" />
            </div>
            <div v-if="widgetMode?.toLowerCase() === 'stele'" class="back-btn">
              <BackButton :icon-name="SvgIconName.arrowCircleLeft" />
            </div>
          </div>
        </div>
        <div class="event-details__summary__details">
          <div class="event-details__summary__details__chips">
            <Chip v-if="event.eventPlace === 'onSite'" :icon="SvgIconName.locationOn"
              :text="t('components_event_chip_onsite')" :hasShadow="false" />
            <Chip v-if="event.eventPlace === 'remote'" :icon="SvgIconName.headphones"
              :text="t('components_event_chip_remote')" :hasShadow="false" />
            <Chip v-if="event.eventChargeable.length && event.eventChargeable.includes('chargeable')"
              :icon="SvgIconName.euroCurrency" :text="t('components_event_chip_chargeable')" :hasShadow="false" />
            <Chip v-if="event.eventPlace === 'webSession'" :icon="SvgIconName.webSession" :hasShadow="false" :text="$t('components_event_chip_websession')" />
          </div>
          <h2 class="event-details__module-title">{{ event.title }}</h2>
          <div class="event-details__summary__details__description" v-html="event.eventContent"></div>

          <!-- Yasen Temporary Change Exhibitor Place -->
          <div class="event-details__section event-details__exhibitor" v-if="event?.exhibitor.length">
            <div class="exhibitor-container">
              <h3 class="event-details__sub_title">{{ t('components_event_subtitle_exhibitor') + ':' }}</h3>
              <Exhibitor :link="event.exhibitor[0].exhibitorUrl"
                :logo="event.exhibitor[0].exhibitorLogo" :name="event.exhibitor[0].exhibitorTitle"
                :address="event.exhibitor[0].exhibitorAddress" />
            </div>
          </div>

          <div class="event-details__summary__details__bottom-row">
            <BookmarkEvent v-if="!(widgetMode?.toLowerCase() === 'stele' || event.eventPlace === 'webSession')" :id="`${event.uid}`"></BookmarkEvent>
            <SubscribeToWebSession v-if="!(widgetMode?.toLowerCase() === 'stele' || event.eventPlace !== 'webSession')" :id="`${event.id}`" :fairId="`${siteGlobalFairIdTest?.fairIdField}`"></SubscribeToWebSession>
            <share-button v-if="!(widgetMode?.toLowerCase() === 'stele')" :id="`${event.uri}`"
              :subject="event.title"></share-button>
            <div v-if="locationActivator()">
              <p class="event-details__summary__details__location" :title="getLocationTitle"
              v-if="getLocationHallPlan.length"
              @click="goToLocationLink()">
              {{ getLocationTitle }}</p>
            </div>
            <div v-else>
              <p class="event-details__summary__details__location old-location" :title="event.eventLocation"
              @click="$event.preventDefault()">{{ event.eventLocation }}</p>
            </div>
          </div>

          <div class="event-details__section event-details__speakers" v-if="event?.speakers.length">
            <h3 class="event-details__sub_title">{{ t('components_event_subtitle_speakers') }}</h3>
            <SpeakersList
              :speakersList="event.speakers.sort((a, b) => (a.speakerLastName > b.speakerLastName) ? 1 : ((b.speakerLastName > a.speakerLastName) ? -1 : 0))">
            </SpeakersList>
          </div>

          <div class="event-details__section event-details__speakers" v-if="event?.chairs.length">
            <h3 class="event-details__sub_title">{{ t('components_event_subtitle_chairs') }}</h3>
            <SpeakersList
              :speakersList="event.chairs.sort((a, b) => (a.speakerLastName > b.speakerLastName) ? 1 : ((b.speakerLastName > a.speakerLastName) ? -1 : 0))">
            </SpeakersList>
          </div>

          <div class="event-details__section event-details__speakers" v-if="event?.moderators.length">
            <h3 class="event-details__sub_title">{{ t('components_event_subtitle_moderators') }}</h3>
            <SpeakersList
              :speakersList="event.moderators.sort((a, b) => (a.speakerLastName > b.speakerLastName) ? 1 : ((b.speakerLastName > a.speakerLastName) ? -1 : 0))">
            </SpeakersList>
          </div>

          <div class="event-details__section event-details__exhibitor_events" v-if="eventsListByExhibitor.length">
            <Slider :eventsList="eventsListByExhibitor" :sliderTitle="t('components_event_subtitle_exhibitor_events')" />
          </div>
        </div>
      </div>
      <div class="event-details__section">
        <Slider :eventsList="eventsBelongToSameSession" :sliderTitle="t('components_event_subtitle_session_events')"
          :sessionName="sessionName || ''" />
        <Slider :eventsList="sortedAndDistinctedSimilarEvents"
          :sliderTitle="t('components_event_subtitle_similar_events')" />
      </div>
    </div>
  </section>
</template>

<script lang="ts">
import { defineComponent, ref, watchEffect, computed, watch, onMounted, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAppointments } from '@/store/appointments'
import { EditorIds } from '@/composables/vis-modules/myAdmin'
import { useEventListener } from '@vueuse/core'
import { FinderEvents } from '@/utils/events'
import { Appointment, AppointmentBase, AppointmentType, createAppointment } from '@/models/Appoinment'
import { getAppointmentState } from '@/utils/appointmentState'
import { EventModel } from '@/models/EventModel'
import { SimilarEventModel } from '@/models/SimilarEventModel'
import { SvgIconName } from '@/components/helpers/SvgIcon.vue'
import BookmarkEvent from '@/components/elements/my-organizer/BookmarkEvent.vue'
import SubscribeToWebSession from '@/components/elements/SubscribeToWebSession.vue'
import { useRoute } from 'vue-router'
import EventCardDate from '@/components/elements/EventCardDate.vue'
import EventCardHourSpan from '@/components/elements/EventCardHourSpan.vue'
import EventCardLocation from '@/components/elements/EventCardLocation.vue'
import EventCardLanguage from '@/components/elements/EventCardLanguage.vue'
import SpeakersList from './SpeakersList.vue'
import Exhibitor from '@/components/elements/Exhibitor.vue'
import Chip from '@/components/elements/Chip.vue'
import Loader from '@/components/elements/Loader.vue'
import ShareButton from '@/components/elements/ShareButton.vue'
import { useLazyQuery } from '@vue/apollo-composable'
import { useRouterStore } from '@/store/router'
import { storeToRefs } from 'pinia'
import { cloneDeep, uniqBy } from 'lodash'
import { useLanguageStore } from '@/store/language'
import { useWidgetMode } from '@/composables/useWidgetMode'
import { useSiteId } from '@/composables/useSiteId'
import { useSessionStoreHistory } from '@/composables/useSessionStoreHistory'
import Slider from '@/components/elements/Slider.vue'
import BackButton from '@/components/elements/BackButton.vue'
import { GET_EVENT_DETAILS } from '@/graphql/queries/eventDetails'
import { GET_EVENTS_BY_EXHIBITOR } from '@/graphql/queries/getEventsByExhibitor'
import { GET_SIMILAR_EVENTS_BY_CATEGORY } from '@/graphql/queries/getSimilarEventsByCategory'
import { SIBLING_CATEGORY } from '@/graphql/queries/siblingCategory'
import { GET_SIMILAR_EVENTS_BY_SIBLING_CATEGORY } from '@/graphql/queries/getSimilarEventsBySiblingsCategory'
import { PARENT_CATEGORY } from '@/graphql/queries/parentCategory'
import { GET_SIMILAR_EVENTS_BY_PARENT_CATEGORY } from '@/graphql/queries/getSimilarEventsByParentCategory'
import { GET_SIMILAR_EVENTS_BY_TAG } from '@/graphql/queries/getSimilarEventsByTag'
import { GET_EVENTS_BELONG_TO_SAME_SESSION } from '@/graphql/queries/getEventsBelongToSameSession'
import { SingleEventRelatedSessionModel } from '@/models/SingleEventRelatedSessionModel'
import { SingleEventModel } from '@/models/SingleEventModel'
import { SiteGlobalLocationModel } from '@/models/SiteGlobalLocationModel'
import { useLocation, responseTransformer } from '@/composables/useLocation'

export enum appointmentListType {
  future = 'listFuture',
  past = 'listPast'
}

export default defineComponent({
  name: 'EventDetails',
  components: {
    Loader,
    BookmarkEvent,
    SubscribeToWebSession,
    EventCardDate,
    EventCardHourSpan,
    EventCardLocation,
    EventCardLanguage,
    SpeakersList,
    Exhibitor,
    Chip,
    ShareButton,
    Slider,
    BackButton
  },
  setup () {
    const { isWidgetMode } = useWidgetMode()

    const { t, locale } = useI18n()

    const languageStore = useLanguageStore()
    const { setLanguage } = languageStore

    const appointmentsStore = useAppointments()
    const currentListType = ref<appointmentListType>(appointmentListType.future)
    const showToggleBtn = ref<boolean>(false)
    const toggleBtnLbl = ref('')
    const showModal = ref<boolean>(false)
    const menuItems = ref([{ title: t('components_generic_info'), anchor: '#appointment-top' }])
    const appointmentList = ref<Array<AppointmentBase>>([])
    const filteredAppointmentList = ref<Array<AppointmentBase>>([])
    const editorsTriggeringReload: Array<string> = [
      EditorIds.boothEventsItem,
      EditorIds.videoConferencesItem
    ]
    const appointmentDetails = ref<Appointment>(createAppointment())
    const { getSitesForGraphQl } = useSiteId()

    interface siteGlobalFairIdModel {
      fairIdField: string,
      siteId: string
    }

    const uri = ref<string>()
    const event = ref<SingleEventModel | null>()
    const relatedSession = ref<SingleEventRelatedSessionModel | null>()
    const siteGlobalLocation = ref<SiteGlobalLocationModel | null>()
    const siteGlobalFairId = ref<siteGlobalFairIdModel>()
    const siteGlobalFairIdTest = ref<siteGlobalFairIdModel>()
    const sessionName = ref<string | null>()
    const eventsListByExhibitor = ref<Array<EventModel>>([])
    const similarEvents = ref<Array<SimilarEventModel>>([])
    let similarEventPriority = 3

    const eventsBelongToSameSession = ref<Array<EventModel>>([])

    const route = useRoute()
    const routerStore = useRouterStore()
    const { currentParams } = storeToRefs(routerStore)

    const widgetMode = inject<string | undefined>('app.widgetMode')
    const locationActivated = inject<string | undefined>('app.locationHallPlanActivated')

    const { 
      goToLocationLink, 
      getLocationTitle, 
      getLocationHallPlan
    } = useLocation(event, relatedSession)

    let parentUrl: string | undefined
    let parentEventFragment: string | undefined
    if (isWidgetMode) {
      parentUrl = inject<string | undefined>('app.parentUrl')
      parentEventFragment = inject<string | undefined>('app.parentEventFragment')
      parentUrl && parentEventFragment && console.info('parent event fragment:', parentEventFragment)
    }

    let startPage: string | undefined
    let detailsId: string | undefined
    if (isWidgetMode) {
      startPage = inject<string | undefined>('app.startPage')
      detailsId = inject<string | undefined>('app.detailsId')
      startPage && console.info('preloading item Id:', detailsId)
    }

    appointmentsStore.fetchAppointmentsLists().then(() => {
      if (appointmentsStore.appointmentsLists[appointmentListType.future].length === 0 && appointmentsStore.appointmentsLists[appointmentListType.past].length > 0) {
        toggleList()
      }
    })

    watch(locale, () => {
      setLanguage(locale.value)
      loadQueries()
    })

    const setUri = () => {
      if (isWidgetMode) {
        if (detailsId) {
          uri.value = detailsId ?? ''
        } else {
          uri.value = currentParams.value?.id ?? ''
        }
      } else {
        uri.value = (route?.params?.uri) as string ?? ''
      }
    }

    if (isWidgetMode) {
      watch(currentParams, () => {
        console.log('🚀 ~ watch ~ currentParams:', currentParams)
        setUri()
        loadQueries()
      })
    } else {
      watch(route, () => {
        console.log('🚀 ~ watch ~ route:', route)
        setUri()
        loadQueries()
      })
    }

    onMounted(() => {
      console.log('🚀 ~ EventDetails ~ onMounted')
      setUri()
      // setSite()
      loadQueries()
      useSessionStoreHistory().append()
    })

    const { result: resultEvent, load: loadEvent, loading: loadingEvent } = useLazyQuery(GET_EVENT_DETAILS)

    watch(resultEvent, value => {
      event.value = cloneDeep(value.event)
      relatedSession.value = value.relatedSession
      siteGlobalLocation.value = value.globalSets.find((gs: any) => gs.siteId === event.value?.siteId)
      siteGlobalFairId.value = value.globalSets.find((fi: any) => fi.fairIdField === event.value?.siteId)
      siteGlobalFairIdTest.value = value.globalSets.find((fi: any) => fi.fairIdField)

      console.log("88888888888888888888888888888888888888888888888", siteGlobalFairId.value)
      console.log("33333333333333333333333333333333333333333333333", siteGlobalFairIdTest.value?.fairIdField)

      scrollToTop()
      const variables = {
        site: getSitesForGraphQl(),
        exhibitorId: event.value?.exhibitor?.[0]?.id ?? null,
        startDateFilter: '>=' + (new Date().getTime() / 1000)
      }
      if (value && value.event && value.event.exhibitor.length) loadEventsByExhibitor(undefined, variables)
      loadSimilarEventsQueries()
    })

    const {
       result: resultEventsByExhibitor, 
       load: loadEventsByExhibitor, 
       loading: loadingEventsByExhibitor 
    } = useLazyQuery(GET_EVENTS_BY_EXHIBITOR, { site: getSitesForGraphQl() }, { fetchPolicy: 'network-only' })

    watch(resultEventsByExhibitor, value => {
      const globalSets = value.globalSets
      let data = [...value.events].filter(e => e.id !== event.value?.id)
      data = responseTransformer({ events: data, globalSets })
      eventsListByExhibitor.value = data
    })

    const { 
      result: resultSimilarEventsByCategory, 
      load: loadSimilarEventsByCategory 
    } = useLazyQuery(GET_SIMILAR_EVENTS_BY_CATEGORY)

    watch(resultSimilarEventsByCategory, value => {
      const globalSets = value.globalSets
      let data = value.events.map((e: SimilarEventModel) => { return { ...e, priority: 1 } })
      data = responseTransformer({ events: data, globalSets })
      similarEvents.value = [...similarEvents.value, ...data.filter((e: SimilarEventModel) => e.id !== event.value?.id)]
    })

    const { 
      result: resultSiblingsCategory, 
      load: loadSiblingsCategory 
    } = useLazyQuery(SIBLING_CATEGORY)

    watch(resultSiblingsCategory, value => {
      const siblingsCategory: any = []
      value.siblingsCategories.forEach((e: { parent: { children: [{ id: number }] } }) => {
        if (e.parent?.children) {
          e.parent.children.forEach(o => siblingsCategory.push(o.id))
        }
      })

      const variablesEventSiblingsCategory = {
        site: getSitesForGraphQl(),
        productCategoryIds: siblingsCategory,
        startDateFilter: '>=' + (new Date().getTime() / 1000)
      }

      loadSimilarEventsBySiblingsCategory(undefined, variablesEventSiblingsCategory)
    })

    const { 
      result: resultSimilarEventsBySiblingsCategory, 
      load: loadSimilarEventsBySiblingsCategory 
    } = useLazyQuery(GET_SIMILAR_EVENTS_BY_SIBLING_CATEGORY)

    watch(resultSimilarEventsBySiblingsCategory, value => {
      const globalSets = value.globalSets
      let data = value.events.map((e: SimilarEventModel) => { return { ...e, priority: 2 } })
      data = responseTransformer({ events: data, globalSets })
      similarEvents.value = [...similarEvents.value, ...data.filter((e: SimilarEventModel) => e.id !== event.value?.id)]
    })

    const { 
      result: resultParentCategory, 
      load: loadParentCategory 
    } = useLazyQuery(PARENT_CATEGORY) 

    watch(resultParentCategory, value => {
      const parentCategories = value.parentCategories.filter((pc: { parent: { id: number } }) => pc.parent).map((pc: { parent: { id: number } }) => pc.parent.id)
      if (parentCategories.length) {
        const variablesEventbyParentCategory = {
          site: getSitesForGraphQl(),
          productCategoryIds: parentCategories,
          startDateFilter: '>=' + (new Date().getTime() / 1000)
        }

        loadSimilarEventsByParentCategory(undefined, variablesEventbyParentCategory)

        const variablesParentCategory = {
          site: getSitesForGraphQl(),
          categoryIds: parentCategories
        }

        loadParentCategory(undefined, variablesParentCategory)
      }
    })

    const { 
      result: resultSimilarEventsByParentCategory, 
      load: loadSimilarEventsByParentCategory 
    } = useLazyQuery(GET_SIMILAR_EVENTS_BY_PARENT_CATEGORY)

    watch(resultSimilarEventsByParentCategory, value => {
      const globalSets = value.globalSets
      let data = value.events.map((e: SimilarEventModel) => { return { ...e, priority: similarEventPriority } })
      data = responseTransformer({ events: data, globalSets })
      similarEvents.value = [...similarEvents.value, ...data.filter((e: SimilarEventModel) => e.id !== event.value?.id)]

      similarEventPriority++
    })

    const { 
      result: resultSimilarEventsByTag, 
      load: loadSimilarEventsByTag 
    } = useLazyQuery(GET_SIMILAR_EVENTS_BY_TAG)

    

    watch(resultSimilarEventsByTag, value => {
      const data = value.events.map((e: SimilarEventModel) => { return { ...e, priority: 10 } })
      similarEvents.value = [...similarEvents.value, ...data.filter((e: SimilarEventModel) => e.id !== event.value?.id)]
    })

    const { 
      result: resultEventsBelongToSameSession, 
      load: loadEventsBelongToSameSession 
    } = useLazyQuery(GET_EVENTS_BELONG_TO_SAME_SESSION)

    watch(resultEventsBelongToSameSession, value => {
      let events: any[] = []
      const globalSets = value.globalSets

      value.entries.forEach((item: any) => {  
        const sessionEvents = item.sessionEvents.map((e: any) => {
          return {...e, relatedSession: { sessionLocationHallPlan: item.locationHallPlan } }
        })
        events = [...events, ...sessionEvents]
      })
      events = events.filter(e => e.id !== event?.value?.id)

      const sortedEvents = events.sort((a, b) => {
        return new Date(a.startEventDatetime).getTime() - new Date(b.startEventDatetime).getTime()
      })

      sessionName.value = sortedEvents.length > 0 ? value.entries[0].title : ''
      eventsBelongToSameSession.value = sortedEvents
      eventsBelongToSameSession.value = uniqBy(eventsBelongToSameSession.value, (i) => {return i.id})
      eventsBelongToSameSession.value = responseTransformer({ events: eventsBelongToSameSession.value, globalSets })
    })

    const loadQueries = () => {
      loadEvent(undefined, { site: getSitesForGraphQl(), uri: uri.value })
    }

    const loadSimilarEventsQueries = () => {
      if (event.value) {
        if (event.value.productCategory.length) {
          const variablesCategory = {
            site: getSitesForGraphQl(),
            productCategoryIds: event.value.productCategory.map(pc => Number(pc.id)),
            startDateFilter: '>=' + (new Date().getTime() / 1000)
          }

          const variablesParentCategory = {
            site: getSitesForGraphQl(),
            categoryIds: event.value.productCategory.map(pc => Number(pc.id))
          }
          // done
          loadSimilarEventsByCategory(undefined, variablesCategory)

          loadParentCategory(undefined, variablesParentCategory)

          loadSiblingsCategory(undefined, variablesParentCategory)
        }

        if (event.value.tags.length) {
          const variablesTag = {
            site: getSitesForGraphQl(),
            categoryIds: [{ id: event.value.tags.map(et => Number(et.id)) }],
            startDateFilter: '>=' + (new Date().getTime() / 1000)
          }

          loadSimilarEventsByTag(undefined, variablesTag)
        }

        
        const variablesEventsBelongToSameSession = {
          site: getSitesForGraphQl(),
          relatedTo: event.value.id
        }
        // done
        loadEventsBelongToSameSession(undefined, variablesEventsBelongToSameSession)
        
      }
    }

    const loading = computed(() => loadingEvent || loadingEventsByExhibitor)

    const sortedAndDistinctedSimilarEvents = computed(() => {
      /* eslint-disable */
      const sortedSimilarEvents = similarEvents.value.sort((a, b) => {
        return a.priority - b.priority || new Date(a.startEventDatetime).getTime() - new Date(b.startEventDatetime).getTime()
      })

      const ids: number[] = []
      const distincSimilarEvents: SimilarEventModel[] = []
      sortedSimilarEvents.forEach(e => {
        if (!ids.includes(e.id)) {
          ids.push(e.id)
          distincSimilarEvents.push(e)
        }
      })

      return distincSimilarEvents
    })

    const date = computed((): Date | null | undefined => {
      return event.value ? (event.value.startEventDatetime ? new Date(event.value.startEventDatetime) : null) : undefined
    })
    const from = computed((): Date | null | undefined => {
      return event.value ? (event.value.startEventDatetime ? new Date(event.value.startEventDatetime) : null) : undefined
    })
    const to = computed((): Date | null | undefined => {
      return event.value ? (event.value.endEventDatetime ? new Date(event.value.endEventDatetime) : null) : undefined
    })

    const locationActivator = () => locationActivated === "true"

    const toggleList = () => {
      if (currentListType.value === appointmentListType.future) {
        currentListType.value = appointmentListType.past
      } else {
        currentListType.value = appointmentListType.future
      }
    }

    const scrollToTop = () => {
      window.scrollTo({
        top: 0,
        behavior: 'smooth'
      })
    }

    watchEffect(() => {
      appointmentDetails.value = appointmentsStore.appointmentDetails
      const {
        hasDownloads
      } = getAppointmentState(appointmentDetails)
      menuItems.value = [{ title: t('components_generic_info'), anchor: '#appointment-top' }]
      if (hasDownloads.value) {
        menuItems.value.push({
          title: t('components_appointment_details_section_downloads'),
          anchor: '#appointment-downloads'
        })
      }
    })

    watchEffect(() => {
      appointmentList.value = appointmentsStore.appointmentsLists[currentListType.value] !== null ? appointmentsStore.appointmentsLists[currentListType.value] : []

      showToggleBtn.value = appointmentsStore.appointmentsLists.listPast.length > 0 && appointmentsStore.appointmentsLists.listFuture.length > 0
      toggleBtnLbl.value = currentListType.value === appointmentListType.future ? t('components_appointment_toggle_bt_lbl_show_past') : t('components_appointment_toggle_bt_lbl_show_future')

      appointmentsStore.selectedAppointmentFilter = []
    })

    watchEffect(() => {
      if (appointmentsStore.selectedAppointmentFilter.length > 0) {
        filteredAppointmentList.value = appointmentList.value.filter(appointment => {
          let includeInFilter = appointmentsStore.selectedAppointmentFilter.includes(appointment.type)
          if (includeInFilter && appointment.type === AppointmentType.websession && appointment.isMeeting) {
            includeInFilter = false
          }
          if (!includeInFilter && appointmentsStore.selectedAppointmentFilter.includes(AppointmentType.roundtable)) {
            includeInFilter = appointment.type === AppointmentType.websession && appointment.isMeeting
          }
          return includeInFilter
        })
      } else {
        filteredAppointmentList.value = appointmentList.value
      }
    })

    useEventListener(window, FinderEvents.dataEditComplete, (event: CustomEvent) => {
      if (editorsTriggeringReload.includes(event.detail)) {
        appointmentsStore.fetchAppointmentsLists(true)
      }
    })

    return {
      t,
      appointmentList,
      filteredAppointmentList,
      appointmentDetails,
      showModal,
      menuItems,
      toggleBtnLbl,
      showToggleBtn,
      currentListType,
      loading,
      scrollToTop,
      toggleList,

      widgetMode,

      event,
      sortedAndDistinctedSimilarEvents,
      eventsBelongToSameSession,
      SvgIconName,
      date,
      from,
      to,
      eventsListByExhibitor,
      sessionName,
      goToLocationLink,
      locationActivator,

      getLocationHallPlan,
      getLocationTitle,

      siteGlobalFairIdTest
    }
  }
})
</script>

<style lang="scss" scoped>
@import 'src/assets/styles/04-common/module-title';

.exhibitor-container {
  display: flex;
  gap: 10px;
  align-items: center;
}

.back-btn {
  position: absolute;
  bottom: -60px;
}

.event-details__summary__details__location {
  margin-top: 10px;
  margin-bottom: 10px;
  cursor: pointer;
}

.event-card-location {
  cursor: pointer;
}

.old-location {
  cursor: unset;
}

.event-details {
  .event-details__module-title {
    font-family: 'Source Sans Pro', sans-serif;
    font-size: 42px;
    font-weight: 300;
    line-height: 50px;
    letter-spacing: 0px;
    text-align: left;
  }

  .event-details__section {
    padding: 4px 0;
    margin-top: 24px;
  }
  .event-details__sub_title {
    display: flex;
    gap: 3px;
    margin-top: 10px;
    margin-bottom: 10px;

    @media (max-width: 480px) {
      display: flex;
    }

    @include finder-module-sub-title;
  }
  .event-details__summary {
    display: flex;
    gap: 10px 0;
    justify-content: space-between;

    .event-details__summary__media {
      flex: 37%;
      max-width: 37%;

      .event-details__summary__media__images {
        position: -webkit-sticky;
        position: sticky;
        top: 80px;
        margin-bottom: 70px;
        width: 100%;
        border-radius: 9px;

        @media (max-width: 480px) {
          margin-bottom: 0px;
        }
        &::after {
          content: '';
          display: block;
          padding-bottom: 100%;
        }
        img {
          position: absolute;
          width: 100%;
          height: 100%;
          object-fit: cover;
          border-radius: 9px;
        }
        .event-details__summary__media__images__overlay {
          position: absolute;
          left: 0%;
          right: 0%;
          top: 0%;
          bottom: 0%;
          border-radius: 9px;
          background: linear-gradient(180deg, rgba(0, 0, 0, 0) 0%, rgba(0, 0, 0, 0.5) 100%);
        }
        .event-details__summary__media__images__details {
          position: absolute;
          bottom: 10px;
          left: 10px;
          color: #ffffff;
          font-size: 20px;
        }
      }
      table {
        width: 100%;
        border-collapse: collapse;
        border-radius: 9px;
        border-style: hidden;
        box-shadow: 0 0 0 1px #ededed;

        td {
          padding: 10px 5px;
          border: 1px solid #ededed;
        }
      }
    }

    .event-details__summary__details {
      flex: 57%;
      max-width: 57%;
      flex-direction: column;

      .event-details__summary__details__top-row,
      .event-details__summary__details__bottom-row {
        display: flex;
        margin-bottom: 12px;
        align-items: center;
        gap: 6px;

        & > * {
          align-items: center;
        }

        &__spacer {
          flex-grow: 1;
        }

        &__buttons {
          gap: 8px;
        }

        &__location {
          cursor: none;
        }
      }

      :deep(.event-card-date),
      :deep(.event-card-hourspan) {
        margin-bottom: 6px;
      }

      :deep(.evet-card-date > span) {
        font-size: 2em;
        line-height: 1.3em;
        font-weight: lighter;
      }

      .event-details__summary__details__chips {
        display: flex;
        align-items: flex-end;
        gap: 6px;
        margin-bottom: 18px;
      }

      .event-details__summary__details__description {
        margin-bottom: 20px;
      }
    }

    @media (max-width: 480px) {
      flex-direction: column;
      gap: 20px 0;

      .event-details__summary__media,
      .event-details__summary__details {
        flex: 100%;
        max-width: 100%;
      }
    }
  }
}
</style>
