<template>
  <teleport to="body" :disabled="!showFullscreen">
    <div :class="[
          'form-drop-down',
          { 'form-drop-down--invalid': isInvalid && !isDisabled },
          { 'form-drop-down--focused' : (hasFocusWithin || isFocused) && !isDisabled },
          { 'form-drop-down--hovered' : isHovered && !isDisabled },
          { 'form-drop-down--disabled': isDisabled },
          { 'form-drop-down--open' : isOpen && !isDisabled },
          `form-drop-down--drop-direction-${dropDirection}`
        ]"
         ref="outerElement">
      <div class="form-drop-down__trigger" :title="title" ref="triggerElement" @click="toggleOpen">
        <input type="text" :tabindex="tabindex" :disabled="isDisabled || undefined"
               :aria-disabled="isDisabled || undefined"
               readonly @keydown="onFocusedInputKeyDown"/>
        <div class="form-drop-down__trigger__content">
          <slot name="trigger"/>
        </div>
        <div class="form-drop-down__trigger__arrow">
          <svg-icon :icon-name="triggerIcon"/>
        </div>
      </div>
      <div class="form-drop-down__menu" ref="menuElement" :style="{'--menu-max-height':menuMaxHeight}">
        <div class="form-drop-down__menu__pane">
          <slot v-bind="{isDisabled, isInvalid}"/>
        </div>
      </div>
    </div>
  </teleport>
</template>

<script lang="ts">
import { computed, defineComponent, Ref, ref, toRefs, watch } from 'vue'
import {
  onClickOutside,
  onKeyStroke,
  useBreakpoints,
  useCssVar,
  useElementBounding,
  useFocusWithin,
  useWindowSize
} from '@vueuse/core'
import { CheckBoxAppearance } from '@/components/elements/Checkbox.vue'
import SvgIcon, { SvgIconName } from '@/components/helpers/SvgIcon.vue'
import { RadioButtonAppearance } from '@/components/elements/RadioButton.vue'
import { BreakpointNames, getBreakpoints } from '@/utils/breakpoints'
import { disableBodyScroll, enableBodyScroll } from 'body-scroll-lock'

export enum FormDropDownDirection {
  up = 'up',
  down = 'down'
}

export default defineComponent({
  name: 'FormDropDown',
  components: { SvgIcon },
  emits: ['update:openValue'],
  props: {

    /** Open state (for usage from outside with v-model:open-value) */
    openValue: {
      type: Boolean,
      required: false,
      default: false
    },

    /** Title attribute for trigger element (optional)  */
    title: {
      type: String,
      required: false
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
    const breakpoints = useBreakpoints(getBreakpoints())
    const isMobileMode = breakpoints.smaller(BreakpointNames.md)
    const { openValue, isDisabled } = toRefs(props)
    const outerElement: Ref<HTMLElement | undefined> = ref(undefined)
    const triggerElement: Ref<HTMLElement | undefined> = ref(undefined)
    const menuElement: Ref<HTMLElement | undefined> = ref(undefined)
    const triggerElementBounding = useElementBounding(triggerElement)
    const { height: windowHeight } = useWindowSize()
    const { focused: hasFocusWithin } = useFocusWithin(outerElement)
    const menuMinHeight = useCssVar('--menu-min-height', menuElement)

    const isOpen = computed({
      get (): boolean {
        return openValue.value
      },
      set (value: boolean | string | number | undefined) {
        emit('update:openValue', value)
      }
    })

    const availSpaceTop = computed(() => {
      const borderTop = 1
      return triggerElementBounding.top.value - borderTop
    })

    const availSpaceBottom = computed(() => {
      const borderBottom = 1
      return windowHeight.value - (triggerElementBounding.bottom.value + borderBottom)
    })

    const dropDirection = computed(() => {
      return availSpaceBottom.value < parseInt(menuMinHeight.value) ? FormDropDownDirection.up : FormDropDownDirection.down
    })

    const showFullscreen = computed(() => {
      return isMobileMode.value && isOpen.value
    })

    const menuMaxHeight = computed(() => {
      const menuBorder = 1 // size of opened menu's border-top or border-bottom (depending on drop direction)
      const spacing = 4 // keep some space to window bounds
      if (showFullscreen.value) {
        return 'auto'
      }
      return (dropDirection.value === FormDropDownDirection.up ? availSpaceTop.value : availSpaceBottom.value) - menuBorder - spacing + 'px'
    })

    const triggerIcon = computed(() => {
      if (isMobileMode.value && isOpen.value) {
        return SvgIconName.close
      }
      return isOpen.value ? SvgIconName.arrowDropUp : SvgIconName.arrowDropDown
    })

    const toggleOpen = () => {
      if (!isDisabled.value) {
        isOpen.value = !isOpen.value
      }
    }

    /* if component is focused by keyboard (tab), allow opening/closing by pressing Enter/Space/... */
    const onFocusedInputKeyDown = (e: KeyboardEvent) => {
      if (['Enter', 'Space', 'ArrowDown', 'ArrowLeft', 'ArrowRight', 'ArrowUp'].includes(e.code)) {
        toggleOpen()
      }
    }

    watch(isOpen, () => {
      if (isOpen.value && showFullscreen.value && menuElement.value) {
        disableBodyScroll(menuElement.value)
      } else if (!isOpen.value && menuElement.value) {
        enableBodyScroll(menuElement.value)
      }
    })

    onClickOutside(menuElement, () => { isOpen.value = false }, { ignore: [outerElement] })
    onKeyStroke('Escape', () => { isOpen.value = false })

    return {
      SvgIconName,
      CheckBoxAppearance,
      RadioButtonAppearance,
      showFullscreen,
      outerElement,
      triggerElement,
      menuElement,
      menuMaxHeight,
      isOpen,
      hasFocusWithin,
      dropDirection,
      triggerIcon,
      toggleOpen,
      onFocusedInputKeyDown
    }
  }
})
</script>

<style lang="scss" scoped>
@import 'src/assets/styles/05-components/form-inputs';

$trigger-height: 44px;

@mixin remove-border($bottom:true) {
  $direction: 'top';
  @if $bottom {
    $direction: 'bottom';
  }
  // replacement for missing border (avoids text from 'jumping' by 1px when border disappears)
  padding-#{$direction}: 1px;
  border-#{$direction}: none;
  border-#{$direction}-left-radius: 0;
  border-#{$direction}-right-radius: 0;
  &__pane {
    padding-#{$direction}: 6px; // distance from trigger to menu pane
  }
}
.form-drop-down {
  position: relative;
  --text-color: var(--finder-color-dark);
  --border-color: var(--finder-color-dark-15);
  --border-radius: #{$border-radius};
  --icon-color: var(--finder-color-dark-50);
  --background-color: var(--finder-color-light);
  --trigger-height: #{$trigger-height};
  --padding-sides: 16px;
  --menu-min-height: #{$trigger-height * 2};
  --menu-max-height: calc(10vh - var(--trigger-height)); // will be overwritten by js

  &:focus,
  &:focus-within,
  &:focus-visible {
    outline: none;
  }

  &--focused {
    --border-color: var(--finder-color-dark-50);
  }

  &--disabled {
    --text-color: var(--finder-color-dark-25);
    --border-color: var(--finder-color-dark-10);
    --icon-color: var(--finder-color-dark-25);
    pointer-events: none;
  }

  &--invalid {
    --text-color: var(--finder-color-error);
    --border-color: var(--finder-color-error);
    --icon-color: var(--finder-color-error);
    --background-color: var(--finder-color-error-bg);
  }

  &:hover,
  &--hovered {
    &:not(.form-drop-down--open):not(.form-drop-down--invalid) {
      --border-color: var(--finder-color-dark-50);
      --background-color: var(--finder-color-dark-5);
    }
  }

  &__trigger {
    display: flex;
    align-items: center;
    gap: 12px;
    height: var(--trigger-height);
    border: 1px solid var(--border-color);
    border-radius: var(--border-radius);
    padding: 0 6px 0 16px;
    background: var(--background-color);
    cursor: pointer;

    & > input {
      @include sr-only;
    }

    &__content {
      flex-grow: 1;
      @include element-text-regular-16;
      color: var(--text-color);
      user-select: none;
      @include max-lines(1);
    }

    &__arrow {
      display: flex;
      align-items: center;
      justify-content: center;
      color: var(--icon-color);

      & > * {
        --svg-icon-size: 11px;
      }
    }
  }

  &__menu {
    display: none;
    position: absolute;
    z-index: z('form-drop-down');
    width: 100%;
    border: 1px solid var(--border-color);
    border-radius: var(--border-radius);
    background: var(--background-color);
    overflow-y: hidden;

    &__pane {
      position: relative;
      padding: var(--padding-sides);
      overflow-y: auto;
      min-height: var(--menu-min-height);
      max-height: var(--menu-max-height);
    }
  }

  &--open {
    @media (max-width: mq(md, true)) {
      --border-radius: 0;
      --border-color: var(--finder-color-light);
      --icon-size: 24px;
      --padding-sides: 20px;
      position: fixed;
      top: 0;
      left: 0;
      bottom: 0;
      right: 0;
      background: var(--background-color);
      z-index: z('form-drop-down-fullscreen');

      .form-drop-down__trigger {
        border-radius: 0;
        box-shadow: 0 -5px 20px rgba(0, 0, 0, 0.15);
      }

      .form-drop-down__menu {
        display: block;
        position: absolute;
        top: var(--trigger-height);
        left: 0;
        height: calc(100% - var(--trigger-height));
        border-radius: 0;
        background: none;

        &__pane {
          height: calc(100% - var(--trigger-height));
        }
      }
    }

    @media (min-width: mq(md, false)) {
      --icon-size: 11px;
      &.form-drop-down {
        .form-drop-down__menu {
          display: block;
        }

        &--drop-direction-up {
          .form-drop-down__menu {
            bottom: var(--trigger-height);
            @include remove-border(true);
          }

          .form-drop-down__trigger {
            @include remove-border(false);
          }
        }

        &--drop-direction-down {
          .form-drop-down__menu {
            @include remove-border(false);
          }

          .form-drop-down__trigger {
            @include remove-border(true);
          }
        }
      }
    }
  }
}
</style>
