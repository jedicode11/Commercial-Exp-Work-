import UiStateGrid from '../../dev/UiStateGrid.vue'
import FormField from './FormField'
import { createFormErrorFeedback, createFormField, createFormLabel } from '@/models/FormFieldInterface'
import { createLink } from '@/models/Link'
import { SvgIconName } from '@/components/helpers/SvgIcon'
import FormTextField from '@/components/elements/forms/FormTextField.vue'
import { ref } from 'vue'

export default {
  components: { FormField },
  title: 'Elements/Forms/FormField',
  component: FormField,
  argTypes: {
    onLinkLabelClick: { action: 'Link "Label" Click' },
    onLinkSeparateClick: { action: 'Link "Separate" Click' },
    onLinkErrorClick: { action: 'Link "Error" Click' }
  }
}

const Template = (args) => ({
  components: {
    FormField,
    UiStateGrid,
    FormTextField
  },
  setup () {
    const text = ref('')
    return { args, text }
  },
  template: `
    <ui-state-grid style="--slot-min-width:400px;">
    <form-field v-bind="args">
      <template #default="slotProps">
        <form-text-field v-bind="slotProps" v-model="text" placeholder="Please enter your data" show-info/>
      </template>
    </form-field>
    <template #disabled>
      <form-field v-bind="args" is-disabled>
        <template #default="slotProps">
          <form-text-field v-bind="slotProps" v-model="text" placeholder="Please enter your data" show-info/>
        </template>
      </form-field>
    </template>
    <template #error>
      <form-field v-bind="args" is-invalid>
        <template #default="slotProps">
          <form-text-field v-bind="slotProps" v-model="text" placeholder="Please enter your data" show-info/>
        </template>
      </form-field>
    </template>
    </ui-state-grid>
  `
})

export const Demo = Template.bind({})
Demo.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/8OaqErurE13XGPDPczvX2v/Messe---Component-Library?node-id=583-4951&t=ONUjF38IzNYRJEMa-0'
  }
}
Demo.args = {
  field: createFormField({
    label: createFormLabel({
      text: 'Form label',
      link: createLink({ text: 'Linktext' }),
      icon: SvgIconName.alternateEmail

    }),
    linkSeparate: createLink({ text: 'Passwort vergessen?' }),
    errorMessage: createFormErrorFeedback({
      text: 'Error text',
      link: createLink({ text: 'Linktext' })
    })
  }),
  isInvalid: false,
  isDisabled: false,
  isRequired: true
}

const SingleComponentTemplate = (args) => ({
  components: {
    FormField,
    UiStateGrid,
    FormTextField
  },
  setup () {
    const text = ref('')
    return { args, text }
  },
  template: `
    <form-field v-bind="args">
    <template v-slot="slotProps">
      <form-text-field v-bind="slotProps" v-model="text"/>
    </template>
    </form-field>`
})

export const SingleComponent = SingleComponentTemplate.bind({})
SingleComponent.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/8OaqErurE13XGPDPczvX2v/Messe---Component-Library?node-id=583-4951&t=ONUjF38IzNYRJEMa-0'
  }
}
SingleComponent.args = {
  field: createFormField({
    label: createFormLabel({
      text: 'Form label',
      link: createLink({ text: 'Linktext' }),
      icon: SvgIconName.alternateEmail

    }),
    linkSeparate: createLink({ text: 'Passwort vergessen?' }),
    errorMessage: createFormErrorFeedback({
      text: 'Error text',
      link: createLink({ text: 'Linktext' })
    })
  }),
  isInvalid: false,
  isDisabled: false,
  isRequired: true
}
