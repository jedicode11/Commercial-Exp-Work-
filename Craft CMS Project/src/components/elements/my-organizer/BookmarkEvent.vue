<template>
  <div class="bookmark-btn">
    <icon-button
      :text="isBookmarked ? t('components_bookmark_button_remove_the_event_to_bookmark') : t('components_bookmark_button_add_the_event_to_bookmark')"
      :icon-name="isBookmarked ? SvgIconName.star : SvgIconName.starBorder"
      :class="{ 'bookmark__selected': isBookmarked }"
      isColored
      @click="handleBookmarkClick"
    ></icon-button>
        <!-- <GDialog
      v-if="dialogState"
      v-model="dialogState"
      depressed
      persistent
      scrollable
      width="90%"
      height="90%"
      background="transparent"
      @update:modelValue="handleDialogClose()"
    >
      <iframe :src="addUrl" width="100%" height="100%"></iframe>
      <icon-button
        style="align-self: flex-start;"
        text=""
        :icon-name="SvgIconName.close"
        isColored
        @click="dialogState = false"
      ></icon-button>
    </GDialog> -->
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, inject, onMounted, ref, toRef } from 'vue'
import IconButton from '@/components/elements/IconButton.vue'
import { SvgIconName } from '@/components/helpers/SvgIcon.vue'
import { useI18n } from 'vue-i18n'
import { useSnackbarMessage } from '@/composables/useSnackbarMessage'

export default defineComponent({
  name: 'BookmarkEvent',
  props: {
    id: {
      type: String,
      required: true
    }
  },
  components: {
    IconButton
  },
  setup (props) {
    const { t } = useI18n()
    const organizerBookmarkAddUrl = inject<string>('app.organizerBookmarkAddUrl')
    const organizerBookmarksGetUrl = inject<string>('app.organizerBookmarksGetUrl')
    const organizerBookmarkDeleteUrl = inject<string>('app.organizerBookmarkDeleteUrl')
    const organizerTicket = inject<string>('app.organizerTicket')
    const organizerHeaders = inject<string>('app.organizerHeaders')
    const organizerUrl = inject<string>('app.organizerUrl')

    const id = toRef(props, 'id')
    const bookmarks = ref<string[]>([])
    const dialogState = ref(false)
    let headers: Record<string, string> | undefined

    onMounted(() => {
      getHeaders()
      fetchBookmarked()
    })

    const getHeaders = () => {
      if (organizerHeaders) {
        try { headers = JSON.parse(organizerHeaders) } catch (error) { console.error(error) }
      }
    }

    const getResponseJSON = async (response: Response) => {
      const contentType = response.headers.get('content-type')
      if (contentType && contentType.indexOf('application/json') !== -1) {
        return await response.json()
      } else {
        return undefined
      }
    }

    const fetchBookmarked = () => {
      if (organizerBookmarksGetUrl) {
        let [method, url] = organizerBookmarksGetUrl.split('::')
        if (!url) {
          url = method
          method = 'GET'
        }
        url = url.replace('{ticket}', organizerTicket ?? '')
        fetch(url, { method, headers }).then(async res => {
          const result = await getResponseJSON(res) ?? []
          console.log('🚀 ~ get ~ result:', result)
          bookmarks.value = result
        })
      }
    }

    const isBookmarked = computed((): boolean => {
      return bookmarks.value?.includes?.(id.value)
    })

    const handleBookmarkClick = () => {
      if (!isBookmarked.value && organizerBookmarkAddUrl) {
        if (!organizerTicket) {
          // trigger parent login popup
          // TODO: change this to widget-specific message to avoid dependency on parent
          document.dispatchEvent(new CustomEvent('metabar.toggleLoginModal', { detail: { show: true } }))
        } else {
          // POST::https://my.organizer.domain/add/{id}?ticket={ticket}
          let [method, url] = organizerBookmarkAddUrl.split('::')
          if (!url) {
            url = method
            method = 'GET'
          }
          url = url.replace('{id}', id.value).replace('{ticket}', organizerTicket ?? '')
          fetch(url, { method, headers }).then(async res => {
            // const result = await getResponseJSON(res)
            const result = await res.status === 200
            console.log('🚀 ~ add ~ status code:', res.status)
            if (result) {
              showMessage(t('components_modal_bookmark_text_add_event_to_bookmark'), 'GO', organizerUrl)
              bookmarks.value = (bookmarks.value ?? []).concat(id.value)
            } else {
              showMessage(t('components_modal_bookmark_text_error'), 'close')
            }
          })
        }
        // dialogState.value = true
      } else if (isBookmarked.value && organizerBookmarkDeleteUrl) {
        // DELETE::https://my.organizer.domain/delete/{id}?ticket={ticket}
        console.log('organizerBookmarkDeleteUrl', organizerBookmarkDeleteUrl)
        let [method, url] = organizerBookmarkDeleteUrl.split('::')
        if (!url) {
          url = method
          method = 'GET'
        }
        url = url.replace('{id}', id.value).replace('{ticket}', organizerTicket ?? '')
        fetch(url, { method: 'GET', headers }).then(async res => {
          const result = await res.status === 200
          console.log('🚀 ~ delete ~ status code:', res.status)
          if (result) {
            showMessage(t('components_modal_bookmark_text_remove_event_to_bookmark'), 'close')
            bookmarks.value = bookmarks.value?.filter?.(bookmark => bookmark !== id.value) ?? []
          } else {
            showMessage(t('components_modal_bookmark_text_error'), 'close')
          }
        })
      }
    }

    const handleDialogClose = () => {
      fetchBookmarked()
    }

    const showMessage = (text: string, actionText: string, linkUrl?: string) => {
      if (linkUrl) {
        useSnackbarMessage({
          text: text || 'Message with <strong>Markup</strong>',
          actionText: actionText,
          linkUrl: linkUrl
          // linkTarget: args.linkTarget,
          // duration: args.duration,
          // onAction: () => console.log('Snackbar Message: Action clicked'),
          // onTimeout: () => console.log('Snackbar Message: Message timeout')
        }).show()
      } else {
        useSnackbarMessage({
          text: text || 'Message with <strong>Markup</strong>',
          actionText: actionText
          // linkUrl: linkUrl || null,
          // linkTarget: args.linkTarget,
          // duration: args.duration,
          // onAction: () => console.log('Snackbar Message: Action clicked'),
          // onTimeout: () => console.log('Snackbar Message: Message timeout')
        }).show()
      }
    }

    return {
      t,
      organizerBookmarkAddUrl,
      SvgIconName,
      isBookmarked,
      handleBookmarkClick,
      dialogState,
      handleDialogClose
    }
  }
})
</script>

<style lang="scss" scoped>
.bookmark-btn {
  background: rgba(255, 255, 255, 0.75);
  border-radius: 4px;
  margin-right: 8px;

  .bookmark__selected {
    --button-background-color: var(--finder-color-primary);
  }
}
</style>
