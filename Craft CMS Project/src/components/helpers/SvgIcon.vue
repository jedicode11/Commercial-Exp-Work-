<template>
  <svg :class="className" xmlns="http://www.w3.org/2000/svg">
    <use :xlink:href="'#' + iconName" />
  </svg>
</template>

<script lang="ts">
import { computed, defineComponent, PropType, toRef } from 'vue'

export enum SvgIconName {
  add = 'add',
  arrowBack = 'arrow_back',
  arrowDropDown = 'arrow-drop-down',
  arrowDropUp = 'arrow-drop-up',
  arrowForward = 'arrow_forward',
  calendar = 'calendar',
  checkbox = 'checkbox',
  checkboxIndeterminate = 'checkbox_indeterminate',
  checkboxSelected = 'checkbox_selected',
  clock = 'clock',
  close = 'close',
  cog = 'cog',
  downloadPdf = 'download_pdf',
  filterDown = 'filter_down',
  edit = 'edit',
  eyeSlash = 'eye_slash',
  fingerprint = 'fingerprint',
  gridOn = 'grid_on',
  info = 'info',
  infoFilled = 'info_filled',
  insertLink = 'insert_link',
  keyboardArrowDown = 'keyboard_arrow_down',
  language = 'language',
  languageTrans = 'language_trans',
  list = 'list',
  loading = 'loading',
  locationOn = 'location_on',
  moreHoriz = 'more_horiz',
  onDemandVideo = 'ondemand_video',
  personPinCircle = 'person_pin_circle',
  playArrow = 'play_arrow',
  premium = 'premium',
  removeRedEye = 'remove_red_eye',
  share = 'share',
  slowMotionVideo = 'slow_motion_video',
  socialFacebook = 'facebook',
  socialInstagram = 'instagram',
  socialLinkedin = 'linkedin',
  socialOkRu = 'social/ok_ru',
  socialQzone = 'social/qzone',
  socialRenren = 'social/renren',
  socialSnapchat = 'social/snapchat',
  socialTudou = 'social/tudou',
  socialTwitter = 'social/twitter',
  socialVkontakte = 'social/vkontakte',
  socialWechat = 'social/wechat',
  socialWeibo = 'social/weibo',
  socialXing = 'xing',
  socialYouku = 'social/youku',
  socialYoutube = 'social/youtube',
  star = 'star',
  starBorder = 'star_border',
  sync = 'sync',
  restart = 'restart',
  warning = 'warning',
  search = 'search',
  arrowCircleLeft = 'arrow-circle-left',
  enter = 'enter',
  headphones = 'headphones',
  euroCurrency = 'euro-currency',
  checkCircle = 'check-circle',
  users = 'users',
  chevronDown = 'chevron_down',
  chevronDownSolid = 'chevron_down_solid',
  chevronExpand = 'chevron_expand',
  check = 'check',
  warningRounded = 'warningRounded',
  webSession = 'websession'
}

export default defineComponent({
  name: 'svg-icon',
  props: {
    iconName: {
      type: String as PropType<SvgIconName>,
      required: true,
      validator: (value: string) => {
        return (Object.values(SvgIconName) as Array<string>).includes(value)
      }
    },

    /** Set component size 'small' */
    isSmall: {
      type: Boolean,
      required: false,
      default: false
    },

    title: {
      type: String,
      default: null
    }
  },
  setup (props) {
    const iconName = toRef(props, 'iconName')
    const isSmall = toRef(props, 'isSmall')

    const className = computed(() => {
      return `svg-icon ${isSmall.value ? 'svg-icon--small' : ''} svg-icon--` + iconName.value.replace('/', '-')
    })

    return {
      className
    }
  }
})
</script>

<style lang="scss" scoped>
.svg-icon {
  --svg-icon-size: 24px;
  fill: currentColor;
  height: var(--svg-icon-size);
  width: var(--svg-icon-size);

  &--small {
    --svg-icon-size: 16px;
  }
}

svg {
  color: inherit;
  fill: currentColor;
}
</style>
