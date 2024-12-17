import gql from 'graphql-tag'

export const GET_GLOBAL_LOCATIONS = gql`
  query getGlobalLocations($site: [String]!) {
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
