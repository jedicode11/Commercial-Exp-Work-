<template>
  <component :is="url !== null ? 'a' : 'button'" :type="!url ? 'button' : null" :href="(url && !isDisabled) ? url : null"
    :target="(url && openInNewTab) ? '_blank' : null" :class="[
      'icon-button',
      { 'icon-button--small': isSmall },
      { 'icon-button--small-on-mobile': isSmallOnMobile },
      { 'icon-button--inverted': isInverted },
      { 'icon-button--background': hasBackground || (isToggle && isActive) },
      { 'icon-button--disabled': isDisabled },
      { 'icon-button--focussed': isFocussed },
      { 'icon-button--hovered': isHovered },
      { 'icon-button--colored': isColored },
      { 'icon-button--inactive': isArrowInactive }
    ]" :disabled="(!url && isDisabled) || null" :aria-disabled="(!url && isDisabled) || null"
    :aria-selected="isActive || null" :title="(isActive && activeText !== '') ? activeText : text">
    <div class="icon-button__inner">
      <svg-icon :icon-name="iconName" />
      <span v-if="isActive && activeText != ''">{{ activeText }}</span>
      <span v-else>{{ text }}</span>
    </div>
  </component>
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue'
import SvgIcon, { SvgIconName } from '@/components/helpers/SvgIcon.vue'

export default defineComponent({
  name: 'IconButton',
  components: { SvgIcon },
  props: {
    /** Name of icon from assets/icon, e.g. 'star' */
    iconName: {
      type: String as PropType<SvgIconName>,
      required: true,
      validator: (value: string) => {
        return (Object.values(SvgIconName) as Array<string>).includes(value)
      }
    },
    /** Button text */
    text: {
      type: String,
      required: true,
      default: ''
    },

    /** Optional internal or external URL for the button */
    url: {
      type: String,
      required: false,
      default: null
    },

    /** Text to display when active */
    activeText: {
      type: String,
      required: false,
      default: ''
    },

    /** Open url in new tab/window */
    openInNewTab: {
      type: Boolean,
      required: false,
      default: true
    },

    /** Set if active */
    isActive: {
      type: Boolean,
      required: false,
      default: false
    },

    /** Set inverted style */
    isInverted: {
      type: Boolean,
      required: false,
      default: false
    },

    /** Component is used like a radio button (e.g. toggle product list's viewType) */
    isToggle: {
      type: Boolean,
      required: false,
      default: false
    },

    /** Flag for "background" style */
    hasBackground: {
      type: Boolean,
      required: false,
      default: false
    },

    /** Set component size 'small' (small on mobile devices by default) */
    isSmall: {
      type: Boolean,
      required: false,
      default: false
    },

    /** Auto-switch to size 'small' in mobile view (default = true) */
    isSmallOnMobile: {
      type: Boolean,
      required: false,
      default: true
    },

    /** Set if disabled */
    isDisabled: {
      type: Boolean,
      required: false,
      default: false
    },

    /** Set 'focus' state (used for UI preview/testing only) */
    isFocussed: {
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

    /** Set if colored */
    isColored: {
      type: Boolean,
      required: false,
      default: false
    },

    isArrowInactive: {
      type: Boolean,
      required: false,
      default: false
    }
  }
})
</script>

<style lang="scss" scoped>
.icon-button {
  --icon-color: var(--finder-color-dark-50);
  --icon-size: 24px;
  --icon-color-hover: var(--finder-color-dark-50);
  --button-size: 44px;
  --button-radius: 50%;
  --button-background-color: transparent;
  --button-background-color-hover: var(--finder-color-dark-10);
  --button-background-color-click: var(--finder-color-dark-10);

  width: var(--button-size);
  height: var(--button-size);
  display: flex;
  justify-content: center;
  align-items: center;
  line-height: 100%;
  border-radius: var(--button-radius);
  transition: color 0.2s ease, opacity 0.2s ease, background-color 0.2s ease;
  border: 0;
  cursor: pointer;
  background-color: var(--button-background-color);

  &--hovered,
  &:hover {
    color: var(--icon-color-hover);
    background-color: var(--button-background-color-hover);
  }

  &--focussed,
  &:focus {
    outline: none;
  }

  &__inner {
    color: var(--icon-color);
    width: var(--button-size);
    height: var(--button-size);
    display: flex;
    align-items: center;
    justify-content: center;
  }

  &--disabled {
    opacity: 0.25;
    cursor: default;
    pointer-events: none;
  }
  &--inactive {
    pointer-events: none;
    opacity: 0.5;
    cursor: not-allowed;
  }
  @media only screen and (max-width: 767px) {
    &--inactive {
      pointer-events: none;
      opacity: 0.5;
      cursor: not-allowed;
    }
  }
  &--background {
    --button-background-color: var(--finder-color-dark-10);
    --button-background-color-hover: var(--finder-color-dark-15);
  }

  &--inverted {
    --icon-color: var(--finder-color-light);
    --icon-color-hover: var(--finder-color-light);
    --button-background-color-hover: var(--finder-color-white-10);

    &.icon-button--background {
      --button-background-color: var(--finder-color-white-10);
      --button-background-color-hover: var(--finder-color-white-15);
    }
  }

  svg {
    width: var(--icon-size);
    height: var(--icon-size);
    color: inherit;
    fill: currentColor;
  }

  span {
    @include sr-only;
  }

  // use mixin to allow shared usage in mobile media query and 'small' modifier class, see below
  @mixin size-small {
    --button-size: 30px;
    --icon-size: 16px;
  }

  &--small-on-mobile {
    @media (max-width: 480px) {
      @include size-small;
    }
  }

  &--colored {
    --icon-color: var(--finder-color-white-50);
    --button-background-color: var(--finder-color-primary);
    --button-background-color-hover: var(--finder-color-primary);
    &:not(:active) {
      --icon-color: var(--finder-color-white-50);
      // --icon-color: #18181880;
      --button-background-color: #adadad80;
    }
    &:focus {
      --button-background-color: var(--finder-color-primary);
    }
  }
}
</style>
