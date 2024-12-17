<template>
  <Loader :loading="loading.value" />
  <section class="events finder-normalize">
    <div class="finder-content-wrapper">
      <div class="finder-content-wrapper__inner">
        <div class="events__header-row">
          <div class="events__header-row__title">
          </div>
        </div>
      </div>
    </div>
    <div v-if="!(String(searchFieldIsHide).toLowerCase() === 'true')" class="events__section odd">
      <div class="finder-content-wrapper">
        <div class="finder-content-wrapper__inner">
          <h3 class="events__sub_title">{{ t('components_events_subtitle_browse') }}</h3>
          <SearchField backgroundColor="#ffffff" showIcon showButton :buttonText="t('components_events_button_start_search')" :placeholder="t('components_events_search_placeholder')" @search="goToSearch($event)" />
        </div>
      </div>
    </div>
    <div class="events__section odd" v-if="!pageSegment || pageSegment === PageSegment.FILTERED_EVENTS">
      <div class="finder-content-wrapper">
        <div>
          <Slider :eventsList="filteredEvents" />
        </div>
      </div>
    </div>
    <div class="events__section even" v-if="!pageSegment || pageSegment === PageSegment.EVENTS_LIST_MOST_BOOKED">
      <div class="finder-content-wrapper">
        <div>
          <h3 class="events__sub_title">{{ t('components_events_subtitle_most_booked') }}</h3>
          <Slider :eventsList="eventsListMostBooked" />
        </div>
      </div>
    </div>
    <div class="events__section odd" v-if="!pageSegment || pageSegment === PageSegment.EVENTS_LIST_FOR_TODAY">
      <div class="finder-content-wrapper">
        <div>
          <h3 class="events__sub_title">{{ t('components_events_subtitle_today') }}</h3>
          <Slider :eventsList="eventsListForToday" />
        </div>
      </div>
    </div>
  </section>
</template>

<script lang="ts">
import { defineComponent, ref, watchEffect, watch, computed, onMounted, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAppointments } from '@/store/appointments'
import { EditorIds } from '@/composables/vis-modules/myAdmin'
import { useEventListener } from '@vueuse/core'
import { FinderEvents } from '@/utils/events'
import { Appointment, AppointmentBase, AppointmentType, createAppointment } from '@/models/Appoinment'
import { convertDatetimeToDate } from '@/utils/date'
import { getAppointmentState } from '@/utils/appointmentState'
import { EventModel } from '@/models/EventModel'
import SearchField from '@/components/elements/SearchField.vue'
import Loader from '@/components/elements/Loader.vue'
import { useLazyQuery } from '@vue/apollo-composable'
import gql from 'graphql-tag'
import router from '@/router'
import { useRouterStore } from '@/store/router'
import { useLanguageStore } from '@/store/language'
import { storeToRefs } from 'pinia'
import { useWidgetMode } from '@/composables/useWidgetMode'
import { useSiteId } from '@/composables/useSiteId'
import { useSessionStoreHistory } from '@/composables/useSessionStoreHistory'
import Slider from '@/components/elements/Slider.vue'
import { utcToZonedTime, format as f } from 'date-fns-tz'
import { GET_FILTERED_EVENTS } from '@/graphql/queries/getFilteredEvents'
import { GET_RELATED_SESSIONS } from '@/graphql/queries/getRelatedSessions'
import { RelatedSessionModel } from '@/models/RelationSessionModel'
import { GET_MOST_BOOKED_EVENTS } from '@/graphql/queries/getMostBookedEvents'
import { GET_TODAY_EVENTS } from '@/graphql/queries/getTodayEvents'
import { ScriptParamsService } from '@/utils/scriptParamService'
import { PageSegment } from '@/enums/script-params'
import { responseTransformer } from '@/composables/useLocation'

export enum appointmentListType {
  future = 'listFuture',
  past = 'listPast'
}

export interface queryObj {
  site: string[] | null | undefined,
  section: string[],
  timeStart: string | null,
  timeEnd: string | null,
  relatedTo: string | null,
  offset: number | null,
  limit: number | null,
  orderBy: string | null
}

export default defineComponent({
  name: 'EventsView',
  components: {
    Loader,
    Slider,
    SearchField
  },
  setup () {
    const { isWidgetMode } = useWidgetMode()
    const eventAllSearchParams = inject<string | undefined>('app.eventAllSearchParams')
    const scriptParamsService = inject<ScriptParamsService | undefined>('app.scriptParamsService')

    const routerStore = useRouterStore()
    const { changePage } = routerStore
    const searchFieldIsHide = inject<boolean | undefined>('app.searchFieldIsHide')

    const { t, locale } = useI18n()
    const i18n = useI18n()

    const languageStore = useLanguageStore()
    const { currentLanguage } = storeToRefs(languageStore)
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

    const speakers = ref<{ id: number, title: string }[]>([])

    let pageSegment
    if (isWidgetMode && scriptParamsService) {
      pageSegment = scriptParamsService.pageSegment
    }
    const { getSitesForGraphQl } = useSiteId()
    const filteredEvents = ref<Array<EventModel>>([])
    const eventsListMostBooked = ref<Array<EventModel>>([])
    const eventsListForToday = ref<Array<EventModel>>([])

    appointmentsStore.fetchAppointmentsLists().then(() => {
      if (appointmentsStore.appointmentsLists[appointmentListType.future].length === 0 && appointmentsStore.appointmentsLists[appointmentListType.past].length > 0) {
        toggleList()
      }
    })

    watch(locale, () => {
      setLanguage(locale.value)
      loadQueries()
    })

    onMounted(() => {
      console.log('🚀 ~ EventsView ~ onMounted:')
      loadSpeakers()
      useSessionStoreHistory().append()
    })

    const { result: resultSpeakers, load: loadSpeakers, loading: loadingSpeakers } = useLazyQuery(gql`
      query getSpeakers ($site: [String]!) {
        speakers: entries(site: $site, section: "person", unique: true)  {
          id
          title
        }
      }
      `, { site: getSitesForGraphQl() })

    watch(resultSpeakers, val => {
      speakers.value = val.speakers
      loadQueries()
    })

    const { 
      result: resultFilteredEvents, 
      load: loadFilteredEvents, 
      loading: loadingFilteredEvents 
    } = useLazyQuery(GET_FILTERED_EVENTS)

    const { 
      result: resultFilteredEventsRelationSession, 
      load: loadFilteredEventsRelationSession, 
      loading: loadingFilteredEventsRelationSession 
    } = useLazyQuery(GET_RELATED_SESSIONS)

    watch(resultFilteredEvents, value => {
      let fe = [...value.events]

      if (eventAllSearchParams) {
        const paramsObj: any = parseQueryParams(eventAllSearchParams)
        const timeZone = 'Europe/Berlin'
        if (paramsObj.timeOfDay && paramsObj.timeOfDay === 'morning') {
          fe = fe.filter(e =>
            Number(f(utcToZonedTime(e.startEventDatetime, timeZone), 'HH', { timeZone })) >= 5 && Number(f(utcToZonedTime(e.startEventDatetime, timeZone), 'HH', { timeZone })) < 14
          )
        } else if (paramsObj.timeOfDay && paramsObj.timeOfDay === 'afternoon') {
          fe = fe.filter(e =>
            Number(f(utcToZonedTime(e.startEventDatetime, timeZone), 'HH', { timeZone })) >= 14
          )
        }
      }

      filteredEvents.value = responseTransformer({events: fe, globalSets: value.globalSets})

      const uri = filteredEvents.value.map((e: any) => e.uri)
      loadFilteredEventsRelationSession(undefined, { site: getSitesForGraphQl(), uri })
    })

    watch(resultFilteredEventsRelationSession, (val: { relatedSession: RelatedSessionModel[] }) => {
      eventsListMostBooked.value = eventsListMostBooked.value.map(e => {
        return {... e, relatedSession: val.relatedSession.find(rs => rs.eventsId.map(i => i.id).includes(e.id))}
      })
    })

    const { 
      result: resultMostBookedEvents, 
      load: loadMostBookedEvents, 
      loading: loadingMostBookedEvents 
    } = useLazyQuery(GET_MOST_BOOKED_EVENTS)

    const { 
      result: resultMostBookedEventsRelationSession, 
      load: loadMostBookedEventsRelationSession, 
      loading: loadingMostBookedEventsRelationSession 
    } = useLazyQuery(GET_RELATED_SESSIONS)

    watch(resultMostBookedEvents, value => {
      eventsListMostBooked.value = responseTransformer(value)
      const uri = value.events.map((e: any) => e.uri)
      loadMostBookedEventsRelationSession(undefined, { site: getSitesForGraphQl(), uri })
    })

    watch(resultMostBookedEventsRelationSession, (val: { relatedSession: RelatedSessionModel[] }) => {
      eventsListMostBooked.value = eventsListMostBooked.value.map(e => {
        return {... e, relatedSession: val.relatedSession.find(rs => rs.eventsId.map(i => i.id).includes(e.id))}
      })
    })

    const { 
      result: resultTodayEvents, 
      load: loadTodayEvents, 
      loading: loadingTodayEvents 
    } = useLazyQuery(GET_TODAY_EVENTS, { site: getSitesForGraphQl(), timeStart: '<' + new Date(), timeEnd: '>=' + new Date() }, { fetchPolicy: 'network-only' })

    const { 
      result: resultTodayEventsRelationSession, 
      load: loadTodayEventsRelationSession, 
      loading: loadingTodayEventsRelationSession 
    } = useLazyQuery(GET_RELATED_SESSIONS)

    watch(resultTodayEvents, value => {
      eventsListForToday.value = responseTransformer(value)
      const uri = value.events.map((e: any) => e.uri)
      loadTodayEventsRelationSession(undefined, { site: getSitesForGraphQl(), uri })
    })

    watch(resultTodayEventsRelationSession, (vall: { relatedSession: RelatedSessionModel[] }) => {
      eventsListForToday.value = eventsListForToday.value.map(e => {
        return {... e, relatedSession: vall.relatedSession.find(rs => rs.eventsId.map(i => i.id).includes(e.id))}
      })
    })

    const loadQueries = () => {
      if (eventAllSearchParams) {
        const paramsObj: any = parseQueryParams(eventAllSearchParams)
        const queryObj = {
          site: paramsObj.forum?.length ? paramsObj.forum.filter((f: string) => getSitesForGraphQl().includes(f)) : getSitesForGraphQl(),
          section: ['event'],
          timeStart: paramsObj.date ? '< ' + new Date(convertDatetimeToDate(new Date(paramsObj.date)).getTime() + 86400000).toISOString() : null,
          timeEnd: paramsObj.date ? '>= ' + new Date(convertDatetimeToDate(new Date(paramsObj.date)).getTime()).toISOString() : null,
          relatedTo: paramsObj.speakers?.length ? speakers.value.filter(s => paramsObj.speakers.includes(s.title)).map(s => s.id) : null,
          offset: paramsObj.offset ? +paramsObj.offset : null,
          limit: paramsObj.limit ? +paramsObj.limit : null,
          orderBy: paramsObj.order ? paramsObj.order : null
        }

        loadFilteredEvents(undefined, queryObj)
      } else {
        const queryObj: queryObj = {
          site: getSitesForGraphQl(),
          section: ['event'],
          timeStart: null,
          timeEnd: null,
          relatedTo: null,
          offset: null,
          limit: null,
          orderBy: null
        }
        loadFilteredEvents(undefined, queryObj)
      }

      loadMostBookedEvents(undefined, { site: getSitesForGraphQl() })
      loadTodayEvents(undefined, {
        site: getSitesForGraphQl(),
        timeStart: '<' + convertDatetimeToDate(new Date(Date.now() + (3600 * 1000 * 24)), true).toISOString(),
        timeEnd: '>=' + convertDatetimeToDate(new Date(Date.now()), true).toISOString()
      })
    }

    const parseQueryParams = (sladerNameSearchParams: string): queryObj => {
      const splitedString = sladerNameSearchParams.split('&').map(i => i.trim())
      const paramsObj = splitedString.reduce((acc: any, val: string) => {
        const [propName, value] = val.split('=')
        if (propName === 'speakers') {
          const speakers = value.split(',').map(s => s.replace('_', ' '))
          acc[propName] = speakers
        } else if (propName === 'forum') {
          let forums = value.split(',').map(s => s.trim())
          const appendLang = (siteId: string) => {
            switch (currentLanguage.value) {
              case 'de':
                return siteId + 'De'
              default:
                return siteId + 'En'
            }
          }
          forums = forums.map(appendLang)
          acc[propName] = forums
        } else {
          acc[propName] = value
        }

        return acc
      }, {})

      return paramsObj
    }

    const loading = computed(() => loadingFilteredEvents || loadingMostBookedEvents || loadingTodayEvents || loadingSpeakers)

    const toggleList = () => {
      if (currentListType.value === appointmentListType.future) {
        currentListType.value = appointmentListType.past
      } else {
        currentListType.value = appointmentListType.future
      }
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

    // const responseTransformer = (val: {events: EventModel[], globalSets: any}): any => {
    //   return val.events.map(e => { return {...e, siteGlobalLocation: val.globalSets.find((gs: any) => gs.siteId === e.siteId )} });
    // }

    const goToSearch = (text: string) => {
      if (isWidgetMode) {
        changePage('SearchView', { text })
      } else {
        router.push(`/search/${text}`)
      }
    }

    useEventListener(window, FinderEvents.dataEditComplete, (event: CustomEvent) => {
      if (editorsTriggeringReload.includes(event.detail)) {
        appointmentsStore.fetchAppointmentsLists(true)
      }
    })

    return {
      t,
      pageSegment,
      searchFieldIsHide,
      appointmentList,
      filteredAppointmentList,
      appointmentDetails,
      showModal,
      menuItems,
      toggleBtnLbl,
      showToggleBtn,
      currentListType,
      loading,
      toggleList,
      filteredEvents,
      eventsListMostBooked,
      eventsListForToday,
      goToSearch,
      PageSegment
    }
  }
})
</script>

<style lang="scss" scoped>
@import 'src/assets/styles/04-common/module-title';

.search__header-row__title {
  display: block;
  @media (max-width: 480px) {
    display: none;
  }
}

.events {

  &__header-row {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;

    &__title {
      font-size: 24px;
      font-weight: 600;
      color: var(--color-text-primary);
    }

    &__count {
      @media (max-width: 480px) {
        display: none;
      }
      padding-bottom: 12px;
      padding-right: 12px;
    }
  }

  &__module-title {
    @include finder-module-title;
    font-size: 3rem;
    line-height: 1;
    color: #006eb7;
    margin: 1.5rem;
  }

  &__section {
    padding: 24px 0;

    &.even {
      background-color: #e4e4e4;
    }
  }

  &__section + &__section {
    border-top: 1px solid #ffffff;
  }

  &__sub_title {
    @include finder-module-sub-title;
    @media (max-width: 480px) {
      margin-left: 5px;
      padding: 5px;
    }
  }
}

</style>
