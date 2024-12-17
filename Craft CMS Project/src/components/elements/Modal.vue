<template>
  <Transition name="modal">
    <div v-if="show" class="modal-mask">
      <div class="modal-container">
        <div class="modal-header">
          <slot name="header">{{ header }}</slot>
        </div>

        <div class="modal-content">
          <slot name="content">{{ content }}</slot>
        </div>

        <div class="modal-footer">
          <slot name="footer" v-if="footer">
            {{ footer }}
          </slot>
        </div>
        <div class="modal-actions">
          <slot name="actions">
            <button class="modal-default-button" @click="$emit('close')">{{ button }}</button>
          </slot>
        </div>
      </div>
    </div>
  </Transition>
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'Modal',
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
    },
    footer: {
      type: String,
      required: false,
      default: ''
    },
    button: {
      type: String,
      required: false,
      default: 'Close'
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
  width: clip(300px 30vw 50vw);
  margin: auto;
  padding: 20px 30px;
  background-color: #fff;
  border-radius: 2px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.33);
  transition: all 0.3s ease;
  transform: scale(1.1);
}

.modal-header {
  padding: 0;
}

.modal-content {
  margin: 20px 0;
  padding: 10px;
  max-height: 50vh;
  overflow-y: scroll;
}

.modal-footer {
  padding: 0;
}

.modal-actions {
  margin-top: 20px;
  display: flex;
  justify-content: end;
}

.modal-default-button {
  padding: 4px 8px;
  line-height: 1rem;
  border-radius: 4px;
}
</style>
