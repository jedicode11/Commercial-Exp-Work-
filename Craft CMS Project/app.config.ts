import { 
  StartPage,
  PageSegment
} from '@/enums/script-params'

export const config = {
  useAPI: process.env.VUE_APP_USE_API || false,
  baseUrl: process.env.BASE_URL || './',
  sectionHandle: {
    event: process.env.VUE_APP_EVENT_HANDLE || 'event',
    session: process.env.VUE_APP_SESSION_HANDLE || 'session'
  },
  scriptParams: {
    allowedDataStartPageValues: [
      StartPage.SEARCH_VIEW, 
      StartPage.EVENTS_VIEW,
      StartPage.EVENT_DETAIL,
      StartPage.SESSION_DETAIL,
      StartPage.SPEAKERS_LIST,
      StartPage.SPEAKER_DETAIL
    ],
    allowedDataPageSegmentValues: [
      PageSegment.FILTERED_EVENTS,
      PageSegment.EVENTS_LIST_MOST_BOOKED,
      PageSegment.EVENTS_LIST_FOR_TODAY
    ]
  }
}
