import createI18n from '@/i18n'
import { useFetchTranslations } from '@/composables/api/useFetchTranslations'

const i18n = createI18n()

export const updateLanguages = (langName: string, i18nData: Record<string, string>): void => {
  if (i18n.global.availableLocales.includes(langName)) {
    i18n.global.mergeLocaleMessage(langName, i18nData)
  } else {
    i18n.global.setLocaleMessage(langName, i18nData)
  }
}

export const loadTranslationsFromApi = async (langName: string, url: string): Promise<string> => {
  const { execute, data } = useFetchTranslations(langName, false, url)
  await execute()
  updateLanguages(langName, data.value as Record<string, string>)
  // don't need to actually return anything but this is to make typescript/async happy. Therefore: Return Promise....
  return new Promise<string>(resolve => {
    resolve('')
  })
}
