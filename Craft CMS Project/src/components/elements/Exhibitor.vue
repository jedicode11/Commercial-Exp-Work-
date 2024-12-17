<template>
  <div class="exhibitor">
    <div class="exhibitor__details">
      <cta-button v-if="widgetMode?.toLowerCase() !== 'stele'" class="exhibitor__details__button"
                  :is-small="true"
                  :is-secondary="true"
                  :is-small-on-mobile="false"
                  @click="goToExhibitorLink()"> {{ name }} </cta-button>
      <cta-button v-else class="exhibitor__details__button--disabled"
                  :is-small="true"
                  :is-secondary="true"
                  :is-small-on-mobile="false"
                  disabled> {{ name }} </cta-button>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, inject, computed } from 'vue'
import { useWidgetMode } from '@/composables/useWidgetMode'
import { useI18n } from 'vue-i18n'
import CtaButton from '@/components/elements/CtaButton.vue'

export default defineComponent({
  name: 'ExhibitorComponent',
  components: {
    CtaButton
  },
  props: {
    logo: String,
    name: String,
    link: String,
    address: String
  },

  setup (props) {
    const { isWidgetMode } = useWidgetMode()
    const widgetMode = inject<string | undefined>('app.widgetMode')
    const { t } = useI18n()

    let parentSpeakerFragment: string | undefined
    if (isWidgetMode) {
      parentSpeakerFragment = inject<string | undefined>('app.parentSpeakerFragment')
      console.info('parent url speaker fragment:', parentSpeakerFragment)
    }

    const origin = inject('app.origin')

    const hostname = computed(() => {
      if (isWidgetMode) {
        return origin + '/'
      }
      return '/'
    })

    const goToExhibitorLink = () => {
      console.log('🚀 ~ file: Exhibitor.vue ~ line 78 ~ goToExhibitoretails')
      let url = props.link
      if (url && !/^https?:\/\//i.test(url)) {
        url = 'http://' + url
      }
      props.link && window.open(url, '_blank')
    }

    return {
      t,
      hostname,
      widgetMode,
      goToExhibitorLink
    }
  }
})
</script>

<style lang="scss" scoped>
.exhibitor {
  display: flex;
  flex-direction: column;
  gap: 10px;

  a {
    text-decoration: none;
    color: var(--finder-color-primary);
  }

  img {
    max-width: 100%;
    max-height: 100%;
    object-fit: cover;
  }

  &__details {
    line-height: 100%;
    text-align: left;

    &__name {
      margin-bottom: 5px;
    }

    &__url {
      display: inline-block;
      margin-top: 10px;
      font-size: larger;
    }
  }
}

.exhibitor__details__button--disabled {
  pointer-events: none;
  // opacity: 0.5;
}

@media (max-width: 480px) {
  .exhibitor {
    flex-direction: row;
    gap: 0;
  }
}
</style>
