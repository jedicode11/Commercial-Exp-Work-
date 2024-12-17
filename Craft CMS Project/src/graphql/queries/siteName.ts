import gql from 'graphql-tag'

export const GET_SITE_NAME = gql`
  query getSite ($site: [String]!) {
      sites: globalSets(site: $site handle: "filterName") {
      ... on filterName_GlobalSet {
        name: siteName
        handle: siteHandle
      }
    }
  }
`
