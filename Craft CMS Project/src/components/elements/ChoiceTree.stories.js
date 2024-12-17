import { ref } from 'vue'
import ChoiceTree, { ChoiceTreeAppearance } from '@/components/elements/ChoiceTree.vue'

export default {
  title: 'Elements/ChoiceTree',
  component: ChoiceTree,
  argTypes: {
    appearance: {
      options: [...Object.values(ChoiceTreeAppearance)],
      control: {
        type: 'select'
      }
    }
  }
}

const Template = (args) => ({
  components: {
    ChoiceTree
  },
  setup () {
    const selectedValues = ref(['value3.2'])
    return { args, selectedValues }
  },
  template: `
    <p>Selected Values:{{ selectedValues }}</p>
    <choice-tree v-bind="args" v-model="selectedValues"></choice-tree>
  `
})

export const Demo = Template.bind({})
Demo.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/8OaqErurE13XGPDPczvX2v/Messe---Component-Library?node-id=572-19511&t=qcesOc7qoPIDPaCA-0'
  },
  controls: { exclude: ['node', 'treeDepth', 'modelValue'] }
}
Demo.args = {
  appearance: ChoiceTreeAppearance.default,
  node: {
    id: 'valueRoot',
    label: 'Root',
    children: [
      { id: 'value1', label: 'Value 1' },
      { id: 'value2', label: 'Value 2' },
      {
        id: 'value3',
        label: 'Value 3',
        children: [
          { id: 'value3.1', label: 'Value 3.1' },
          { id: 'value3.2', label: 'Value 3.2' },
          { id: 'value3.3', label: 'Value 3.3' }
        ]
      },
      { id: 'value4', label: 'Value 4', disabled: true },
      { id: 'value5', label: 'Value 5' }
    ]
  }
}
