import gql from 'graphql-tag'

export const GET_EVENT_DATEFRAMES = gql`
  query getEventDateFrames ($site: [String]!, $section: [String!]) {
    dateFrames: entries (
      site: $site
      section: $section
      type: "default"
      ) {
        ...on event_default_Entry {
        startEventDatetime: timeStart @formatDateTime(timezone: "UTC")
        endEventDatetime: timeEnd @formatDateTime(timezone: "UTC")
        id
        title
      }
      ...on session_default_Entry {
        startEventDatetime: sessionStartTime @formatDateTime(timezone: "UTC")
        endEventDatetime: sessionEndTime @formatDateTime(timezone: "UTC")
        id
        title
      }
    }
  }
`
