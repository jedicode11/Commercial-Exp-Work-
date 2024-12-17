import TabNavigation from './TabNavigation.vue'

const dummyTabNavItems = [
  { id: 'profil', title: 'Profil', selector: '#' },
  { id: 'marken', title: 'Marken', selector: '#' },
  { id: 'produkte', title: 'Produkte', selector: '#' },
  { id: 'stand', title: 'Stand', selector: '#' },
  { id: 'terminvereinbarung', title: 'Terminvereinbarung', selector: '#' },
  { id: 'events', title: 'Events', selector: '#' },
  { id: 'entdecke', title: 'Entdecke Ähnliches', selector: '#' }
]

export default {
  title: 'Elements/Navigation',
  component: TabNavigation,
  argTypes: {
    containerWidth: {
      options: [
        '100%',
        '50%',
        '25%'
      ],
      control: {
        type: 'select'
      }
    }
  }
}

const Template = (args) => ({
  components: {
    TabNavigation
  },
  setup () {
    return {
      args
    }
  },
  template: `
    <div :style="{width:args.containerWidth}">
    <tab-navigation v-bind="args"/>
    </div>
  `
})

export const TabNavigationBar = Template.bind({})
TabNavigationBar.args = {
  items: dummyTabNavItems,
  containerWidth: '100%'
}
TabNavigationBar.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/UBD14Sn7Yd4VSWWa64DDWA/Messe---Component-Library?node-id=18%3A1908'
  },
  controls: { include: ['containerWidth', 'isExpandable', 'isSmall'] }
}
