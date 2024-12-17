import gql from 'graphql-tag'

export const sessionDefaultFragment = {
  session: gql`
    fragment SessionDefault on session_default_Entry {
      id
      uri
      siteId
      sectionHandle
      title
      startEventDatetime: sessionStartTime @formatDateTime(timezone: "UTC")
      endEventDatetime: sessionEndTime @formatDateTime(timezone: "UTC")
      eventCategories: eventCategory {
        title
      }
      productCategory: sessionProductCategory {
        title
      }
      eventLocation: sessionLocation
      locationHallPlan: sessionLocationHallPlan {
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
      eventLanguage: sessionLanguage {
        id
        title
      }
      eventPlace: sessionPlace
      featureImage: sessionImage {
        url @transform(handle: "eventFeatureImages")
        alt
      }
      speakers: sessionModerator {
        ... on person_default_Entry {
          id
          speakerName: title
        }
      }
    }
  `
}
