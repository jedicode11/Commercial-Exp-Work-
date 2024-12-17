<template>
  <div class="form-multi-select">
    <form-drop-down v-bind="{isInvalid, isFocused, isHovered, isDisabled:(isDisabled || !options.length) }"
                    :title="text"
                    v-model:open-value="isOpen">
      <template #trigger>
        {{ text }}
      </template>
      <template #default="{isInvalid}">
        <div class="select-options">
          <checkbox v-for="(option, index) in options" :key="index" :checked="option.checked"
                    :value="option.value" :label="option.label" :is-invalid="isInvalid" :is-disabled="option.disabled"
                    :appearance="CheckBoxAppearance.form"
                    v-model="selected"/>
        </div>
      </template>
    </form-drop-down>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, ref } from 'vue'
import Checkbox, { CheckBoxAppearance } from '@/components/elements/Checkbox.vue'
import FormDropDown from '@/components/elements/forms/FormDropDown.vue'
import { FormSelectOption } from '@/components/elements/forms/FormSelect.vue'

export interface FormMultiSelectOption {
  value: string | number
  label: string,
  checked?: boolean
  disabled?: boolean
}

export default defineComponent({
  name: 'FormMultiSelect',
  components: { FormDropDown, Checkbox },
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
      type: Array as () => Array<FormSelectOption>,
      required: false,
      default: () => []
    },

    /** Text shown on trigger element  */
    text: {
      type: String,
      required: true
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
    const isOpen = ref(false)

    const selected = computed({
      get (): boolean | string | number | (string | number)[] | undefined {
        return props.modelValue
      },
      set (value: boolean | string | number | (string | number)[] | undefined) {
        emit('update:modelValue', value)
      }
    })

    return {
      CheckBoxAppearance,
      isOpen,
      selected
    }
  }
})
</script>

<style lang="scss" scoped>

.select-options {
  display: flex;
  flex-direction: column;
  gap: 16px;

  & > * {
    margin: 0;
    padding: 0;
    color: var(--finder-color-50);
    width: 100%;
  }
}
</style>
