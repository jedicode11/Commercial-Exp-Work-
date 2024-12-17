import FavButton from './FavButton.vue'
import UiStateGrid from '../dev/UiStateGrid'

const favButtonParams = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/UBD14Sn7Yd4VSWWa64DDWA/Messe---Component-Library?node-id=12%3A1551'
  },
  controls: { exclude: ['subHeadline', 'headline', 'gridInverted'] }
}

export default {
  title: 'Elements/Buttons/FavButton',
  component: FavButton
}

const Template = (args) => ({
  components: { FavButton, UiStateGrid },
  setup () {
    return { args }
  },
  template: `
    <ui-state-grid :title="args.headline" :subtitle="args.subHeadline" :is-inverted="args.gridInverted">
    <fav-button v-bind="args"/>
    <fav-button v-bind="args" :show-text="true"/>
    <template #hover>
      <fav-button v-bind="args" :is-hovered="true"/>
      <fav-button v-bind="args" :is-hovered="true" :show-text="true"/>
    </template>
    <template #disabled>
      <fav-button v-bind="args" :is-disabled="true"/>
      <fav-button v-bind="args" :is-disabled="true" :show-text="true"/>
    </template>
    </ui-state-grid>

    <ui-state-grid :title="args.headline" :subtitle="args.subHeadline + ' / Active'" :is-inverted="args.gridInverted">
    <fav-button v-bind="args" :is-active="true"/>
    <fav-button v-bind="args" :is-active="true" :show-text="true"/>
    <template #hover>
      <fav-button v-bind="args" :is-active="true" :is-hovered="true"/>
      <fav-button v-bind="args" :is-active="true" :is-hovered="true" :show-text="true"/>
    </template>
    <template #disabled>
      <fav-button v-bind="args" :is-active="true" :is-disabled="true"/>
      <fav-button v-bind="args" :is-active="true" :is-disabled="true" :show-text="true"/>
    </template>
    </ui-state-grid>
  `
})

export const Big = Template.bind({})
Big.args = {
  headline: 'Fav Button',
  gridInverted: false,
  subHeadline: 'Big',
  itemId: 'profile=1'
}
Big.parameters = favButtonParams

export const Small = Template.bind({})
Small.args = {
  headline: 'Fav Button',
  gridInverted: false,
  subHeadline: 'Small',
  isSmall: true,
  itemId: 'profile=1'
}
Small.parameters = favButtonParams

export const BigInverted = Template.bind({})
BigInverted.args = {
  headline: 'Fav Button',
  gridInverted: true,
  subHeadline: 'Big Inverted',
  isInverted: true,
  itemId: 'profile=1'
}
Big.parameters = favButtonParams

export const SmallInverted = Template.bind({})
SmallInverted.args = {
  headline: 'Fav Button',
  gridInverted: true,
  subHeadline: 'Small Inverted',
  isSmall: true,
  isInverted: true,
  itemId: 'profile=1'
}
SmallInverted.parameters = favButtonParams

const SingleComponentTemplate = (args) => ({
  components: {
    FavButton
  },
  setup () {
    return { args }
  },
  template: '<fav-button v-bind="args" />'
})

export const SingleComponent = SingleComponentTemplate.bind({})
SingleComponent.parameters = favButtonParams
SingleComponent.args = {
  itemId: 'profile=1'
}
