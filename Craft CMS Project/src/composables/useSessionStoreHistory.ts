type LocalStoreHistoryComposable = {
  append: () => void,
  back: () => void,
}

export function useSessionStoreHistory (): LocalStoreHistoryComposable {
  const sessionStoreKey = 'ems:h:w'

  const getHistory = (): Location[] => {
    const history = sessionStorage.getItem(sessionStoreKey)

    return history ? JSON.parse(history) : []
  }

  const setHistory = (history: Location[]): void => {
    sessionStorage.setItem(sessionStoreKey, JSON.stringify(history))
  }

  const popHistory = (): void => {
    const history = getHistory()
    history.pop()
    setHistory(history)
  }

  const append = (location?: Location): void => {
    location = location || window.location
    const history = getHistory()
    const lastLocation = history[history.length - 1]

    if (!lastLocation) {
      history.push(location)
      setHistory(history)
      return
    }

    const currentPath = new URLSearchParams(location.search)
    currentPath.delete('ticket')
    const secondToLastPath = new URLSearchParams(lastLocation.search)
    secondToLastPath.delete('ticket')

    const lastLocationWithoutTicket = lastLocation.origin + lastLocation.pathname + '?' + secondToLastPath.toString()
    const currentLocationWithoutTicket = location.origin + location.pathname + '?' + currentPath.toString()

    if (lastLocationWithoutTicket !== currentLocationWithoutTicket) {
      history.push(location)
    }

    setHistory(history)
  }

  const back = (): void => {
    let url = ''
    const history = getHistory()
    const secondToLastURL = history[history.length - 2]

    if (secondToLastURL) {
      url = secondToLastURL.href
    }

    const urlParams = new URLSearchParams(location.search)
    const ticketValue = urlParams.get('ticket')

    const secondToLastURLParals = new URLSearchParams(secondToLastURL.search)

    if (url && ticketValue && secondToLastURL) {
      secondToLastURLParals.delete('ticket')
      secondToLastURLParals.append('ticket', ticketValue)
      url = secondToLastURL.origin + secondToLastURL.pathname + '?' + secondToLastURLParals.toString()
    }

    if (url) {
      popHistory()
    }

    url ? window.location.href = url : window.history.back()
  }

  return {
    append,
    back
  }
}
