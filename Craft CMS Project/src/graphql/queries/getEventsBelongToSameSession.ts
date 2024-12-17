import gql from 'graphql-tag'

export const GET_EVENTS_BELONG_TO_SAME_SESSION = gql`
query getEventsBelongToSameSession (
  $site: [String]!,
  $relatedTo: [QueryArgument]
  ) {
    entries(
      site: $site,
      section: "session",
      relatedTo: $relatedTo
      ) {
      ...on session_default_Entry {
        title
        locationHallPlan: sessionLocationHallPlan {
          ...on location_default_Entry {
            title
            locationNumber
          }
        }
        sessionEvents {
          ... on event_default_Entry {
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
