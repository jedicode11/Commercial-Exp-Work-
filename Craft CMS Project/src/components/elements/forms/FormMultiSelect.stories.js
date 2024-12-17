import { SvgIconName } from '@/components/helpers/SvgIcon'
import UiStateGrid from '../../dev/UiStateGrid.vue'
import { ref } from 'vue'
import FormMultiSelect from '@/components/elements/forms/FormMultiSelect.vue'
import FormField from '@/components/elements/forms/FormField.vue'
import { createFormErrorFeedback, createFormField, createFormLabel } from '@/models/FormFieldInterface'
import { createLink } from '@/models/Link'

export default {
  components: { FormMultiSelect },
  title: 'Elements/Forms/FieldTypes/Select/MultiSelect',
  component: FormMultiSelect
}

const defaultParams = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/8OaqErurE13XGPDPczvX2v/Messe---Component-Library?type=design&node-id=572-19511&t=feCygJGygjFT8NRR-0'
  },
  controls: { exclude: ['modelValue', 'options'] }
}

const defaultArgs = {
  text: 'Choose one or more options',
  options: [
    { value: 'value1', label: 'Value 1' },
    {
      value: 'value2',
      label: 'Value 2 with long label for layout testing ipsum dolor sit amet, consectetur adipiscing elit/In at erat lacus, quis luctus mauris.'
    },
    { value: 'value3', label: 'Value 3' },
    { value: 'value4', label: 'Value 4 (disabled)', disabled: true },
    { value: 'value5', label: 'Value 5' },
    { value: 'value6', label: 'Value 6' },
    { value: 'value7', label: 'Value 7' },
    { value: 'value8', label: 'Value 8' },
    { value: 'value9', label: 'Value 9' },
    { value: 'value10', label: 'Value 10' },
    { value: 'value11', label: 'Value 11' },
    { value: 'value12', label: 'Value 12' }
  ]
}

const Template = (args) => ({
  components: {
    FormMultiSelect,
    UiStateGrid,
    FormField
  },
  setup () {
    const selectedValues = ref(['value2'])
    const field = createFormField({
      label: createFormLabel({
        text: 'Form label',
        link: createLink({ text: 'Linktext' }),
        icon: SvgIconName.alternateEmail
      }),
      errorMessage: createFormErrorFeedback({
        text: 'Error text',
        link: createLink({ text: 'Linktext' })
      })
    })

    return { args, selectedValues, field }
  },
  template: `
    <ui-state-grid style="--slot-min-width:400px;">
    <form-field :field="field">
      <template #default="slotProps">
        <form-multi-select v-bind="Object.assign({}, args, slotProps)" v-model="selectedValues"/>
      </template>
    </form-field>
    <template #hover>
      <form-field :field="field" is-hovered>
        <template #default="slotProps">
          <form-multi-select v-bind="Object.assign({}, args, slotProps)" v-model="selectedValues" is-hovered/>
        </template>
      </form-field>
    </template>
    <template #disabled>
      <form-field :field="field" is-disabled>
        <template #default="slotProps">
          <form-multi-select v-bind="Object.assign({}, args, slotProps)" v-model="selectedValues"/>
        </template>
      </form-field>
    </template>
    <template #error>
      <form-field :field="field" is-invalid>
        <template #default="slotProps">
          <form-multi-select v-bind="Object.assign({}, args, slotProps)" v-model="selectedValues"/>
        </template>
      </form-field>
    </template>
    </ui-state-grid>`
})

export const Demo = Template.bind({})
Demo.parameters = Object.assign({}, defaultParams, {
  controls: { exclude: ['modelValue', 'options', 'isDisabled', 'isInvalid', 'isHovered', 'tabindex'] }
})
Demo.args = defaultArgs

const SingleComponentTemplate = (args) => ({
  components: {
    FormMultiSelect,
    UiStateGrid
  },
  setup () {
    const selectedValues = ref(['value2'])
    return { args, selectedValues }
  },
  template: `
    <p>Selected Values:{{ selectedValues }}</p>
    <form-multi-select v-bind="args" v-model="selectedValues"></form-multi-select>
  `
})

export const SingleComponent = SingleComponentTemplate.bind({})
SingleComponent.parameters = defaultParams
SingleComponent.args = defaultArgs
