<template>
  <div class="form-tree-select">
    <form-drop-down v-bind="{isInvalid, isFocused, isHovered, isDisabled:(isDisabled || !hasChildren) }"
                    :title="text" v-model:open-value="isOpen">
      <template #trigger>
        {{ text }}
      </template>
      <template #default="{isInvalid}">
        <choice-tree class="select-options" :node="options" v-model="selected" :appearance="ChoiceTreeAppearance.form"
                     :sub-tree-indent="subTreeIndent"
                     v-bind="{isInvalid, showRoot}"/>
      </template>
    </form-drop-down>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, ref, toRefs } from 'vue'
import FormDropDown from '@/components/elements/forms/FormDropDown.vue'
import ChoiceTree, { ChoiceTreeAppearance } from '@/components/elements/ChoiceTree.vue'
import { ChoiceTreeNode, useChoiceTree } from '@/composables/useChoiceTree'

export default defineComponent({
  name: 'FormTreeSelect',
  components: { FormDropDown, ChoiceTree },
  emits: ['update:modelValue'],
  props: {

    /** List of selected IDs (v-model) */
    modelValue: {
      type: Array as () => Array<string>,
      required: false,
      default: () => []
    },

    /** Set select options (tree or list) */
    options: {
      type: Object as () => ChoiceTreeNode,
      required: false,
      default: () => { return { id: '', label: '', children: [] } as ChoiceTreeNode }
    },

    /** Text shown on trigger element  */
    text: {
      type: String,
      required: true
    },

    /** Show root node of tree  */
    showRoot: {
      type: Boolean,
      required: false,
      default: true
    },

    /** Indentation of child levels (in px, defaults to 16) */
    subTreeIndent: {
      type: Number,
      required: false,
      default: 16
    },

    /** Set disabled state */
    isDisabled: {
      type: Boolean,
      default: false
    },

    /** Set invalid/error state */
    isInvalid: {
      type: Boolean,
      default: false
    },

    /** Set 'hover' state  (used for UI preview/testing only)  */
    isHovered: {
      type: Boolean,
      required: false,
      default: false
    },

    /** Set 'focused' state  (used for UI preview/testing only)  */
    isFocused: {
      type: Boolean,
      required: false,
      default: false
    },

    /** Set input's 'tabindex'  */
    tabindex: {
      type: Number,
      required: false,
      default: 0
    }
  },
  setup (props, { emit }) {
    const { options } = toRefs(props)
    const isOpen = ref(false)

    const selected = computed({
      get (): string[] {
        return props.modelValue
      },
      set (value: string []) {
        emit('update:modelValue', value)
      }
    })

    const { hasChildren } = useChoiceTree(options, selected)

    return {
      ChoiceTreeAppearance,
      isOpen,
      hasChildren,
      selected
    }
  }
})
</script>
