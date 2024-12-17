<template>
  <Loader :loading="loading.value" />
  <section class="session-details finder-normalize finder-content-wrapper">

    <div class="finder-content-wrapper__inner">
      <div class="session-details__section">
        <BackButton v-if="widgetMode?.toLowerCase() !== 'stele'" :icon-name="SvgIconName.arrowCircleLeft" />
      </div>
    </div>

    <div class="finder-content-wrapper__inner" v-if="session">
      <div class="session-details__section session-details__summary">
        <div class="session-details__summary__media">
          <div class="session-details__summary__media__images">
            <img :src="session.image[0]?.url ?? '/images/image-placeholder.jpg'" :alt="session.image[0]?.alt ?? ''"
              :title="session.image[0]?.alt ?? ''" />
            <div class="session-details__summary__media__images__overlay"></div>
            <div class="session-details__summary__media__images__details">
              <EventCardDate class="event-card-date" v-if="date" :date="date" />
              <EventCardHourSpan class="event-card-hourspan" v-if="from && to" :from="from" :to="to" />
              <!-- <div v-if="locationActivator()">
                <EventCardLocation class="event-card-location" v-if="session.locationHallPlan[0]?.title"
                :location="`${session.locationHallPlan[0]?.title} ${session.locationHallPlan[1]?.title}`"
                @click="goToLocationLink(`${session.locationHallPlan[0]?.locationNumber}`, `${session.locationHallPlan[1]?.locationNumber}`)" />
              </div> -->
              <div v-if="locationActivator()">
                <EventCardLocation class="event-card-location" v-if="getLocationHallPlan.length"
                :location="getLocationTitle"
                @click="goToLocationLink()" />
              </div>
              <div v-else>
                <EventCardLocation class="event-card-location old-location" v-if="session.location" :location="session.location" />
              </div>
              <EventCardLanguage class="event-card-language" v-if="session.language" :language="session.language" />
            </div>
            <div v-if="widgetMode?.toLowerCase() === 'stele'" class="back-btn">
              <BackButton :icon-name="SvgIconName.arrowCircleLeft" />
            </div>
          </div>
        </div>

        <div class="session-details__summary__details">
          <div class="session-details__summary__details__chips">
            <Chip v-if="session.place === 'onSite'" :icon="SvgIconName.locationOn" :text="t('components_event_chip_onsite')" :hasShadow="false" />
            <Chip v-if="session.place === 'remote'" :icon="SvgIconName.headphones" :text="t('components_event_chip_remote')" :hasShadow="false" />
            <Chip v-if="session.chargeable.length && session.chargeable.includes('chargeable')" :icon="SvgIconName.euroCurrency" :text="t('components_event_chip_chargeable')" :hasShadow="false" />
            <Chip v-if="session.place === 'webSession'" :icon="SvgIconName.webSession" :hasShadow="false" :text="$t('components_event_chip_websession')" />
          </div>

          <h2 class="session-details__module-title">{{ session.title }}</h2>

          <div class="session-details__summary__details__description" v-html="session.content"></div>

          <!-- Yasen Temporary Change Exhibitor Place -->
          <div v-if="session?.exhibitor.length" class="session-details__section event-details__exhibitor">
            <div class="exhibitor-container">
              <h3 class="session-details__sub_title">{{ t('components_event_subtitle_exhibitor') + ':' }}</h3>
              <Exhibitor :link="session.exhibitor[0]?.exhibitorLink"
                :logo="session.exhibitor[0]?.exhibitorFeatureImage" :name="session.exhibitor[0]?.exhibitorName"
                :address="session.exhibitor[0]?.exhibitorAddress" />
            </div>
          </div>

          <div class="session-details__summary__details__bottom-row">
            <share-button v-if="!(widgetMode?.toLowerCase() === 'stele')" :id="`${session.uri}`"
              :subject="session.title"></share-button>
            <!-- <BookmarkEvent v-if="!(widgetMode?.toLowerCase() === 'stele')" :id="`${session.uid}`"></BookmarkEvent> -->
            <div v-if="locationActivator()">
              <p class="session-details__summary__details__location"
              v-if="getLocationHallPlan.length"
              :title="getLocationTitle"
              @click="goToLocationLink()">
              {{ getLocationTitle }}</p>
            </div>
            <div v-else>
              <p class="session-details__summary__details__location old-location" :title="session.location"
              @click="$event.preventDefault()">{{ session.location }}</p>
            </div>
          </div>

          <div class="session-details__section event-details__speakers" v-if="session.chairs.length">
            <h3 class="session-details__sub_title">{{ t('components_event_subtitle_chairs') }}</h3>
            <SpeakersList
              :speakersList="session.chairs.sort((a, b) => (a.speakerLastName > b.speakerLastName) ? 1 : ((b.speakerLastName > a.speakerLastName) ? -1 : 0))">
            </SpeakersList>
          </div>

          <div class="session-details__section event-details__speakers" v-if="session.moderators.length">
            <h3 class="session-details__sub_title">{{ t('components_event_subtitle_moderators') }}</h3>
            <SpeakersList
              :speakersList="session.moderators.sort((a, b) => (a.speakerLastName > b.speakerLastName) ? 1 : ((b.speakerLastName > a.speakerLastName) ? -1 : 0))">
            </SpeakersList>
          </div>

          <!-- <div v-if="session?.exhibitor.length" class="session-details__section event-details__exhibitor">
            <h3 class="session-details__sub_title">{{ t('components_event_subtitle_exhibitor') }}</h3>
            <Exhibitor :link="widgetMode?.toLowerCase() === 'stele' ? '' : session.exhibitor[0].exhibitorUrl"
              :logo="session.exhibitor[0].exhibitorLogo" :name="session.exhibitor[0].exhibitorTitle"
              :address="session.exhibitor[0].exhibitorAddress" />
          </div> -->
        </div>
      </div>

      <div class="session-details__section session-details__sub_title">
        <Slider :eventsList="sortedEvents" :sliderTitle="t('components_event_subtitle_session_events')"
          :sessionName="session?.title" />
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
import { SvgIconName } from '@/components/helpers/SvgIcon.vue'
import { useRoute } from 'vue-router'
import EventCardDate from '@/components/elements/EventCardDate.vue'
import EventCardHourSpan from '@/components/elements/EventCardHourSpan.vue'
import EventCardLocation from '@/components/elements/EventCardLocation.vue'
import EventCardLanguage from '@/components/elements/EventCardLanguage.vue'
import Exhibitor from '@/components/elements/Exhibitor.vue'
import Chip from '@/components/elements/Chip.vue'
import Loader from '@/components/elements/Loader.vue'
import SpeakersList from '@/components/modules/event-details/SpeakersList.vue'
import { useLazyQuery } from '@vue/apollo-composable'
import { useRouterStore } from '@/store/router'
import { storeToRefs } from 'pinia'
import { useLanguageStore } from '@/store/language'
import { useWidgetMode } from '@/composables/useWidgetMode'
import { SessionModel } from '@/models/Session'
import { useSiteId } from '@/composables/useSiteId'
import { useSessionStoreHistory } from '@/composables/useSessionStoreHistory'
import Slider from '@/components/elements/Slider.vue'
import BackButton from '@/components/elements/BackButton.vue'
import { GET_SESSION_DETAILS } from '@/graphql/queries/sessionDetails'
import { SiteGlobalLocationModel } from '@/models/SiteGlobalLocationModel'
import { useLocation, responseTransformer } from '@/composables/useLocation'
import { SingleEventRelatedSessionModel } from '@/models/SingleEventRelatedSessionModel'
import { EventModel } from '@/models/EventModel'

export enum appointmentListType {
  future = 'listFuture',
  past = 'listPast'
}

export default defineComponent({
  name: 'SessionDetails',
  components: {
    Loader,
    EventCardDate,
    EventCardHourSpan,
    EventCardLocation,
    EventCardLanguage,
    Exhibitor,
    SpeakersList,
    Chip,
    Slider,
    BackButton
  },
  setup () {
    const { isWidgetMode } = useWidgetMode()
    const widgetMode = inject<string | undefined>('app.widgetMode')
    const locationActivated = inject<string | undefined>('app.locationHallPlanActivated')

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

    const uri = ref<string>()
    const session = ref<SessionModel | null>()
    const siteGlobalLocation = ref<SiteGlobalLocationModel | null>()
    const relatedSession = ref<SingleEventRelatedSessionModel | null>()

    const globalSets = ref()

    const route = useRoute()
    const routerStore = useRouterStore()
    const { currentParams } = storeToRefs(routerStore)

    const { goToLocationLink, getLocationTitle, getLocationHallPlan } = useLocation(session, relatedSession)

    let parentUrl: string | undefined
    let parentSessionFragment: string | undefined
    if (isWidgetMode) {
      parentUrl = inject<string | undefined>('app.parentUrl')
      parentSessionFragment = inject<string | undefined>('app.parentSessionFragment')
      parentUrl && parentSessionFragment && console.info('parent session fragment:', parentSessionFragment)
    }

    let startPage: string | undefined
    let detailsId: string | undefined
    if (isWidgetMode) {
      startPage = inject<string | undefined>('app.startPage')
      detailsId = inject<string | undefined>('app.detailsId')
      startPage && console.info('preloading details Id:', detailsId)
    }

    appointmentsStore.fetchAppointmentsLists().then(() => {
      if (appointmentsStore.appointmentsLists[appointmentListType.future].length === 0 && appointmentsStore.appointmentsLists[appointmentListType.past].length > 0) {
        toggleList()
      }
    })

    const sortedEvents = computed(() => {
      let arr: any[] = []
      if (session.value && session.value.events) {
        let ev = [...session.value.events].map(e => {
          return { ...e, relatedSession: { sessionLocationHallPlan: session.value?.locationHallPlan }}
          })
        ev = responseTransformer({ events: ev as EventModel[], globalSets: globalSets.value })
        arr = ev.sort((a, b) => new Date(a.startEventDatetime).getTime() - new Date(b.startEventDatetime).getTime())
      }
      return arr
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
          uri.value = currentParams.value?.uri ?? ''
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
      console.log('🚀 ~ SessionDetails ~ onMounted')
      setUri()
      loadQueries()
      useSessionStoreHistory().append()
    })

    const eventsListBySessions = ref<Array<SessionModel>>([])

    const { result: resultSession, load: loadSession, loading: loadingSession } = useLazyQuery(GET_SESSION_DETAILS)

    watch(resultSession, value => {
      session.value = value.entry
      siteGlobalLocation.value = value.globalSets.find((gs: any) => gs.siteId === session.value?.siteId)
      globalSets.value = value.globalSets
    })

    const loadQueries = () => {
      loadSession(undefined, { site: getSitesForGraphQl(), uri: uri.value })
    }

    const loading = computed(() => loadingSession || eventsListBySessions)

    const date = computed((): Date | null | undefined => {
      return session.value ? (session.value.startTime ? new Date(session.value.startTime) : null) : undefined
    })
    const from = computed((): Date | null | undefined => {
      return session.value ? (session.value.startTime ? new Date(session.value.startTime) : null) : undefined
    })
    const to = computed((): Date | null | undefined => {
      return session.value ? (session.value.endTime ? new Date(session.value.endTime) : null) : undefined
    })

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
      // the following line is necessary because right now we become null instead of empty array if listPast/listFuture is empty
      appointmentList.value = appointmentsStore.appointmentsLists[currentListType.value] !== null ? appointmentsStore.appointmentsLists[currentListType.value] : []

      showToggleBtn.value = appointmentsStore.appointmentsLists.listPast.length > 0 && appointmentsStore.appointmentsLists.listFuture.length > 0
      toggleBtnLbl.value = currentListType.value === appointmentListType.future ? t('components_appointment_toggle_bt_lbl_show_past') : t('components_appointment_toggle_bt_lbl_show_future')

      appointmentsStore.selectedAppointmentFilter = []
    })

    watchEffect(() => {
      if (appointmentsStore.selectedAppointmentFilter.length > 0) {
        filteredAppointmentList.value = appointmentList.value.filter(appointment => {
          // api delivers three appointment types: activity, videoconf and websession. roundtable is not an ordinary type.
          // so we cannot simply check for appointment type roundtable.
          // is the appointment type one of the ordinary types activity, videoconf or websession?
          let includeInFilter = appointmentsStore.selectedAppointmentFilter.includes(appointment.type)
          // if appointment type is websession, verify that isMeeting is false, because websession AND isMeeting equals true
          // is of type AppointmentType.roundtable.
          if (includeInFilter && appointment.type === AppointmentType.websession && appointment.isMeeting) {
            includeInFilter = false
          }
          // if this aapointment is not included, test if it is a roundtable.
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

    const locationActivator = () => locationActivated === 'true'

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
      sortedEvents,
      session,
      SvgIconName,
      date,
      from,
      to,
      goToLocationLink,
      locationActivator,

      getLocationHallPlan,
      getLocationTitle
    }
  }
})
</script>

<style lang="scss" scoped>
@import 'src/assets/styles/04-common/module-title';

.exhibitor-container {
  display: flex;
  gap: 10px;
  align-items: baseline;
}

.back-btn {
  position: absolute;
  bottom: -60px;
}

.session-details__summary__details__location {
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

.session-details {
  .session-details__module-title {
    font-family: 'Source Sans Pro', sans-serif;
    font-size: 42px;
    font-weight: 300;
    line-height: 50px;
    letter-spacing: 0px;
    text-align: left;
  }
  .session-details__section {
    padding: 4px 0;
    margin-top: 24px;
  }
  .session-details__sub_title {
    display: flex;
    gap: 3px;
    @include finder-module-sub-title;
    line-height: 166%;
    margin-top: 10px;
  }
  .session-details__summary {
    display: flex;
    gap: 10px 0;
    justify-content: space-between;
    .session-details__summary__media {
      flex: 37%;
      max-width: 37%;
      .session-details__summary__media__images {
        position: -webkit-sticky;
        position: sticky;
        top: 80px;
        margin-bottom: 80px;
        width: 100%;
        border-radius: 9px;
        z-index: 0;
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
        .session-details__summary__media__images__overlay {
          position: absolute;
          left: 0%;
          right: 0%;
          top: 0%;
          bottom: 0%;
          border-radius: 9px;
          background: linear-gradient(180deg, rgba(0, 0, 0, 0) 0%, rgba(0, 0, 0, 0.5) 100%);
        }
        .session-details__summary__media__images__details {
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

    .session-details__summary__details {
      flex: 57%;
      max-width: 57%;
      flex-direction: column;

      &__top-row,
      &__bottom-row {
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

      .session-details__summary__details__chips {
        display: flex;
        align-items: flex-end;
        gap: 6px;
        margin-bottom: 18px;
      }

      .session-details__summary__details__description {
        margin-bottom: 20px;
      }
    }
    @media (max-width: 480px) {
      flex-direction: column;
      gap: 20px 0;

      .session-details__summary__media,
      .session-details__summary__detailsx {
        flex: 100%;
        max-width: 100%;
      }
    }
  }
}
</style>
