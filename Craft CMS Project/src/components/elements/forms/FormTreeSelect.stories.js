import { SvgIconName } from '@/components/helpers/SvgIcon'
import UiStateGrid from '../../dev/UiStateGrid.vue'
import { ref } from 'vue'
import FormTreeSelect from '@/components/elements/forms/FormTreeSelect.vue'
import FormField from '@/components/elements/forms/FormField.vue'
import { createFormErrorFeedback, createFormField, createFormLabel } from '@/models/FormFieldInterface'
import { createLink } from '@/models/Link'

export default {
  components: { FormTreeSelect },
  title: 'Elements/Forms/FieldTypes/Select/Tree',
  component: FormTreeSelect
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
  options: {
    id: 'rootValue',
    label: 'Root',
    children: [
      { id: 'value1', label: 'Value 1' },
      {
        id: 'value2',
        label: 'Value 2 with long label for layout testing ipsum dolor sit amet, consectetur adipiscing elit/In at erat lacus, quis luctus mauris.'
      },
      {
        id: 'value3',
        label: 'Value 3',
        children: [
          { id: 'value3.1', label: 'Value 3.1' },
          { id: 'value3.2', label: 'Value 3.2' },
          { id: 'value3.3', label: 'Value 3.3' }
        ]
      },
      { id: 'value4', label: 'Value 4 (disabled)', disabled: true },
      { id: 'value5', label: 'Value 5' },
      { id: 'value6', label: 'Value 6' },
      { id: 'value7', label: 'Value 7' },
      { id: 'value8', label: 'Value 8' },
      { id: 'value9', label: 'Value 9' },
      { id: 'value10', label: 'Value 10' },
      { id: 'value11', label: 'Value 11' },
      { id: 'value12', label: 'Value 12' }
    ]
  }
}

const Template = (args) => ({
  components: {
    FormTreeSelect,
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
        <form-tree-select v-bind="Object.assign({}, args, slotProps)" v-model="selectedValues"/>
      </template>
    </form-field>
    <template #hover>
      <form-field :field="field" is-hovered>
        <template #default="slotProps">
          <form-tree-select v-bind="Object.assign({}, args, slotProps)" v-model="selectedValues" is-hovered/>
        </template>
      </form-field>
    </template>
    <template #disabled>
      <form-field :field="field" is-disabled>
        <template #default="slotProps">
          <form-tree-select v-bind="Object.assign({}, args, slotProps)" v-model="selectedValues"/>
        </template>
      </form-field>
    </template>
    <template #error>
      <form-field :field="field" is-invalid>
        <template #default="slotProps">
          <form-tree-select v-bind="Object.assign({}, args, slotProps)" v-model="selectedValues"/>
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
    FormTreeSelect,
    UiStateGrid
  },
  setup () {
    const selectedValues = ref(['value2'])
    return { args, selectedValues }
  },
  template: `
    <p>Selected Values:{{ selectedValues }}</p>
    <form-tree-select v-bind="args" v-model="selectedValues"></form-tree-select>
  `
})

export const SingleComponent = SingleComponentTemplate.bind({})
SingleComponent.parameters = defaultParams
SingleComponent.args = defaultArgs
