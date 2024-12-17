<template>
  <div v-if="showFavButton" :class="[
    'fav-button',
    { 'fav-button--inverted': isInverted },
    { 'fav-button--active': (isActive || isFavorite) && !(isDisabled || isBusy) },
    { 'fav-button--disabled': isDisabled },
    { 'fav-button--focussed': isFocussed },
    { 'fav-button--hovered': isHovered }
  ]">
    <icon-button v-if="!showText" @click.stop="toggleFavState"
      :icon-name="(isActive || isFavorite) ? 'star' : " :is-small="isSmall" :is-inverted="isInverted"
      :is-small-on-mobile="isSmallOnMobile" :text="text || t('components_fav_add_lbl')"
      :active-text="activeText || t('components_fav_remove_lbl')" :is-active="(isActive || isFavorite)"
      :is-disabled="isDisabled || isBusy" :is-focussed="isFocussed" :is-hovered="isHovered" />
    <cta-button v-else @click.stop="toggleFavState" :icon-name="(isActive || isFavorite) ? 'star' : "
      :is-small="isSmall" :is-inverted="isInverted" :is-small-on-mobile="isSmallOnMobile"
      :text="text || t('components_fav_add_lbl')" :active-text="activeText || t('components_fav_remove_lbl')"
      :is-disabled="isDisabled || isBusy" :is-focussed="isFocussed" :is-hovered="isHovered">{{
        (isActive || isFavorite) ? (activeText || t('components_fav_remove_lbl')) : text || t('components_fav_add_lbl')
      }}
    </cta-button>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, onMounted, ref, toRef, watch } from 'vue'
import IconButton from '@/components/elements/IconButton.vue'
import { useI18n } from 'vue-i18n'
import { getShowFavButton } from '@/composables/vis-modules/myPlanner'
import CtaButton from '@/components/elements/CtaButton.vue'
import { useMyPlanner } from '@/store/myplanner'

export default defineComponent({
  name: 'FavButton',
  components: {
    IconButton,
    CtaButton
  },
  props: {
    /** id of the related item (e.g. 'prodinfo=123456789') */
    itemId: {
      type: String,
      required: true
    },

    /** Set inverted style */
    isInverted: {
      type: Boolean,
      required: false,
      default: false
    },

    /** Manually set active state (for storybook or testing only) */
    isActive: {
      type: Boolean,
      default: false
    },

    /** Button text */
    text: {
      type: String,
      required: false
    },

    /** Text to display when active */
    activeText: {
      type: String,
      required: false
    },

    /** Show text next to icon, otherwise text will only be used in title tag (tooltip) */
    showText: {
      type: Boolean,
      required: false,
      default: false
    },

    /** Set component size 'small' (small on mobile devices by default) */
    isSmall: {
      type: Boolean,
      required: false,
      default: false
    },

    /** Auto-switch to size 'small' in mobile view (default = true) */
    isSmallOnMobile: {
      type: Boolean,
      required: false,
      default: true
    },

    /** Manually set disabled (=busy) state (used for UI preview/testing only) */
    isDisabled: {
      type: Boolean,
      required: false,
      default: false
    },

    /** Set 'focus' state (used for UI preview/testing only) */
    isFocussed: {
      type: Boolean,
      required: false,
      default: false
    },

    /** Set 'hover' state  (used for UI preview/testing only)  */
    isHovered: {
      type: Boolean,
      required: false,
      default: false
    }
  },
  setup (props) {
    const { t } = useI18n()
    const myPlannerStore = useMyPlanner()
    const isBusy = ref(true)
    const itemId = toRef(props, 'itemId')
    const isFavorite = ref(false)
    const showFavButton = computed(() => {
      return getShowFavButton(props.itemId)
    })

    const updateFavState = () => {
      isFavorite.value = myPlannerStore.isFavorite(itemId.value)
    }

    const toggleFavState = () => {
      if (isBusy.value === true) {
        return
      }
      isBusy.value = true
      if (!isFavorite.value) {
        myPlannerStore.addToFavorites(itemId.value).then(() => {
          isBusy.value = false
        })
      } else {
        myPlannerStore.removeFromFavorites(itemId.value).then(() => {
          isBusy.value = false
        })
      }
    }

    // fav state has changed in store
    myPlannerStore.$subscribe(updateFavState)

    // item id was changed or set initially (workaround for timing issue that occurred in some use cases)
    watch(itemId, updateFavState)

    onMounted(() => {
      myPlannerStore.fetchFavoriteList()
        .then(() => {
          isBusy.value = false
        })
    })

    return {
      t,
      isBusy,
      isFavorite,
      toggleFavState,
      showFavButton
    }
  }
})
</script>
<style lang="scss" scoped>
.fav-button {
  display: inline;

  > * {
    --icon-color: var(--finder-color-dark-50);
    --icon-color-hover: var(--finder-color-dark-50);
    --button-color: var(--finder-color-dark-50);
    --button-color-hover: var(--finder-color-dark-50);
    --button-background-color: var(--finder-color-dark-10);
    --button-background-color-hover: var(--finder-color-dark-15);

    &:focus {
      outline: none;
    }
  }

  &--active {
    > * {
      --icon-color: var(--finder-color-light);
      --icon-color-hover: var(--finder-color-light);
      --button-color: var(--finder-color-light);
      --button-color-hover: var(--finder-color-light);
      --button-background-color: var(--finder-color-primary);
      --button-background-color-hover: var(--finder-color-primary-50);
    }
  }

  &--inverted {
    > * {
      --icon-color: var(--finder-color-dark-50);
      --icon-color-hover: var(--finder-color-dark-50);
      --button-color: var(--finder-color-dark-50);
      --button-color-hover: var(--finder-color-dark-50);
      --button-background-color: var(--finder-color-white-75);
      --button-background-color-hover: var(--finder-color-white-90);
    }
  }

  &--inverted.fav-button--active {
    > * {
      --icon-color: var(--finder-color-primary);
      --icon-color-hover: var(--finder-color-primary);
      --button-color: var(--finder-color-primary);
      --button-color-hover: var(--finder-color-primary);
      --button-background-color: var(--finder-color-light);
      --button-background-color-hover: var(--finder-color-white-75);
    }
  }
}
</style>
