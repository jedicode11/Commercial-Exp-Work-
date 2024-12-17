import gql from 'graphql-tag'

export const GET_CATEGORY_TAGS = gql`
  query getCategoryTags ($site: [String]!, $id: [QueryArgument]) {
    tags: entry(site: $site, id: $id)  {
      children {
        id
        title
      }
    }
  }
`
