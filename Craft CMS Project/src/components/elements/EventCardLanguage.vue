<template>
  <div class="event-card-language">
    <svg-icon :icon-name="SvgIconName.languageTrans" />
    <span>{{ languageFormatted || '&nbsp;' }}</span>
  </div>
</template>

<script lang="ts">
import { PropType, defineComponent } from 'vue'
import SvgIcon, { SvgIconName } from '@/components/helpers/SvgIcon.vue'

export default defineComponent({
  name: 'EventCardLanguage',
  components: {
    SvgIcon
  },
  props: {
    language: Object as PropType<Array<{ title: string }> | string>
  },
  data () {
    return {
      SvgIconName
    }
  },
  computed: {
    languageFormatted () {
      if (!this.language) {
        return ''
      }

      return Array.isArray(this.language) ? this.language.map(l => l.title).join(', ') : this.language
    }
  }
})
</script>

<style lang="scss" scoped>
.event-card-language {
  padding: 0 4px;

  .svg-icon {
    margin-right: 4px;
  }
}
</style>
