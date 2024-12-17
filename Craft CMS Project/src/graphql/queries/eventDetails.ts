import gql from 'graphql-tag'

export const GET_EVENT_DETAILS = gql`
query getEvent ($site: [String]!, $uri: [String]) {
  event: entry(site: $site, section: "event", type: "default", uri: $uri) {
    ...on event_default_Entry {
      id
      uri
      uid
      siteId
      title
      startEventDatetime: timeStart
      endEventDatetime: timeEnd
      eventCategories {
        id
        title
      }
      productCategory {
        id
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
      featureImage {
        url @transform(handle: "eventImage")
        alt
      }
      exhibitor {
        ... on exhibitor_default_Entry {
          id
          exhibitorTitle: title
          exhibitorAddress
          exhibitorLogo: exhibitorFeatureImage
          exhibitorUrl: exhibitorLink
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
      speakers: speaker {
        ... on person_default_Entry {
          id
          uri
          company: personCompany
          speakerName: title
          speakerLastName: personLastName
          speakerTitle: personTitle
          speakerJobTitle: personJobTitle
          # speakerPosition: eventCategories {
          #   title
          # }
          speakerAvatar: personAvatar {
            url @transform(handle: "speakerProfileImage")
            alt
          }
        }
      }
      chairs: chair {
        ... on person_default_Entry {
          id
          uri
          company: personCompany
          speakerName: title
          speakerLastName: personLastName
          speakerTitle: personTitle
          speakerJobTitle: personJobTitle
          speakerAvatar: personAvatar {
            url @transform(handle: "speakerProfileImage")
            alt
          }
        }
      }
      moderators: moderator {
        ... on person_default_Entry {
          id
          uri
          company: personCompany
          speakerName: title
          speakerLastName: personLastName
          speakerTitle: personTitle
          speakerJobTitle: personJobTitle
          speakerAvatar: personAvatar {
            url @transform(handle: "speakerProfileImage")
            alt
          }
        }
      }
      eventContent
      eventPlace
      eventChargeable
      tags {
        id
        title
      }
    }
  }
  relatedSession: entry(site: $site, section: "session", type: "default", relatedToEntries: {site: $site, uri: $uri}) {
    ...on session_default_Entry {
      id
      sessionLocationHallPlan {
        ...on location_default_Entry {
          id
          title
          locationHallPlan: locationNumber
        } 
      }
    }
  }
  globalSets(site: $site, handle: ["siteLocation", "fairId"]) {
    ... on siteLocation_GlobalSet {
      siteId
      locationHallPlan: globalLocation {
        ... on location_default_Entry {
          locationNumber
          title
        }
      }
    }
    ... on fairId_GlobalSet {
      siteId
      fairIdField
    }
  }
}
`
