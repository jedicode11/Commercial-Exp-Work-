import gql from 'graphql-tag'

export const GET_SIMILAR_EVENTS_BY_TAG = gql`
query getSimilarEventsByTag ($site: [String]!, $categoryIds: [CategoryCriteriaInput], $startDateFilter: [QueryArgument]) {
  events: entries(site: $site, section: "event", type: "default", relatedToCategories: $categoryIds, timeEnd: $startDateFilter) {
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
      eventLocation
      locationHallPlan: location {
        ...on location_default_Entry{
          locationNumber
          title
        }
      }
      eventLanguage {
        title
        abbreviatedLanguage
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
      # eventSummary
      featureImage {
        url @transform(handle: "sliderImage")
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
          # speakerPosition: eventCategories {
          #   title
          # }
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
}
`
