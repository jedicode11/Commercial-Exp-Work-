<template>
	<div class="sub-btn">
    <icon-button
      :icon-name="isSubscribed ? SvgIconName.arrowDropUp : SvgIconName.arrowDropDown"
      :text="t('components_sub_websession')"
      :class="{ 'sub__selected': isSubscribed }"
      isColored
      @click="handleSubscribeClick">
    </icon-button>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, inject, onMounted, ref, toRef } from 'vue'
import IconButton from '@/components/elements/IconButton.vue'
import { SvgIconName } from '@/components/helpers/SvgIcon.vue'
import { useI18n } from 'vue-i18n'
import { useSnackbarMessage } from '@/composables/useSnackbarMessage'
import { ScriptParamsService, sps } from '@/utils/scriptParamService'

export default defineComponent({
  name: 'SubscribeToWebSession',
  props: {
    id: {
      type: String,
      required: true
    },
    fairId: {
      type: String
    }
  },
  components: {
    IconButton
  },
  setup (props) {
    const scriptParamsService = inject<ScriptParamsService>(sps)
    const organizerTicket = inject<string>('app.organizerTicket')
    const organizerHeaders = inject<string>('app.organizerHeaders')
    const organizerUrl = inject<string>('app.organizerUrl')

    const { t } = useI18n()
    const id = toRef(props, 'id')
    const fairId = toRef(props, 'fairId')
    const subscribe = ref<string[]>([])
    let headers: Record<string, string> | undefined

    onMounted(() => {
      getHeaders()
      fetchSubscribed()
    })

    const isSubscribed = computed((): boolean => {
      return subscribe.value?.includes?.(id.value)
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

    const fetchSubscribed = () => {
      if (scriptParamsService?.websessionGetUrl) {
        let [method, url] = scriptParamsService.websessionGetUrl.split('::')
        if (!url) {
          url = method
          method = 'GET'
        }
        url = url.replace('{ticket}', organizerTicket ?? '').replace('FAIRID', fairId.value ?? '')
        fetch(url, { method, headers }).then(async res => {
          const result = await getResponseJSON(res) ?? []
          console.log('🚀 ~ get ~ result:', result)
          subscribe.value = result
        })
      }
    }

    const handleSubscribeClick = () => {
      if (!isSubscribed.value && scriptParamsService?.websessionAddUrl) {
        if (!organizerTicket) {
          // trigger parent login popup
          // TODO: change this to widget-specific message to avoid dependency on parent
          document.dispatchEvent(new CustomEvent('metabar.toggleLoginModal', { detail: { show: true } }))
        } else {
          // POST::https://my.organizer.domain/add/{id}?ticket={ticket}
          let [method, url] = scriptParamsService.websessionAddUrl.split('::')
          if (!url) {
            url = method
            method = 'GET'
          }
          url = url.replace('WEBSESSIONID', id.value).replace('{ticket}', organizerTicket ?? '').replace('FAIRID', fairId.value ?? '')
          fetch(url, { method, headers }).then(async res => {
            // const result = await getResponseJSON(res)
            const result = await res.status === 200
            console.log('🚀 ~ add ~ status code:', res.status)
            if (result) {
              showMessage(t('components_subscribe_websession_toast'), 'GO', organizerUrl)
              subscribe.value = (subscribe.value ?? []).concat(id.value)
            } else {
              showMessage(t('components_modal_bookmark_text_error'), 'close')
            }
          })
        }
      }
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
      SvgIconName,
      isSubscribed,
      handleSubscribeClick
    }
  }
})
</script>

<style lang="scss" scoped>
.sub-btn {
  background: rgba(255, 255, 255, 0.75);
  border-radius: 4px;
  margin-right: 8px;

  .sub__selected {
    --button-background-color: var(--finder-color-primary);
  }
}
</style>
