import { onBeforeUnmount, onMounted, ref, Ref, shallowRef, watch } from 'vue'
import { throttle as _throttle } from 'lodash-es'

export interface NavItemElement {
  id: string,
  el: HTMLElement
  index: number
}

export interface UseExpandableNavReturn {
  navItemElements: Ref<Record<string, NavItemElement>>,
  navWrapperElement: Ref<HTMLElement | null | undefined>,
  primaryNavBoundsRight: Ref<number>,

  isPrimaryItem (id: string): boolean
}

export function useExpandableNav (isEnabled: Ref<boolean>, expandBtnWidth: Ref<number>): UseExpandableNavReturn {
  const navItemElements = ref({} as Record<string, NavItemElement>)
  const navWrapperElement = ref<HTMLElement | null>()
  const resizeObserver = shallowRef()
  const primaryNavItems = ref([] as Array<string>)
  const primaryNavBoundsRight = ref<number>(0)

  const setPrimaryItem = (isPrimary: boolean, id: string): void => {
    if (isPrimary && !primaryNavItems.value.includes(id)) {
      primaryNavItems.value.push(id)
    } else if (!isPrimary && primaryNavItems.value.includes(id)) {
      primaryNavItems.value.splice(primaryNavItems.value.indexOf(id), 1)
    }
  }

  const isPrimaryItem = (id: string) => {
    if (!isEnabled.value) {
      return true
    }
    return primaryNavItems.value.includes(id)
  }

  const update = () => {
    if (navWrapperElement.value) {
      let lastPrimaryId = ''
      const wrapperRect = (navWrapperElement.value as HTMLElement).getBoundingClientRect()
      const itemList = Object.values(navItemElements.value)
        .sort((a, b) => {
          return a.index - b.index
        })

      itemList.forEach((navItemElement, index) => {
        const isLast = index === itemList.length - 1
        const elementRect = navItemElement.el.getBoundingClientRect()
        const isPrimary = (elementRect.right - wrapperRect.left) + (isLast ? 0 : expandBtnWidth.value) < (wrapperRect.width)
        setPrimaryItem(isPrimary, navItemElement.id)
      })
      lastPrimaryId = primaryNavItems.value[primaryNavItems.value.length - 1]
      primaryNavBoundsRight.value = navItemElements.value[lastPrimaryId].el.getBoundingClientRect().right - wrapperRect.left
    }
  }
  const throttledUpdate = _throttle(update, 100)

  onMounted(() => {
    resizeObserver.value = new ResizeObserver(throttledUpdate)

    if (isEnabled.value) {
      resizeObserver.value.observe(navWrapperElement.value)
      const [firstNavItem] = Object.values(navItemElements.value)
      // observe resizes on some nav item (picking the first one here)
      // as item width will change as soon as font is loaded
      if (firstNavItem) {
        resizeObserver.value.observe(firstNavItem.el)
      }
      throttledUpdate()
    }
  })

  onBeforeUnmount(() => {
    if (resizeObserver.value) {
      resizeObserver.value.disconnect()
    }
  })

  watch(isEnabled, (value) => {
    let wrapperElement
    if (resizeObserver.value) {
      resizeObserver.value.disconnect()
      if (value) {
        resizeObserver.value.observe(navWrapperElement.value)
      }
    }
    if (value) {
      // reset scroll position when leaving mobileMode as it might have changed by dragscroll
      if (navWrapperElement.value) {
        wrapperElement = navWrapperElement.value as HTMLElement
        if (wrapperElement) {
          wrapperElement.scrollLeft = 0
        }
      }
    }
  })

  return {
    navItemElements,
    navWrapperElement,
    isPrimaryItem,
    primaryNavBoundsRight
  }
}
