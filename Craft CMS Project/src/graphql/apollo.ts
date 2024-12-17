
import { ApolloClient, createHttpLink, InMemoryCache } from '@apollo/client/core'
import { onError } from 'apollo-link-error'
import { DefaultApolloClient } from '@vue/apollo-composable'

const httpLink = createHttpLink({
  uri: process.env.VUE_APP_BACKEND_URI,
  headers: {
    Authorization: `Bearer ${process.env.VUE_APP_BACKEND_AUTH_BEARER}`,
    'Content-Type': 'application/json'
  }
})

onError(({ graphQLErrors, networkError }) => {
  if (graphQLErrors) {
    graphQLErrors.map(({ message, locations, path }) =>
      console.log(
          `[GraphQL error]: Message: ${message}, Location: ${locations}, Path: ${path}`
      )
    )
    if (networkError) console.log(`[Network error]: ${networkError}`)
  }
})

export const apolloClient = new ApolloClient({
  link: httpLink,
  cache: new InMemoryCache(),
  connectToDevTools: true
})
export { DefaultApolloClient }
