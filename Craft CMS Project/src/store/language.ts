import { defineStore } from 'pinia'

export const useLanguageStore = defineStore('language', {
  state: () => ({
    language: 'en'
  }),

  getters: {
    currentLanguage: (state) => state.language
  },

  actions: {
    setLanguage (language: string) {
      this.language = language
    }
  }

})
