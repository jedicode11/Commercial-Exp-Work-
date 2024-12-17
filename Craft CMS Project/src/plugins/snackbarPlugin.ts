import { App, createVNode, render, VNode } from 'vue'
import Snackbar, { SnackbarGlobalComponent } from '@/components/elements/Snackbar.vue'

let snackbarVNode: VNode

const SnackbarPlugin = {
  install (app: App): void {
    app.component('Snackbar', Snackbar)
    if (!snackbarVNode) {
      snackbarVNode = createVNode(Snackbar)
      snackbarVNode.appContext = app._context
      render(snackbarVNode, document.createElement('div'))
    }
  }
}

function useSnackbarGlobalComponent (): SnackbarGlobalComponent {
  if (!snackbarVNode.component?.exposed) {
    throw new Error('Unable to access global Snackbar component!')
  }
  return snackbarVNode.component.exposed as SnackbarGlobalComponent
}

export { SnackbarPlugin, useSnackbarGlobalComponent }
