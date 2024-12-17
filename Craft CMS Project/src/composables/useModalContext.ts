import { useModalContainerGlobalComponent } from '@/plugins/modalContainerPlugin'
import { computed, ComputedRef, Ref } from 'vue'
import { ModalTransitionDirection } from '@/components/helpers/ModalContainer.vue'

export interface UseModalContextReturn {
  transitionDirection: Ref<ModalTransitionDirection>,
  teleportTarget: ComputedRef<HTMLElement | undefined>,
  isFirstOpenedModal: ComputedRef<boolean>,
  isLastOpenedModal: ComputedRef<boolean>,
  setActive: () => void,
  setInactive: () => void
}

export function useModalContext (): UseModalContextReturn {
  const {
    transitionDirection,
    createModalContext,
    getTeleportTarget,
    getIsFirstOpenedModal,
    getIsLastOpenedModal,
    setModalActive,
    setModalInactive
  } = useModalContainerGlobalComponent()

  const modalId = createModalContext()
  const teleportTarget = computed(() => getTeleportTarget(modalId))
  const isFirstOpenedModal = computed(() => getIsFirstOpenedModal(modalId))
  const isLastOpenedModal = computed(() => getIsLastOpenedModal(modalId))

  const setActive = () => setModalActive(modalId)
  const setInactive = () => setModalInactive(modalId)

  return {
    transitionDirection,
    teleportTarget,
    isFirstOpenedModal,
    isLastOpenedModal,
    setActive,
    setInactive
  }
}
