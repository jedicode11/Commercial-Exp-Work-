<template>
  <SearchField v-if="!(String(searchFieldIsHide).toLowerCase() === 'true')" :text="query"
    :title="t('components_search_field_title')" z-showButton :placeholder="t('components_events_search_placeholder')"
    :buttonText="t('components_events_button_start_search')" x-backgroundColor="#EDEDED" iconAsSuffix showIcon
    @search="handleSearch($event)" />

  <div v-if="searchFiltersVisible && widgetMode?.toLowerCase() === 'stele'" class="search-filters-box">
    <div :class="widgetMode?.toLowerCase() === 'stele' ? 'search-filters-stele' : 'search-filters'">
      <template>
        <div class="search-filters-category flex flex-cols">
          <div>
            <cta-button is-secondary @click="() => openModal()">{{
              t('components_events_search_filters__select_product_category') }}</cta-button>
            <content-modal ref="modal" panelClass="modal-categories"
              :title="t('components_events_search_filter_by_product_category')">
              <choice-tree :node="{ id: 0, title: '', children: eventCategories }" v-model="selectedEventCategories"
                :showRoot="false"></choice-tree>
            </content-modal>
          </div>
        </div>
      </template>
      <div v-if="showSiteDropdown && !(String(forumBasedOnEntry).toLowerCase() === 'true')" class="search-filters-type flex flex-cols">
        <p>{{ t('components_events_search_filter_by_forum') }}</p>
        <form-multi-select :options="filteredSites"
          :text="selectedSiteName.length > 0 ? selectedSiteName.join(', ') : t('components_events_search_filters__select_forum')"
          v-model="selectedSite" />
      </div>

      <div v-if="showForumDropdown && (String(forumBasedOnEntry).toLowerCase() === 'true')" class="search-filters-type flex flex-cols">
        <p>{{ t('components_events_search_filter_by_forum') }}</p>
        <form-multi-select :options="transformedEventForums"
          :text="selectedForumsLabel.length > 0 ? selectedForumsLabel.join(', ') : t('components_events_search_filters__select_forum')"
          v-model="selectedEventForums" />
      </div>

      <div v-if="!(String(speakerFilterIsHide).toLowerCase() === 'true')" class="search-filters-speaker flex flex-cols">
        <p>{{ t('components_events_search_filters__speaker') }}</p>
        <div class="flex flex-cols">
          <form-multi-select :options="filteredSpeakers"
            :text="selectedSpeakersName.length > 0 ? selectedSpeakersName.join(', ') : t('components_events_search_filters__select_speaker')"
            v-model="selectedSpeakers" />
        </div>
      </div>
      <div class="search-filters-date flex flex-cols">
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
      </div>
      <div class="search-filters-type flex flex-cols">
        <p>{{ t('components_events_search_filter_by_lang') }}</p>
        <form-multi-select :options="transformedEventLangs"
          :text="selectedLangsLabel.length > 0 ? selectedLangsLabel.join(', ') : t('components_events_search_filters__select_lang')"
          v-model="selectedEventLangs" />
      </div>
      <div v-if="!(String(tagFilterIsHide).toLowerCase() === 'true')" class="search-filters-type flex flex-cols">
        <p>{{ t('components_events_search_filters__tag') }}</p>
        <form-multi-select :options="transformedEventTags"
          :text="selectedTagsLabel.length > 0 ? selectedTagsLabel.join(', ') : t('components_events_search_filters__select_topic')"
          v-model="selectedEventTags" />
      </div>

      <div class="search-filters-reset">
        <CheckBoxForm :options="eventTypes" x-v-model="checkedEventTypes" @input="checkedEventTypes = $event" />
        <div class="reset-btn">
          <cta-button :buttonType="CtaButtonType.reset" is-secondary isSmall @click="() => resetFilters()">{{
            t('components_events_search_filters__reset') }}</cta-button>
        </div>
      </div>

    </div>

  </div>
  <div v-else class="search-filters-box">
    <div class="search-filters">

      <template>
        <div class="search-filters-category flex flex-cols">
          <div>
            <cta-button is-secondary @click="() => openModal()">{{
              t('components_events_search_filters__select_product_category') }}</cta-button>
            <content-modal ref="modal" panelClass="modal-categories"
              :title="t('components_events_search_filter_by_product_category')">
              <choice-tree :node="{ id: 0, title: '', children: eventCategories }" v-model="selectedEventCategories"
                :showRoot="false"></choice-tree>
            </content-modal>
          </div>
        </div>
      </template>

      <div v-if="showSiteDropdown && !(String(forumBasedOnEntry).toLowerCase() === 'true')" class="search-filters-type flex flex-cols">
        <p>{{ t('components_events_search_filter_by_forum') }}</p>
        <form-multi-select :options="filteredSites"
          :text="selectedSiteName.length > 0 ? selectedSiteName.join(', ') : t('components_events_search_filters__select_forum')"
          v-model="selectedSite" />
      </div>

      <div v-if="showForumDropdown && (String(forumBasedOnEntry).toLowerCase() === 'true')" class="search-filters-type flex flex-cols">
        <p>{{ t('components_events_search_filter_by_forum') }}</p>
        <form-multi-select :options="transformedEventForums"
          :text="selectedForumsLabel.length > 0 ? selectedForumsLabel.join(', ') : t('components_events_search_filters__select_forum')"
          v-model="selectedEventForums" />
      </div>

      <div v-if="!(String(speakerFilterIsHide).toLowerCase() === 'true')" class="search-filters-speaker flex flex-cols">
        <p>{{ t('components_events_search_filters__speaker') }}</p>
        <div class="flex flex-cols">
          <form-multi-select :options="filteredSpeakers"
            :text="selectedSpeakersName.length > 0 ? selectedSpeakersName.join(', ') : t('components_events_search_filters__select_speaker')"
            v-model="selectedSpeakers" />
        </div>
      </div>
      <div class="search-filters-type flex flex-cols">
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

      <div class="search-filters-type flex flex-cols">
        <p>{{ t('components_events_search_filter_by_lang') }}</p>
        <form-multi-select :options="transformedEventLangs"
          :text="selectedLangsLabel.length > 0 ? selectedLangsLabel.join(', ') : t('components_events_search_filters__select_lang')"
          v-model="selectedEventLangs" />
      </div>

      <div v-if="!(String(tagFilterIsHide).toLowerCase() === 'true')" class="search-filters-type flex flex-cols">
        <p>{{ t('components_events_search_filters__tag') }}</p>
        <form-multi-select :options="transformedEventTags"
          :text="selectedTagsLabel.length > 0 ? selectedTagsLabel.join(', ') : t('components_events_search_filters__select_topic')"
          v-model="selectedEventTags" />
      </div>
      <div class="search-filters-reset">
        <CheckBoxForm :options="eventTypes" x-v-model="checkedEventTypes" @input="checkedEventTypes = $event" />
        <div class="reset-btn">
          <cta-button is-secondary isSmall @click="resetFilters">{{ t('components_events_search_filters__reset')
          }}</cta-button>
        </div>
      </div>
    </div>

  </div>
  <span class="found-events">{{ entryCount }}</span>

  <!-- ..................>>>>>>>>>>>>>>>>>>>>>>>>>..................>>>>>>>>>>>>>>>>>>>>>>>>>>>.....................>>>>>>>>>>>>>>>>>>>>>>>>>>>........................>>>>>>>>>>>>>>>>>>>>>>>>>....................... -->

  <cta-button class="popup-cta-button" :class="{ 'hidden': isButtonHidden }" :icon-pos="CtaButtonIconPos.right"
    :badgeNumber="(selectedEventDays ? 1 : 0) + selectedSpeakers?.length + selectedEventTags?.length + selectedSite?.length + selectedEventLangs?.length"
    :icon-name="SvgIconName.filterDown" @click="showOverlay">
    {{ t('components_products_filter_popup_filter') }}
  </cta-button>

  <div class="overlay" :class="{ 'no-scroll': showingOverlay }" v-if="showingOverlay">
    <div class="overlay-layout">
      <h>{{ t('components_events_search_filter_title') }}</h>

      <icon-button @click="hideOverlay" isSmallOnMobile class="overlay-close-button">
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
        </svg>
      </icon-button>

    </div>
    <div class="filter-frame">
      <cta-button is-secondary isSmallOnMobile isSmall isFullWidth @click="resetFilters">{{
        t('components_events_search_filters__reset') }}
      </cta-button>

      <div class="search-filters">
        <div v-if="showSiteDropdown && !(String(forumBasedOnEntry).toLowerCase() === 'true')" class="search-filters-speaker flex flex-cols">
          <!-- <p>{{ t('components_events_search_filter_by_forum') }}</p> -->
          <AccordionMultiSelect :options="filteredSites"
          :text="selectedSiteName.length > 0 ? selectedSiteName.join(', ') : t('components_events_search_filter_by_forum')"
          v-model="selectedSite" />
        </div>

        <div v-if="showForumDropdown && (String(forumBasedOnEntry).toLowerCase() === 'true')" class="search-filters-speaker flex flex-cols">
          <!-- <p>{{ t('components_events_search_filter_by_forum') }}</p> -->
          <AccordionMultiSelect :options="transformedEventForums"
          :text="selectedForumsLabel.length > 0 ? selectedForumsLabel.join(', ') : t('components_events_search_filter_by_forum')"
          v-model="selectedEventForums" />
        </div>

        <div v-if="!(String(speakerFilterIsHide).toLowerCase() === 'true')" class="search-filters-speaker flex flex-cols">
          <AccordionMultiSelect :options="filteredSpeakers"
            :text="selectedSpeakersName.length > 0 ? selectedSpeakersName.join(', ') : t('components_events_search_filters__speaker')"
            v-model="selectedSpeakers" />
        </div>

        <div class="search-filters-speaker flex flex-cols">
          <AccordionSelect :options="eventDays" v-model="selectedEventDays"
            :placeholder="t('components_events_search_filters__select_day')" />
        </div>

        <div class="search-filters-speaker flex flex-cols">
          <AccordionMultiSelect :options="transformedEventLangs"
            :text="selectedLangsLabel.length > 0 ? selectedLangsLabel.join(', ') : t('components_events_search_filters__select_lang')"
            v-model="selectedEventLangs" />
        </div>

        <div v-if="!(String(tagFilterIsHide).toLowerCase() === 'true')" class="search-filters-type flex flex-cols">
          <AccordionMultiSelect :options="transformedEventTags"
            :text="selectedTagsLabel.length > 0 ? selectedTagsLabel.join(', ') : t('components_events_search_filters__tag')"
            v-model="selectedEventTags" />
        </div>
      </div>

      <div class="flex flex-cols actual-button">
        <cta-button :icon-name="SvgIconName.restart" :icon-pos="CtaButtonIconPos.right" isFocused isFullWidth
          @click="hideOverlay">
          {{ t('components_events_search_filters__update') }}
        </cta-button>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import SearchField from '@/components/elements/SearchField.vue'
import { EventLangModel } from '@/models/EventLangModel'
import { EventTagModel } from '@/models/EventTagModel'
import { useLazyQuery } from '@vue/apollo-composable'
import { computed, defineComponent, inject, onMounted, ref, toRef, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { useLanguageStore } from '@/store/language'
import { storeToRefs } from 'pinia'
import { useRoute } from 'vue-router'
import { EventCategoryModel } from '@/models/EventCategoryModel'
import { isEqual } from 'lodash'
import CtaButton, { CtaButtonType, CtaButtonIconPos } from '@/components/elements/CtaButton.vue'
import ChoiceTree from '@/components/elements/ChoiceTree.vue'
import Checkbox from '@/components/elements/Checkbox.vue'
import { createFormField, createFormLabel, createFormErrorFeedback } from '@/models/FormFieldInterface'
import { createLink } from '@/models/Link'
import { SvgIconName } from '@/components/helpers/SvgIcon.vue'
import FormSelect, { FormSelectOption } from '@/components/elements/forms/FormSelect.vue'
import FormMultiSelect from '@/components/elements/forms/FormMultiSelect.vue'
import { useSiteId } from '@/composables/useSiteId'
import { compareAsc } from 'date-fns/esm'
import AccordionMultiSelect from './forms/AccordionMultiSelect.vue'
import AccordionSelect from './forms/AccordionSelect.vue'
import moment from 'moment-timezone'
import { QuerySearchEventModel } from '@/models/QuerySearchEventModel'
import { handleSpecialCarecters, QueryHandler } from '@/utils/queryHelper'

import { GET_EVENTS } from '@/graphql/queries/events'
// import { GET_PRODUCT_CATEGORY } from '@/graphql/queries/productCategory'
import { GET_SITE_NAME } from '@/graphql/queries/siteName'
import { GET_DROPDOWN_FORUMS } from '@/graphql/queries/dropdownForums'
import { GET_DROPDOWN_SPEAKERS } from '@/graphql/queries/dropdownSpeakers'
import { GET_DROPDOWN_EVENT_LANDGS } from '@/graphql/queries/dropdownEventLangs'
import { GET_CATEGORY_TAGS } from '@/graphql/queries/dropdownCategoryTags'
import { GET_EVENT_DATEFRAMES } from '@/graphql/queries/dropdownEventDateFrames'
import { QuerySearchEventResponseModel } from '@/models/QuerySearchEventResponseModel'
import { EventForumModel } from '@/models/EventForum'
import { ScriptParamsService, sps } from '@/utils/scriptParamService'
import { GET_RELATED_SESSIONS } from '@/graphql/queries/getRelatedSessions'
import { RelatedSessionModel } from '@/models/RelationSessionModel'

export enum appointmentListType {
  future = 'listFuture',
  past = 'listPast'
}

export interface queryObj {
  site: string[] | null | undefined,
  textFilter: string | null,
  section: string[],
  relatedTo: string[] | null,
  relatedToEntries: Object,
  relatedToCategories: Object,
  timeStart: string[] | null,
  timeEnd: string[] | null,
  offset: number | null,
  limit: number | null,
  orderBy: string | null,
}

export interface site {
  name: string,
  handle: string
}

export default defineComponent({
  name: ' OverviewSearch',
  data () {
    return {
      isButtonHidden: false,
      scrollTimeout: undefined as number | undefined,
      showingOverlay: false
    }
  },
  mounted () {
    window.addEventListener('scroll', this.handleScroll)
  },
  beforeUnmount () {
    window.removeEventListener('scroll', this.handleScroll)
  },
  methods: {
    handleScroll () {
      this.isButtonHidden = true

      if (this.scrollTimeout !== undefined) {
        clearTimeout(this.scrollTimeout)
      }

      this.scrollTimeout = window.setTimeout(() => {
        this.isButtonHidden = false
      }, 300)
    },
    showOverlay () {
      this.showingOverlay = true
      document.body.style.top = String(`${-window.scrollY}px`)
      document.body.style.position = 'fixed'
      document.body.style.overflow = 'hidden'
      document.body.style.width = '100%'
      document.body.style.height = 'auto'
    },
    hideOverlay () {
      this.showingOverlay = false
      const scrollY = document.body.style.top
      document.body.style.top = ''
      document.body.style.position = ''
      document.body.style.overflow = ''
      document.body.style.width = ''
      document.body.style.height = ''
      window.scrollTo(0, parseInt(scrollY || '0', 10) * -1)
    }
  },
  components: {
    SearchField,
    CtaButton,
    ChoiceTree,
    Checkbox,
    FormSelect,
    FormMultiSelect,
    AccordionMultiSelect,
    AccordionSelect
  },
  props: {
    text: {
      type: String,
      required: true,
      default: ''
    },
    categories: {
      type: String,
      required: false
    },
    date: {
      type: String,
      required: false
    },
    currentEvents: {
      type: Boolean,
      required: false
    },
    tags: {
      type: String,
      required: false
    },
    timeOfDay: {
      type: String,
      required: false
    },
    speakers: {
      type: String,
      required: false
    },
    langs: {
      type: String,
      required: false
    },
    forums: {
      type: String,
      required: false
    },
    infiniteScrollButtonId: {
      type: String,
      required: false
    }
  },
  setup (props, { emit }) {
    const widgetMode = inject<string | undefined>('app.widgetMode')
    const searchFieldIsHide = inject<boolean | undefined>('app.searchFieldIsHide')
    const speakerFilterIsHide = inject<boolean | undefined>('app.speakerFilterIsHide')
    const tagFilterIsHide = inject<boolean | undefined>('app.tagFilterIsHide')
    const searchResultOrder = inject<string | undefined>('app.searchResultOrder')
    const tagCategoryParentId = inject<string | undefined>('app.tagCategoryParentId')
    const querySearchSections = inject<string | undefined>('app.querySearchSections')
    const permittedForumsString = inject<string | undefined>('app.permittedForums')
    const forumBasedOnEntry = inject<string | undefined>('app.forumBasedOnEntry')

    const text = toRef(props, 'text')
    const categories = toRef(props, 'categories')
    const externalDate = toRef(props, 'date')
    const externalTimeOfDay = toRef(props, 'timeOfDay')
    const externalSpeakers = toRef(props, 'speakers')
    const externalLangs = toRef(props, 'langs')
    const forums = toRef(props, 'forums')
    const externalTags = toRef(props, 'tags')
    const infiniteScrollButtonId = toRef(props, 'infiniteScrollButtonId')

    const { t, locale } = useI18n()
    console.log('🚀 ~ setup ~ locale:', locale, t('components_events_button_start_search'))

    const route = useRoute()

    const languageStore = useLanguageStore()
    const { currentLanguage } = storeToRefs(languageStore)
    const { setLanguage } = languageStore

    const { getSitesForGraphQl } = useSiteId()

    const querySections = querySearchSections?.split(',').map(s => s.trim()) || ['session', 'event']

    const searchFiltersVisible = ref(true)
    const query = ref<string>('')
    const entries = ref<any[]>([])
    const date = ref<Date | null>(externalDate.value ? new Date(externalDate.value) : null)
    const currentEvents = ref<boolean>(toRef(props, 'currentEvents').value)
    const selectedDate = ref<Date | null>(null)
    const masks = { input: 'DD.MM.YYYY' }
    const timeSet = ref<string | number | undefined>(undefined)
    const eventTypes = ref<string[]>([])
    const checkedEventTypes = ref<string[]>([])

    const speakers = ref<any[]>([])
    const eventCategories = ref<EventCategoryModel[]>([])
    const checkedEventCategories = ref<EventCategoryModel[]>([])
    const selectedEventCategories = ref<string[]>([])

    const eventSpeakers = ref<string[]>([])
    const searchEventSpeaker = ref<string>('')
    const focusedEventSpeaker = ref<boolean>(false)
    const selectedSpeakers = ref<string[]>([])

    const modal = ref()
    const openModal = () => modal.value.open()

    const sites = ref<site[]>([])
    const selectedSite = ref<string[]>([])
    const focusedSite = ref<boolean>(false)
    const inputSearchSite = ref<HTMLElement>()
    const searchSite = ref<string>('')

    const eventForums = ref<EventForumModel[]>([])
    const selectedEventForums = ref<string[]>([])

    const eventLangs = ref<EventLangModel[]>([])
    const selectedEventLangs = ref<string[]>([])

    const eventTags = ref<EventTagModel[]>([])
    const selectedEventTags = ref<string[]>([])
    const loadCouter = ref(0)

    const startDateTimes = ref<{ startEventDatetime: string }[]>([])
    const eventDays = ref<FormSelectOption[]>([])
    const selectedEventDays = ref<string>()

    const entryCount = ref<number>(0)
    const entriesCount = ref<number>(0)

    const isTimeSetMorning = ref<boolean>(false)
    const isTimeSetAfternoon = ref<boolean>(false)

    const scriptParamsService = inject<ScriptParamsService>(sps)

    const infiniteScrollDistancePx = 20

    const qh = new QueryHandler((scriptParamsService as ScriptParamsService).limit, (scriptParamsService as ScriptParamsService).offset, [...querySections])
    // const qh = new QueryHandler(scriptParamsService.limit, scriptParamsService.offset, [...querySections])

    const setQuery = () => {
      console.log('🚀 ~ setQuery ~ text.value:', text.value)
      if (text.value) {
        query.value = Array.isArray(text.value) ? text.value[0] : text.value
      } else {
        console.log('🚀 ~ setQuery ~ route?.params?.text:', route?.params?.text)
        query.value = Array.isArray(route?.params?.text) ? route?.params?.text[0] : route?.params?.text ?? ''
      }

      console.log('🚀 ~ setQuery ~ externalDate.value:', externalDate.value)

      if (externalTimeOfDay.value) {
        if (externalTimeOfDay.value === 'morning') {
          setTimeSetMorning(true)
        }
        if (externalTimeOfDay.value === 'afternoon') {
          setTimeSetAfternoon(true)
        }
      }
    }

    const getSites = (): string[] => {
      return selectedSite.value.length ? selectedSite.value : getSitesForGraphQl()
    }

    const getPermittedForumSlugs = () => {
      if (!permittedForumsString) {
        return null
      }

      return permittedForumsString.split(',').map(i => i.trim())
    }

    const showSiteDropdown = computed((): boolean => {
      return getSitesForGraphQl().length > 1
    })

    const showForumDropdown = computed((): boolean => {
      return eventForums.value.length > 1
    })

    const clearAllresult = () => {
      qh.reset()
      resetInvisibleScroll()
      eventSpeakers.value = []
      entries.value = []
    }

    const setCategories = () => {
      console.log('🚀 ~ setCategories ~ categories.value:', categories.value)
      if (categories.value) {
        let categoryIds: Array<string>
        try {
          categoryIds = JSON.parse(categories.value)
        } catch {
          categoryIds = categories.value.split(', ').map(cat => cat.trim())
        }
        categoryIds = Array.isArray(categoryIds) ? categoryIds : [categoryIds]
        console.log('🚀 ~ setCategories ~ categoryIds:', categoryIds)

        const getChildrens = (cat: EventCategoryModel): EventCategoryModel[] => {
          if (cat.children && cat.children.length > 0) {
            return [cat, ...cat.children.map(c => getChildrens(c)).flat()]
          } else {
            return [cat]
          }
        }

        const allCategories = eventCategories.value.map(cat => getChildrens(cat)).flat()
        console.log('🚀 ~ setCategories ~ allCategories:', allCategories)

        // eslint-disable-next-line eqeqeq
        const checkedCategories = allCategories.filter(cat => categoryIds.some(id => cat.id == id))
        checkedEventCategories.value = checkedCategories.filter((e, i) => checkedCategories.findIndex(a => a.id === e.id) === i)
        console.log('🚀 ~ setCategories ~ checkedEventCategories.value:', checkedEventCategories.value)

        selectedEventCategories.value = categoryIds

        searchFiltersVisible.value = true
        resetInvisibleScroll()
        loadQueries()
      }
    }

    const setSpeaker = () => {
      console.log('🚀 ~ setSpeaker ~ speaker.value:', externalSpeakers.value)
      if (externalSpeakers.value) {
        const externalSpeakersName = externalSpeakers.value.split(',').map(f => f.trim()).map(esn => esn.replace('_', ' '))
        speakers.value.forEach(s => {
          if (externalSpeakersName.includes(s.title)) {
            selectedSpeakers.value.push(s.id.toString())
          }
        })
          
        resetInvisibleScroll()
        loadQueries()
      }
    }

    const setTag = () => {
      if (externalTags.value) {
        const externalTagsNames = externalTags.value.split(',').map(f => f.toLowerCase().trim())
        eventTags.value.forEach(tag => {
          if (externalTagsNames.includes(tag.id.toString())) {
            selectedEventTags.value.push(tag.id.toString())
          }
        })

        resetInvisibleScroll()
        loadQueries()
      }
    }

    const setLang = () => {
      if (externalLangs.value) {
        const externalLangsName = externalLangs.value.split(',').map(f => f.toLowerCase().trim())
        eventLangs.value.forEach(l => {
          if (externalLangsName.includes(l.title.toLowerCase())) {
            selectedEventLangs.value.push(l.id.toString())
          }
        })

        resetInvisibleScroll()
        loadQueries()
      }
    }

    const setForum = () => {
      if (forums.value) {
        let forumNames: Array<string>
        forumNames = forums.value.split(',').map(f => f.trim())
        forumNames = Array.isArray(forumNames) ? forumNames : [forumNames]

        const appendLang = (siteId: string) => {
          switch (currentLanguage.value) {
            case 'de':
              return siteId + 'De'
            default:
              return siteId + 'En'
          }
        }

        forumNames = forumNames.map(appendLang)

        const allSites = getSites()

        forumNames.forEach(f => {
          if (allSites.includes(f)) {
            selectedSite.value.push(f)
          }
        })
      }
    }

    watch(locale, () => {
      console.log('locale', locale.value)
      setLanguage(locale.value)
      date.value = null
      selectedDate.value = null
      timeSet.value = undefined
      // isTimeSetMorning.value
      // isTimeSetAfternoon.value
      isTimeSetMorning.value = false
      isTimeSetAfternoon.value = false
      checkedEventTypes.value = []
      searchEventSpeaker.value = ''
      focusedEventSpeaker.value = false
      selectedSpeakers.value = []
      selectedEventTags.value = []
      // loadProductCategories(undefined, { site: getSites() })
      loadSpeakers(undefined, { site: getSites() })
      loadQueries()
    })

    watch(route, () => {
      setQuery()
      loadQueries()
    })

    watch(selectedEventCategories, () => {
      if (eventCategories.value.length) {
        const getChildren = (cat: EventCategoryModel): EventCategoryModel[] => {
          if (cat.children && cat.children.length > 0) {
            return [cat, ...cat.children.map(c => getChildren(c)).flat()]
          } else {
            return [cat]
          }
        }

        const allCategories = eventCategories.value.map(cat => getChildren(cat)).flat()
        console.log('🚀 ~ watch(selectedEventCategories ~ allCategories:', allCategories)
        const allSelectedCategories = allCategories.filter(cat => selectedEventCategories.value.includes(`${cat.id}`))
        checkedEventCategories.value = allSelectedCategories.filter((e, i) => allSelectedCategories.findIndex(a => a.id === e.id) === i)
        console.log('🚀 ~ watch ~ checkedEventCategories.value:', checkedEventCategories.value)
      }

      clearAllresult()
      setQuery()
      loadQueries()
    })

    const zeroCurrentEvents = () => {
      currentEvents.value = false
    }

    watch(selectedSpeakers, () => {
      zeroCurrentEvents()
      clearAllresult()
      setQuery()
      loadQueries()
    })

    watch(selectedEventLangs, () => {
      zeroCurrentEvents()
      clearAllresult()
      setQuery()
      loadQueries()
    })

    watch(selectedEventTags, () => {
      zeroCurrentEvents()
      clearAllresult()
      setQuery()
      loadQueries()
    })

    watch(selectedEventDays, () => {
      zeroCurrentEvents()
      clearAllresult()
      setQuery()
      loadQueries()
    })

    watch(isTimeSetMorning, () => {
      zeroCurrentEvents()
      clearAllresult()
      setQuery()
      loadQueries()
    })

    watch(isTimeSetAfternoon, () => {
      zeroCurrentEvents()
      clearAllresult()
      setQuery()
      loadQueries()
    })

    watch(selectedEventForums, () => {
      zeroCurrentEvents()
      clearAllresult()
      setQuery()
      loadQueries()
    })

    onMounted(() => {
      console.log('🚀 ~ onMounted ~ onMounted:', onMounted)
      setQuery()
      loadSites(undefined, { site: getSites() })
      setForum()
      // loadProductCategories(undefined, { site: getSites() })
      loadSpeakers(undefined, { site: getSites() })
      loadEventLangs(undefined, { site: getSites() })
      loadEventForums(undefined, { site: getSites() })
      loadEventTags(undefined, { site: getSites(), id: tagCategoryParentId })
      loadEventDays(undefined, { site: getSites(), section: querySections })
      loadQueries()

      window.addEventListener('scroll', () => {
        infiniteScroll()
      })
      window.addEventListener('resize', () => {
        infiniteScroll()
      })
      // infiniteScroll()
    })

    const filteredEventTypes = computed<FormSelectOption[]>(() => {
      return eventTypes.value.map(type => ({ label: type, value: type }))
    })

    const filteredSites = computed<FormSelectOption[]>(() => {
      return sites.value.map(s => ({ label: s.name, value: s.handle }))
    })

    const filteredSpeakers = computed<FormSelectOption[]>(() => {
      return speakers.value.map(s => ({ label: s.title, value: s.id, lastName: s.personLastName })).sort((a, b) => (a.lastName > b.lastName) ? 1 : ((b.lastName > a.lastName) ? -1 : 0))
    })

    const transformedEventLangs = computed<FormSelectOption[]>(() => {
      return eventLangs.value.map(el => ({ label: el.title, value: el.id }))
    })

    const transformedEventTags = computed<FormSelectOption[]>(() => {
      return eventTags.value.map(el => ({ label: el.title, value: el.id }))
    })

    const transformedEventForums = computed<FormSelectOption[]>(() => {
      return eventForums.value.map(el => ({ label: el.title, value: el.id }))
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

    const selectedSpeakersName = computed(() => {
      const names: string[] = []
      speakers.value.forEach(s => {
        if (selectedSpeakers.value.includes(s.id.toString())) {
          names.push(s.title)
        }
      })
      return names
    })

    const selectedLangsLabel = computed(() => {
      const labels: string[] = []
      eventLangs.value.forEach(el => {
        if (selectedEventLangs.value.includes(el.id.toString())) {
          labels.push(el.title)
        }
      })
      return labels
    })

    const selectedTagsLabel = computed(() => {
      const labels: string[] = []
      eventTags.value.forEach(el => {
        if (selectedEventTags.value.includes(el.id.toString())) {
          labels.push(el.title)
        }
      })
      return labels
    })

    const selectedForumsLabel = computed(() => {
      const labels: string[] = []
      eventForums.value.forEach(el => {
        if (selectedEventForums.value.includes(el.id.toString())) {
          labels.push(el.title)
        }
      })
      return labels
    })

    watch(selectedSite, () => {
      zeroCurrentEvents()
      clearAllresult()

      // loadProductCategories(undefined, { site: getSites() })
      loadSpeakers(undefined, { site: getSites() })
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

    // const { result: resultProductCategories, load: loadProductCategories } = useLazyQuery(GET_PRODUCT_CATEGORY)

    // watch(resultProductCategories, val => {
    //   eventCategories.value = Array.from(new Set(val.categories))
    //   setCategories()
    // })

    const { result: resultSites, load: loadSites } = useLazyQuery(GET_SITE_NAME)

    watch(resultSites, val => {
      sites.value = val.sites
    })

    const { result: resultSpeakers, load: loadSpeakers } = useLazyQuery(GET_DROPDOWN_SPEAKERS)

    watch(resultSpeakers, val => {
      speakers.value = val.speakers
      setSpeaker()
    })

    const { result: resultEventLangs, load: loadEventLangs } = useLazyQuery(GET_DROPDOWN_EVENT_LANDGS)

    watch(resultEventLangs, val => {
      eventLangs.value = val.langs
      setLang()
    })

    const { result: resultEventTags, load: loadEventTags } = useLazyQuery(GET_CATEGORY_TAGS)

    watch(resultEventTags, val => {
      eventTags.value = val.tags.children
      setTag()
    })

    const { result: resultEventForums, load: loadEventForums } = useLazyQuery<{forums: Array<EventForumModel>}>(GET_DROPDOWN_FORUMS)

    watch(resultEventForums, val => {
      if (val?.forums) {
        if (!permittedForumsString) {
          eventForums.value = val.forums
        } else {
          const permittedForumSlugs = getPermittedForumSlugs()
          eventForums.value = val.forums.reduce((acc, value): EventForumModel[] => {
            if (permittedForumSlugs?.includes(value.slug)) {
              return [...acc, value]
            }

            return acc
          }, [] as EventForumModel[])
        }
      }
    })

    const { result: resultEventDays, load: loadEventDays } = useLazyQuery(GET_EVENT_DATEFRAMES)

    watch(resultEventDays, val => {
      startDateTimes.value = val.dateFrames
    })

    const { result, load, loading, refetch } = useLazyQuery<QuerySearchEventResponseModel>(GET_EVENTS)
    const { result: resultRelatedSessions, load: loadRelatedSessions } = useLazyQuery<{ relatedSession: RelatedSessionModel[] }>(GET_RELATED_SESSIONS)

    watch (resultRelatedSessions, val => {
      handleRelatedSession(val?.relatedSession)
    })

    const handleRelatedSession = (relatedSessions: RelatedSessionModel[] | undefined) => {
      if (relatedSessions) {

        entries.value.forEach(e => {
          relatedSessions.forEach(s => {
            if (s.eventsId.map(i => i.id).includes(e.id)) {
              e.relatedSession = s
            }
          })
        })
        // entries.value = [...entries.value]
      }
    }

    watch(result, val => {
      if (val) {
        if (!qh.next || widgetMode?.toLowerCase() === 'stele') {
          emit('showLoadBtn', false)
        } else {
          emit('showLoadBtn', true)
          enableInfiniteScrollButton()
        }

        if (val.events.length) {
          const eventURIs = val.events.map(e => e.uri)
          loadRelatedSessions(undefined, { site: getSites(), uri: eventURIs })
        }

        // Removing items that are already loaded
        const filteredRelatedBySpeaker = val.relatedBySpeaker.filter(e => !entries.value.map(en => en.id).includes(e.id))

        entries.value = entries.value.concat([...val.sessions, ...val.events, ...filteredRelatedBySpeaker])
        const entriesAsMap = new Map(entries.value.map(e => [e.id, e]))
        entries.value = [...entriesAsMap.values()]

        eventTypes.value = Array.from(new Set(entries.value.map(e => e.eventPlace)))
        entriesCount.value = (querySections.includes('event') ? val.eventsCount : 0) + (querySections.includes('session') ? val.sessionsCount : 0) + (query.value ? val.relatedBySpeakerCount : 0)
        // emit('eventCount', entriesCount.value)
        emit('eventCount', query.value ? (new Set([...val.allEntriesId.map(e => e.id), ...val.allEntriesRelatedBySpeakerId.map(e => e.id)]).size) : entriesCount.value)

        qh.queryResponse(val)
        if (qh.autoLoad) {
          loadQueries()
        }
      }
    })

    function filterEvents () {
      let fe = [...entries.value]

      if (searchFiltersVisible.value) {
        // types
        fe = checkedEventTypes.value.length > 0
          ? fe.filter(e => checkedEventTypes.value.includes(e.eventPlace))
          : fe
        fe = fe.map(event => {
          if (event.eventLanguage && event.eventLanguage.length) {
            const sortedEventLanguages: EventLangModel[] = []
            event.eventLanguage.forEach((lang: any) => {
              if (selectedEventLangs.value.includes(lang.id.toString())) {
                sortedEventLanguages.unshift(lang)
              }
            })
            event.eventLanguage.forEach((lang: any) => {
              if (!sortedEventLanguages.some(l => l.id === lang.id)) {
                sortedEventLanguages.push(lang)
              }
            })
            const updatedEvent = {
              ...event,
              eventLanguage: sortedEventLanguages
            }
            return updatedEvent
          }
          return event
        })
      }
      
      return fe
    }

    // This ensures that the filtering logic is executed dynamically.
    watch([entries, checkedEventTypes, timeSet], () => {
      const filteredEvents = filterEvents()
      emit('results', filteredEvents)
    }, { deep: true })

    const queryObjOld = ref<QuerySearchEventModel>()

    const loadQueries = () => {
      qh.searchText = query.value

      const newQueryValue = qh.getNewValue()

      const queryObj: QuerySearchEventModel = {
        site: getSites(),
        eventSection: newQueryValue.eventSection,
        sessionSection: newQueryValue.sessionSection,
        relatedBySpeakerSection: newQueryValue.relatedBySpeakerSection,
        textFilter: null,
        relatedTo: null,
        relatedToCategories: [{ id: null }],
        timeStart: null,
        timeEnd: null,

        eventLimit: widgetMode?.toLowerCase() === 'stele' ? null : newQueryValue.eventLimit,
        eventOffset: widgetMode?.toLowerCase() === 'stele' ? null : newQueryValue.eventOffset,
        eventOrderBy: 'timeStart ASC',

        sessionLimit: widgetMode?.toLowerCase() === 'stele' ? null : newQueryValue.sessionLimit,
        sessionOffset: widgetMode?.toLowerCase() === 'stele' ? null : newQueryValue.sessionOffset,
        sessionOrderBy: 'sessionStartTime ASC',

        relatedBySpeakerLimit: widgetMode?.toLowerCase() === 'stele' ? null : newQueryValue.relatedBySpeakerLimit,
        relatedBySpeakerOffset: widgetMode?.toLowerCase() === 'stele' ? null : newQueryValue.relatedBySpeakerOffset,

        eventCountSection: newQueryValue.eventCountSection,
        sessionCountSection: newQueryValue.sessionCountSection,
        relatedBySpeakerCountSection: query.value ? newQueryValue.relatedBySpeakerCountSection : []
      }

      if (query.value) {
        queryObj.textFilter = handleSpecialCarecters(query.value);
      }

      if (checkedEventCategories.value.length) {
        const getChildrens = (cat: EventCategoryModel): EventCategoryModel[] => {
          if (cat.children && cat.children.length > 0) {
            return [cat, ...cat.children.map(c => getChildrens(c)).flat()]
          } else {
            return [cat]
          }
        }
      }
      
      queryObj.relatedTo = selectedEventCategories.value

      if (selectedSpeakers.value.length) {
        queryObj.relatedTo = queryObj.relatedTo ? [...queryObj.relatedTo, ...selectedSpeakers.value] : selectedSpeakers.value
      }

      if (selectedEventLangs.value.length) {
        queryObj.relatedToCategories = queryObj.relatedToCategories ? [{ id: [...selectedEventLangs.value] }] : [{ id: null }]
      }

      if (selectedEventTags.value.length) {
        queryObj.relatedTo = queryObj.relatedTo ? [...queryObj.relatedTo, ...selectedEventTags.value] : selectedEventTags.value
      }

      if (searchResultOrder) {
        queryObj.eventOrderBy = searchResultOrder
        queryObj.sessionOrderBy = searchResultOrder
      }

      if (selectedEventDays.value) {
        const timeStart = new Date(new Date(selectedEventDays.value).getTime()).toISOString()
        const middleStart = new Date(new Date(selectedEventDays.value).getTime() + 50400000).toISOString()
        const endStart = new Date(new Date(selectedEventDays.value).getTime() + 86400000).toISOString()
        queryObj.timeStart = ['AND', '>= ' + timeStart, '< ' + endStart]

        if (isTimeSetMorning.value && !isTimeSetAfternoon.value) {
          queryObj.timeStart = ['AND', '>= ' + timeStart, '< ' + middleStart]
        }
        if (!isTimeSetMorning.value && isTimeSetAfternoon.value) {
          queryObj.timeStart = ['AND', '>= ' + middleStart, '< ' + endStart]
        }
      }

      if (currentEvents.value) {
        const todayDate = new Date().toISOString()
        queryObj.timeEnd = ['>= ' + todayDate]
      }

      if (isEqual(queryObj, queryObjOld.value)) {
        refetch()
      } else {
        load(undefined, queryObj, { fetchPolicy: 'network-only' })
        queryObjOld.value = queryObj
      }
    }

    watch(loading, val => {
      emit('loading', val)
    })

    watch(startDateTimes, val => {
      const ed = new Set<Date>()
      const re = new RegExp('^\\s*(3[01]|[12][0-9]|0[1-9])\\.(1[012]|0[1-9])\\.((?:19|20)\\d{2})\\s*$', 'gm')
      const matches = externalDate.value && externalDate.value.match(re)

      val.forEach(item => {
        const m = moment(item.startEventDatetime)
        m.tz('Europe/Berlin')
        m.startOf('day')
        ed.add(m.toDate())
      })

      const sortedEventDays = Array.from(new Set(Array.from(ed).sort(compareAsc).map(d => d.toISOString())))
      eventDays.value = sortedEventDays.map(d => {
        const label = new Date(d).toLocaleDateString('de-DE', { year: 'numeric', month: '2-digit', day: '2-digit' })

        return {
          label,
          value: d,
          ...(label === matches?.[0] && { checked: true })
        }
      })
    })

    const handleMoreFilterOptionsClick = (event: Event) => {
      event.preventDefault()
      searchFiltersVisible.value = true
    }

    const resetInvisibleScroll = () => {
      entries.value = []
      loadCouter.value = 0
    }

    const handleSearch = (text: string) => {
      qh.reset()

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

    const handleCategorySelectionChange = ({ checked, category }: { checked: boolean, category: EventCategoryModel }) => {
      console.log('🚀 ~ handleCategorySelectionChange ~ checked, category:', checked, category)
      if (checked) {
        checkedEventCategories.value.push(category)
      } else {
        checkedEventCategories.value = checkedEventCategories.value.filter(cat => cat.id !== category.id)
      }
      categories.value = undefined
      resetInvisibleScroll()
      loadQueries()
    }

    const eventSpeakersField = createFormField({
      label: createFormLabel({
        text: 'Form label',
        link: createLink({ text: 'Linktext' }),
        icon: '' as SvgIconName
      }),
      errorMessage: createFormErrorFeedback({
        text: 'Error text',
        link: createLink({ text: 'Linktext' })
      })
    })

    const resetFilters = () => {
      date.value = null
      selectedDate.value = null
      timeSet.value = undefined

      isTimeSetMorning.value = false
      isTimeSetAfternoon.value = false

      selectedSite.value = []
      selectedEventCategories.value = []
      selectedSpeakers.value = []
      selectedEventForums.value = []
      selectedEventLangs.value = []
      selectedEventTags.value = []
      selectedEventDays.value = ''
      qh.reset()
      handleSearch('')
    }

    return {
      t,
      currentLanguage,
      SvgIconName,
      query,
      searchFiltersVisible,
      selectedDate,
      masks,
      timeSet,
      handleSearch,
      eventTypes,
      checkedEventTypes,
      filteredEventTypes,
      eventCategories,
      eventSpeakers,
      checkedEventCategories,
      selectedEventCategories,
      searchEventSpeaker,
      focusedEventSpeaker,
      selectedSpeakers,
      eventSpeakersField,
      modal,
      openModal,

      searchFieldIsHide,
      widgetMode,
      CtaButtonIconPos,

      handleMoreFilterOptionsClick,
      setTimeSetMorning,
      setTimeSetAfternoon,
      isTimeSetMorning,
      isTimeSetAfternoon,

      handleCategorySelectionChange,

      sites,
      focusedSite,
      inputSearchSite,
      speakerFilterIsHide,
      tagFilterIsHide,
      searchSite,
      selectedSite,
      showSiteDropdown,

      showForumDropdown,
      selectedEventForums,
      transformedEventForums,
      selectedForumsLabel,

      forumBasedOnEntry,

      selectedSiteName,

      filteredSpeakers,
      selectedSpeakersName,

      selectedLangsLabel,

      transformedEventLangs,
      selectedEventLangs,

      transformedEventTags,
      selectedEventTags,
      selectedTagsLabel,

      entryCount,
      filteredSites,
      resetFilters,
      CtaButtonType,

      eventDays,
      selectedEventDays
    }
  }
})
</script>

<style lang="scss" scoped>
.search-filters-dayset {
  width: 100%;
}
.popup-cta-button {
  display: none;
  @media (max-width: 480px) {
    display: flex;
    position: fixed;
    bottom: 20px;
    left: 50%;
    transform: translateX(-50%);
    z-index: 999;
    color: #fff;
    padding: 5px;
    max-width: 156px;
  }
}
.overlay {
  display: none;
  @media (max-width: 480px) {
    display: block;
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 115%;
    background-color: white;
    z-index: 1000;
    align-items: center;
  }
  .overlay-layout {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
    height: 70px;
    top: 45px;
    padding: 20px;
    background-color: white;
    box-shadow: 0px 2px 4px rgba(0, 0, 0, 0.2);
    margin-bottom: 10px;
    .overlay-close-button {
      width: 25px;
      height: 25px;
      background: none;
      border: none;
      outline: none;
      padding: 0;
      font-size: 20px;
      line-height: 24px;
      color: black;
    }
  }
  .filter-frame {
    width: 100%;
    height: calc(90% - 80px);
    background: transparent;
    padding: 20px;
    overflow: auto;
    .actual-button {
      width: 100%;
      display: flex;
      justify-content: center;
      position: sticky;
      margin: 0 auto;
      margin-bottom: 10%;
      top: 200%;
    }
  }
  .no-scroll {
    overflow-y: hidden;
  }
}
.reset-btn {
  margin-top: 20px;
}
.search-filters-box {
  border-bottom: 1px solid #efefef;
  @media (max-width: 480px) {
    display: none;
    flex-direction: column;
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
  padding-bottom: 20px;
  gap: 4px;

  @media (max-width: 480px) {
    display: flex;
    flex-direction: column;
  }

  p {
    margin-bottom: 8px;
  }

  .flex {
    flex: 1;
    display: flex;
    align-items: flex-start;
    gap: 0px 12px;

    &.flex-cols {
      flex-direction: column;
    }

    & > div {
      flex-grow: 1;
    }

    @media (max-width: 480px) {
      flex: 1;
      display: flex;
      align-items: flex-start;
      gap: 0px 12px;
      &.flex-cols {
        flex-direction: column;
      }

      & > div {
        flex-grow: 1;
      }
    }
  }

  & > div {
    position: relative;
  }

  &:hover {
    background-color: var(--finder-color-primary-05);
  }

  .search-filters-date {
    & > div {
      position: relative;
    }

    &:hover {
      background-color: var(--finder-color-primary-05);
    }
  }

  .search-filters-timeset {
    display: flex;
    gap: 6px;
    padding-top: 4px;
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

  .search-filters-date {

    & > div {
      position: relative;
    }
    &:hover {
      background-color: var(--finder-color-primary-05);
    }
  }

  .search-filters-timeset {
    display: none;
    gap: 6px;
    padding-top: 4px;

    &:hover {
      border-color: var(--finder-color-primary-25);
    }
    @media (max-width: 480px) {
      display: none;
      flex-direction: column;
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
