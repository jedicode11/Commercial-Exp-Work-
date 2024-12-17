import gql from 'graphql-tag'

export const GET_TODAY_EVENTS = gql`
query getTodayEvents ($site: [String]!, $timeStart: [QueryArgument]!, $timeEnd: [QueryArgument]!) {
    events: entries(site: $site, section: "event", type: "default", timeStart: $timeStart, timeEnd: $timeEnd) {
      ...on event_default_Entry {
        id
        uri
        title
        startEventDatetime: timeStart
        endEventDatetime: timeEnd
        eventCategories {
          title
        }
        productCategory {
          title
        }
        locationHallPlan: location {
          ...on location_default_Entry{
            locationNumber
            title
          }
        }
        eventLocation
        eventLanguage {
          ...on eventLanguage_default_Entry {
            title
            abbreviated: abbreviatedLanguage
          }
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
        eventPlace
        eventChargeable
        featureImage {
          url @transform(handle: "speakerImage")
          alt
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
        siteId
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
  }
`
