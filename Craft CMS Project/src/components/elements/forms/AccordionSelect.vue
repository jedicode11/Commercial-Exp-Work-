<template>
    <div class="accordion">
        <div class="accordion-trigger" @click="open = !open">
          <div class="accordion-icon" :class="{ open: open }">
            <svg-icon :icon-name="SvgIconName.chevronDown"/>
          </div>
          <div class="accordion-title">{{ selectedLabel ? selectedLabel : placeholder }}</div>
        </div>
        <div class="select-options">
            <transition name="accordion-slide"
                v-bind="{ isInvalid, isFocused, isHovered, isDisabled: (isDisabled || !options.length) }" :title="placeholder">
                <div v-if="open" class="accordion-content">
                    <radio-button v-for="(option, index) in options" :key="index" :value="option.value"
                        :label="option.label" :is-invalid="isInvalid" :checked="option.checked"
                        :is-disabled="option.disabled" :appearance="RadioButtonAppearance.form" hide-icon v-model="selected"
                        @change="close" />
                    <slot></slot>
                </div>
            </transition>
        </div>
    </div>
</template>

<script lang="ts">
import { computed, defineComponent, ref, toRefs } from 'vue'
import RadioButton, { RadioButtonAppearance } from '@/components/elements/RadioButton.vue'
import SvgIcon, { SvgIconName } from '@/components/helpers/SvgIcon.vue'

export interface FormSelectOption {
  value: string | number,
  label: string,
  checked?: boolean
  disabled?: boolean
}

export default defineComponent({
  name: 'AccordionSelect',
  components: {
    RadioButton,
    SvgIcon
  },
  emits: ['update:modelValue'],
  props: {
    title: String,
    /** List of selected IDs (v-model) */
    modelValue: {
      type: [String, Number],
      required: false,
      default: ''
    },
    options: {
      type: Array as () => Array<FormSelectOption>,
      required: false,
      default: () => []
    },

    /** Text shown on trigger element if selection is empty */
    placeholder: {
      type: String,
      required: true
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
  data () {
    return {
      open: false as boolean
    }
  },
  setup (props, { emit }) {
    const { options, isDisabled } = toRefs(props)
    const isOpen = ref(false)

    const selected = computed({
      get (): string | number | undefined {
        return props.modelValue
      },
      set (value: string | number | undefined) {
        emit('update:modelValue', value)
      }
    })

    const getLabelByValue = (value: string | number) => {
      if (options.value) {
        const [option] = options.value?.filter(option => option.value === value)
        if (option) {
          return option.label
        }
      }
      return ''
    }

    const selectedLabel = computed(() => {
      if (!selected.value) {
        return ''
      }
      return getLabelByValue(selected.value)
    })

    const close = () => {
      if (!isDisabled.value) {
        isOpen.value = !isOpen.value
      }
    }
    return {
      SvgIconName,
      RadioButtonAppearance,
      selected,
      selectedLabel,
      isOpen,
      close
    }
  }
})
</script>

<style lang="scss" scoped>
@import 'src/assets/styles/05-components/form-inputs';
.accordion {
  margin: 10px 0;
  border: none;
}

.accordion-trigger {
  display: flex;
  justify-content: space-between;
  align-items: center;

  /* padding: 10px; */
  cursor: pointer;
  border-bottom: 1px solid #ccc;
}

.accordion-title {
  flex: 1;
  font-weight: bold;
  margin-left: 10px;
}

.accordion-icon {
  /* font-size: 1.2rem; */
  transition: transform 0.3s;
  margin-left: 10px;
  color: var(--finder-color-dark-50);

  & > * {
    --svg-icon-size: 16px;
  }
}

.accordion-icon.open {
  transform: rotate(180deg);
}

.accordion-content {
  display: flex;
  flex-direction: column;

  /* padding: 10px; */

  border-bottom: 1px solid #ccc;
  overflow: scroll;
  max-height: 130px;
  margin: 5px 0;
  margin-bottom: 10px;
  gap: 10px;
}
.accordion.open .accordion-content {
  display: flex;
  flex-direction: column;

  /* padding: 10px; */

  border-bottom: none; /* Hide the border when the content is expanded */
  overflow: scroll;
  max-height: 130px;
  margin: 3px 0;
}
</style>
