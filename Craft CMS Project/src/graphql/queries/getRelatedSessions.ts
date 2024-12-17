import gql from 'graphql-tag'

export const GET_RELATED_SESSIONS = gql`
  query getRelatedSessions ($site: [String]! $uri: [String]) {
    relatedSession: entries(site: $site, section: "session", type: "default", relatedToEntries: {site: $site uri: $uri}) {
      ...on session_default_Entry {
        id
        eventsId: sessionEvents {
          ...on event_default_Entry {
            id
          }
        }
        sessionLocationHallPlan {
          ...on location_default_Entry {
            id
            title
            locationHallPlan: locationNumber
          } 
        }
      }
    }
  }
`
