import { computed, ComputedRef, readonly, Ref, ref } from 'vue'
import { createModalSectionAnchor } from '@/models/ModalSectionAnchor'
import { SectionAnchor } from '@/models/base'

export interface ModalSectionTarget {
  id: string,
  title: string,
  top: Ref,
  height: Ref
}

export interface ModalSectionsProvider {
  addSection: (id: string, title: string, top: Ref<number>, height: Ref<number>) => void
  removeSection: (id: string) => void
}

export interface UseModalSectionsReturn {
  provider: ModalSectionsProvider,
  sections: ComputedRef<readonly SectionAnchor[]>
  reset: () => void
}

export function useModalSections (): UseModalSectionsReturn {
  const sectionsList = ref([] as Array<ModalSectionTarget>)

  const addSection = (id: string, title: string, top: Ref<number>, height: Ref<number>) => {
    sectionsList.value.push({ id, title, top, height })
  }

  const removeSection = (id: string) => {
    sectionsList.value = sectionsList.value.filter(section => section.id !== id)
  }

  const provider: ModalSectionsProvider = {
    addSection,
    removeSection
  }

  const sections = computed(() => {
    return readonly(sectionsList.value
      .filter(({ height }) => height > 0)
      .sort(({ top: topA }, { top: tobB }) => topA - tobB)
      .map(({ id, title }) => createModalSectionAnchor(id, title, `#${id}`) as SectionAnchor))
  })

  const reset = () => {
    sectionsList.value = []
  }

  return {
    provider,
    sections,
    reset
  }
}
