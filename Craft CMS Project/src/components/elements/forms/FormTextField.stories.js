import { SvgIconName } from '@/components/helpers/SvgIcon'
import FormTextField from '@/components/elements/forms/FormTextField.vue'
import FormTextFieldButton from '@/components/elements/forms/FormTextFieldButton.vue'
import { ref } from 'vue'
import UiStateGrid from '@/components/dev/UiStateGrid.vue'
import FormField from '@/components/elements/forms/FormField.vue'
import { createFormErrorFeedback, createFormField, createFormLabel } from '@/models/FormFieldInterface'
import { createLink } from '@/models/Link'

export default {
  title: 'Elements/Forms/FieldTypes/Text',
  component: FormTextField
}

const Template = (args) => ({
  components: {
    FormTextField,
    UiStateGrid,
    FormField
  },
  setup () {
    const text = ref('')
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

    return { args, text, field }
  },
  template: `
    <ui-state-grid style="--slot-min-width:400px;">
    <form-field :field="field">
      <template #default="slotProps">
        <form-text-field v-bind="Object.assign({}, args, slotProps)" v-model="text"/>
      </template>
    </form-field>
    <template #hover>
      <form-field :field="field" is-hovered>
        <template #default="slotProps">
          <form-text-field v-bind="Object.assign({}, args, slotProps)" v-model="text" is-hovered/>
        </template>
      </form-field>
    </template>
    <template #disabled>
      <form-field :field="field" is-disabled>
        <template #default="slotProps">
          <form-text-field v-bind="Object.assign({}, args, slotProps)" v-model="text"/>
        </template>
      </form-field>
    </template>
    <template #error>
      <form-field :field="field" is-invalid>
        <template #default="slotProps">
          <form-text-field v-bind="Object.assign({}, args, slotProps)" v-model="text"/>
        </template>
      </form-field>
    </template>
    </ui-state-grid>`
})

export const Demo = Template.bind({})
Demo.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/8OaqErurE13XGPDPczvX2v/Messe---Component-Library?node-id=583-4951&t=XTJEPNSYxUTDorZM-0'
  },
  controls: { exclude: ['modelValue', 'isDisabled', 'isInvalid', 'isHovered'] }
}
Demo.args = {
  placeholder: 'Please enter your data'
}

const TextFieldTemplate = (args) => ({
  components: {
    FormTextField
  },
  setup () {
    const text = ref('')
    return { args, text }
  },
  template: '<form-text-field v-bind="args" v-model="text"/>'
})

export const TextField = TextFieldTemplate.bind({})
TextField.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/8OaqErurE13XGPDPczvX2v/Messe---Component-Library?node-id=583-4951&t=XTJEPNSYxUTDorZM-0'
  }
}
TextField.args = {
  isInvalid: false,
  isDisabled: false,
  showInfo: true,
  placeholder: 'Please enter your text'
}

const SearchFieldTemplate = (args) => ({
  components: {
    FormTextField,
    FormTextFieldButton
  },
  setup () {
    const text = ref('')
    return { args, text }
  },
  template: `
    <form-text-field v-bind="args" v-model="text">
    <template #buttons="buttonProps">
      <form-text-field-button icon="search" v-bind="buttonProps"/>
    </template>
    </form-text-field>
  `
})

export const SearchField = SearchFieldTemplate.bind({})
SearchField.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/8OaqErurE13XGPDPczvX2v/Messe---Component-Library?node-id=572-20502&t=gCoFI7JVeX3NV76l-0'
  }
}
SearchField.args = {
  isInvalid: false,
  isDisabled: false,
  isRequired: false,
  type: 'search',
  placeholder: 'Please enter your search keywords'
}
