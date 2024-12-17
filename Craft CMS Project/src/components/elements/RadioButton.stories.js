import UiStateGrid from '../dev/UiStateGrid.vue'
import RadioButton, { RadioButtonAppearance } from './RadioButton'
import { ref } from 'vue'

export default {
  title: 'Elements/RadioButton',
  component: RadioButton,
  argTypes: {
    appearance: {
      options: [...Object.values(RadioButtonAppearance)],
      control: {
        type: 'select'
      }
    }
  }
}

const selected = ref('selected')

const Template = (args) => ({
  components: {
    RadioButton,
    UiStateGrid
  },
  setup () {
    return { args, selected }
  },
  template: `
    <p>Selected: {{ selected }}</p>
    <ui-state-grid title="Appearance 'default'">
    <template #default>
      <radio-button v-bind="args" value="default" v-model="selected"></radio-button>
    </template>
    <template #focus>
      <radio-button v-bind="args" is-focused value="focus" v-model="selected"></radio-button>
    </template>
    <template #hover>
      <radio-button v-bind="args" is-hovered value="hover" v-model="selected"></radio-button>
    </template>
    <template #selected>
      <radio-button v-bind="args" value="selected" v-model="selected"></radio-button>
    </template>
    <template #selectedFocused>
      <radio-button v-bind="args" is-focused value="selected" v-model="selected"></radio-button>
    </template>
    <template #disabled>
      <radio-button v-bind="args" is-disabled value="disabled" v-model="selected"></radio-button>
    </template>
    <template #error>
      <radio-button v-bind="args" is-invalid value="error" v-model="selected"></radio-button>
    </template>
    </ui-state-grid>
    <ui-state-grid title="Appearance 'form'">
    <template #default>
      <radio-button v-bind="args" value="default" v-model="selected" appearance="form"></radio-button>
    </template>
    <template #focus>
      <radio-button v-bind="args" is-focused value="focus" v-model="selected" appearance="form"></radio-button>
    </template>
    <template #hover>
      <radio-button v-bind="args" is-hovered value="hover" v-model="selected" appearance="form"></radio-button>
    </template>
    <template #selected>
      <radio-button v-bind="args" value="selected" v-model="selected" appearance="form"></radio-button>
    </template>
    <template #selectedFocused>
      <radio-button v-bind="args" is-focused value="selected" v-model="selected" appearance="form"></radio-button>
    </template>
    <template #disabled>
      <radio-button v-bind="args" is-disabled value="disabled" v-model="selected"
                    appearance="form"></radio-button>
    </template>
    <template #error>
      <radio-button v-bind="args" is-invalid value="error" v-model="selected" appearance="form"></radio-button>
    </template>
    </ui-state-grid>`
})

export const Demo = Template.bind({})
Demo.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/8OaqErurE13XGPDPczvX2v/Messe---Component-Library?node-id=33-3643&t=V7mT0HWhaLvM46If-0'
  },
  controls: { exclude: ['modelValue', 'value', 'tabindex', 'isSelected', 'isInvalid', 'isHovered', 'isFocused', 'isDisabled', 'appearance'] }
}
Demo.args = {
  label: 'Label'
}

const SingleComponentTemplate = (args) => ({
  components: {
    RadioButton
  },
  setup () {
    return { args }
  },
  template: `
    <radio-button v-bind="args" value="Radio Button 1"/>`
})

export const SingleComponent = SingleComponentTemplate.bind({})
SingleComponent.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/8OaqErurE13XGPDPczvX2v/Messe---Component-Library?node-id=33-3637&t=V7mT0HWhaLvM46If-0'
  },
  controls: { exclude: ['modelValue'] }
}
SingleComponent.args = {
  appearance: RadioButtonAppearance.default,
  label: 'Label'
}
