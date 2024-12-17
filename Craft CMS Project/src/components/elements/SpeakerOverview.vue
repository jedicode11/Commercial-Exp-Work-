<template>
  <Loader :loading="loading" />
  <section class="events-search finder-normalize finder-content-wrapper">
    <div class="finder-content-wrapper__inner">
      <div class="events-search__header-row">
        <div class="events__header-row__cta">
          <BackButton v-if="false" :icon-name="SvgIconName.arrowCircleLeft" />
        </div>
        <div class="events-search__header-row__count">{{ speakersCount }} {{ t('components_speakers_search_count', speakersCount) }}</div>
      </div>
      <div class="events-search__section">
        <SpeakersSearch
          :text="query ?? ''"
          :infiniteScrollButtonId="uniqueId"
          @search="handleSearch($event)"
          @loading="handleSearchLoading($event)"
          @results="handleSearchResults($event)"
          @showLoadBtn="handleShowLoadBtn($event)"
          @speakersCount="handleSpeakersCount($event)"
        />
      </div>
      <div class="events-search__speaker-section">
        <SpeakersSearchResult :speakers="speakers" :showLoadBtn="showLoadBtn" :infiniteScrollButtonId="uniqueId" />
      </div>
    </div>
  </section>
</template>

<script lang="ts">
import SpeakersSearch from '@/components/elements/SpeakersSearch.vue'
import SpeakersSearchResult from '@/components/elements/SpeakersSerachResult.vue'
import Loader from '@/components/elements/Loader.vue'
import { SvgIconName } from '@/components/helpers/SvgIcon.vue'
import { SpeakerModel } from '@/models/SpeakerModel'
import router from '@/router'
import { defineComponent, ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouterStore } from '@/store/router'
import { storeToRefs } from 'pinia'
import uniqid from 'uniqid'
import { useWidgetMode } from '@/composables/useWidgetMode'
import { useSessionStoreHistory } from '@/composables/useSessionStoreHistory'
import BackButton from '@/components/elements/BackButton.vue'

export default defineComponent({
  name: 'SpeakerOverview',
  components: {
    Loader,
    SpeakersSearch,
    SpeakersSearchResult,
    BackButton
  },
  setup () {
    const { isWidgetMode, isWidgetModeRef } = useWidgetMode()

    const { t } = useI18n()

    const routerStore = useRouterStore()
    const { hasHistory } = storeToRefs(routerStore)
    const { changePage } = routerStore

    const query = ref<string | null>(null)

    const speakers = ref<SpeakerModel[]>([])
    const loading = ref<boolean>(false)
    const showLoadBtn = ref<boolean>(true)
    const speakersCount = ref<number>(0)

    const goToSearch = (text: string) => {
      if (isWidgetMode) {
        changePage('SpeakerOverview', { text })
      } else {
        router.replace(`/speakers/${text}`)
      }
    }

    const handleSearch = (text: string) => {
      query.value = text
      goToSearch(query.value)
    }

    const handleSearchLoading = (val: boolean) => {
      loading.value = val
    }

    const handleSearchResults = (res: SpeakerModel[]) => {
      speakers.value = res
    }

    const handleShowLoadBtn = (val: boolean) => {
      showLoadBtn.value = val
    }

    const handleSpeakersCount = (val: number) => {
      speakersCount.value = val
    }

    onMounted(() => {
      useSessionStoreHistory().append()
    })

    const uniqueId = uniqid()

    return {
      isWidget: isWidgetModeRef,
      t,
      uniqueId,
      hasHistory,
      query,
      loading,
      handleSearch,
      handleSearchLoading,
      handleSearchResults,
      handleSpeakersCount,
      speakersCount,
      SvgIconName,
      handleShowLoadBtn,
      showLoadBtn,
      speakers
    }
  }
})
</script>
<style lang="scss" scoped>
@import 'src/assets/styles/04-common/module-title';
.events-search {

  &__header-row {
    @media (max-width: 480px) {
      display: none;
    }
    display: flex;
    justify-content: space-between;
    align-items: flex-end;

    &__count {
      @media (max-width: 480px) {
        display: none;
      }
      padding-bottom: 12px;
      padding-right: 12px;
    }
  }

  &__module-title {
    @media (max-width: 480px) {
      display: none;
    }
    @include finder-module-title;
    font-size: 2.5rem;
    line-height: 1;
    color: #006eb7;
    margin: 1.5rem;
  }

  &__section {
    @media (max-width: 480px) {
      margin-bottom: -10%;
    }
    padding: 24px 0;
    padding-bottom: 10px;
  }

  &__sub_title {
    @include finder-module-sub-title;
  }
}

@include media-breakpoint-up(sm) {
  .events-search__module-title {
    display: block;
  }
}
</style>
