import { defineStore } from 'pinia'
import { useStore } from '@/store'
import { Appointment, AppointmentsLists, createAppointment } from '@/models/Appoinment'
import { useFetchAppointment, useFetchAppointmentsLists } from '@/composables/api/useFetchAppointments'

interface AppointmentsState {
  appointmentsLists: AppointmentsLists,
  appointmentDetails: Appointment,
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  selectedAppointmentFilter: Array<any>
}

let currExhId = ''

export const useAppointments = defineStore('appointments', {
  state: (): AppointmentsState => ({
    appointmentsLists: { listPast: [], listFuture: [] },
    appointmentDetails: createAppointment(),
    selectedAppointmentFilter: []
  }),

  actions: {
    async fetchAppointmentsLists (forceLoad = false) {
      const store = useStore()

      if (currExhId !== store.$state.exhId || forceLoad) {
        currExhId = store.$state.exhId
        this.selectedAppointmentFilter = []
        const { data, execute } = useFetchAppointmentsLists(store.$state.exhId, store.$state.langCode, false)
        await execute()
        if (data.value) {
          this.appointmentsLists = data.value
        }
      }
    },
    async fetchAppointment (appointmentId: string) {
      const store = useStore()

      const { data, execute } = useFetchAppointment(store.$state.exhId, appointmentId, store.$state.langCode, false)
      await execute()
      if (data.value) {
        this.appointmentDetails = data.value
      }
    }
  }
})
