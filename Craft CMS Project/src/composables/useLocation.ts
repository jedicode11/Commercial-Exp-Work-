import { computed, inject, Ref } from 'vue'
import { LocationModel } from '@/models/LocationModel'
import { SingleEventModel } from '@/models/SingleEventModel'
import { SessionModel } from '@/models/Session'
import { EventModel } from '@/models/EventModel'
import { ResponseTransformerModel } from '@/models/responseTransformerModel'
import { globalLocationStore } from '@/store/location'
import { storeToRefs } from 'pinia'

export function useLocation (event: Ref<SingleEventModel | SessionModel | EventModel | null | undefined>, relatedSession: any) {
  const widgetMode = inject<string | undefined>('app.widgetMode')
  const locationUrl = inject<string | undefined>('app.locationHallPlanUrl')
  const forumBasedOnEntry = inject<string | undefined>('app.forumBasedOnEntry')

  const { globalLocations } = storeToRefs(globalLocationStore())

  const goToLocationLink = (): void => {
    const [hall, booth] = getLocationHallPlan.value

    if (hall && booth && locationUrl && widgetMode?.toLowerCase() !== 'stele') {
      const linkUrl = `${locationUrl}?action=showBooth&hall=${hall.locationNumber}&booth=${booth.locationNumber}`
      window.open(linkUrl, '_blank')
    } else if (hall && locationUrl && widgetMode?.toLowerCase() !== 'stele') {
      const linkUrl = `${locationUrl}?action=showBooth&hall=${hall.locationNumber}`
      window.open(linkUrl, '_blank')
    }
  }

  const getLocationTitle = computed((): string => {
    const [hall, booth] = getLocationHallPlan.value
    if (hall && booth) {
      return `${hall.title} ${booth.title}`
    } else if (hall) {
      return hall.title
    }
    return ''
  })

  const getLocationHallPlan = computed((): LocationModel[] => {
    const rs = relatedSession.value
    const forum = event.value?.forum.length ? event.value?.forum[0] : undefined
    const sgl = globalLocations.value.find(gl => gl.siteId === event.value?.siteId)

    if (event.value?.locationHallPlan.length) {
        return event.value?.locationHallPlan
    } else if (rs?.sessionLocationHallPlan?.length) {
        return rs?.sessionLocationHallPlan
    } else if (forum?.locationHallPlan?.length && forumBasedOnEntry === "true") {
        return forum.locationHallPlan
    } else if (sgl?.locationHallPlan?.length && forumBasedOnEntry !== "true") {
        return sgl.locationHallPlan
    } else {
        return []
    }
  })

  return {
    goToLocationLink,
    getLocationTitle,
      getLocationHallPlan
  }
}

export const responseTransformer = (res: ResponseTransformerModel): any => {
  return res.events.map((e: EventModel) => { return {...e, siteGlobalLocation: res.globalSets.find((gs: any) => gs.siteId === e.siteId )} })
}
