import { ref } from 'vue'
import { useCssVar } from '@vueuse/core'
import Swal from 'sweetalert2'

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export function useSweetAlert (): any {
  const el = ref(null)
  const colorSuccess = useCssVar('--finder-color-success', el)
  const colorError = useCssVar('--finder-color-error', el)
  const finderSwal = Swal.mixin({
    confirmButtonColor: colorSuccess.value,
    denyButtonColor: colorError.value
  })

  return {
    finderSwal
  }
}
