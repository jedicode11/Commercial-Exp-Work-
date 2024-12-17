<template>
  <div class="badge" :style="{ color }">
    <svg-icon v-if="icon" :icon-name="icon" :type="solid ? 'solid' : 'line'" :color="color" />
    {{ text }}
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue'
import SvgIcon, { SvgIconName } from '@/components/helpers/SvgIcon.vue'

export default defineComponent({
  name: 'BadgeComponent',
  components: {
    SvgIcon
  },
  props: {
    color: String,
    icon: {
      type: String as PropType<SvgIconName>
    },
    solid: Boolean,
    text: String
  }
})
</script>

<style lang="scss" scoped>
.badge {
  font-weight: bold;
  font-size: smaller;
  padding: 2px 6px;

  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
