import { onBeforeUnmount, onMounted, Ref, ref, watch } from 'vue'
import { getPsOffsetDown, getPsOffsetUp, PSOffsetIds, setPsOffset } from '@/composables/vis-modules/pageScrolling'
import { useElementBounding, useEventListener, useIntersectionObserver } from '@vueuse/core'

export interface UseStickMetaBarReturn {
  isSticky: Ref
  outerElement: Ref
  innerElement: Ref
  offsetTop: Ref
}

export function useStickyMetaBar (isEnabled: boolean): UseStickMetaBarReturn {
  const isSticky = ref<boolean>(false)
  const outerElement = ref<HTMLElement | null>(null)
  const innerElement = ref<HTMLElement | null>(null)
  const { height: innerElementHeight } = useElementBounding(innerElement)
  const offsetTop = ref<number>(0)
  const isScrollingUp = ref<boolean>(false)
  let lastScrollTop = 0

  const updateOffsetTop = () => {
    // determine offset top with values from 'PageScrolling' module,
    // to avoid being overlapped by other fixed elements (e.g. cms meta bar)
    if (isScrollingUp.value === true) {
      offsetTop.value = getPsOffsetUp() - innerElementHeight.value
    } else {
      offsetTop.value = getPsOffsetDown() - innerElementHeight.value
    }
  }

  const updateScrollDirection = () => {
    const scrollTop = document.documentElement.scrollTop
    const scrollDst = scrollTop - lastScrollTop
    lastScrollTop = scrollTop
    isScrollingUp.value = scrollDst < 0
  }

  watch(isScrollingUp, () => {
    // cms meta bar will collapse when scrolling up (unless in mobile view),
    // so update offsetTop when scroll direction changes
    updateOffsetTop()
  })

  watch(innerElementHeight, (newHeight) => {
    // inform global PageScrolling module about height updates (e.g. on breakpoint change)
    setPsOffset(PSOffsetIds.metabar, newHeight)
  })

  if (isEnabled) {
    useEventListener(document, 'scroll', updateScrollDirection)

    // will be triggered by page scrolling module if any offset (e.g. cms metabar on breakpont change) changes
    // required to avoid race conditions during initialization
    useEventListener(window.document, 'pageScrolling.offsetChange', () => {
      updateOffsetTop()
    })

    useIntersectionObserver(outerElement, (entries: IntersectionObserverEntry[]) => {
      const [{ isIntersecting }] = entries
      isSticky.value = !isIntersecting
    }, { threshold: 1 })
  }

  onMounted(() => {
    // register this component as fixed element in 'PageScrolling' module , as it has to be taken into account
    // when calculating anchor scroll targets  + d:vis floating nav / accessibility mode positions
    setPsOffset(PSOffsetIds.metabar, innerElementHeight.value)
    // no need to call updateOffsetTop() here,
    // as 'setPsOffset' will cause 'pageScrolling.offsetChange' event to fire
  })

  onBeforeUnmount(() => {
    setPsOffset(PSOffsetIds.metabar, 0)
  })

  return {
    isSticky,
    outerElement,
    innerElement,
    offsetTop
  }
}
