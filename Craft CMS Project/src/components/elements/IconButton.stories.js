import IconButton from './IconButton.vue'
import { SvgIconName } from '@/components/helpers/SvgIcon'
import UiStateGrid from '../dev/UiStateGrid.vue'

const iconButtonParams = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/UBD14Sn7Yd4VSWWa64DDWA/Messe---Component-Library?node-id=12%3A1502'
  },
  controls: { exclude: ['subHeadline', 'headline', 'gridInverted'] }
}

export default {
  title: 'Elements/Buttons/IconButton',
  component: IconButton,
  argTypes: {
    iconName: {
      options: Object.values(SvgIconName),
      control: {
        type: 'select'
      }
    }
  }
}

const Template = (args) => ({
  components: { IconButton, UiStateGrid },
  setup () {
    return { args }
  },
  template: `
    <ui-state-grid :title="args.headline" :subtitle="args.subHeadline" :is-inverted="args.gridInverted">
    <icon-button v-bind="args"/>
    <template #disabled>
      <icon-button v-bind="args" :is-disabled="true"/>
    </template>
    <template #hover>
      <icon-button v-bind="args" :is-hovered="true"/>
    </template>
    </ui-state-grid>

    <ui-state-grid :title="args.headline" :subtitle="args.subHeadline + ' / Background'"
                   :is-inverted="args.gridInverted">
    <icon-button v-bind="args" :has-background="true"/>
    <template #disabled>
      <icon-button v-bind="args" :has-background="true" :is-disabled="true"/>
    </template>
    <template #hover>
      <icon-button v-bind="args" :has-background="true" :is-hovered="true"/>
    </template>
    </ui-state-grid>

    <ui-state-grid :title="args.headline" :subtitle="args.subHeadline + ' / Toggle On'"
                   :is-inverted="args.gridInverted">
    <icon-button v-bind="args" :is-toggle="true" :is-Active="true"/>
    <template #disabled>
      <icon-button v-bind="args" :is-toggle="true" :is-Active="true" :is-disabled="true"/>
    </template>
    <template #hover>
      <icon-button v-bind="args" :is-toggle="true" :is-Active="true" :is-hovered="true"/>
    </template>
    </ui-state-grid>
  `
})

export const Big = Template.bind({})
Big.args = {
  iconName: 'grid_on',
  headline: 'Icon Button',
  gridInverted: false,
  subHeadline: 'Big'
}
Big.parameters = iconButtonParams

export const Small = Template.bind({})
Small.args = {
  iconName: 'grid_on',
  headline: 'Icon Button',
  gridInverted: false,
  subHeadline: 'Small',
  isSmall: true
}
Small.parameters = iconButtonParams

export const BigInverted = Template.bind({})
BigInverted.args = {
  iconName: 'grid_on',
  headline: 'Icon Button',
  gridInverted: true,
  subHeadline: 'Big Inverted',
  isInverted: true
}
Big.parameters = iconButtonParams

export const SmallInverted = Template.bind({})
SmallInverted.args = {
  iconName: 'grid_on',
  headline: 'Icon Button',
  gridInverted: true,
  subHeadline: 'Small Inverted',
  isSmall: true,
  isInverted: true
}
SmallInverted.parameters = iconButtonParams

const SingleComponentTemplate = (args) => ({
  components: {
    IconButton
  },
  setup () {
    return { args }
  },
  template: '<icon-button v-bind="args"/>'
})

export const SingleComponent = SingleComponentTemplate.bind({})
SingleComponent.parameters = iconButtonParams
SingleComponent.args = {
  iconName: 'grid_on',
  url: 'http://www.example.com'
}
