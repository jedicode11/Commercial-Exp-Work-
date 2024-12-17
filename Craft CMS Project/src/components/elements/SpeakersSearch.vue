<template>
  <SearchField :text="query" :title="t('components_search_field_title')" z-showButton
    :placeholder="t('components_speakers_search_placeholder')" :buttonText="t('components_events_button_start_search')"
    x-backgroundColor="#EDEDED" iconAsSuffix showIcon @search="handleSearch($event)" />
  <div class="search-filters-box">
    <div class="search-filters">
      <div class="search-filters-speaker flex flex-cols">
        <div v-if="showSiteDropdown" class="search-filters-type flex flex-cols">
          <p>{{ t('components_events_search_filter_by_forum') }}</p>
          <form-multi-select :options="filteredSites"
            :text="selectedSiteName.length > 0 ? selectedSiteName.join(', ') : t('components_events_search_filters__select_forum')"
            v-model="selectedSite" />
        </div>
      </div>
      <div class="search-filters-dayset">
        <p>{{ t('components_events_search_filters__datetime') }}</p>
        <form-select :options="eventDays" v-model="selectedEventDays"
          :placeholder="t('components_events_search_filters__select_day')" />
        <div class="search-filters-timeset">
          <checkbox :label="t('components_events_search_filters__datetime_morning')" value="morning"
            v-model="isTimeSetMorning" :isDisabled="!selectedEventDays" @click="setTimeSetMorning($event)" />
          <checkbox :label="t('components_events_search_filters__datetime_afternoon')" value="afternoon"
            v-model="isTimeSetAfternoon" :isDisabled="!selectedEventDays" @click="setTimeSetAfternoon($event)" />
        </div>
      </div>

      <div class="search-filters-reset">
        <div class="reset-btn">
          <cta-button is-secondary isSmall @click="resetFilters">{{ t('components_events_search_filters__reset') }}</cta-button>
        </div>
      </div>

    </div>
  </div>
  <span class="found-events">{{ entryCount }}</span>
</template>

<script lang="ts">
import SearchField from '@/components/elements/SearchField.vue'
import { useLazyQuery } from '@vue/apollo-composable'
import gql from 'graphql-tag'
import { computed, defineComponent, inject, onMounted, ref, toRef, watch } from 'vue'
import { compareAsc } from 'date-fns/esm'
import { useI18n } from 'vue-i18n'
import { useLanguageStore } from '@/store/language'
import { storeToRefs } from 'pinia'
import { useRoute } from 'vue-router'
import { isEqual } from 'lodash'
import CtaButton, { CtaButtonType } from '@/components/elements/CtaButton.vue'
import Checkbox from '@/components/elements/Checkbox.vue'
import { SvgIconName } from '@/components/helpers/SvgIcon.vue'
import FormSelect, { FormSelectOption } from '@/components/elements/forms/FormSelect.vue'
import FormMultiSelect from '@/components/elements/forms/FormMultiSelect.vue'
import { useSiteId } from '@/composables/useSiteId'
import { utcToZonedTime, format as f } from 'date-fns-tz'
import { GET_SITE_NAME } from '@/graphql/queries/siteName'
import moment from 'moment-timezone'

// import { utcToZonedTime } from 'date-fns-tz';

export interface queryObj {
  site: string[] | null | undefined,
  textFilter: string | null,
  id: string[] | null,
  section: string[],
  relatedTo: string[] | null,
  offset: number | null,
  limit: number | null,
  orderBy: string | null
}

export interface queryEventIdsObj {
  site: string[] | null | undefined,
  section: string[],
  timeStart: string[] | null,
  // timeEnd: string | null,
}

export interface site {
  name: string,
  handle: string
}

export interface SpeakerOverviewEventModel {
  id: string
  speaker: Array<{id: number}>
  startEventDatetime: string,
  endEventDatetime: string
}

const initialLimit = 10
const defaultLimit = 5
const initialOffset = 0
const offsetIncrement = 5

export default defineComponent({
  name: 'SpeakersSearch',
  components: {
    SearchField,
    CtaButton,
    Checkbox,
    FormSelect,
    FormMultiSelect
  },
  props: {
    text: {
      type: String,
      required: true,
      default: ''
    },
    infiniteScrollButtonId: {
      type: String,
      required: false
    }
  },
  setup (props, { emit }) {
    const widgetMode = inject<string | undefined>('app.widgetMode')

    const text = toRef(props, 'text')
    const infiniteScrollButtonId = toRef(props, 'infiniteScrollButtonId')

    const { t, locale } = useI18n()

    const route = useRoute()

    const languageStore = useLanguageStore()
    const { currentLanguage } = storeToRefs(languageStore)
    const { setLanguage } = languageStore

    const { getSitesForGraphQl } = useSiteId()

    const querySections = ['event']

    const searchFiltersVisible = ref(true)
    const query = ref<string>('')

    const speakers = ref<any[]>([])
    const events = ref<SpeakerOverviewEventModel[]>([])

    const modal = ref()

    const sites = ref<site[]>([])
    const selectedSite = ref<string[]>([])
    const focusedSite = ref<boolean>(false)
    const inputSearchSite = ref<HTMLElement>()
    const searchSite = ref<string>('')

    const timeSet = ref<string | number | undefined>(undefined)
    const dateFrames = ref<{ startEventDatetime: string, endEventDatetime: string }[]>([])
    const eventDays = ref<FormSelectOption[]>([])
    const selectedEventDays = ref<string>()

    const limit = ref(initialLimit)
    const offset = ref(initialOffset)
    const loadCouter = ref(0)

    const entryCount = ref<number>(0)

    const infiniteScrollDistancePx = 20

    const setQuery = () => {
      if (text.value) {
        query.value = Array.isArray(text.value) ? text.value[0] : text.value
      } else {
        query.value = Array.isArray(route?.params?.text) ? route?.params?.text[0] : route?.params?.text ?? ''
      }
    }

    const getSites = (): string[] => {
      return selectedSite.value.length ? selectedSite.value : getSitesForGraphQl()
    }

    const showSiteDropdown = computed((): boolean => {
      return getSitesForGraphQl().length > 1
    })

    const clearAllresult = () => {
      resetInvisibleScroll()
      speakers.value = []
    }

    watch(locale, () => {
      setLanguage(locale.value)
      loadQueries()
    })

    watch(route, () => {
      setQuery()
      loadQueries()
    })

    const filteredSites = computed<FormSelectOption[]>(() => {
      return sites.value.map(s => ({ label: s.name, value: s.handle }))
    })

    onMounted(() => {
      setQuery()
      loadSites(undefined, { site: getSites() })
      loadEventDays()
      loadQueries()

      window.addEventListener('scroll', () => {
        infiniteScroll()
      })
      window.addEventListener('resize', () => {
        infiniteScroll()
      })
      infiniteScroll()
    })

    const selectedSiteName = computed(() => {
      const names: string[] = []
      sites.value.forEach(s => {
        if (selectedSite.value.includes(s.handle)) {
          names.push(s.name)
        }
      })
      return names
    })

    watch(selectedSite, () => {
      clearAllresult()
      loadEventDays(undefined, { site: getSites(), section: querySections })
      loadQueries()
    })

    const infiniteScroll = () => {
      const loadingButtonEl = document.querySelector<HTMLElement>(`#infinite-scroll-button-${infiniteScrollButtonId.value}`)
      if (loadingButtonEl &&
        loadingButtonEl.getBoundingClientRect().top < document.documentElement.clientHeight + infiniteScrollDistancePx &&
        !(loadingButtonEl as HTMLButtonElement).disabled
      ) {
        disableInfiniteScrollButton()
        loadQueries()
      }
    }
    const disableInfiniteScrollButton = () => {
      const loadingButtonEl = document.querySelector<HTMLElement>(`#infinite-scroll-button-${infiniteScrollButtonId.value}`)
      if (loadingButtonEl) {
        (loadingButtonEl as HTMLButtonElement).disabled = true
      }
    }
    const enableInfiniteScrollButton = () => {
      const loadingButtonEl = document.querySelector<HTMLElement>(`#infinite-scroll-button-${infiniteScrollButtonId.value}`)
      if (loadingButtonEl) {
        (loadingButtonEl as HTMLButtonElement).disabled = false
      }
    }

    const { result: resultSites, load: loadSites } = useLazyQuery(GET_SITE_NAME, { site: getSites() })

    watch(resultSites, val => {
      sites.value = val.sites
    })

    const { result, load, loading, refetch } = useLazyQuery(gql`
      query getSpeakers (
        $site: [String]!,
        $section: [String]!,
        $relatedTo: [QueryArgument],
        $textFilter: String,
        $id: [QueryArgument]
        $limit: Int,
        $offset: Int,
        $orderBy: String
        ) {
        speakers: entries(
          site: $site,
          section: $section,
          type: "default",
          relatedTo: $relatedTo,
          search: $textFilter,
          id: $id
          limit: $limit,
          offset: $offset,
          orderBy: $orderBy
          ) {
          ...on person_default_Entry {
            id
            uri
            speakerName: title
            speakerFirstName: personFirstName
            speakerLastName: personLastName
            company: personCompany
            speakerTitle: personTitle
            speakerJobTitle: personJobTitle
            # speakerPosition: eventCategories {
            #   title
            # }
            speakerAvatar: personAvatar {
              url
              alt
            }
            speakerBio: personDescription
          }
        }
        entryCount (
          site: $site,
          section: $section,
          type: "default",
          relatedTo: $relatedTo,
          search: $textFilter,
        )
      }
    `)

    watch(result, val => {
      if (val) {
        if (val.speakers.length < limit.value) {
          emit('showLoadBtn', false)
        } else {
          emit('showLoadBtn', true)
        }

        if (loadCouter.value) {
          limit.value = defaultLimit
          offset.value += offsetIncrement
        } else {
          limit.value = defaultLimit
          offset.value += initialLimit
        }
        loadCouter.value += 1

        speakers.value = speakers.value.concat([...val.speakers])

        // preventing repeat events
        const speakersAsMap = new Map(speakers.value.map(e => [e.id, e]))
        speakers.value = [...speakersAsMap.values()]

        entryCount.value = val.entryCount
        emit('speakersCount', entryCount.value)

        enableInfiniteScrollButton()
      }
    })

    function filterSpeakers () {
      const fs = [...speakers.value]

      if (searchFiltersVisible.value) {
      }
      return fs
    }

    watch([speakers], () => {
      const filteredSpeakers = filterSpeakers()

      emit('results', filteredSpeakers)
    })

    const { result: resultEventDays, load: loadEventDays } = useLazyQuery(gql`
      query getEventDays ($site: [String]!, $section: [String!]) {
        dateFrames: entries (
          site: $site
          section: $section
          type: "default"
          speaker: ":notempty:"
          ) {
          ...on session_default_Entry {
            startEventDatetime: sessionStartTime
            endEventDatetime: sessionEndTime
          }
          ...on event_default_Entry {
            startEventDatetime: timeStart
          }
        }
      }
      `, { site: getSites(), section: querySections })

    watch(resultEventDays, val => {
      dateFrames.value = val.dateFrames
    })

    const { result: resultEvents, load: loadEvents, refetch: refetchEvents } = useLazyQuery(gql`
      query getEvents (
        $site: [String]!,
        $section: [String]!,
        $timeStart: [QueryArgument],
        $timeEnd: [QueryArgument],
        ) {
        events: entries(
          site: $site,
          section: $section,
          type: "default",
          timeStart: $timeStart,
          timeEnd: $timeEnd,
          ) {
          ...on event_default_Entry {
            id
            startEventDatetime: timeStart
            endEventDatetime: timeEnd
            speaker {
              id
            }
          }
        }
      }
    `)

    watch(resultEvents, val => {
      let fe: any[] = []

      if (val && val.events) {
        fe = val.events
      }

      if (eventDays.value.length && timeSet.value) {
        const timeZone = 'Europe/Berlin'
        if (isTimeSetMorning.value && !isTimeSetAfternoon.value) {
          fe = fe.filter(e =>
            Number(f(utcToZonedTime(e.startEventDatetime, timeZone), 'HH', { timeZone })) >= 5 && Number(f(utcToZonedTime(e.startEventDatetime, timeZone), 'HH', { timeZone })) < 14
          )
        } else if (!isTimeSetMorning.value && isTimeSetAfternoon.value) {
          fe = fe.filter(e =>
            Number(f(utcToZonedTime(e.startEventDatetime, timeZone), 'HH', { timeZone })) >= 14
          )
        } else {
          fe = fe.filter(e =>
            (Number(f(utcToZonedTime(e.startEventDatetime, timeZone), 'HH', { timeZone })) >= 5 && Number(f(utcToZonedTime(e.startEventDatetime, timeZone), 'HH', { timeZone })) < 14) ||
            Number(f(utcToZonedTime(e.startEventDatetime, timeZone), 'HH', { timeZone })) >= 14
          )
        }

        if (!fe.length) {
          fe.push({ id: '0', startEventDatetime: '', endEventDatetime: '' })
        }
      }

      events.value = fe

      clearAllresult()
      loadQueries()
    })

    const queryObjOld = ref<queryObj>()

    const loadQueries = () => {
      const queryObjNew: queryObj = {
        site: getSites(),
        section: ['person'],
        id: null,
        textFilter: null,
        relatedTo: null,
        limit: limit.value,
        offset: offset.value,
        orderBy: 'personLastName ASC'
      }

      if (query.value) {
        queryObjNew.textFilter = query.value
      }

      if (events.value.length) {
        queryObjNew.relatedTo = events.value.map(e => e.id)
        const speakerIds = events.value.reduce((acc: any, val: any) => {
          const ids = val.speaker.map((s: any) => s.id)
          return [...acc, ...ids]
        }, [])
        queryObjNew.id = speakerIds
      }

      if (isEqual(queryObjNew, queryObjOld.value)) {
        refetch()
      } else {
        load(undefined, queryObjNew, { fetchPolicy: 'network-only' })
        queryObjOld.value = queryObjNew
      }
    }

    watch(loading, val => {
      emit('loading', val)
    })

    watch(dateFrames, val => {
      const ed = new Set<Date>()

      val.forEach(df => {
        const startDay = moment(df.startEventDatetime)
        startDay.tz('Europe/Berlin')
        startDay.startOf('day')
        ed.add(startDay.toDate())
      })

      const sortedEventDays = Array.from(new Set(Array.from(ed).sort(compareAsc).map(d => d.toISOString())))
      eventDays.value = sortedEventDays.map(d => {
        const label = new Date(d).toLocaleDateString('de-DE', { year: 'numeric', month: '2-digit', day: '2-digit' })

        return {
          label,
          value: d
        }
      })
    })

    watch(selectedEventDays, () => {
      loadEventsQuery()
    })

    // was that
    // const loadEventsQuery = () => {
    //   if (selectedEventDays.value) {
    //     const queryEventIdsObj: queryEventIdsObj = {
    //       site: getSites(),
    //       section: ['event'],
    //       timeStart: '< ' + new Date(convertDatetimeToDate(new Date(selectedEventDays.value)).getTime() + 86400000).toISOString(),
    //       // timeEnd: '>= ' + new Date(convertDatetimeToDate(new Date(selectedEventDays.value)).getTime()).toISOString()
    //     }

    //     loadEvents(undefined, queryEventIdsObj, { fetchPolicy: 'network-only' })
    //   }
    // }

    const queryEventIdsObjOld = ref<queryEventIdsObj>()

    const loadEventsQuery = () => {
      if (selectedEventDays.value) {
        const timeStart = new Date(new Date(selectedEventDays.value).getTime()).toISOString()
        const endStart = new Date(new Date(selectedEventDays.value).getTime() + 86400000).toISOString()
        const queryEventIdsObj: queryEventIdsObj = {
          site: getSites(),
          section: ['event'],
          timeStart: ['AND', '>= ' + timeStart, '< ' + endStart],
          // timeEnd: '>= ' + new Date(convertDatetimeToDate(new Date(selectedEventDays.value)).getTime()).toISOString()
        }

        if (isEqual(queryEventIdsObj, queryEventIdsObjOld.value)) {
          refetchEvents()
        } else {
          loadEvents(undefined, queryEventIdsObj, { fetchPolicy: 'network-only' })
          queryEventIdsObjOld.value = queryEventIdsObj
        }
      }
    }
    
    watch(timeSet, () => {
      refetchEvents()
    })

    const handleMoreFilterOptionsClick = (event: Event) => {
      event.preventDefault()
      searchFiltersVisible.value = true
    }

    const resetInvisibleScroll = () => {
      speakers.value = []
      limit.value = initialLimit
      offset.value = initialOffset
      loadCouter.value = 0
    }

    const handleSearch = (text: string) => {
      if (text === query.value) {
        resetInvisibleScroll()
        loadQueries()
      } else {
        resetInvisibleScroll()
        query.value = text
        emit('search', query.value)
        loadQueries()
      }
    }

    const isTimeSetMorning = ref<boolean>(false)
    const isTimeSetAfternoon = ref<boolean>(false)
    const setTimeSetMorning = (checked: boolean) => {
      if (!checked) {
        timeSet.value = undefined
        isTimeSetMorning.value = false
        isTimeSetAfternoon.value = false
      } else {
        timeSet.value = 'morning'
        isTimeSetMorning.value = true
        isTimeSetAfternoon.value = false
      }
    }
    const setTimeSetAfternoon = (checked: boolean) => {
      if (!checked) {
        timeSet.value = undefined
        isTimeSetMorning.value = false
        isTimeSetAfternoon.value = false
      } else {
        timeSet.value = 'afternoon'
        isTimeSetMorning.value = false
        isTimeSetAfternoon.value = true
      }
    }

    const resetFilters = () => {
      selectedSite.value = []
      events.value = []
      selectedEventDays.value = undefined
      isTimeSetMorning.value = false
      isTimeSetAfternoon.value = false
      handleSearch('')
    }

    return {
      t,
      currentLanguage,
      SvgIconName,
      query,
      searchFiltersVisible,

      handleSearch,
      modal,

      widgetMode,

      handleMoreFilterOptionsClick,
      showSiteDropdown,
      filteredSites,

      sites,
      focusedSite,
      inputSearchSite,
      searchSite,
      selectedSite,

      selectedSiteName,
      eventDays,
      selectedEventDays,

      entryCount,
      isTimeSetMorning,
      isTimeSetAfternoon,
      setTimeSetMorning,
      setTimeSetAfternoon,

      resetFilters,
      CtaButtonType
    }
  }
})
</script>

<style lang="scss" scoped>
.reset-btn {
  margin-top: 20px;
}

.search-filters-box {
  border-bottom: 1px solid #efefef;

  @media (max-width: 480px) {
    display: none;
  }
}
.search-filters:only-child {
  grid-template-rows: repeat(1, minmax(0, 1fr));
}

.search-filters {
  padding-top: 15px;
  display: inline-grid;
  width: 100%;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  grid-template-rows: repeat(2, minmax(0, 1fr));
  gap: 4px;
  padding-bottom: 20px;

  p {
    margin-bottom: 8px;
  }

  .flex {
    flex: 1;
    display: flex;
    gap: 0px 12px;
    align-items: flex-start;

    &.flex-cols {
      flex-direction: column;
    }

    & > div {
      flex-grow: 1;
    }
  }
  .search-filters-speaker {

    div {
      width: 100%;
    }
  }

  .search-filters-type {

    div {
      width: 100%;

      li {
        margin-left: -8px;
        margin-right: -8px;
        padding: 4px 8px 0px 28px;
        line-height: 1.1em;
        cursor: pointer;

        &:hover {
          background-color: #f1f1f1;
        }
      }
    }
  }

  .search-filters-reset {
    grid-column: 4;
    grid-row: 2;
    margin-top: -10px;
    margin-left: auto;
    display: flex;
    flex-direction: column-reverse;
  }
}

.search-filters-stele:only-child {
  grid-template-rows: repeat(1, minmax(0, 1fr));
}

.search-filters-stele {
  padding-top: 15px;
  display: inline-grid;
  width: 100%;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  grid-template-rows: repeat(2, minmax(0, 1fr));
  gap: 4px;
  padding-bottom: 20px;

  & > div {
    position: relative;
    margin: 7px;
  }

  p {
    margin-bottom: 8px;
  }

  .flex {
    flex: 1;
    display: flex;
    align-items: flex-start;

    &.flex-cols {
      flex-direction: column;
    }

    & > div {
      flex-grow: 1;
    }
  }
  &:hover {
    border-color: var(--finder-color-primary-25);
  }
  .search-filters-speaker {
    div {
      width: 100%;
    }
  }
  .search-filters-type {

    div {
      width: 100%;
      li {
        margin-left: -8px;
        margin-right: -8px;
        padding: 4px 8px 0px 28px;
        line-height: 1.1em;
        cursor: pointer;

        &:hover {
          background-color: #f1f1f1;
        }
      }
    }
  }
}
.found-events {
  color: transparent;
  pointer-events: none;

  &::before {
    content: 'found ';
  }

  &::after {
    content: ' events';
  }
}

svg {
  color: inherit;
  fill: currentColor;
}
</style>
