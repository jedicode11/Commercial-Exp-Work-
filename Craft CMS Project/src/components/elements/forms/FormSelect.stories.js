import { SvgIconName } from '@/components/helpers/SvgIcon'
import UiStateGrid from '../../dev/UiStateGrid.vue'
import { ref } from 'vue'
import FormSelect from '@/components/elements/forms/FormSelect.vue'
import FormField from '@/components/elements/forms/FormField.vue'
import { createFormErrorFeedback, createFormField, createFormLabel } from '@/models/FormFieldInterface'
import { createLink } from '@/models/Link'

export default {
  components: { FormSelect },
  title: 'Elements/Forms/FieldTypes/Select/Select',
  component: FormSelect
}

const defaultParams = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/8OaqErurE13XGPDPczvX2v/Messe---Component-Library?type=design&node-id=572-19511&t=feCygJGygjFT8NRR-0'
  },
  controls: { exclude: ['modelValue', 'options'] }
}

const defaultArgs = {
  placeholder: 'Choose one or more options',
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
    FormSelect,
    UiStateGrid,
    FormField
  },
  setup () {
    const selectedValue = ref(undefined)
    // const selectedValue = ref('value1')

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

    return { args, selectedValue, field }
  },
  template: `
    <ui-state-grid style="--slot-min-width:400px;">
    <form-field :field="field">
      <template #default="slotProps">
        <form-select v-bind="Object.assign({}, args, slotProps)" v-model="selectedValue"/>
      </template>
    </form-field>
    <template #hover>
      <form-field :field="field" is-hovered>
        <template #default="slotProps">
          <form-select v-bind="Object.assign({}, args, slotProps)" v-model="selectedValue" is-hovered/>
        </template>
      </form-field>
    </template>
    <template #disabled>
      <form-field :field="field" is-disabled>
        <template #default="slotProps">
          <form-select v-bind="Object.assign({}, args, slotProps)" v-model="selectedValue"/>
        </template>
      </form-field>
    </template>
    <template #error>
      <form-field :field="field" is-invalid>
        <template #default="slotProps">
          <form-select v-bind="Object.assign({}, args, slotProps)" v-model="selectedValue"/>
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
    FormSelect,
    UiStateGrid
  },
  setup () {
    const selectedValue = ref(undefined)
    // const selectedValue = ref('value1')
    return { args, selectedValue }
  },
  template: `
    <p>Selected Value:{{ selectedValue }}</p>
    <form-select v-bind="args" v-model="selectedValue"></form-select>
  `
})

export const SingleComponent = SingleComponentTemplate.bind({})
SingleComponent.parameters = defaultParams
SingleComponent.args = defaultArgs
