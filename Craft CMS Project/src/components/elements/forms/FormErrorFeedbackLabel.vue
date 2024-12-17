<template>
  <div class="form-error-feedback-label">
    <svg-icon class="form-error-feedback-label__icon" :icon-name="SvgIconName.warningRounded" />
    <span class="form-error-feedback-label__text" :title="text">{{ text }}</span>
    <component v-if="link?.text" :is="link.href ? 'a' : 'span'" class="form-error-feedback-label__link" :href="link.href"
      :target="link.target" @click="linkClick">{{ link.text }}
    </component>
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue'
import SvgIcon, { SvgIconName } from '@/components/helpers/SvgIcon.vue'
import { Link } from '@/models/Link'

export default defineComponent({
  name: 'FormErrorFeedbackLabel',
  components: { SvgIcon },
  emits: ['linkClick'],
  props: {
    text: {
      type: String,
      required: true
    },
    link: {
      type: Object as PropType<Link>,
      required: false
    }
  },
  setup (props, { emit }) {
    const linkClick = () => emit('linkClick')
    return {
      linkClick,
      SvgIconName
    }
  }
})
</script>

<style lang="scss" scoped>
.form-error-feedback-label {
  display: inline-flex;
  align-items: center;
  gap: 4px;

  &__icon {
    --svg-icon-size: 17px;
    flex-shrink: 0;
    color: var(--finder-color-error);
  }

  &__text {
    @include element-text-regular-14;
    @include max-lines(1);
    color: var(--finder-color-error);
  }

  &__link {
    @include text-link-small(var(--finder-color-error), var(--finder-color-dark-50));
    white-space: nowrap;
  }
}
</style>
