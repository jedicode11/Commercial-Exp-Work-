<template>
  <button :class="[
    'form-text-field-button',
    { 'form-text-field-button--has-background': hasBackground },
    { 'form-text-field-button--active': isActive },
    { 'form-text-field-button--hover': isHovered },
    { 'form-text-field-button--disabled': isDisabled },
    { 'form-text-field-button--invalid': isInvalid }
  ]" :title="title" :disabled="isDisabled || undefined" :aria-disabled="isDisabled || undefined">
    <svg-icon class="form-text-field-button__icon" :icon-name="icon" />
  </button>
</template>

<script lang="ts">
import { defineComponent, PropType, ref } from 'vue'
import SvgIcon, { SvgIconName } from '@/components/helpers/SvgIcon.vue'

export default defineComponent({
  name: 'FormTextFieldButton',
  components: {
    SvgIcon
  },
  props: {
    icon: {
      type: String as PropType<SvgIconName>,
      required: true,
      validator: (value: string) => {
        return (Object.values(SvgIconName) as Array<string>).includes(value)
      }
    },
    title: {
      type: String,
      required: false,
      default: undefined
    },
    /** Set if button has a background color */
    hasBackground: {
      type: Boolean,
      required: false,
      default: true
    },
    /** Set if disabled */
    isDisabled: {
      type: Boolean,
      required: false
    },
    /** Set invalid/error state */
    isInvalid: {
      type: Boolean,
      required: false
    },
    /** Set 'active' state  (when text field is focused)  */
    isActive: {
      type: Boolean,
      required: false
    },
    /** Set 'hover' state  (used for UI preview/testing only)  */
    isHovered: {
      type: Boolean,
      required: false
    }
  },
  setup() {
    const myVar = ref('')

    return {
      SvgIconName,
      myVar
    }
  }
})
</script>

<style lang="scss" scoped>
.form-text-field-button {
  --button-size: 32px;
  --background-color: rgba(255, 255, 255, 0);
  --background-color-hover: rgba(255, 255, 255, 0);
  --icon-size: 24px;
  --icon-color: var(--finder-color-dark-50);
  --icon-color-hover: var(--finder-color-dark-75);

  display: flex;
  align-items: center;
  justify-content: center;
  width: var(--button-size);
  height: var(--button-size);
  border-radius: 4px;
  background: var(--background-color);
  transition: background-color 0.2s ease;
  // button css resets
  margin: 0;
  border: 0;
  padding: 0;
  white-space: normal;
  line-height: inherit;
  opacity: 1;

  &:focus {
    outline: none;
  }

  &:hover,
  &--hover {
    background: var(--background-color-hover);

    .form-text-field-button__icon {
      color: var(--icon-color-hover);
    }
  }

  &--invalid {
    --icon-color: var(--finder-color-error);
    --icon-color-hover: var(--finder-color-dark-50);
  }

  &--disabled {
    pointer-events: none;
    --icon-color: var(--finder-color-dark-25);
  }

  &:not(&--disabled) {
    cursor: pointer;
  }

  &--has-background {
    --icon-color-hover: var(--finder-color-dark-50);
    --background-color-hover: var(--finder-color-dark-15);

    &.form-text-field-button--active:not(.form-text-field-button--disabled) {
      --background-color: var(--finder-color-dark-10);
    }
  }

  &__icon {
    --svg-icon-size: var(--icon-size);
    color: var(--icon-color);
  }
}
</style>
