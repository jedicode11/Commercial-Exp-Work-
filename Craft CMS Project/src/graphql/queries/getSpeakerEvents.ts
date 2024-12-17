import gql from 'graphql-tag'

export const GET_SPEAKER_EVENTS = gql`
query getSpeakerEvents ($site: [String]!, $speackerId: [QueryArgument], $startDateFilter: [QueryArgument]) {
    events: entries(site: $site, section: "event", type: "default", speaker: $speackerId, timeStart: $startDateFilter) {
      ...on event_default_Entry {
        id
        uri
        title
        siteId
        startEventDatetime: timeStart
        endEventDatetime: timeEnd
        eventCategories {
          title
        }
        productCategory {
          title
        }
        eventLocation
        locationHallPlan: location {
          ...on location_default_Entry{
            locationNumber
            title
          }
        }
        eventLanguage {
          ...on eventLanguage_default_Entry {
          title
          abbreviated: abbreviatedLanguage
          }
        }
        eventPlace
        eventChargeable
        # eventSummary
        featureImage {
          url @transform(handle: "sliderImage")
        }
        forum: eventForum {
          ...on forum_default_Entry {
            id
            title
            locationHallPlan: forumLocation {
              ...on location_default_Entry {
                id
                title
                locationNumber
              } 
            }
          }
        }
        exhibitor {
          ... on exhibitor_default_Entry {
            id
            exhibitorTitle: title
            exhibitorAddress
          }
        }
        speakers: speaker {
          ... on person_default_Entry {
            id
            speakerName: title
          }
        }
        eventContent
      }
    }
    globalSets(site: $site, handle: "siteLocation") {
      ... on siteLocation_GlobalSet {
        siteId
        locationHallPlan: globalLocation {
          ... on location_default_Entry {
            locationNumber
            title
          }
        }
      }
    }
    relatedSessions: entries(
      site: $site,
      section: "session", 
      relatedToEntries: [{site: $site, section: "event", type: "default", speaker: $speackerId, timeStart: $startDateFilter}]) {
      ... on session_default_Entry {
        id
        eventsIds: sessionEvents {
          ...on event_default_Entry {
            id
          }
        }
        sessionLocationHallPlan {
          ... on location_default_Entry {
            id
            title
            locationHallPlan: locationNumber
          }
        }
      }
    }
  }
`
