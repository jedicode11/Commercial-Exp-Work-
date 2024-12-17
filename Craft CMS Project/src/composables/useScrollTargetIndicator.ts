import { onBeforeUnmount, onMounted, ref, Ref, shallowRef } from 'vue'

export interface UseScrollTargetIndicatorReturn {
  closestSelector: Ref<string>
}

export function useScrollTargetIndicator (targetSelectors: Ref<Array<string>>, scrollAreaElement?: HTMLElement): UseScrollTargetIndicatorReturn {
  const intersectionObserver = shallowRef()
  const closestSelector = ref<string>('')
  const intersections = {} as Record<string, number>

  const update = () => {
    const sortedIntersections = Object.entries(intersections).sort(([, valueA], [, valueB]) => {
      return valueB - valueA
    })
    closestSelector.value = sortedIntersections.length ? sortedIntersections[0][0] : ''
  }

  const intersectionCallback = (entries: IntersectionObserverEntry[]) => {
    entries.forEach((entry) => {
      if (entry) {
        const entrySelector = '#' + entry.target.id
        intersections[entrySelector] = entry.intersectionRatio
      }
    })
    update()
  }

  onMounted(() => {
    intersectionObserver.value = new IntersectionObserver(intersectionCallback, {
      root: scrollAreaElement || null, // Defaults to the browser viewport
      threshold: [0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1]
    })
    targetSelectors.value.forEach((selector) => {
      let targetElement
      try {
        targetElement = document.querySelector(selector)
      } catch (e) {
        console.warn(`Unable to watch intersections of "${selector}"`)
      }
      if (targetElement) {
        intersectionObserver.value.observe(targetElement)
      }
    })
    update()
  })

  onBeforeUnmount(() => {
    intersectionObserver.value.disconnect()
  })

  return {
    closestSelector
  }
}
