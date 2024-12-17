<template>
  <div :class="[
    'form-text-field',
    { 'form-text-field--invalid': isInvalid && !isDisabled },
    { 'form-text-field--focused': (hasFocusWithin || isFocused) && !isDisabled },
    { 'form-text-field--hovered': isHovered && !isDisabled },
    { 'form-text-field--disabled': isDisabled }
  ]">
    <input size="1" class="form-text-field__input" ref="inputElement" v-model="text" :type="type" :tabindex="tabindex"
      :placeholder="placeholder" :maxlength="maxLength" :readonly="isReadOnly || undefined"
      :aria-readonly="isReadOnly || undefined" :required="isRequired || undefined"
      :aria-required="isRequired || undefined" :disabled="isDisabled || undefined"
      :aria-disabled="isDisabled || undefined" @focus="$emit('inputFocus')" @blur="$emit('inputBlur')"
      @keyup.enter="$emit('confirmValue')">
    <div class="form-text-field__buttons">
      <transition-group name="fade">
        <form-text-field-button v-if="showClear" class="form-clear-btn" :has-background="false" :icon="SvgIconName.close"
          v-bind="buttonProps" @click="clear()" />
        <form-text-field-button v-if="showInfo" :has-background="false" :icon="SvgIconName.infoFilled"
          v-bind="buttonProps" @click="info()" />
        <slot name="buttons" v-bind="buttonProps"></slot>
      </transition-group>
    </div>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, reactive, Ref, ref, toRefs } from 'vue'
import { SvgIconName } from '@/components/helpers/SvgIcon.vue'
import { useFocus } from '@vueuse/core'
import FormTextFieldButton from '@/components/elements/forms/FormTextFieldButton.vue'

export default defineComponent({
  name: 'FormTextField',
  components: { FormTextFieldButton },
  emits: ['update:modelValue', 'clearClick', 'infoClick', 'inputFocus', 'inputBlur', 'confirmValue'],
  props: {
    /** Support for Vues two-way-binding (v-model) */
    modelValue: {
      type: String,
      required: false,
      default: ''
    },

    /** Set placeholder text of the input */
    placeholder: {
      type: String,
      required: false,
      default: undefined
    },

    /** Set input type like 'tel','url',... (defaults to 'text') */
    type: {
      type: String,
      required: false,
      default: 'text'
    },

    /** Set input's 'tabindex'  */
    tabindex: {
      type: Number,
      required: false,
      default: 0
    },

    /** Set input's 'tabindex'  */
    maxLength: {
      type: Number,
      required: false,
      default: undefined
    },

    /** Show info icon  */
    showInfo: {
      type: Boolean,
      required: false
    },

    /** Show 'clear' button (default=true) */
    isClearable: {
      type: Boolean,
      required: false,
      default: true
    },

    /** Set 'readonly' state for text input */
    isReadOnly: {
      type: Boolean,
      required: false,
      default: false
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

    /** Set required attribute */
    isRequired: {
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
    }
  },
  setup(props, { emit }) {
    const inputElement: Ref<HTMLInputElement | undefined> = ref(undefined)
    const { focused: hasFocusWithin } = useFocus(inputElement)
    const { isDisabled, isInvalid, isReadOnly, isClearable, isFocused } = toRefs(props)
    const buttonProps = reactive({ isDisabled, isInvalid, isActive: hasFocusWithin || isFocused })
    const text = computed({
      get(): string | undefined {
        return props.modelValue
      },
      set(value: string | undefined) {
        emit('update:modelValue', value)
      }
    })

    const showClear = computed(() => {
      return isClearable.value && !isDisabled.value && !isReadOnly.value && text.value && text.value.length
    })

    const clear = () => {
      text.value = ''
      emit('clearClick')
    }

    const info = () => {
      emit('infoClick')
    }

    return {
      SvgIconName,
      text,
      inputElement,
      buttonProps,
      hasFocusWithin,
      showClear,
      clear,
      info
    }
  }
})
</script>

<style lang="scss" scoped>
@import 'src/assets/styles/05-components/form-inputs';

.form-text-field {
  --input-text-color: var(--finder-color-dark);
  --placeholder-text-color: var(--finder-color-dark-50);
  --border-color: var(--finder-color-dark-15);
  --icon-color: var(--finder-color-dark-50);
  --background-color: var(--finder-color-light);

  display: flex;
  align-items: center;
  gap: 12px;
  height: 44px;
  border: 1px solid var(--border-color);
  border-radius: $border-radius;
  padding: 10px 6px 10px 16px;
  background: var(--background-color);

  &--focused,
  &--hovered,
  &:hover {
    --border-color: var(--finder-color-error-hover);

    &:not(.form-text-field--invalid) {
      --border-color: var(--finder-color-dark-50);
    }
  }

  &--disabled {
    --input-text-color: var(--finder-color-dark-25);
    --placeholder-text-color: var(--finder-color-dark-25);
    --border-color: var(--finder-color-dark-10);
    --icon-color: var(--finder-color-dark-25);
    pointer-events: none;
  }

  &--invalid {
    --input-text-color: var(--finder-color-error);
    --placeholder-text-color: var(--finder-color-error-disabled);
    --border-color: var(--finder-color-error);
    --icon-color: var(--finder-color-error);
    --background-color: var(--finder-color-error-10);
  }

  &__input {
    @include input-reset;
    @include element-text-regular-16;
    color: var(--input-text-color);
    flex-grow: 1;

    &::placeholder {
      color: var(--placeholder-text-color);
    }
  }

  &__buttons {
    display: flex;
    gap: 2px;
  }
}

.form-clear-btn {
  --icon-size: 16px;
}

@keyframes button-enter {
  0% {
    opacity: 0;
    transform: translateX(10%);
  }

  100% {
    opacity: 1;
    transform: translateX(0%);
  }
}

@keyframes button-leave {
  0% {
    opacity: 1;
    transform: translateX(0%);
  }

  100% {
    opacity: 0;
    transform: translateX(10%);
  }
}
</style>
