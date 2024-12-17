import { useSnackbarGlobalComponent } from '@/plugins/snackbarPlugin'
import SnackbarMessageOptions from '@/models/SnackbarMessageOptions'

export interface UseSnackBarMessageReturn {
  show (): void,

  hide (messageId: number): void
}

export function useSnackbarMessage (message: SnackbarMessageOptions): UseSnackBarMessageReturn {
  const snackbar = useSnackbarGlobalComponent()
  let messageId = ''

  const show = () => {
    messageId = snackbar.showMessage(message)
  }

  const hide = () => {
    snackbar.hideMessage(messageId)
  }

  return {
    show,
    hide
  }
}
