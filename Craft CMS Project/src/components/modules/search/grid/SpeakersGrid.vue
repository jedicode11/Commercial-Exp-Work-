<template>
  <div tag="div" class="speakers-grid">
    <SpeakerGridItem v-for="item in speakersList" :key="item.id" :speaker="item" :isRegistered="true" />
  </div>
</template>

<script lang="ts">
import { SpeakerModel } from '@/models/SpeakerModel'
import { defineComponent, PropType } from 'vue'
import SpeakerGridItem from '../grid-item/SpeakerGridItem.vue'

export default defineComponent({
  name: 'SpeakersGrid',
  components: {
    SpeakerGridItem
  },
  props: {
    speakersList: {
      type: Object as PropType<Array<SpeakerModel>>,
      required: true
    }
  },
  setup () {
    return {
    }
  }
})
</script>

<style lang="scss" scoped>
.speakers-grid {

  display: flex;
  flex-wrap: wrap;
  margin: 0;
  scrollbar-width: none; /* hide scrollbars */
  -ms-overflow-style: none; /* for Firefox */

  /* for Internet Explorer, Edge */
  &::-webkit-scrollbar {
    display: none;
    width: 0 !important; /* for Chrome, Safari, and Opera */
  }

  @media (max-width: 480px) {
    display: block;
    flex-direction: column;
    align-items: center; // Centered vertically mobile
    justify-content: center;
    overflow-y: scroll;
    // max-height: 70vh;
    padding-bottom: 10%;
    gap: 24px;
  }

  gap: 48px 18px;

  & > * {
    @media (max-width: 480px) {
      flex: 1;
      min-width: calc(100% - 2 * 18px / 3);
      // width: calc(100% - 2 * 18px / 3);
      width: 100%;
    }

    min-width: calc(33.333% - 2 * 18px / 3);
    width: calc(33.333% - 2 * 18px / 3);
  }
}
</style>
