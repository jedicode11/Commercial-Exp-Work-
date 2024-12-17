<template>
  <div class="snackbar-message">
    <div class="snackbar-message__text" v-html="text"/>
    <a class="snackbar-message__action" v-if="actionText"
       @click="onActionClick"
       :href="actionLinkUrl || null"
       :target="(actionLinkUrl && actionLinkTarget) ? actionLinkTarget : null"
    >{{ actionText }}</a>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'SnackbarMessage',
  emits: ['actionClick'],
  props: {
    /** message text with markup (as v-html is used for output) */
    text: {
      types: String,
      required: true
    },
    /** link text for the action if available */
    actionText: {
      types: String,
      required: false
    },
    /** link url (if desired action is navigating to an url) */
    actionLinkUrl: {
      types: String,
      required: false
    },
    /** link target (optional)  */
    actionLinkTarget: {
      types: String,
      required: false
    }
  },
  setup: (props, { emit }) => {
    const onActionClick = () => {
      emit('actionClick')
    }
    return {
      onActionClick
    }
  }
})
</script>

<style lang="scss" scoped>
.snackbar-message {
  display: inline-flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 6px;
  padding: 10px 27px;
  color: var(--finder-color-white);
  background-color: var(--finder-color-dark-90);
  border-radius: $border-radius;

  &__text {
    @include copy-text-regular-16; // using 'copy-text' instead of 'button-text' (figma) for same line height as link
    color: var(--finder-color-light);

    & > b,
    & > strong {
      @include copy-text-bold-16;
    }
  }

  &__action {
    pointer-events: auto;
    @include copy-text-regular-16;
    @include text-link-states-snackbar;
  }
}
</style>
