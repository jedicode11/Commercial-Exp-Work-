<template>
  <EventsGrid v-if="events.length || loading" :eventsList="events" />
  <div v-else class="events-search-result__module-title">{{ t('components_events_search_no_results') }}</div>
  <button v-if="showLoadBtn && !loading" :id="`infinite-scroll-button-${infiniteScrollButtonId}`"
    class="events-search-result__show-more-btn" @click="handleShowMoreClick()">{{ t('components_generic_button_show_more')
    }}</button>
</template>

<script lang="ts">
import EventsGrid from '@/components/modules/search/grid/EventsGrid.vue'
import { EventModel } from '@/models/EventModel'
import { defineComponent, onMounted, PropType } from 'vue'
import { useI18n } from 'vue-i18n'

export default defineComponent({
  name: 'EventsSearchResult',
  components: {
    EventsGrid
  },
  props: {
    events: {
      type: Array as PropType<EventModel[]>,
      required: true,
      default: () => []
    },
    loading: {
      type: Boolean,
      required: true
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

    onMounted(() => {

    })

    const handleShowMoreClick = () => {
      console.log('Button clicked!')
    }

    return {
      t,
      handleShowMoreClick
    }
  }
})
</script>
<style lang="scss" scoped>
@import 'src/assets/styles/04-common/module-title';

.events-search-result__module-title {
  @include finder-module-title;
  font-size: 3rem;
  line-height: 1;
  color: var(--finder-color-primary);
  margin: 1.5rem;
}

.events-search-result__show-more-btn {
  padding: 4px 8px;
  line-height: 1rem;
  border-radius: 4px;
}
</style>
