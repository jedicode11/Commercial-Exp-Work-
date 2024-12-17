import CtaButton, { CtaButtonIconPos, CtaButtonType } from './CtaButton.vue'
import { SvgIconName } from '@/components/helpers/SvgIcon'
import UiStateGrid from '../dev/UiStateGrid.vue'

export default {
  title: 'Elements/Buttons/CtaButton',
  component: CtaButton,
  argTypes: {
    iconPos: {
      options: Object.values(CtaButtonIconPos),
      control: {
        type: 'select'
      }
    },
    icon: {
      options: ['', ...Object.values(SvgIconName)],
      control: {
        type: 'select'
      }
    },
    buttonType: {
      options: Object.values(CtaButtonType),
      control: {
        type: 'select'
      }
    }
  }
}

const Template = (args) => ({
  components: {
    CtaButton,
    UiStateGrid
  },
  setup () {
    return { args }
  },
  template: `
    <ui-state-grid :title="args.headline" :subtitle="args.subHeadline">
    <cta-button v-bind="args">{{ args.caption }}</cta-button>
    <cta-button v-bind="args" :icon-name="args.icon" icon-pos="right">{{ args.caption }}</cta-button>
    <cta-button v-bind="args" :icon-name="args.icon">{{ args.caption }}</cta-button>
    <template #hover>
      <cta-button v-bind="args" :is-hovered="true">{{ args.caption }}</cta-button>
      <cta-button v-bind="args" :is-hovered="true" :icon-name="args.icon" icon-pos="right">{{ args.caption }}
      </cta-button>
      <cta-button v-bind="args" :is-hovered="true" :icon-name="args.icon">{{ args.caption }}</cta-button>
    </template>
    <template #focus>
      <cta-button v-bind="args" :is-focussed="true">{{ args.caption }}</cta-button>
      <cta-button v-bind="args" :is-focussed="true" :icon-name="args.icon" icon-pos="right">{{ args.caption }}
      </cta-button>
      <cta-button v-bind="args" :is-focussed="true" :icon-name="args.icon">{{ args.caption }}</cta-button>
    </template>
    <template #disabled>
      <cta-button v-bind="args" :is-disabled="true">{{ args.caption }}</cta-button>
      <cta-button v-bind="args" :is-disabled="true" :icon-name="args.icon" icon-pos="right">{{ args.caption }}
      </cta-button>
      <cta-button v-bind="args" :is-disabled="true" :icon-name="args.icon">{{ args.caption }}</cta-button>
    </template>
    </ui-state-grid>

    <ui-state-grid :is-inverted="true" :title="args.headline" :subtitle="args.subHeadline + ' / Inverted'">
    <cta-button v-bind="args" :is-inverted="true">{{ args.caption }}</cta-button>
    <cta-button v-bind="args" :is-inverted="true" :icon-name="args.icon" icon-pos="right">{{ args.caption }}
    </cta-button>
    <cta-button v-bind="args" :is-inverted="true" :icon-name="args.icon">{{ args.caption }}</cta-button>
    <template #hover>
      <cta-button v-bind="args" :is-inverted="true" :is-hovered="true">{{ args.caption }}</cta-button>
      <cta-button v-bind="args" :is-inverted="true" :is-hovered="true" :icon-name="args.icon" icon-pos="right">{{ args.caption }}
      </cta-button>
      <cta-button v-bind="args" :is-inverted="true" :is-hovered="true" :icon-name="args.icon">{{ args.caption }}</cta-button>
    </template>
    <template #focus>
      <cta-button v-bind="args" :is-inverted="true" :is-focussed="true">{{ args.caption }}</cta-button>
      <cta-button v-bind="args" :is-inverted="true" :is-focussed="true" :icon-name="args.icon" icon-pos="right">{{ args.caption }}
      </cta-button>
      <cta-button v-bind="args" :is-inverted="true" :is-focussed="true" :icon-name="args.icon">{{ args.caption }}</cta-button>
    </template>
    <template #disabled>
      <cta-button v-bind="args" :is-disabled="true" :is-inverted="true">{{ args.caption }}</cta-button>
      <cta-button v-bind="args" :is-disabled="true" :is-inverted="true" :icon-name="args.icon" icon-pos="right">
        {{ args.caption }}
      </cta-button>
      <cta-button v-bind="args" :is-disabled="true" :is-inverted="true" :icon-name="args.icon">{{ args.caption }}
      </cta-button>
    </template>
    </ui-state-grid>
  `
})

export const PrimaryBig = Template.bind({})
PrimaryBig.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/UBD14Sn7Yd4VSWWa64DDWA/Messe---Component-Library?node-id=6%3A251'
  },
  controls: { exclude: ['subHeadline', 'headline'] }
}
PrimaryBig.args = {
  caption: 'Button',
  icon: 'download_pdf',
  headline: 'Button Primary',
  subHeadline: 'Big'
}

export const PrimarySmall = Template.bind({})
PrimarySmall.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/UBD14Sn7Yd4VSWWa64DDWA/Messe---Component-Library?node-id=6%3A251'
  },
  controls: { exclude: ['subHeadline', 'headline'] }
}
PrimarySmall.args = {
  isSmall: true,
  caption: 'Button',
  icon: 'download_pdf',
  headline: 'Button Primary',
  subHeadline: 'Small'
}

export const SecondaryBig = Template.bind({})
SecondaryBig.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/UBD14Sn7Yd4VSWWa64DDWA/Messe---Component-Library?node-id=6%3A251'
  },
  controls: { exclude: ['subHeadline', 'headline'] }
}
SecondaryBig.args = {
  isSecondary: true,
  caption: 'Button',
  icon: 'download_pdf',
  headline: 'Button Secondary',
  subHeadline: 'Big'
}

export const SecondarySmall = Template.bind({})
SecondarySmall.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/UBD14Sn7Yd4VSWWa64DDWA/Messe---Component-Library?node-id=6%3A251'
  },
  controls: { exclude: ['subHeadline', 'headline'] }
}
SecondarySmall.args = {
  isSmall: true,
  isSecondary: true,
  caption: 'Button',
  icon: 'download_pdf',
  headline: 'Button Secondary',
  subHeadline: 'Small'
}

const SingleComponentTemplate = (args) => ({
  components: {
    CtaButton
  },
  setup () {
    return { args }
  },
  template: `
    <cta-button v-bind="args" :icon-name="args.icon">{{ args.caption }}</cta-button>
  `
})
export const SingleComponent = SingleComponentTemplate.bind({})
SingleComponent.parameters = {
  design: {
    type: 'figma',
    url: 'https://www.figma.com/file/UBD14Sn7Yd4VSWWa64DDWA/Messe---Component-Library?node-id=6%3A251'
  },
  controls: { exclude: ['subHeadline', 'headline'] }
}
SingleComponent.args = {
  caption: 'Button',
  url: 'http://www.example.com'
}
