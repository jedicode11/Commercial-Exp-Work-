import Stand from './Stand.vue'

export default {
  title: 'Modules/Stand',
  component: Stand
}

const Template = (args) => ({
  components: { Stand },
  setup () {
    return { args }
  },
  template: '<stand v-bind="args" />'
})

export const Demo = Template.bind({})
Demo.args = {
}
Demo.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/UBD14Sn7Yd4VSWWa64DDWA/Messe---Component-Library?node-id=575%3A13378'
  },
  addWrapper: false // disable wrapper (see finder-template) as 'Stand' is an app and brings its own wrapper
}
