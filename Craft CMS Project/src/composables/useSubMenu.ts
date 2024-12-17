import { onUnmounted, Ref, ref, watch } from 'vue'

export interface UseSubMenuReturn {
  isOpen: Ref<boolean>

  toggle (): void
}

export function useSubMenu (triggerElement: Ref): UseSubMenuReturn {
  const isOpen = ref(false)

  const open = () => {
    isOpen.value = true
  }

  const close = () => {
    isOpen.value = false
  }

  watch(isOpen, (value) => {
    if (value) {
      listenClickEvents(true)
    } else {
      listenClickEvents(false)
    }
  }, { immediate: false })

  const onClickEvent = (e: MouseEvent) => {
    const targetElements = document.elementsFromPoint(e.clientX, e.clientY)
    if (targetElements) {
      if (targetElements.includes(triggerElement.value)) {
        return
      }
    }
    close()
  }

  const listenClickEvents = (enable: boolean) => {
    document.removeEventListener('click', onClickEvent)
    if (enable) {
      document.addEventListener('click', onClickEvent)
    }
  }

  onUnmounted(() => {
    listenClickEvents(false)
  })

  const toggle = () => {
    if (isOpen.value === true) {
      close()
    } else {
      open()
    }
  }

  return {
    toggle,
    isOpen
  }
}
