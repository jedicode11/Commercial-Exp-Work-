import { ref } from 'vue'
import { Appointment, AppointmentsLists } from '@/models/Appoinment'
import { UseFetchReturn } from '@vueuse/core'
import { myCreateFetch } from '@/composables/api/myCreateFetch'
import { useStore } from '@/store'
import { config } from '../../../app.config'
import demoAppointments from './../../models/demo/appoinments.json'

export function useFetchAppointmentsLists (
  exhibitorId: string,
  language: string,
  immediate = true
): UseFetchReturn<AppointmentsLists> {
  const store = useStore()
  if (!config.useAPI) {
    return {
      isFetching: false,
      isFinished: true,
      error: null,
      data: ref(demoAppointments.appointmentsList),
      execute: function () {
        return new Promise(resolve => {
          resolve('')
        })
      }
    } as unknown as UseFetchReturn<AppointmentsLists>
  }
  const useMyFetch = myCreateFetch()
  const editMode = store.$state.editMode
  let url = store.$state.urls.slices
  url = url.replace('{{exhId}}', exhibitorId).replace('{{language}}', language)
  url = url + '/appointments'
  if (editMode && store.$state.ticket) {
    url = url + `?ticket=${store.$state.ticket}`
  }

  // Filter the results and try to map certain properties
  return useMyFetch(url, {
    immediate: immediate,
    initialData: [],
    afterFetch (ctx) {
      return ctx
    }
  }).json()
}

export function useFetchAppointment (
  exhibitorId: string,
  appointmentId: string,
  language: string,
  immediate = true
): UseFetchReturn<Appointment> {
  const store = useStore()
  if (!config.useAPI) {
    return {
      isFetching: false,
      isFinished: true,
      error: null,
      data: ref(demoAppointments.appointment),
      execute: function () {
        return new Promise(resolve => {
          resolve('')
        })
      }
    } as unknown as UseFetchReturn<Appointment>
  }
  const useMyFetch = myCreateFetch()
  let url = store.$state.urls.slices
  url = url.replace('{{exhId}}', exhibitorId).replace('{{language}}', language)
  url = url + `/appointments/${appointmentId}`

  // Filter the results and try to map certain properties
  return useMyFetch(url, {
    immediate: immediate,
    afterFetch (ctx) {
      return ctx
    }
  }).json()
}
