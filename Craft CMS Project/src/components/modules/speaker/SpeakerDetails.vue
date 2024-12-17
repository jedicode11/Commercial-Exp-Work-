<template>
  <Loader :loading="loading.value" />
  <section class="speaker-details finder-normalize finder-content-wrapper">
    <div class="finder-content-wrapper__inner">
      <div class="speaker-details__section">
        <BackButton v-if="widgetMode?.toLowerCase() !== 'stele'" :icon-name="SvgIconName.arrowCircleLeft" />
      </div>
    </div>
    <div class="finder-content-wrapper__inner" v-if="speaker">
      <div class="speaker-details__section speaker-details__summary">
        <div class="speaker-details__summary__media">
          <div class="speaker__frame">
            <img v-if="speaker.speakerAvatar?.length" :src="speaker.speakerAvatar[0]?.url"
              :alt="speaker.speakerAvatar[0]?.alt" :title="speaker.speakerAvatar[0]?.alt" />
            <img v-else :src="hostname + 'img/no_image_icon.png'" />
          </div>
        </div>
        <div class="speaker-details__summary__details">
          <h2 class="speaker-details__summary__details__company">{{ speaker.company }}</h2>
          <h3 class="speaker-details__summary__details__names negative-margin">{{ speaker.speakerTitle ? speaker.speakerTitle : ' ' }} {{ speaker.speakerFirstName }} {{ speaker.speakerLastName }}</h3>
          <h3 class="speaker-details__summary__details__position">{{ speaker.speakerJobTitle }}</h3>
          <div class="speaker-details__summary__details__bio" v-html="speaker.speakerBio"></div>
        </div>
      </div>
    </div>
    <div class="finder-content-wrapper__inner">
        <BackButton v-if="widgetMode?.toLowerCase() === 'stele'" class="back-btn" :icon-name="SvgIconName.arrowCircleLeft" />
      <div class="speaker-details__section speaker-details__speaker_events">
      <Slider
        :sliderTitle="`${t('components_speaker_subtitle_events')} ${speaker?.speakerTitle ? (speaker?.speakerTitle + ' ') : ''}${speaker?.speakerFirstName} ${speaker?.speakerLastName}`"
        :eventsList="sortedEvents"/>
      </div>
    </div>
  </section>
</template>

<script lang="ts">
import { defineComponent, ref, watchEffect, watch, onMounted, computed, inject } from 'vue'
import { useI18n } from 'vue-i18n'
import { useAppointments } from '@/store/appointments'
import { EditorIds } from '@/composables/vis-modules/myAdmin'
import { useEventListener } from '@vueuse/core'
import { FinderEvents } from '@/utils/events'
import { Appointment, AppointmentBase, AppointmentType, createAppointment } from '@/models/Appoinment'
import { getAppointmentState } from '@/utils/appointmentState'
import { SpeakerModel } from '@/models/SpeakerModel'
import { SvgIconName } from '@/components/helpers/SvgIcon.vue'
import { useRoute } from 'vue-router'
import { EventModel } from '@/models/EventModel'
import Loader from '@/components/elements/Loader.vue'
import { useLazyQuery } from '@vue/apollo-composable'
import gql from 'graphql-tag'
import { useRouterStore } from '@/store/router'
import { storeToRefs } from 'pinia'
import { cloneDeep } from 'lodash'
import { useLanguageStore } from '@/store/language'
import { useWidgetMode } from '@/composables/useWidgetMode'
import { useSiteId } from '@/composables/useSiteId'
import { useSessionStoreHistory } from '@/composables/useSessionStoreHistory'
import Slider from '@/components/elements/Slider.vue'
import BackButton from '@/components/elements/BackButton.vue'
import { responseTransformer } from '@/composables/useLocation'
import { GET_SPEAKER_EVENTS } from '@/graphql/queries/getSpeakerEvents'

export enum appointmentListType {
  future = 'listFuture',
  past = 'listPast'
}
export default defineComponent({
  name: 'SpeakerDetails',
  components: {
    Loader,
    Slider,
    BackButton
  },
  setup () {
    const { isWidgetMode } = useWidgetMode()
    const widgetMode = inject<string | undefined>('app.widgetMode')
    const origin = inject('app.origin')

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

    const uri = ref<string | string[]>()
    const speaker = ref<SpeakerModel>()

    const route = useRoute()
    const routerStore = useRouterStore()
    const { currentParams } = storeToRefs(routerStore)

    const globalSets = ref()

    let parentUrl: string | undefined
    let parentSpeakerFragment: string | undefined
    if (isWidgetMode) {
      parentUrl = inject<string | undefined>('app.parentUrl')
      parentSpeakerFragment = inject<string | undefined>('app.parentSpeakerFragment')
      parentUrl && parentSpeakerFragment && console.info('parent speaker fragment:', parentSpeakerFragment)
    }

    let startPage: string | undefined
    let detailsId: string | undefined
    if (isWidgetMode) {
      startPage = inject<string | undefined>('app.startPage')
      detailsId = inject<string | undefined>('app.detailsId')
      startPage && console.info('preloading details Id:', detailsId)
    }

    const eventsListBySpeaker = ref<Array<EventModel>>([])

    appointmentsStore.fetchAppointmentsLists().then(() => {
      if (appointmentsStore.appointmentsLists[appointmentListType.future].length === 0 && appointmentsStore.appointmentsLists[appointmentListType.past].length > 0) {
        toggleList()
      }
    })

    const hostname = computed(() => {
      if (isWidgetMode) {
        return origin + '/'
      }

      return '/'
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
        uri.value = route?.params?.uri ?? ''
      }
    }

    if (isWidgetMode) {
      watch(currentParams, () => {
        setUri()
        loadQueries()
      })
    } else {
      watch(route, () => {
        setUri()
        loadQueries()
      })
    }

    onMounted(() => {
      console.log('🚀 ~ SpeakerDetails ~ onMounted:')
      setUri()
      loadQueries()
      useSessionStoreHistory().append()
    })

    const { result: resultsSpeaker, load: loadSpeaker, loading: loadingSpeaker } = useLazyQuery(gql`
      query getSpeaker ($site: [String]!, $uri:  [String])  {
        speaker: entry(site: $site, section: "person", type: "default", uri: $uri) {
          ...on person_default_Entry {
            id
            speakerFirstName: personFirstName
            speakerLastName: personLastName
            company: personCompany
            speakerTitle: personTitle
            speakerJobTitle: personJobTitle
            speakerAvatar: personAvatar {
              url @transform(handle: "speakerImage")
              alt
            }
            speakerBio: personDescription
          }
        }
      }
    `, { site: getSitesForGraphQl(), uri: uri.value }, { fetchPolicy: 'network-only' })

    watch(resultsSpeaker, value => {
      speaker.value = cloneDeep(value.speaker)
      scrollToTop()
      const variables = {
        site: getSitesForGraphQl(),
        speackerId: speaker.value?.id ?? null,
        startDateFilter: '>=' + (new Date().getTime() / 1000)
      }
      loadEventsBySpeaker(undefined, variables)
    })

    const { result: resultEventsBySpeaker, load: loadEventsBySpeaker, loading: loadingEventsBySpeaker } = useLazyQuery<any, {
      site?: string[] | null
      speackerId?: number | null
      startDateFilter?: string | null
    }
    >(GET_SPEAKER_EVENTS)

    watch(resultEventsBySpeaker, value => {
      globalSets.value = value.globalSets
      eventsListBySpeaker.value = [...value.events].map(e => {
        let rs
        value.relatedSessions.forEach((session: any) => {
          if (session.eventsIds.map((i: any) => i.id).includes(e.id)) {
            rs = session.sessionLocationHallPlan
          }
        })

        return { ...e, relatedSession: { sessionLocationHallPlan: rs } }
      })
    })

    const sortedEvents = computed(() => {
      let arr: any[] = []
      if (eventsListBySpeaker.value) {
        let ev = [...eventsListBySpeaker.value]
        ev = responseTransformer({ events: ev as EventModel[], globalSets: globalSets.value })
        arr = ev.sort((a, b) => new Date(a.startEventDatetime).getTime() - new Date(b.startEventDatetime).getTime())
      }
      return arr
    })

    const loadQueries = () => {
      loadSpeaker(undefined, { site: getSitesForGraphQl(), uri: uri.value })
    }

    const loading = computed(() => loadingSpeaker || loadingEventsBySpeaker)

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

    const scrollToTop = () => {
      window.scrollTo({
        top: 0,
        behavior: 'smooth'
      })
    }

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
      widgetMode,
      hostname,
      scrollToTop,
      toggleList,
      loading,
      speaker,
      SvgIconName,
      eventsListBySpeaker,
      sortedEvents
    }
  }
})
</script>

<style lang="scss" scoped>
@import 'src/assets/styles/04-common/module-title';
.speaker-details__section {
  padding: 4px 0;
  margin-top: 24px;
}
.speaker-details {
  .speaker-details__summary {
    display: flex;
    gap: 10px 0;
    justify-content: space-between;

    @media screen and (max-width: 480px) {
      display: block;
    }
    .speaker-details__summary__media {
      .speaker__frame {
        border: 1px solid #d7d7d7;
        margin-bottom: 50px;
        border-radius: 10px;
        width: 300px;
        height: auto;
      }

      img {
        border-radius: 9px;
        width: 300px;
        height: auto;
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
    .speaker-details__summary__details {
      flex: 57%;
      max-width: 57%;
      @media only screen and (max-width: 480px) {
        /* For desktop: */
        flex: 100%;
        max-width: 100%;
        margin-top: 10px;
      }

      .speaker-details__summary__details__company {
        font-family: 'Source Sans Pro', sans-serif;
        font-size: 14px;
        font-weight: 700;
        line-height: 24px;
        color: rgba(24, 24, 24, 0.5);
        margin-bottom: 5px;
      }
      .speaker-details__summary__details__names {
        font-family: 'Source Sans Pro', sans-serif;
        font-size: 42px;
        font-weight: 300;
        line-height: 50.4px;
        white-space: normal;
      }
      .negative-margin {
        margin-left: -3px;
      }
      .speaker-details__summary__details__position {
        font-family: 'Source Sans Pro', sans-serif;
        font-size: 19px;
        font-weight: 400;
        line-height: 23px;
      }
      .speaker-details__summary__details__bio {
        font-family: 'Source Sans Pro', sans-serif;
        font-size: 16px;
        font-weight: 400;
        line-height: 27px;
      }
    }
  }
  .back-btn {
    margin-top: -20%;
  }
}
</style>
