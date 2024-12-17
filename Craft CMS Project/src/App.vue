<template>
  <finder-template :add-wrapper="false">
    <router-view />
  </finder-template>
</template>

<script lang="ts">
import { defineComponent, WritableComputedRef, onMounted, inject } from 'vue'
import FinderTemplate from '@/components/dev/finder-template/FinderTemplate.vue'

import { useLanguageStore } from './store/language'
import { useI18n } from 'vue-i18n'
import { useSiteId } from './composables/useSiteId'

export default defineComponent({
  name: 'App',
  components: {
    FinderTemplate
  },
  setup () {
    const i18n = useI18n()

    const languageStore = useLanguageStore()
    const { setLanguage } = languageStore

    const origin = inject<string | undefined>('app.origin')

    const initLang = process.env.VUE_APP_I18N_LOCALE ?? 'en'
    console.info('initial language:', initLang)
    setLanguage(initLang)
    console.log('i18n', i18n)
    i18n.locale = initLang as unknown as WritableComputedRef<any>
    console.log('🚀 ~ setup ~ i18n.locale:', i18n.locale)

    const { setupAppSites } = useSiteId()
    setupAppSites(process.env.VUE_APP_SITE || '')

    const loadIcons = () => {
      var ajax = new XMLHttpRequest()
      ajax.open('GET', `${origin ?? ''}/img/icons.svg`, true)
      ajax.responseType = 'document'
      ajax.onload = function () {
        if (ajax.responseXML) {
          document.body.insertBefore(ajax.responseXML.documentElement, document.body.childNodes[0])
        }
      }
      ajax.send()
    }

    onMounted(() => {
      loadIcons()
    })
  }
})
</script>

<style lang="scss">
  .clickable {
    cursor: pointer;
  }

  /* nicer nested checkboxes */
  .checkbox-form {
    .items {
      .item:hover {
        background: rgba($color: #000000, $alpha: 0.025);

        & > label > span:nth-child(1) {
          text-decoration: underline;
        }
      }
    }
  }
</style>
