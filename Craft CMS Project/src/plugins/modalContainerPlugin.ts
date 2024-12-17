import { App, createVNode, render, VNode } from 'vue'
import ModalContainer, { ModalContainerGlobalComponent } from '@/components/helpers/ModalContainer.vue'

let modalContainerVNode: VNode

const ModalContainerPlugin = {
  install (app: App): void {
    app.component('ModalContainer', ModalContainer)
    if (!modalContainerVNode) {
      modalContainerVNode = createVNode(ModalContainer)
      modalContainerVNode.appContext = app._context
      render(modalContainerVNode, document.createElement('div'))
    }
  }
}

function useModalContainerGlobalComponent (): ModalContainerGlobalComponent {
  if (!modalContainerVNode.component?.exposed) {
    throw new Error('Unable to access global ModalContainer component!')
  }
  return modalContainerVNode.component.exposed as ModalContainerGlobalComponent
}

export { ModalContainerPlugin, useModalContainerGlobalComponent }
