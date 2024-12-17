import gql from 'graphql-tag'

export const PARENT_CATEGORY = gql`
query parentCategory($site: [String]!, $categoryIds: [QueryArgument]) {
        parentCategories: entries(site: $site, id: $categoryIds) {
          ...on productCategory_default_Entry {
            parent {
              id
            }
          }
        }
      }
`
