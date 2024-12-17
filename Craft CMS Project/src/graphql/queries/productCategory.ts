import gql from 'graphql-tag'

export const GET_PRODUCT_CATEGORY = gql`
  query getProductCategory ($site: [String]!) {
    entries: entries(site: $site, section: "productCategory", type: "default") {
      id
      title
      children {
        id
        title
        children {
          id
          title
          children {
            id
            title
          }
        }
      }
    }
  }
`
