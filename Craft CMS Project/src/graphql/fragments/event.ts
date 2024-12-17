import gql from 'graphql-tag'

export const eventDefaultFragment = {
  event: gql`
    fragment EventDefault on event_default_Entry {
      id
      uri
      siteId
      sectionHandle
      title
      startEventDatetime: timeStart @formatDateTime(timezone: "UTC")
      endEventDatetime: timeEnd @formatDateTime(timezone: "UTC")
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
        id
        title
      }
      eventPlace
      featureImage {
        url @transform(handle: "eventFeatureImages")
        alt
      }
      speakers: speaker {
        ... on person_default_Entry {
          id
          speakerName: title
        }
      }
    }
  `
}
