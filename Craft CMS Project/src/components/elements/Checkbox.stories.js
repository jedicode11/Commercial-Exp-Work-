import UiStateGrid from '../dev/UiStateGrid.vue'
import Checkbox, { CheckBoxAppearance } from './Checkbox'
import { ref, watch } from 'vue'

export default {
  title: 'Elements/Checkbox',
  component: Checkbox,
  argTypes: {
    appearance: {
      options: [...Object.values(CheckBoxAppearance)],
      control: {
        type: 'select'
      }
    }
  }
}

const checked = ref(['selected'])

const Template = (args) => ({
  components: {
    Checkbox,
    UiStateGrid
  },
  setup () {
    return { args, checked }
  },
  template: `
    <p>Checked: {{ checked }}</p>
    <ui-state-grid title="Appearance 'default'">
    <template #default>
      <checkbox v-bind="args" value="default" v-model="checked"></checkbox>
    </template>
    <template #focus>
      <checkbox v-bind="args" is-focused value="focus" v-model="checked"></checkbox>
    </template>
    <template #hover>
      <checkbox v-bind="args" is-hovered value="hover" v-model="checked"></checkbox>
    </template>
    <template #selected>
      <checkbox v-bind="args" value="selected" v-model="checked"></checkbox>
    </template>
    <template #selectedFocused>
      <checkbox v-bind="args" is-focused value="selected" v-model="checked"></checkbox>
    </template>
    <template #disabled>
      <checkbox v-bind="args" is-disabled value="disabled" v-model="checked"></checkbox>
    </template>
    <template #error>
      <checkbox v-bind="args" is-invalid value="error" v-model="checked"></checkbox>
    </template>
    </ui-state-grid>
    <ui-state-grid title="Appearance 'form'">
    <template #default>
      <checkbox v-bind="args" value="default" v-model="checked" appearance="form"></checkbox>
    </template>
    <template #focus>
      <checkbox v-bind="args" is-focused value="focus" v-model="checked" appearance="form"></checkbox>
    </template>
    <template #hover>
      <checkbox v-bind="args" is-hovered value="hover" v-model="checked" appearance="form"></checkbox>
    </template>
    <template #selected>
      <checkbox v-bind="args" value="selected" v-model="checked" appearance="form"></checkbox>
    </template>
    <template #selectedFocused>
      <checkbox v-bind="args" is-focused value="selected" v-model="checked" appearance="form"></checkbox>
    </template>
    <template #disabled>
      <checkbox v-bind="args" is-disabled value="disabled" v-model="checked" appearance="form"></checkbox>
    </template>
    <template #error>
      <checkbox v-bind="args" is-invalid value="error" v-model="checked" appearance="form"></checkbox>
    </template>
    </ui-state-grid>
  `
})

export const DemoDefault = Template.bind({})
DemoDefault.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/8OaqErurE13XGPDPczvX2v/Messe---Component-Library?node-id=33-3637&t=V7mT0HWhaLvM46If-0'
  }
}
DemoDefault.args = {
  label: 'Label'
}

export const DemoIndeterminate = Template.bind({})
DemoIndeterminate.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/8OaqErurE13XGPDPczvX2v/Messe---Component-Library?node-id=33-3637&t=V7mT0HWhaLvM46If-0'
  }
}
DemoIndeterminate.args = {
  isIndeterminate: true,
  label: 'Label'
}

const SingleComponentTemplate = (args) => ({
  components: {
    Checkbox
  },
  setup () {
    const selected = ref(true)
    watch(selected, (newVal) => {
      console.log('[selected.value] = ', newVal)
    })
    return { args, selected }
  },
  template: `
    <checkbox v-bind="args" value="Checkbox 1" v-model="selected"/>`
})

export const SingleComponent = SingleComponentTemplate.bind({})
SingleComponent.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/8OaqErurE13XGPDPczvX2v/Messe---Component-Library?node-id=33-3637&t=V7mT0HWhaLvM46If-0'
  }
}
SingleComponent.args = {
  appearance: CheckBoxAppearance.default,
  isIndeterminate: true,
  label: 'Label'
}
