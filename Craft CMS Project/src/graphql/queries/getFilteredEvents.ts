import gql from 'graphql-tag'

export const GET_FILTERED_EVENTS = gql`
    query getFilteredEvents (
        $site: [String]!,
        $section: [String]!,
        $relatedTo: [QueryArgument],
        $timeStart: [QueryArgument],
        $timeEnd: [QueryArgument],
        $limit: Int,
        $offset: Int,
        $orderBy: String
      ) {
      events: entries(
        site: $site,
        section: $section,
        relatedTo: $relatedTo,
        timeStart: $timeStart,
        timeEnd: $timeEnd,
        limit: $limit,
        offset: $offset,
        orderBy: $orderBy
      ) {
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
        }
        siteId
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