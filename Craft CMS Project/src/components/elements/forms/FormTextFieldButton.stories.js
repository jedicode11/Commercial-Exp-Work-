import UiStateGrid from '../../dev/UiStateGrid.vue'
import { SvgIconName } from '@/components/helpers/SvgIcon'
import FormTextFieldButton from './FormTextFieldButton'

export default {
  title: 'Elements/Forms/FieldTypes/Text/Elements/TextFieldButton',
  component: FormTextFieldButton,
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
    FormTextFieldButton,
    UiStateGrid
  },
  setup () {
    return { args }
  },
  template: `
    <ui-state-grid title="Background">
    <form-text-field-button v-bind="args"/>
    <template #disabled>
      <form-text-field-button v-bind="args" is-disabled/>
    </template>
    <template #focus>
      <form-text-field-button v-bind="args" is-active/>
    </template>
    <template #hover>
      <form-text-field-button v-bind="args" is-hovered/>
    </template>
    <template #error>
      <form-text-field-button v-bind="args" is-invalid/>
    </template>
    </ui-state-grid>

    <ui-state-grid title="No Background">
    <form-text-field-button v-bind="args" :has-background="false"/>
    <template #disabled>
      <form-text-field-button v-bind="args" :has-background="false" is-disabled/>
    </template>
    <template #focus>
      <form-text-field-button v-bind="args" :has-background="false" is-active/>
    </template>
    <template #hover>
      <form-text-field-button v-bind="args" :has-background="false" is-hovered/>
    </template>
    <template #error>
      <form-text-field-button v-bind="args" :has-background="false" is-invalid/>
    </template>
    </ui-state-grid>
  `
})

export const Demo = Template.bind({})
Demo.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/8OaqErurE13XGPDPczvX2v/Messe---Component-Library?node-id=572-20947&t=qyez7lywRf3e2uUO-0'
  },
  controls: { exclude: ['hasBackground', 'isDisabled', 'isInvalid', 'isActive', 'isHovered'] }
}
Demo.args = {
  icon: SvgIconName.search,
  title: 'Title'
}

const SingleComponentTemplate = (args) => ({
  components: {
    FormTextFieldButton
  },
  setup () {
    return { args }
  },
  template: '<form-text-field-button v-bind="args"/>'
})

export const SingleComponent = SingleComponentTemplate.bind({})
SingleComponent.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/8OaqErurE13XGPDPczvX2v/Messe---Component-Library?node-id=572-20947&t=qyez7lywRf3e2uUO-0'
  }
}
SingleComponent.args = {
  icon: SvgIconName.search,
  title: 'Title'
}
