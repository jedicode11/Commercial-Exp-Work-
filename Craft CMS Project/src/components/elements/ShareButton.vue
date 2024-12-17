<template>
  <div class="share-btn">
    <icon-button :text="t('components_share_button_text')" :icon-name="SvgIconName.share" isColored id="dropdown-share-btn" data-bs-toggle="dropdown" aria-expanded="false"></icon-button>
    <div class="dropdown-menu" aria-labelledby="dropdown-share-btn">
      <form class="row g-3">
        <div class="col-12 mx-auto email-field">
          <label for="exampleInputEmail1" class="form-label not-allowed">{{ t('components_share_button_share_by_email') }}</label>
          <input type="email" v-model="email" class="form-control" aria-describedby="emailHelp" :placeholder="t('components_share_button_placeholder')" @keyup.enter="shareByMail"/>
          <!--Was in the input id="exampleInputEmail1" -->
          <div class="col-12 social-buttons">
            <icon-button :text="t('components_share_button_text_xing')" :icon-name="SvgIconName.socialXing" isColored :url="xingUrl"></icon-button>

            <icon-button :text="t('components_share_button_text_facebook')" :icon-name="SvgIconName.socialFacebook" isColored :url="FacebookIcon"></icon-button>

            <icon-button :text="t('components_share_button_text_linkedin')" :icon-name="SvgIconName.socialLinkedin" isColored :url="LinkedInIcon"></icon-button>
          </div>

          <!-- <div class="ui basic label pointing red" v-if="validateEmail">
            {{ validateEmail }}
          </div> -->
        </div>
        <div class="col-9">
          <button type="button" class="btn btn-link btn-sm" @click="copyLink">{{ t('components_share_button_copy_link') }}</button>
        </div>
        <div class="col-3 d-flex align-items-center">
          <cta-button isSecondary isSmall @click="shareByMail">{{ t('components_share_button_share') }}</cta-button>
        </div>
      </form>
    </div>
  </div>
</template>

<script lang="ts">
import { useI18n } from 'vue-i18n'
import { defineComponent, computed, ref, inject, toRef } from 'vue'
import { SvgIconName } from '@/components/helpers/SvgIcon.vue'
import IconButton from '@/components/elements/IconButton.vue'
import CtaButton from '@/components/elements/CtaButton.vue'
import { useWidgetMode } from '@/composables/useWidgetMode'

export default defineComponent({
  name: 'ShareButton',
  props: {
    id: {
      type: String,
      required: true
    },
    subject: {
      type: String,
      required: false,
      default: 'New mail'
    }
  },
  components: {
    IconButton,
    CtaButton
  },

  setup (props) {
    const { t } = useI18n()
    const { isWidgetMode } = useWidgetMode()

    const parentUrl = inject<string | undefined>('app.parentUrl')
    const parentEventFragment = inject<string | undefined>('app.parentEventFragment')

    const id = toRef(props, 'id')
    const subject = toRef(props, 'subject')
    const email = ref(null)
    const emailRule = /^\w+([.-]?\w+)*@\w+([.-]?\w+)*(\.\w{2,3})+$/

    const getUrl = () => {
      if (isWidgetMode) {
        if (parentUrl && parentEventFragment) {
          return parentUrl.split('?')[0] + '?' + parentEventFragment.replace('{uri}', `${id.value}`)
        } else {
          console.warn('Missing data-static-link-root or data-static-link-event-fragment')
          return ''
        }
      } else {
        return window.location.href
      }
    }

    const validateEmail = computed(() => {
      if (email.value) {
        return !emailRule.test(email.value) ? 'The Input field is required' : ''
      } else {
        return ''
      }
    })

    const copyLink = () => {
      const url = getUrl()
      navigator.clipboard.writeText(url)
    }

    const shareByMail = () => {
      const mail = email.value
      const url = getUrl()
      // TODO subject - get from event
      // window.location.href = `mailto:${mail}?subject=${t('components_share_button_mail_subject')}&body=${t('components_share_button_mail_text')}${url}`
      window.open(`mailto:${mail}?subject=${subject.value}&body=${t('components_share_button_mail_text')}${url}`, '_blank')
    }

    const xingUrl = computed(() => {
      return 'https://www.xing.com/spi/shares/new?url=' + getUrl()
    })

    const FacebookIcon = computed(() => {
      return 'http://www.facebook.com/sharer.php?u=' + getUrl()
    })

    const LinkedInIcon = computed(() => {
      return 'https://www.linkedin.com/sharing/share-offsite/?url=' + getUrl()
    })

    return {
      t,
      SvgIconName,
      copyLink,
      shareByMail,
      email,
      validateEmail,
      xingUrl,
      FacebookIcon,
      LinkedInIcon
    }
  }
})
</script>

<style lang="scss" scoped>
.social-buttons {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}

.not-allowed {
  pointer-events: none;
}

.email-field {
  width: 90% !important;
}
</style>
