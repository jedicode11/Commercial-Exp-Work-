import { useStore } from '@/store'
import { hasRights } from '@/composables/hasRights'
import { Profile } from '@/models/Profile'
import { contentIsEmpty } from '@/utils/helpers'
import { computed, Ref } from 'vue'

interface ProfileState {
  hasAddress: Ref<boolean | null>
  hasBusinessData: Ref<boolean | null>
  hasContact: Ref<boolean | null>
  hasDownloads: Ref<boolean | null>
  hasSocials: Ref<boolean | null>
}

export function getProfileState (profile: Ref<Profile | null>): ProfileState {
  const store = useStore()
  const isEditMode = store.$state.editMode
  const isKioskMode = store.$state.kioskMode

  const hasAddress = computed(() => {
    return profile.value && (!contentIsEmpty(profile.value.profileAddress) ||
      (isEditMode && hasRights('profile.address')))
  })
  const hasBusinessData = computed(() => {
    return profile.value && ((!!profile.value.email || !contentIsEmpty(profile.value.phone) || profile.value.links.length > 0) ||
      (isEditMode && hasRights('profile.contactData')))
  })
  const hasContact = computed(() => {
    return profile.value && (Object.values(profile.value.businessData).length > 0 ||
      (isEditMode && hasRights('profile.businessData')))
  })
  const hasDownloads = computed(() => {
    return !isKioskMode && profile.value && (profile.value.pdfs.length > 0 ||
      (isEditMode && hasRights('profile.pdfs')))
  })
  const hasSocials = computed(() => {
    return profile.value && (profile.value.socialMedia.length > 0 ||
      (isEditMode && hasRights('profile.socialMedia')))
  })

  return {
    hasAddress,
    hasBusinessData,
    hasContact,
    hasDownloads,
    hasSocials
  }
}
