import { createI18n, LocaleMessages, VueMessageType } from 'vue-i18n'
/**
 * Load locale messages
 *
 * The loaded `JSON` locale messages is pre-compiled by `@intlify/vue-i18n-loader`, which is integrated into `vue-cli-plugin-i18n`.
 * See: https://github.com/intlify/vue-i18n-loader#rocket-i18n-resource-pre-compilation
 */
function loadLocaleMessages (): LocaleMessages<VueMessageType> {
  const locales = require.context('./locales', true, /[A-Za-z0-9-_,\s]+\.json$/i)
  const messages: LocaleMessages<VueMessageType> = {}
  locales.keys().forEach(key => {
    const matched = key.match(/([A-Za-z0-9-_]+)\./i)
    if (matched && matched.length > 1) {
      const locale = matched[1]
      messages[locale] = locales(key).default
    }
  })
  console.log('🚀 ~ loadLocaleMessages ~ messages:', messages)
  return messages
}

console.log('🚀 ~ createI18n')
// export default (locale: string = process.env.VUE_APP_I18N_LOCALE!): any => {
export default (locale: string | undefined = process.env.VUE_APP_I18N_LOCALE): any => {
  console.log('🚀 ~ createI18n ? locale:', locale)
  return createI18n({
    legacy: false,
    allowComposition: true,
    globalInjection: true,
    locale: locale || 'en',
    fallbackLocale: process.env.VUE_APP_I18N_FALLBACK_LOCALE || 'en',
    messages: loadLocaleMessages() as any
  })
}
