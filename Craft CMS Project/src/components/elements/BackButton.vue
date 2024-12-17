<template>
          <cta-button :icon-name="SvgIconName.arrowCircleLeft" @click="back()">
            {{ t('components_generic_button_back') }}
          </cta-button>
</template>

<script lang="ts">
import { useI18n } from 'vue-i18n'
import { useSessionStoreHistory } from '@/composables/useSessionStoreHistory'
import { useRouter } from 'vue-router'
import { SvgIconName } from '@/components/helpers/SvgIcon.vue'
import { useWidgetMode } from '@/composables/useWidgetMode'
import CtaButton from '@/components/elements/CtaButton.vue'

export default {
  name: 'BackButton',
  components: {
    CtaButton
  },
  props: {
    isWidgetMode: {
      type: Boolean,
      default: false
    },
    widgetMode: {
      type: String,
      default: ''
    }
  },
  setup () :any {
    const router = useRouter()
    const { t } = useI18n()
    const { isWidgetMode } = useWidgetMode()

    const back = () => {
      if (isWidgetMode) {
        useSessionStoreHistory().back()
      } else {
        router.go(-1)
      }
    }

    return {
      t,
      back,
      SvgIconName
    }
  }
}
</script>
