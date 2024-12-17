<template>
  <div :class="{ 'chip': true, 'shadow': hasShadow }" :style="{ backgroundColor: color }">
    <svg-icon v-if="icon" :icon-name="icon" :size="16" color="inherit" />
    {{ text }}
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue'
import SvgIcon, { SvgIconName } from '@/components/helpers/SvgIcon.vue'

export default defineComponent({
  name: 'ChipComponent',
  components: {
    SvgIcon
  },
  props: {
    color: String,
    icon: {
      type: String as PropType<SvgIconName>
    },
    text: String,
    hasShadow: {
      type: Boolean,
      required: false,
      default: true
    }
  },
  data () {
    return {
      SvgIconName
    }
  }
})
</script>

<style lang="scss" scoped>
.chip {
  font-family: 'Source Sans Pro', sans-serif;
  font-size: 14px;
  font-weight: 700;
  line-height: 24px;
  border-radius: 4px;
  flex-wrap: wrap;
  background-color: #fff;
  color: #18181880;
  padding: 4px;
  display: flex;
  align-items: center;
  gap: 4px;

  &.shadow {
    box-shadow: rgba(0, 0, 0, 0.16) 0px 1px 4px;
  }
}
</style>
