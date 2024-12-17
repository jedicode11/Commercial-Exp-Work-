<template>
  <label :class="[
    'checkbox',
    `checkbox--${appearance}-appearance`,
    { 'checkbox--invalid': isInvalid && !isDisabled },
    { 'checkbox--focused': isFocused && !isDisabled },
    { 'checkbox--hovered': isHovered && !isDisabled },
    { 'checkbox--disabled': isDisabled }
  ]">
    <input type="checkbox" :value="value" v-model="checkedValue" :tabindex="tabindex" ref="inputElement"
      :disabled="isDisabled || undefined" :aria-disabled="isDisabled || undefined" @change="onInputChange" />
    <svg-icon class="checkbox__icon checkbox__icon--not-checked" :icon-name="SvgIconName.checkbox" />
    <svg-icon class="checkbox__icon checkbox__icon--checked" :icon-name="SvgIconName.checkboxSelected" />
    <svg-icon class="checkbox__icon checkbox__icon--indeterminate" :icon-name="SvgIconName.checkboxIndeterminate" />
    <span v-if="label" class="checkbox__label">
      {{ label }}
    </span>
  </label>
</template>

<script lang="ts">
import { computed, defineComponent, onMounted, PropType, Ref, ref, toRef, watch } from 'vue'
import SvgIcon, { SvgIconName } from '../helpers/SvgIcon.vue'

export enum CheckBoxAppearance {
  default = 'default',
  form = 'form'
}

export default defineComponent({
  name: 'Checkbox',
  components: { SvgIcon },
  emits: ['update:modelValue', 'update:isIndeterminate', 'click'],
  props: {
    /** Set value of the checkbox */
    value: {
      type: [String, Number, Array as () => Array<string>],
      required: false
    },

    /** Set label for checkbox (optional) */
    label: {
      type: String,
      required: false
    },

    /** Set checkbox to disabled */
    isDisabled: {
      type: Boolean,
      default: false
    },

    /** Set checkbox to invalid/error state */
    isInvalid: {
      type: Boolean,
      default: false
    },

    /** Support for Vues two-way-binding (v-model) */
    modelValue: {
      type: [
        Boolean,
        String,
        Number,
        Array as () => Array<string | number>
      ]
    },

    /** Set 'indeterminate' state (state will be lost on input change) */
    isIndeterminate: {
      type: Boolean,
      required: false,
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

    /** Set 'appearance' (default/form)  */
    appearance: {
      type: String as PropType<CheckBoxAppearance>,
      required: false,
      default: CheckBoxAppearance.default
    },

    /** Set input's 'tabindex'  */
    tabindex: {
      type: Number,
      required: false,
      default: undefined
    }
  },
  setup (props, { emit }) {
    const inputElement: Ref<HTMLInputElement | undefined> = ref(undefined)
    const isIndeterminate = toRef(props, 'isIndeterminate')
    const checkedValue = computed({
      get (): boolean | string | number | (string | number)[] | undefined {
        return props.modelValue
      },
      set (value: boolean | string | number | (string | number)[] | undefined) {
        emit('update:modelValue', value)
        emit('click', value)
      }
    })

    const onInputChange = () => {
      const indeterminate = inputElement.value?.indeterminate || false
      if (indeterminate !== isIndeterminate.value) {
        emit('update:isIndeterminate', indeterminate)
      }
    }

    const setIndeterminate = () => {
      if (inputElement.value) {
        inputElement.value.indeterminate = isIndeterminate.value
      }
    }

    watch(isIndeterminate, setIndeterminate)

    onMounted(setIndeterminate)

    return {
      SvgIconName,
      checkedValue,
      inputElement,
      onInputChange
    }
  }
})
</script>

<style lang="scss" scoped>
@import 'src/assets/styles/05-components/choice-element';
.checkbox {
  @include choice-element;
}
</style>
