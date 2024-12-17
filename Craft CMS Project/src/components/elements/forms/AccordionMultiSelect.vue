<template>
  <div class="accordion">
    <div class="accordion-trigger" @click="open = !open">
      <div class="accordion-icon" :class="{ open: open }">
        <svg-icon :icon-name="SvgIconName.chevronDown"/>
      </div>
      <div class="accordion-title">{{ text }}</div>
    </div>
    <div class="select-options">
      <transition name="accordion-slide"
        v-bind="{ isInvalid, isFocused, isHovered, isDisabled: (isDisabled || !options.length) }" :title="text">
        <div v-if="open" class="accordion-content">
          <checkbox v-for="(option, index) in options" :key="index" :value="option.value" :label="option.label"
            :is-invalid="isInvalid" :is-disabled="option.disabled" :appearance="CheckBoxAppearance.form"
            v-model="selected" />
          <slot></slot>
        </div>
      </transition>
    </div>
  </div>
</template>

<script lang="ts">
import Checkbox, { CheckBoxAppearance } from '@/components/elements/Checkbox.vue'
import { computed, defineComponent, ref } from 'vue'
import SvgIcon, { SvgIconName } from '@/components/helpers/SvgIcon.vue'

export interface FormSelectOption {
  value: string | number,
  label: string,
  checked?: boolean
  disabled?: boolean
}

export default defineComponent({
  name: 'AccordionMultiSelect',
  components: {
    Checkbox,
    SvgIcon
  },
  emits: ['update:modelValue'],
  props: {

    modelValue: {
      type: [String, Number, Array as () => Array<string>],
      required: false,
      default: ''
    },

    options: {
      type: Array as () => Array<FormSelectOption>,
      required: false,
      default: () => []
    },

    text: {
      type: String,
      required: true
    },

    isDisabled: {
      type: Boolean,
      default: false
    },

    isInvalid: {
      type: Boolean,
      default: false
    },

    isHovered: {
      type: Boolean,
      required: false,
      default: false
    },

    isFocused: {
      type: Boolean,
      required: false,
      default: false
    },

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
    const isOpen = ref(false)

    const selected = computed({
      get (): boolean | string | number | (string | number)[] | undefined {
        return props.modelValue
      },
      set (value: boolean | string | number | (string | number)[] | undefined) {
        emit('update:modelValue', value)
      }
    })

    return {
      CheckBoxAppearance,
      isOpen,
      selected,
      SvgIconName
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
  cursor: pointer;
  border-bottom: 1px solid #ccc;
}

.accordion-title {
  flex: 1;
  font-weight: bold;
  margin-left: 10px;
}

.accordion-icon {
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

  border-bottom: none;
  overflow: scroll;
  max-height: 130px;
  margin: 3px 0;
}
</style>
