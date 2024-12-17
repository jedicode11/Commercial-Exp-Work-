import gql from 'graphql-tag'

export const SIBLING_CATEGORY = gql`
      query siblingsCategory($site: [String]!, $categoryIds: [QueryArgument]) {
        siblingsCategories: entries(site: $site, id: $categoryIds) {
          ...on productCategory_default_Entry {
            parent {
              children {
                id
              }
            }
          }
        }
      }
    `
