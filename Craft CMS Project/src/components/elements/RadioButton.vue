<template>
  <label
    :class="[
      'radio',
      `radio--${appearance}-appearance`,
      { 'radio--invalid': isInvalid && !isDisabled },
      { 'radio--focused': isFocused && !isDisabled },
      { 'radio--hovered' : isHovered && !isDisabled },
      { 'radio--disabled': isDisabled }
    ]">
    <input type="radio" :value="value" v-model="selectedValue" :tabindex="tabindex"
           :disabled="isDisabled || undefined" :aria-disabled="isDisabled || undefined"/>
    <span
      v-if="label"
      :class="[
        'radio__label',
        { 'radio__label--disabled': isDisabled }
      ]"
    >
      {{ label }}
    </span>
  </label>
</template>

<script lang="ts">
import { computed, defineComponent, PropType, onMounted } from 'vue'
import { SvgIconName } from '../helpers/SvgIcon.vue'

export enum RadioButtonAppearance {
  default = 'default',
  form = 'form'
}

export default defineComponent({
  name: 'RadioButton',
  emits: ['update:modelValue'],
  props: {
    /** Set value of the radio button */
    value: {
      type: [String, Number],
      required: false
    },

    /** Set label for radio button (optional) */
    label: {
      type: String,
      required: false
    },

    /** Value is checked */
    checked: {
      type: Boolean,
      required: false
    },

    /** Set radio button to disabled */
    isDisabled: {
      type: Boolean,
      default: false
    },

    /** Set radio button to invalid/error state */
    isInvalid: {
      type: Boolean,
      default: false
    },

    /** Support for vue's two-way-binding (v-model) */
    modelValue: {
      type: [String, Number]
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
      type: String as PropType<RadioButtonAppearance>,
      required: false,
      default: RadioButtonAppearance.default
    },

    /** show label only  */
    hideIcon: {
      type: Boolean,
      required: false,
      default: false
    },

    /** Set input's 'tabindex'  */
    tabindex: {
      type: Number,
      required: false,
      default: undefined
    }
  },
  setup (props, { emit }) {
    const selectedValue = computed({
      get (): boolean | string | number | undefined {
        return props.modelValue
      },
      set (value: boolean | string | number | undefined) {
        console.log(value)
        emit('update:modelValue', value)
      }
    })

    onMounted(() => {
      if (props.checked) {
        selectedValue.value = props.value
      }
    })

    return {
      SvgIconName,
      selectedValue
    }
  }
})
</script>

<style lang="scss" scoped>
@import 'src/assets/styles/05-components/choice-element';

.radio {
  @include choice-element(false);
  margin-left: 30px;
}

</style>
