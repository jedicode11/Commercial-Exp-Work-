<template>
  <teleport to="body">
    <div :class="[
      'snackbar',
      {'snackbar--edit-mode':isEditMode},
      {'snackbar--edit-mode-allowed':editModeAllowed}
    ]">
      <transition-group name="fade">
        <div class="snackbar__item" v-for="(message, messageId) in messages" :key="messageId">
          <snackbar-message :text="message.text"
                            :action-text="message.actionText"
                            :action-link-url="message.linkUrl"
                            :action-link-target="message.linkTarget"
                            @actionClick="onActionClick(messageId)"
          />
        </div>
      </transition-group>
    </div>
  </teleport>
</template>

<script lang="ts">
import { defineComponent, Ref, ref } from 'vue'
import SnackbarMessage from '@/components/elements/SnackbarMessage.vue'
import SnackbarMessageOptions from '@/models/SnackbarMessageOptions'

export interface SnackbarGlobalComponent {
  showMessage: (message: SnackbarMessageOptions) => string
  hideMessage: (messageId: string) => void
}

export default defineComponent({
  name: 'Snackbar',
  components: { SnackbarMessage },
  setup: (props, { expose }) => {
    let messageIdCounter = 1
    const defaultDuration = 5000
    const messages: Ref<Record<string, SnackbarMessageOptions>> = ref({})
    const closeTimeoutHandles: Record<string, number> = {}

    const isEditMode = true
    const editModeAllowed = true

    const onActionClick = (messageId: string) => {
      if (Object.keys(messages.value).includes(messageId)) {
        const message = messages.value[messageId]
        if (message.onAction) {
          message.onAction()
        }
      }
      hideMessage(messageId)
    }

    const onTimeout = (messageId: string) => {
      if (Object.keys(messages.value).includes(messageId)) {
        const message = messages.value[messageId]
        if (message.onTimeout) {
          message.onTimeout()
        }
      }
      hideMessage(messageId)
    }

    const hideAllMessages = () => {
      for (const messageId in messages.value) {
        onTimeout(messageId)
      }
    }

    const showMessage = (message: SnackbarMessageOptions): string => {
      const messageId = `message${messageIdCounter++}`
      hideAllMessages()
      messages.value[messageId] = message
      closeTimeoutHandles[messageId] = window.setTimeout(() => onTimeout(messageId), message.duration || defaultDuration)
      return messageId
    }

    const hideMessage = (messageId: string): void => {
      if (closeTimeoutHandles[messageId]) {
        clearTimeout(closeTimeoutHandles[messageId])
        delete closeTimeoutHandles[messageId]
      }
      delete messages.value[messageId]
    }

    expose({ showMessage, hideMessage } as SnackbarGlobalComponent)

    return {
      messages,
      isEditMode,
      editModeAllowed,
      onActionClick
    }
  }
})
</script>

<style lang="scss" scoped>
.snackbar {
  --snackbar-outer-padding: 10px;
  --snackbar-pos-bottom: 30px;
  width: 100%;
  pointer-events: none;

  @media print {
    display: none;
  }

  &--edit-mode,
  &--edit-mode-allowed {
    --snackbar-pos-bottom: calc(var(--finder-myadmin-meta-bar-mobile-trigger-bottom) + 60px);
    @media (min-width: mq(md, false)) {
      --snackbar-pos-bottom: 30px;
    }
  }

  .fade-enter-active {
    animation: snackbar-enter .3s ease;
  }

  .fade-leave-active {
    animation: snackbar-leave .2s ease;
  }

  &__item {
    position: fixed;
    width: calc(100% - 2 * var(--snackbar-outer-padding));
    bottom: var(--snackbar-pos-bottom);
    z-index: z('snackbar');
    display: flex;
    justify-content: center;
    padding: 0 var(--snackbar-outer-padding);
  }

  @keyframes snackbar-enter {
    0% {
      opacity: 0;
      transform: scale(0.9, 0.9);
    }
    50% {
      opacity: 1;
      transform: scale(1.03, 1.03);
    }
    100% {
      opacity: 1;
      transform: scale(1, 1);
    }
  }

  @keyframes snackbar-leave {
    0% {
      opacity: 1;
      transform: scale(1, 1);
    }
    50% {
      opacity: 0.5;
      transform: scale(1.01, 1.01);
    }
    100% {
      opacity: 0;
      transform: scale(0.8, 0.8);
    }
  }
}
</style>
