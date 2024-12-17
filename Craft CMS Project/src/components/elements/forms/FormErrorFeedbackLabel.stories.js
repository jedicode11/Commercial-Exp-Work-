import UiStateGrid from '../../dev/UiStateGrid.vue'
import FormErrorFeedbackLabel from './FormErrorFeedbackLabel'
import { createLink, LinkTarget } from '@/models/Link'

export default {
  title: 'Elements/Forms/FormField/Elements',
  component: FormErrorFeedbackLabel,
  argTypes: {
    onLinkClick: { action: 'Link Click' }
  }
}

const Template = (args) => ({
  components: {
    FormErrorFeedbackLabel,
    UiStateGrid
  },
  setup () {
    return { args }
  },
  template: '<form-error-feedback-label v-bind="args"/>'
})

export const ErrorFeedback = Template.bind({})
ErrorFeedback.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/8OaqErurE13XGPDPczvX2v/Messe---Component-Library?node-id=472-14975&t=ONUjF38IzNYRJEMa-0'
  }
}
ErrorFeedback.args = {
  text: 'Error text',
  link: createLink({ text: 'Link text', href: 'http://www.example.com', target: LinkTarget.blank })
}
