import gql from 'graphql-tag'

export const GET_EVENTS_BY_EXHIBITOR = gql`
query getEventsByExhibitor ($site: [String]!, $exhibitorId: [QueryArgument], $startDateFilter: [QueryArgument]) {
  events: entries(site: $site, section: "event", type: "default", relatedTo: $exhibitorId, timeEnd: $startDateFilter) {
    ...on event_default_Entry {
      id
      uri
      title
      siteId
      startEventDatetime: timeStart
      endEventDatetime: timeEnd
      exhibitor {
        title
      }
      locationHallPlan: location {
        ...on location_default_Entry{
          locationNumber
          title
        }
      }
      eventCategories {
        title
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
      eventLanguage {
        ...on eventLanguage_default_Entry {
          title
          abbreviated: abbreviatedLanguage
        }
      }
      featureImage {
        url @transform(handle: "sliderImage")
        alt
      }
      eventPlace
      eventChargeable
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
