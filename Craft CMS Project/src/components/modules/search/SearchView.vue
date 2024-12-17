<template>
  <Loader :loading="loading" />
  <section class="events-search finder-normalize finder-content-wrapper">
    <div class="finder-content-wrapper__inner">
      <div class="events-search__header-row">
        <div class="events__header-row__cta">
          <cta-button v-if="false" :icon-name="SvgIconName.arrowCircleLeft" @click="goHome()">{{ t('components_generic_button_back') }}</cta-button>
        </div>
        <div v-if="widgetMode?.toLowerCase() !== 'stele'" class="events-search__header-row__count">
          {{ eventCount }} {{ t('components_events_search_count', eventCount) }}
        </div>
      </div>
      <div class="events-search__section">
        <OverviewSearch
          :text="query ?? ''"
          :categories="productCategories"
          :date="date"
          :currentEvents="currentEvents"
          :tags="tags"
          :timeOfDay="timeOfDay"
          :speakers="speakers"
          :langs="langs"
          :forums="forums"
          :infiniteScrollButtonId="uniqueId"
          @search="handleSearch($event)"
          @loading="handleSearchLoading($event)"
          @results="handleSearchResults($event)"
          @showLoadBtn="handleShowLoadBtn($event)"
          @eventCount="handleEventCount($event)"
        />
      </div>
      <div class="events-search__event-section">
        <EventsSearchResult :events="events" :showLoadBtn="showLoadBtn" :infiniteScrollButtonId="uniqueId" :loading="loading"/>
      </div>
    </div>
  </section>
</template>

<script lang="ts">
import CtaButton, { CtaButtonIconPos } from '@/components/elements/CtaButton.vue'
import OverviewSearch from '@/components/elements/ OverviewSearch.vue'
import EventsSearchResult from '@/components/elements/EventsSearchResult.vue'
import Loader from '@/components/elements/Loader.vue'
import { SvgIconName } from '@/components/helpers/SvgIcon.vue'
import { EventModel } from '@/models/EventModel'
import router from '@/router'
import { defineComponent, inject, onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRoute } from 'vue-router'
import { useRouterStore } from '@/store/router'
import { storeToRefs } from 'pinia'
import uniqid from 'uniqid'
import { useWidgetMode } from '@/composables/useWidgetMode'
import { useSessionStoreHistory } from '@/composables/useSessionStoreHistory'

export default defineComponent({
  name: 'SearchView',
  components: {
    Loader,
    OverviewSearch,
    EventsSearchResult,
    CtaButton
  },
  setup () {
    const widgetMode = inject<string | undefined>('app.widgetMode')
    const { isWidgetMode, isWidgetModeRef } = useWidgetMode()

    const { t } = useI18n()

    const route = useRoute()
    const routerStore = useRouterStore()
    const { currentParams, hasHistory } = storeToRefs(routerStore)
    const { changePage } = routerStore

    const searchPhrase = inject<string | undefined>('app.searchPhrase')
    console.info('preloading search phrase:', searchPhrase)
    const searchProductCategories = inject<string | undefined>('app.searchProductCategories')
    console.info('preloading search categories:', searchProductCategories)
    const searchDate = inject<string | undefined>('app.searchDate')
    console.info('preloading search date:', searchDate)
    const searchTimeOfDay = inject<string | undefined>('app.searchTimeOfDay')
    console.info('preloading search time of day:', searchTimeOfDay)
    const searchSpeaker = inject<string | undefined>('app.searchSpeaker')
    console.info('preloading search speaker:', searchSpeaker)
    const searchLang = inject<string | undefined>('app.searchLang')
    console.info('preloading search lang:', searchLang)
    const searchForums = inject<string | undefined>('app.searchForums')
    console.info('preloading search forums:', searchForums)

    const query = ref<string | null>(null)
    const productCategories = ref<string | undefined>()
    const date = ref<string | undefined>()
    const currentEvents = ref<boolean | undefined>()
    const tags = ref<string | undefined>()
    const timeOfDay = ref<string | undefined>()
    const speakers = ref<string | undefined>()
    const langs = ref<string | undefined>()
    const forums = ref<string | undefined>()
    const events = ref<EventModel[]>([])
    const loading = ref<boolean>(false)
    const showLoadBtn = ref<boolean>(true)
    const eventCount = ref<number>(0)

    onMounted(() => {
      // setSite()
      useSessionStoreHistory().append()
    })

    const goToSearch = (text: string) => {
      if (isWidgetMode) {
        changePage('SearchView', { text })
      } else {
        router.replace(`/search/${text}`)
      }
    }

    let text: string | string[]
    if (isWidgetMode) {
      if (searchPhrase) {
        console.log('🚀 ~ setup ~ searchPhrase:', searchPhrase)
        text = searchPhrase ?? ''
      } else {
        console.log('🚀 ~ setup ~ currentParams.value:', currentParams.value)
        text = currentParams.value?.text ?? ''
      }
      if (searchProductCategories) {
        console.log('🚀 ~ setup ~ searchProductCategories:', searchProductCategories)
        productCategories.value = searchProductCategories
      }
      if (currentParams.value && currentParams.value.date && !currentParams.value.currentEvents) {
        console.log('🚀 ~ setup ~ currentParams.value:', currentParams.value)
        date.value = currentParams.value.date
      } else {
        if (searchDate && !currentParams.value.currentEvents) {
          console.log('🚀 ~ setup ~ date:', searchDate)
          date.value = searchDate
        }
      }
      if (currentParams.value && currentParams.value.timeOfDay) {
        console.log('🚀 ~ setup ~ currentParams.value:', currentParams.value)
        timeOfDay.value = currentParams.value.timeOfDay
      } else {
        if (searchTimeOfDay) {
          console.log('🚀 ~ setup ~ time of day:', searchTimeOfDay)
          timeOfDay.value = searchTimeOfDay
        }
      }
      if (currentParams.value && currentParams.value.speaker) {
        console.log('🚀 ~ setup ~ currentParams.value:', currentParams.value)
        speakers.value = currentParams.value.speaker
      } else {
        console.log('🚀 ~ setup ~ searchSpeaker:', searchSpeaker)
        if (searchSpeaker) {
          speakers.value = searchSpeaker
        }
      }
      if (currentParams.value && currentParams.value.langs) {
        console.log('🚀 ~ setup ~ currentParams.value:', currentParams.value)
        langs.value = currentParams.value.langs
      } else {
        console.log('🚀 ~ setup ~ searchLang:', searchLang)
        if (searchLang) {
          langs.value = searchLang
        }
      }
      if (currentParams.value && currentParams.value.forums) {
        console.log('🚀 ~ setup ~ currentParams.value:', currentParams.value)
        forums.value = currentParams.value.forums
      } else {
        if (searchForums) {
          console.log('🚀 ~ setup ~ forums:', searchForums)
          forums.value = searchForums
        }
      }
      if (currentParams.value && currentParams.value.tags) {
        console.log('🚀 ~ setup ~ currentParams.value:', currentParams.value)
        tags.value = currentParams.value.tags
      }
      if (currentParams.value && 'currentEvents' in currentParams.value) {
        currentEvents.value = currentParams.value.currentEvents
      }
    } else {
      text = route.params.text ?? ''
    }
    if (text) {
      query.value = Array.isArray(text) ? text[0] : text
    }

    const handleSearch = (text: string) => {
      query.value = text
      goToSearch(query.value)
    }

    const handleSearchLoading = (val: boolean) => {
      loading.value = val
    }

    const handleSearchResults = (res: EventModel[]) => {
      console.log('🚀 ~ SearchVue ~ handleSearchResults:', res)
      events.value = res
    }

    const handleShowLoadBtn = (val: boolean) => {
      showLoadBtn.value = val
    }

    const handleEventCount = (val: number) => {
      eventCount.value = val
    }

    const goHome = () => {
      if (isWidgetMode) {
        changePage('EventsView')
      } else {
        router.push('/')
      }
    }

    const uniqueId = uniqid()

    return {
      widgetMode,
      isWidget: isWidgetModeRef,
      t,
      uniqueId,
      CtaButtonIconPos,

      goHome,
      hasHistory,

      query,
      productCategories,
      date,
      currentEvents,
      tags,
      timeOfDay,
      speakers,
      langs,
      forums,
      loading,
      handleSearch,
      handleSearchLoading,
      handleSearchResults,
      handleEventCount,
      eventCount,
      SvgIconName,

      handleShowLoadBtn,
      showLoadBtn,

      events
    }
  }
})
</script>
<style lang="scss" scoped>
@import 'src/assets/styles/04-common/module-title';

.events-search {
  .events-search__header-row {
    @media (max-width: 480px) {
      display: none;
    }
    display: flex;
    justify-content: space-between;
    align-items: flex-end;

    .events-search__header-row__count {
      @media (max-width: 480px) {
        display: none;
      }
      padding-bottom: 12px;
      padding-right: 12px;
    }
  }

  .events-search__section {
    @media (max-width: 480px) {
      margin-bottom: -10%;
    }
    padding: 24px 0;
    padding-bottom: 10px;
  }
}

@include media-breakpoint-up(sm) {
}
</style>
