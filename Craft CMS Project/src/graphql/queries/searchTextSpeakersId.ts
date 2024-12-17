import gql from 'graphql-tag'

export const GET_SEARCH_TEXT_SPEAKERS_ID = gql`
  query getSearchTextSpeakersId ($site: [String]! $text: String) {
    ids: entries(site: $site, section: "person" search: $text) {
        ...on person_default_Entry {
          id
        }
    }
  }
`