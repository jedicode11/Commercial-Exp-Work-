<template>
  <div :class="[
    'choice-tree',
    { 'choice-tree--is-root': isRoot },
    { 'choice-tree--hide-root': treeDepth === 0 && !showRoot },
    { 'choice-tree--is-leaf': isLeaf }
  ]">
    <template v-if="!isRoot || showRoot">
      <span class="choice-tree__label">{{ node.label || node.title }}</span>
    </template>
    <ul v-if="hasChildren" class="choice-tree__sub-tree">
      <li v-for="nodeChild in node.children" :key="nodeChild.id" class="choice-tree__sub-tree__item">
        <choice-tree :node="nodeChild" :tree-depth="treeDepth + 1" :appearance="appearance" :is-disabled="isDisabled"
          :is-invalid="isInvalid" v-model="selected" ref="subTree" />
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, PropType, toRefs } from 'vue'
import { CheckBoxAppearance } from '@/components/elements/Checkbox.vue'
import { ChoiceTreeNode, useChoiceTree } from '@/composables/useChoiceTree'

export enum ChoiceTreeAppearance {
  default = 'default',
  form = 'form'
}

export default defineComponent({
  name: 'ChoiceTree',
  emits: ['update:modelValue'],
  props: {

    /** Support for Vues two-way-binding (v-model) */
    modelValue: {
      type: Array as () => Array<string>,
      required: true
    },

    /** Set current tree level node */
    node: {
      type: Object as () => ChoiceTreeNode,
      required: true
    },

    /** Show tree's root */
    showRoot: {
      type: Boolean,
      required: false,
      default: true
    },

    /** Current tree depth level (number of parent nodes) */
    treeDepth: {
      type: Number,
      required: false,
      default: 0
    },

    /** Indentation of child levels (in px, defaults to 16) */
    subTreeIndent: {
      type: Number,
      required: false,
      default: 16
    },

    /** Set 'appearance' (default/form)  */
    appearance: {
      type: String as PropType<ChoiceTreeAppearance>,
      required: false,
      default: ChoiceTreeAppearance.default
    },

    /** Set select to disabled */
    isDisabled: {
      type: Boolean,
      default: false
    },

    /** Set invalid/error state */
    isInvalid: {
      type: Boolean,
      default: false
    }
  },
  setup (props, { emit }) {
    const { node, modelValue, treeDepth } = toRefs(props)
    const isRoot = computed(() => { return treeDepth.value === 0 })

    const selected = computed({
      get (): Array<string> {
        return modelValue.value
      },
      set (value: Array<string>) {
        emit('update:modelValue', value)
      }
    })
    const {
      isLeaf,
      hasChildren,
      areAllDescendantsSelected,
      areSomeDescendantsSelected,
      toggleDescendantSelection
    } = useChoiceTree(node, selected)

    return {
      ChoiceTreeAppearance,
      CheckBoxAppearance,
      isRoot,
      isLeaf,
      hasChildren,
      selected,
      areAllDescendantsSelected,
      areSomeDescendantsSelected,
      toggleDescendantSelection
    }
  }
})
</script>

<style lang="scss" scoped>
.choice-tree {
  display: flex;
  flex-direction: column;
  gap: 16px;

  &__input {
    display: flex;
  }

  &--is-root {
    &.choice-tree--hide-root {
      & > ul {
        margin-left: 0;
      }
    }
  }

  & > ul {
    display: flex;
    flex-direction: column;
    gap: 16px;
    margin: 0 0 0 var(--sub-tree-indent);
    padding: 0;
    list-style: none;

    & > li {
      margin: 0;
      padding: 0;
      display: flex;
    }
  }
}
</style>
