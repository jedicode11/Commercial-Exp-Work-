<template>
  <Transition name="modal">
    <div v-if="show" class="modal-mask">
      <div class="modal-container">
        <div class="modal-header">
          <div class="header-content">
            <slot name="header">{{ header }}</slot>
          </div>
          <div class="header-close-btn">
            <icon-button title="close" :icon-name="SvgIconName.close" @click="$emit('close')"></icon-button>
          </div>
        </div>

        <div class="modal-content">
          <slot name="content">{{ content }}</slot>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import IconButton from '@/components/elements/IconButton.vue'
import { SvgIconName } from '@/components/helpers/SvgIcon.vue'

export default defineComponent({
  name: 'ModalBookmark',
  props: {
    show: Boolean,
    header: {
      type: String,
      required: false,
      default: ''
    },
    content: {
      type: String,
      required: false,
      default: ''
    }
  },
  components: {
    IconButton
  },
  setup() {
    return {
      SvgIconName
    }
  }
})
</script>

<style lang="scss" scoped>
.modal-mask {
  position: fixed;
  z-index: 9998;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  transition: opacity 0.3s ease;
}

.modal-container {
  min-width: 300px;
  width: 80%;
  max-height: 70vh;
  min-height: 315px;
  margin: auto;
  background-color: #fff;
  color: #666666;
  border-radius: 10px;
  box-shadow: 0 10px 30px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  transform: scale(1.1);
}

.modal-header {
  color: #000000;
  height: 70px;
  padding: 0;
  box-shadow: 0 5px 10px 0 rgba(216, 216, 216, 0.4);
}

.header-content {
  margin: 1.5rem 4rem;
}

.header-close-btn {
  width: 2rem;
  height: 2rem;
  position: relative;
  right: 0;
  top: 0;
  -ms-flex-pack: center;
  justify-content: center;
  border: transparent;
  border-radius: 50%;
  margin: 1rem 1.5rem 1.5rem 0;
  outline: none;
  transition: 0.2s;
}

.modal-content {
  width: 80%;
  margin: 2rem 4rem;
  padding: 10px;
  max-height: 50vh;
  border: none;
}
</style>
