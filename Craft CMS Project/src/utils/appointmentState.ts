import { useStore } from '@/store'
import { Appointment } from '@/models/Appoinment'
import { computed, Ref } from 'vue'

interface AppointmentState {
  hasDownloads: Ref<boolean | null>
}

export function getAppointmentState (appointment: Ref<Appointment | null>): AppointmentState {
  const store = useStore()
  const isEditMode = store.$state.editMode
  const isKioskMode = store.$state.kioskMode

  const hasDownloads = computed(() => {
    return !isKioskMode && appointment.value && appointment.value.pdfs && (appointment.value.pdfs.length > 0 || isEditMode)
  })

  return {
    hasDownloads
  }
}
