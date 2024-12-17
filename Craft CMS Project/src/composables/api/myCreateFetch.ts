import { useStore } from '@/store'
import { createFetch, useFetch } from '@vueuse/core'

export function myCreateFetch (): typeof useFetch {
  const store = useStore()

  return createFetch({
    baseUrl: store.$state.apiBaseUrl,
    options: {
      async beforeFetch ({ options }) {
        options.headers = {
          ...options.headers,
          Accept: 'application/json',
          'X-Vis-Domain': store.$state.visDomain.replace('https://', '').replace('http://', '')
        }
        options.credentials = 'include' // send (maybe) existing cookies with this api request

        if (store.$state.visValidation) {
          Object.assign(options.headers, {
            'X-Vis-Validation': store.$state.visValidation
          })
        }

        return { options }
      }
    },
    fetchOptions: {
      mode: 'cors'
    }
  })
}
