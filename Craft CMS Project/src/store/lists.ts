import { defineStore } from 'pinia'
import { useFetchLanguages, Language } from '@/composables/api/useFetchLanguages'
import { TransferSection, useFetchDataTransfer } from '@/composables/api/useFetchDataTransfer'

export interface ListsState {
  languages: Array<Language>
  dataTransferSection: TransferSection | null
}

export const useLists = defineStore('lists', {
  state: (): ListsState => ({
    languages: [],
    dataTransferSection: null
  }),

  actions: {
    async fetchLanguages (userLanguage: string, forceLoad = false) {
      if (this.languages.length > 0 && !forceLoad) {
        return
      }

      const { data, execute } = useFetchLanguages(userLanguage, false)
      await execute()
      if (data.value) {
        this.languages = data.value as unknown as Array<Language>
      }
    },
    async fetchDataTransfer (section: string | undefined = undefined, forceLoad = false): Promise<TransferSection | null> {
      if (this.dataTransferSection && !forceLoad) {
        return this.dataTransferSection
      }

      const { data, execute } = useFetchDataTransfer(section, false)
      await execute()
      if (data.value) {
        this.dataTransferSection = data.value as unknown as TransferSection
      }
      return this.dataTransferSection
    }
  }
})
