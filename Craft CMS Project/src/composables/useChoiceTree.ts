import { computed, ComputedRef, Ref } from 'vue'

export interface ChoiceTreeNode {
  id: string | number,
  label?: string,
  title?: string,
  disabled?: boolean,
  children?: Array<ChoiceTreeNode>
}

export interface UseChoiceTreeReturn {
  isLeaf: ComputedRef<boolean>,
  hasChildren: ComputedRef<boolean>,
  descendantsById: ComputedRef<Array<string>>,
  unselectedDescendantsById: ComputedRef<Array<string>>,
  numDescendants: ComputedRef<number>,
  numSelectedDescendants: ComputedRef<number>,
  areAllDescendantsSelected: ComputedRef<boolean>,
  areSomeDescendantsSelected: ComputedRef<boolean>,
  toggleDescendantSelection: () => void
}

export function useChoiceTree (nodeLevel: Ref<ChoiceTreeNode>, selectedIds: Ref<Array<string>>): UseChoiceTreeReturn {
  const getHasChildren = (node: ChoiceTreeNode) => { return (node.children && node.children.length > 0) || false }

  const getDescendantIds = (node: ChoiceTreeNode, leavesOnly = true, isRoot = false) => {
    let returnValue: Array<string> = []
    if (!isRoot && !node.disabled && (!getHasChildren(node) || !leavesOnly)) {
      returnValue.push(`${node.id}`)
    }
    if (getHasChildren(node) && node.children) {
      node.children.forEach((nodeChild) => {
        returnValue = returnValue.concat(getDescendantIds(nodeChild))
      })
    }
    return returnValue
  }

  const isLeaf = computed(() => {
    return !getHasChildren(nodeLevel.value)
  })

  const hasChildren = computed(() => {
    return getHasChildren(nodeLevel.value)
  })

  const descendantsById = computed(() => {
    return getDescendantIds(nodeLevel.value, true, true)
  })

  const unselectedDescendantsById = computed(() => {
    return descendantsById.value.filter(id => !selectedIds.value.includes(id))
  })

  const numDescendants = computed(() => {
    return descendantsById.value.length
  })

  const numSelectedDescendants = computed(() => {
    return descendantsById.value.length - unselectedDescendantsById.value.length
  })

  const areAllDescendantsSelected = computed(() => {
    return unselectedDescendantsById.value.length === 0
  })

  const areSomeDescendantsSelected = computed(() => {
    return numSelectedDescendants.value < numDescendants.value && numSelectedDescendants.value > 0
  })

  const toggleDescendantSelection = () => {
    if (areAllDescendantsSelected.value) {
      selectedIds.value = selectedIds.value.filter(id => !descendantsById.value.includes(id))
    } else {
      selectedIds.value = selectedIds.value.concat(descendantsById.value.filter(id => !selectedIds.value.includes(id)))
    }
  }

  return {
    isLeaf,
    hasChildren,
    descendantsById,
    unselectedDescendantsById,
    numDescendants,
    numSelectedDescendants,
    areAllDescendantsSelected,
    areSomeDescendantsSelected,
    toggleDescendantSelection
  }
}
