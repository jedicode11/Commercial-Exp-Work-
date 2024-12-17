import gql from 'graphql-tag'

export const GET_DROPDOWN_EVENT_LANDGS = gql`
  query getEventLangs ($site: [String]!) {
    langs: entries(site: $site, section: "eventLanguage", unique: true)  {
      id
      title
    }
  }
`
