import gql from 'graphql-tag'

export const GET_SESSION_DETAILS = gql`
query getSession ($site: [String]!, $uri: [String]!) {
  entry(site: $site, section: "session", type: "default", uri: $uri) {
    ...on session_default_Entry {
      siteId
      title
      startTime: sessionStartTime
      endTime: sessionEndTime
      eventCategory {
        uri
      }
      productCategory: sessionProductCategory {
        id
        title
      }
      location: sessionLocation
      locationHallPlan: sessionLocationHallPlan {
        ...on location_default_Entry {
          title
          locationNumber
        }
      }
      language: sessionLanguage {
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
      place: sessionPlace
      chargeable: sessionChargeable
      summary: sessionSummary
      content: sessionContent
      image: sessionImage {
        url @transform(handle: "sessionImage")
        alt
      }
      exhibitor: sessionExhibitor {
        ...on exhibitor_default_Entry {
          exhibitorName
          exhibitorAddress
          exhibitorFinderId
          exhibitorLink
          exhibitorFeatureImage
        }
      }
      chairs: sessionChair {
        ... on person_default_Entry {
          id
          uri
          company: personCompany
          speakerName: title
          speakerTitle: personTitle
          speakerLastName: personLastName
          speakerJobTitle: personJobTitle
          speakerAvatar: personAvatar {
            url  @transform(handle: "speakerProfileImage")
            alt
          }
        }
      }
      moderators: sessionModerator {
        ... on person_default_Entry {
          id
          uri
          company: personCompany
          speakerName: title
          speakerTitle: personTitle
          speakerLastName: personLastName
          speakerJobTitle: personJobTitle
          speakerAvatar: personAvatar {
            url  @transform(handle: "speakerProfileImage")
            alt
          }
        }
      }
      tags {
        title
      }
      events: sessionEvents {
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
          # eventSummary
          featureImage {
            url @transform(handle: "eventImage")
            alt
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
              exhibitorLogo: exhibitorFeatureImage
              exhibitorUrl: exhibitorLink
            }
          }
          speakers: speaker {
            ... on person_default_Entry {
              id
              company: personCompany
              speakerName: title
              speakerTitle: personTitle
              speakerJobTitle: personJobTitle
              # speakerPosition: eventCategories {
              #   title
              # }
              speakerAvatar: personAvatar {
                url  @transform(handle: "speakerProfileImage")
                alt
              }
            }
          }
          eventContent
          eventPlace
          eventChargeable
        }
      }
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
}`
