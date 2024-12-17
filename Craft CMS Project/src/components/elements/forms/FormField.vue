<template>
  <div :class="[
    'form-field',
    { 'form-field--invalid': isInvalid },
    { 'form-field--disabled': isDisabled }
  ]" ref="fieldElement">
    <div class="form-field__label" v-if="hasSlot('label') || field.label?.text || field.linkSeparate?.text">
      <div class="form-field__label__primary" v-if="hasSlot('label') || field.label?.text">
        <slot name="label" v-bind="slotProps">
          <form-label v-if="field.label?.text" :is-required="isRequired" :is-disabled="isDisabled" :is-invalid="isInvalid"
            :text="field.label.text" :link="field.label.link" :icon="field.label.icon" @link-click="linkLabelClick" />
        </slot>
      </div>
      <div v-if="field.linkSeparate?.text" class="form-field__label__link-separate">
        <component v-if="field.linkSeparate?.text" :is="field.linkSeparate.href ? 'a' : 'span'"
          :href="field.linkSeparate.href" :target="field.linkSeparate.target" @click="linkSeparateClick">{{
            field.linkSeparate.text }}
        </component>
      </div>
    </div>
    <div class="form-field__input">
      <slot v-bind="slotProps" />
    </div>
    <div class="form-field__error" v-if="isInvalid && !isDisabled && (hasSlot('error') || field.errorMessage?.text)">
      <slot name="error" v-bind="slotProps">
        <form-error-feedback-label v-if="field.errorMessage?.text && isInvalid" :text="field.errorMessage?.text || ''"
          :link="field.errorMessage?.link" @link-click="linkErrorClick" />
      </slot>
    </div>

  </div>
</template>

<script lang="ts">
import { defineComponent, PropType, reactive, Ref, ref, toRefs } from 'vue'
import { SvgIconName } from '@/components/helpers/SvgIcon.vue'
import FormLabel from '@/components/elements/forms/FormLabel.vue'
import FormFieldInterface from '@/models/FormFieldInterface'
import FormErrorFeedbackLabel from '@/components/elements/forms/FormErrorFeedbackLabel.vue'
import { useFocusWithin } from '@vueuse/core'

export default defineComponent({
  name: 'FormField',
  components: { FormErrorFeedbackLabel, FormLabel },
  emits: ['linkLabelClick', 'linkSeparateClick', 'linkErrorClick'],
  props: {

    /** Set form field values */
    field: {
      type: Object as PropType<FormFieldInterface>,
      required: true
    },

    /** Set 'invalid/error' state  */
    isInvalid: {
      type: Boolean,
      required: false
    },

    /** Set if disabled */
    isDisabled: {
      type: Boolean,
      required: false
    },

    /** Set 'required' attribute */
    isRequired: {
      type: Boolean,
      required: false
    }
  },
  setup (props, { emit, slots }) {
    const fieldElement: Ref<HTMLElement | undefined> = ref(undefined)
    const linkLabelClick = () => emit('linkLabelClick')
    const linkSeparateClick = () => emit('linkSeparateClick')
    const linkErrorClick = () => emit('linkErrorClick')
    const { focused: isFocused } = useFocusWithin(fieldElement)
    const { isDisabled, isInvalid, isRequired } = toRefs(props)
    const hasSlot = (name: string) => !!slots[name]
    const slotProps = reactive({ isDisabled, isInvalid, isFocused, isRequired })

    return {
      fieldElement,
      hasSlot,
      linkLabelClick,
      linkSeparateClick,
      linkErrorClick,
      slotProps,
      SvgIconName
    }
  }
})
</script>

<style lang="scss" scoped>
.form-field {
  display: flex;
  width: 100%;
  flex-direction: column;
  margin-bottom: 44px;
  --link-color-default: var(--finder-color-dark-50);
  --link-color-hover: var(--finder-color-primary);

  &--invalid {
    --link-color-default: var(--finder-color-error);
    --link-color-hover: var(--finder-color-dark-50);
  }

  &__label {
    display: flex;
    gap: 16px;
    margin-bottom: 4px;

    &__primary {
      display: flex; // fighting whitespace below inline-flex elements
      flex-grow: 1;
      margin-bottom: 2px;
    }

    &__link-separate {
      @include text-link(var(--link-color-default), var(--link-color-hover));
    }
  }

  &__input {
    min-height: 44px;
    margin-bottom: 8px;
  }
}
</style>
