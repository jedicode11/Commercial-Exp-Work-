<template>
  <div class="form-select-question">
    <form-drop-down v-bind="{isInvalid, isFocused, isHovered, isDisabled:(isDisabled || !options.length) }"
                    :title="placeholder"
                    v-model:open-value="isOpen">
      <template #trigger>
        {{ selectedLabel ? selectedLabel : placeholder }}
      </template>
      <template #default="{isInvalid}">
        <div :class="scriptParamsService?.isSteleMode ? 'select-options-stele' : 'select-options'">
          <radio-button v-for="(option, index) in options" :key="index"
                        :value="option.value" :label="option.label" :is-invalid="isInvalid"
                        :checked="option.checked" :is-disabled="option.disabled"
                        :appearance="RadioButtonAppearance.form" hide-icon
                        v-model="selected" @change="close"/>
        </div>
      </template>
    </form-drop-down>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, ref, toRefs, inject } from 'vue'
import { SvgIconName } from '@/components/helpers/SvgIcon.vue'
import RadioButton, { RadioButtonAppearance } from '@/components/elements/RadioButton.vue'
import FormDropDown from '@/components/elements/forms/FormDropDown.vue'
import { ScriptParamsService, sps } from '@/utils/scriptParamService'

export interface FormSelectOption {
  value: string | number,
  label: string,
  checked?: boolean
  disabled?: boolean
}

export default defineComponent({
  name: 'FormSelect',
  components: { FormDropDown, RadioButton },
  emits: ['update:modelValue'],
  props: {

    /** selected option value */
    modelValue: {
      type: [String, Number],
      required: false
    },

    /** Set select options (tree or list) */
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
  setup (props, { emit }) {
    const { options, isDisabled } = toRefs(props)
    const isOpen = ref(false)

    const scriptParamsService = inject<ScriptParamsService | undefined>(sps)

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
      close,
      scriptParamsService
    }
  }
})
</script>

<style lang="scss" scoped>

.select-options {
  display: flex;
  flex-direction: column;
  gap: 16px;

  & > * {
    margin: 0;
    padding: 0;
    color: var(--finder-color-50);
    width: 100%;
  }
}
.select-options-stele {

  & > * {
    margin: 0;
    padding: 0;
    padding-bottom: 16px;
    color: var(--finder-color-50);
    width: 100%;
  }
}
</style>
