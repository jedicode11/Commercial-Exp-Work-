<template>
  <component :is="url !== null ? 'a' : 'button'" :type="!url ? buttonType : null"
    :href="(url && !isDisabled) ? url : null" :download="(url && isDownload) || null"
    :disabled="(!url && isDisabled) || null" :aria-disabled="(!url && isDisabled) || null" :class="[
      'cta-button',
      'cta-button--icon-pos-' + iconPos,
      { 'cta-button--small': isSmall },
      { 'cta-button--small-on-mobile': isSmallOnMobile },
      { 'cta-button--has-icon': iconName.length > 0 },
      { 'cta-button--inverted': isInverted },
      { 'cta-button--primary': !isSecondary },
      { 'cta-button--secondary': isSecondary },
      { 'cta-button--full-width': isFullWidth },
      { 'cta-button--disabled': isDisabled },
      { 'cta-button--focussed': isFocussed },
      { 'cta-button--hovered': isHovered }
    ]">
    <div class="cta-button__inner">
      <span v-if="badgeNumber" class="badge">{{ badgeNumber }}</span>
      <span class="cta-button__icon" v-if="iconName !== ''">
        <svg-icon :icon-name="iconName" />
      </span>
      <span class="cta-button__label">
        <slot></slot>
      </span>
    </div>
  </component>
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue'
import HyperLink from '@/components/helpers/HyperLink.vue'
import SvgIcon, { SvgIconName } from '@/components/helpers/SvgIcon.vue'

export enum CtaButtonIconPos {
  left = 'left',
  right = 'right'
}

export enum CtaButtonType {
  button = 'button',
  submit = 'submit',
  reset = 'reset'
}

export default defineComponent({
  name: 'CtaButton',
  components: {
    HyperLink,
    SvgIcon
  },
  props: {
    /* Optional button type, e.g. 'button', 'submit', 'reset' */
    buttonType: {
      type: String as PropType<CtaButtonType>,
      required: false,
      default: CtaButtonType.button,
      validator: (value: string) => {
        return (Object.values(CtaButtonType) as Array<string>).includes(value)
      }
    },

    /** Optional internal or external URL for the button */
    url: {
      type: String,
      required: false,
      default: null
    },

    /** Optional internal or external URL for the button */
    badgeNumber: {
      type: Number,
      required: false,
      default: null
    },

    /** Optional flag for downloads */
    isDownload: {
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

    /** Flag for secondary style */
    isSecondary: {
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

    /** Stretch button to full width (used in mobile view) */
    isFullWidth: {
      type: Boolean,
      required: false,
      default: false
    },

    /** Name of SVG icon, see @/assets/icons/ */
    iconName: {
      type: String as PropType<SvgIconName>,
      required: false,
      default: '',
      validator: (value: string) => {
        return value === '' || (Object.values(SvgIconName) as Array<string>).includes(value)
      }
    },

    /** position of icon ('left' or 'right') */
    iconPos: {
      type: String as PropType<CtaButtonIconPos>,
      required: false,
      default: CtaButtonIconPos.left,
      validator: (value: string) => {
        return (Object.values(CtaButtonIconPos) as Array<string>).includes(value)
      }
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
    }
  }
})
</script>

<style lang="scss" scoped>
.cta-button {
  --button-color: var(--finder-color-white);
  --button-color-hover: var(--finder-color-white);
  --button-background-color: var(--finder-color-primary);
  --button-background-color-hover: var(--finder-color-primary-hover);
  --button-border-radius: 44px;
  --button-padding: 10px 27px;
  --icon-color: var(--finder-color-white-50);
  --icon-color-hover: var(--finder-color-white-50);
  --icon-size: 22px;
  --icon-spacing: 4px;

  display: inline-block;
  border: 0;
  padding: 0;
  opacity: 1;
  cursor: pointer;
  border-radius: var(--button-border-radius);
  background-color: var(--button-background-color);
  transition: color 0.2s ease, opacity 0.2s ease, background-color 0.2s ease;

  &--hovered,
  &:hover {
    background-color: var(--button-background-color-hover);
    .cta-button__label {
      color: var(--button-color-hover);
    }

    .cta-button__icon {
      color: var(--icon-color-hover);
    }

    &:active {
      @include focus-outline;
    }
  }

  &__inner {
    display: flex;
    align-items: center;
    position: relative;
    padding: var(--button-padding);
    color: white;
  }

  &__icon {
    line-height: var(--icon-size);
    display: flex;
    justify-content: center;
  }

  &__label {
    @include button-text-bold;
    color: white;
    text-decoration: none;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }

  &--disabled {
    opacity: 0.25;
    cursor: default;
    pointer-events: none;
  }

  &--secondary {
    --button-color: var(--finder-color-primary);
    --button-color-hover: var(--finder-color-primary);
    --icon-color: var(--finder-color-primary-50);
    --icon-color-hover: var(--finder-color-primary-50);
    --button-background-color: var(--finder-color-primary-10);
    --button-background-color-hover: var(--finder-color-primary-15);

    &.cta-button--inverted {
      --button-color: var(--finder-color-white);
      --button-color-hover: var(--finder-color-white);
      --icon-color: var(--finder-color-white-50);
      --icon-color-hover: var(--finder-color-white-50);
      --button-background-color: var(--finder-color-white-15);
      --button-background-color-hover: var(--finder-color-white-25);
    }
  }

  &--inverted {
    --button-color: var(--finder-color-primary);
    --button-color-hover: var(--finder-color-primary);
    --icon-color: var(--finder-color-primary-50);
    --icon-color-hover: var(--finder-color-primary-50);
    --button-background-color: var(--finder-color-white);
    --button-background-color-hover: var(--finder-color-white-90);
  }

  &--full-width {
    width: 100%;

    .cta-button__inner {
      @media (max-width: 480px) {
        padding: var(--button-padding);
      }
      width: 100%;
      justify-content: center;
    }
  }

  &--has-icon {
    &.cta-button--icon-pos-left {
      .cta-button__label {
        margin-left: var(--icon-spacing);
      }
    }

    &.cta-button--icon-pos-right {
      .cta-button__inner {
        flex-direction: row-reverse;
      }

      .cta-button__label {
        margin-right: var(--icon-spacing);
      }
    }
  }

  // use mixin to allow shared usage in mobile media query and 'small' modifier class, see below
  @mixin size-small {
    --icon-size: 16px;
    --icon-spacing: 10px;
    --button-border-radius: 32px;
    --button-padding: 3px 16px;

    .cta-button__label {
      @include button-text-small-regular;
      color: var(--button-color);
    }
  }
  &--small {
    @include size-small;
  }

  &--small-on-mobile {
    &:first-child {
      margin-left: 0%;
    }
  }

  .badge {
    margin-left: 10px;
    min-width: 12px;
    padding: 6px 10px;
    border-radius: 50%;
    font-size: 15px;
    text-align: center;
    background: #ffffff;
    color: var(--button-background-color);
  }
}
</style>
