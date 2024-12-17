import gql from 'graphql-tag'

export const GET_DROPDOWN_SPEAKERS = gql`
  query getSpeakers ($site: [String]!) {
    speakers: entries(site: $site, section: "person", unique: true)  {
      ...on person_default_Entry {
      id
      title
      personLastName
      }
    }
  }
`
