import { dispatchFinderEvent, FinderEvents } from '@/utils/events'
import { onBeforeUnmount, onMounted } from 'vue'

export enum MyAdminEvents {
  triggerEditor = 'myadmin.triggerEditor',
  editorClose = 'myadmin.editorClose',
  videoThumbChanged = 'myadmin.videoThumbChanged'
}

export enum EditorIds {
  contactsItem = 'contacts.item',
  profileContactsOrder = 'profileContacts.order',
  standContactsOrder = 'standContacts.order',
  productsItem = 'products.item',
  productsOrder = 'products.order',
  boothEventsItem = 'boothEvents.item',
  newsItem = 'news.item',
  videoConferencesItem = 'videoConferences.item',
  profileSynonyms = 'profile.synonyms',
  profileXTags = 'profile.xtags',
  profileTags = 'profile.tags',
  profileExhAddress = 'profile.exhAddress',
  profileExhContact = 'profile.exhContact',
  profileGallery = 'profile.gallery',
  profileSocial = 'profile.social',
  profileGetInTouch = 'profile.getInTouch',
  profileText = 'profile.text',
  profileDownloads = 'profile.downloads',
  profileBusinessData = 'profile.businessData',
  exhCategories = 'exhCategories'
}

interface EditorCloseEventDetails {
  editorId: string,
  itemId: string,
  changeHistory: Array<Record<string, unknown>>,
  detail?: Record<string, unknown>
}

interface EditorCloseEvent {
  type: string,
  detail?: EditorCloseEventDetails
}

interface VideoThumbChangeDetails {
  videoId: string,
}

interface VideoThumbChangeEvent {
  type: string,
  detail?: VideoThumbChangeDetails
}

export const triggerEditor = (editorId: EditorIds, itemId?: string): void => {
  const editorCloseHandler = (closeEvent: EditorCloseEvent) => {
    const detail = closeEvent.detail
    const editorId = detail?.editorId || ''
    window.document.removeEventListener(MyAdminEvents.editorClose, editorCloseHandler)
    if (detail && detail.changeHistory.length) {
      dispatchFinderEvent(FinderEvents.dataEditComplete, { detail: editorId })
    }
  }
  window.document.addEventListener(MyAdminEvents.editorClose, editorCloseHandler)

  window.document.dispatchEvent(
    new CustomEvent(MyAdminEvents.triggerEditor, {
      detail: {
        editorId: editorId,
        itemId
      }
    })
  )
}

export const useVideoThumbChangeListener = (callbackFn: (videoId: string) => void): void => {
  const videoThumbChangeHandler = (videoThumbChangeEvent: VideoThumbChangeEvent) => {
    const videoId = videoThumbChangeEvent.detail?.videoId || ''
    callbackFn(videoId)
  }
  onMounted(() => {
    if (videoThumbChangeHandler) {
      window.document.addEventListener(MyAdminEvents.videoThumbChanged, videoThumbChangeHandler)
    }
  })
  onBeforeUnmount(() => {
    if (videoThumbChangeHandler) {
      window.document.removeEventListener(MyAdminEvents.videoThumbChanged, videoThumbChangeHandler)
    }
  })
}
