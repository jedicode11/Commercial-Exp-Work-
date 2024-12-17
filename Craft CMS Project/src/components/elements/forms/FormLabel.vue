<template>
  <div :class="[
    'form-label',
    { 'form-label--disabled': isDisabled },
    { 'form-label--invalid': isInvalid }
  ]">
    <svg-icon v-if="icon" class="form-label__icon" :icon-name="icon" />
    <span class="form-label__text" :title="text">{{ `${text}${isRequired ? ' *' : ''}` }}</span>
    <component v-if="link?.text" :is="link.href ? 'a' : 'span'" class="form-label__link" :href="link.href"
      :target="link.target" @click="linkClick">{{ link.text }}
    </component>
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue'
import SvgIcon, { SvgIconName } from '@/components/helpers/SvgIcon.vue'
import { Link } from '@/models/Link'

export default defineComponent({
  name: 'FormLabel',
  components: { SvgIcon },
  emits: ['linkClick'],
  props: {

    /** Set text  */
    text: {
      type: String,
      required: true
    },

    /** Set icon (optional) */
    icon: {
      type: String as PropType<SvgIconName>,
      required: false
    },

    /** Set link (optional) */
    link: {
      type: Object as PropType<Link>,
      required: false
    },

    /** Set if disabled */
    isDisabled: {
      type: Boolean,
      default: false
    },

    /** Set required */
    isRequired: {
      type: Boolean,
      default: false
    },

    /** Set 'invalid/error' state  */
    isInvalid: {
      type: Boolean,
      required: false,
      default: false
    }
  },
  setup (props, { emit, slots }) {
    const hasSlot = (name: string) => !!slots[name]
    const linkClick = () => emit('linkClick')
    return {
      hasSlot,
      linkClick,
      SvgIconName
    }
  }
})
</script>

<style lang="scss" scoped>
.form-label {
  --text-color: var(--finder-color-dark);
  --link-color-default: var(--finder-color-dark-50);
  --link-color-hover: var(--finder-color-primary);
  display: inline-flex;
  align-items: center;
  gap: 6px;

  &--disabled {
    --text-color: var(--finder-color-dark-25);
  }

  &--invalid {
    --text-color: var(--finder-color-error);
    --link-color-default: var(--finder-color-error);
    --link-color-hover: var(--finder-color-dark-50);
  }

  &__icon {
    flex-shrink: 0;
    color: var(--text-color);
  }

  &__text {
    @include element-text-regular-16;
    @include max-lines(1);
    color: var(--text-color);
  }

  &__link {
    @include text-link(var(--link-color-default), var(--link-color-hover));
    white-space: nowrap;
  }
}
</style>
