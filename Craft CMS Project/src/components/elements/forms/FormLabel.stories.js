import UiStateGrid from '../../dev/UiStateGrid.vue'
import { SvgIconName } from '@/components/helpers/SvgIcon'
import FormLabel from './FormLabel'
import { createLink, LinkTarget } from '@/models/Link'

export default {
  title: 'Elements/Forms/FormField/Elements',
  component: FormLabel,
  argTypes: {
    icon: {
      options: ['', ...Object.values(SvgIconName)],
      control: {
        type: 'select'
      }
    }
  }
}

const Template = (args) => ({
  components: {
    FormLabel,
    UiStateGrid
  },
  setup () {
    return { args }
  },
  template: `
    <ui-state-grid>
    <form-label v-bind="args"/>
    <template #disabled>
      <form-label v-bind="args" is-disabled/>
    </template>
    <template #error>
      <form-label v-bind="args" is-invalid/>
    </template>
    </ui-state-grid>`
})

export const Label = Template.bind({})
Label.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/8OaqErurE13XGPDPczvX2v/Messe---Component-Library?node-id=420-13843&t=IjZN3hmuOUYcilER-0'
  }
}
Label.args = {
  icon: SvgIconName.alternateEmail,
  text: 'Label text',
  link: createLink({ text: 'Link text', href: 'http://www.example.com', target: LinkTarget.blank })
}
