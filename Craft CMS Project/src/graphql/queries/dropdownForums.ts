import gql from 'graphql-tag'

export const GET_DROPDOWN_FORUMS = gql`
  query getForums ($site: [String]!) {
    forums: entries(site: $site, section: "forum", unique: true)  {
      ...on forum_default_Entry {
        id
        title
        slug
      }
    }
  }
`
