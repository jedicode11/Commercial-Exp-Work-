<template>
  <SpeakersGrid v-if="speakers.length" :speakersList="speakers"/>
  <div v-else class="events-search-result__module-title">{{ t('components_events_search_no_results') }}</div>
  <button v-if="showLoadBtn" :id="`infinite-scroll-button-${infiniteScrollButtonId}`" class="events-search-result__show-more-btn">{{ t('components_generic_button_show_more') }}</button>
</template>

<script lang="ts">
import SpeakersGrid from '@/components/modules/search/grid/SpeakersGrid.vue'
import { SpeakerModel } from '@/models/SpeakerModel'
import { defineComponent, PropType } from 'vue'
import { useI18n } from 'vue-i18n'

export default defineComponent({
  name: 'SpeakersSearchResult',
  components: {
    SpeakersGrid
  },
  props: {
    speakers: {
      type: Array as PropType<SpeakerModel[]>,
      required: true,
      default: () => []
    },
    showLoadBtn: {
      type: Boolean,
      required: true,
      default: true
    },
    infiniteScrollButtonId: String
  },
  setup () {
    const { t } = useI18n()

    return {
      t
    }
  }
})
</script>
<style lang="scss" scoped>
@import 'src/assets/styles/04-common/module-title';
.speakers-grid {
  margin-bottom: 24px;
}

.events-search-result {

  &__module-title {
    @include finder-module-title;
    font-size: 3rem;
    line-height: 1;
    color: #006eb7;
    margin: 1.5rem;
  }

  &__show-more-btn {
    padding: 4px 8px;
    line-height: 1rem;
    border-radius: 4px;
  }
}
</style>
