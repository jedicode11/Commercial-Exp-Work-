<template>
  <teleport to="body" :disabled="!teleportTargets.length">
    <div class="modal-container">
      <transition name="transition" mode="out-in">
        <div v-if="activeModals.length" class="modal-window">
          <div class="modal-window__background"></div>
          <div class="modal-window__inner">
            <div class="modal-window__page-wrapper" ref="pageWrapperElement">
              <div v-for="target in teleportTargets"
                   class="modal-window__page"
                   :style="{'--modal-page-z-order':getModalZ(target.id)}"
                   :key="target.id"
                   :ref="(el) => associateElement(target.id, el)"
              ></div>
            </div>
          </div>
        </div>
      </transition>
    </div>
  </teleport>
</template>

<script lang="ts">
import { computed, defineComponent, Ref, ref, Transition } from 'vue'
import { dispatchFinderEvent, FinderEvents } from '@/utils/events'
import { onClickOutside } from '@vueuse/core'

export enum ModalTransitionDirection {
  forward = 'forward',
  backward = 'backward'
}

export interface ModalTeleportTarget {
  id: string
  targetElement: HTMLElement | undefined
  openedTimestamp: number
  isActive: boolean
}

export interface ModalContainerGlobalComponent {
  createModalContext: (/* componentRef: Ref<HTMLElement | undefined> */) => string
  getTeleportTarget: (id: string) => HTMLElement | undefined
  getIsFirstOpenedModal: (id: string) => boolean
  getIsLastOpenedModal: (id: string) => boolean
  setModalActive: (id: string) => void
  setModalInactive: (id: string) => Promise<void>
  transitionDirection: Ref<ModalTransitionDirection>
}

export default defineComponent({
  name: 'ModalContainer',
  components: { Transition },
  setup: (props, { expose }) => {
    const pageWrapperElement: Ref<HTMLElement | null> = ref(null)
    const teleportTargets: Ref<Array<ModalTeleportTarget>> = ref([])
    const transitionDirection = ref(ModalTransitionDirection.forward)
    let modalIdCounter = 1

    const activeModals = computed(() => {
      return teleportTargets.value
        .filter(target => target.isActive)
        .sort((targetA, targetB) => targetA.openedTimestamp - targetB.openedTimestamp)
        .map(modal => modal.id)
    })

    const getModalById = (id: string) => {
      const [modal] = teleportTargets.value.filter(target => target.id === id)
      return modal
    }

    const createModalContext = (): string => {
      const modalId = `modal${modalIdCounter++}`
      const newTarget = {
        id: modalId,
        targetElement: undefined
      } as ModalTeleportTarget
      teleportTargets.value.push(newTarget)
      return modalId
    }

    const associateElement = (id: string, el: HTMLElement) => {
      const item = getModalById(id)
      if (item) {
        item.targetElement = el
      }
    }

    const getTeleportTarget = (id: string): HTMLElement | undefined => {
      const item = getModalById(id)
      if (item && item.targetElement) {
        return item.targetElement
      }
      return undefined
    }

    const getIsFirstOpenedModal = (id: string): boolean => {
      const item = getModalById(id)
      return (item && item.isActive && activeModals.value.indexOf(id) === 0)
    }

    const getIsLastOpenedModal = (id: string): boolean => {
      const item = getModalById(id)
      return (item && item.isActive && activeModals.value.indexOf(id) === activeModals.value.length - 1)
    }

    const setModalActive = (id: string) => {
      const item = getModalById(id)
      if (item && !item.isActive) {
        item.isActive = true
        item.openedTimestamp = Date.now()
      }
    }

    const setModalInactive = async (id: string): Promise<void> => {
      const item = getModalById(id)
      if (item && item.isActive) {
        item.isActive = false
        item.openedTimestamp = 0
      }
    }

    const getModalZ = (id: string) => {
      return activeModals.value.indexOf(id) + 1
    }

    onClickOutside(pageWrapperElement, () => {
      dispatchFinderEvent(FinderEvents.closeAllModals)
    })

    expose({
      transitionDirection,
      createModalContext,
      getTeleportTarget,
      getIsFirstOpenedModal,
      getIsLastOpenedModal,
      setModalActive,
      setModalInactive
    } as ModalContainerGlobalComponent)

    return {
      pageWrapperElement,
      teleportTargets,
      activeModals,
      associateElement,
      getModalZ
    }
  }
})
</script>

<style lang="scss" scoped>
@import 'src/assets/styles/04-common/modal-contents';

.modal-container {
  position: fixed;
  top: 0;
  left: 0;
  z-index: z('modal-container');
}

.modal-window {
  $transition-time: 0.2s;

  --titlebar-height: var(--finder-metabar-height-small);
  @media (min-width: mq(lg, false)) {
    --titlebar-height: var(--finder-metabar-height);
  }

  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;

  &__inner {
    position: relative;
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
    transition: transform;
  }

  &__background {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: var(--finder-color-modal-layer-bg);
  }

  &__page-wrapper {
    position: relative;
    width: 100%;
    height: 100%;
    background-color: var(--finder-color-light);
    @include finder-modal-shadow;
    overflow: hidden;
    @media (min-width: mq(lg, false)) {
      max-width: var(--finder-content-modal-max-width);
      max-height: var(--finder-content-modal-max-height);
      border-radius: 14px;
    }
  }

  &__page {
    position: absolute;
    top: 0;
    left: 0;
    bottom: 0;
    right: 0;
    z-index: var(--modal-page-z-order);
  }

  // animations
  &.transition-enter-active,
  &.transition-leave-active {
    transition: opacity $transition-time ease;

    .modal-window__inner {
      transition: transform $transition-time ease;
    }
  }

  &.transition-leave-to,
  &.transition-enter-from {
    opacity: 0;

    .modal-window__inner {
      transform: translateY(20px);
    }
  }

  &.transition-leave-from,
  &.transition-enter-to {
    opacity: 1;

    .modal-window__inner {
      transform: translateY(0);
    }
  }
}
</style>
