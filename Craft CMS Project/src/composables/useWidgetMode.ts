import { Ref, ref } from 'vue'

type EntityDetailsFactoryParams = {
  parentUrl?: string;
  parentTicketFragment?: string;
  parentTicket?: string;
}

type NavigateToEntity<T> = (params: T) => (id: string) => void

type EventDetailsFactoryParams = EntityDetailsFactoryParams & {
  parentEventFragment?: string;
}

type SpeakerDetailsFactoryParams = EntityDetailsFactoryParams & {
  parentSpeakerFragment?: string;
}

type SessionDetailsFactoryParams = EntityDetailsFactoryParams & {
  parentSessionFragment?: string;
}

type WidgetModeComposable = {
  isWidgetMode: boolean;
  isWidgetModeRef: Ref<boolean>;
  navigateToEventDetailsFactory: NavigateToEntity<EventDetailsFactoryParams>;
  navigateToSpeakerDetailsFactory: NavigateToEntity<SpeakerDetailsFactoryParams>;
  navigateToSessionDetailsFactory: NavigateToEntity<SessionDetailsFactoryParams>;
}

export function useWidgetMode (): WidgetModeComposable {
  const isWidgetMode: boolean = process.env.VUE_APP_WIDGET === '1' ?? false
  const isWidgetModeRef = ref<boolean>(isWidgetMode)

  const navigateToEventDetailsFactory = ({ parentUrl, parentEventFragment, parentTicketFragment, parentTicket }: EventDetailsFactoryParams) =>
    (uri: string) => {
      if (parentUrl && parentEventFragment) {
        let url = parentUrl

        let ticketFragment
        if (parentTicketFragment && parentTicket) {
          ticketFragment = parentTicketFragment.replace('{ticket}', parentTicket)
        } else {
          ticketFragment = ''
        }
        console.info('- using ticket fragment:', ticketFragment)
        url = url.replace('{ticket_fragment}', ticketFragment)

        const eventFragment = parentEventFragment.replace('{uri}', uri)
        console.info('- using event fragment:', eventFragment)
        url = url.replace('{entity_fragment}', eventFragment)

        console.info('- preparing parent URL:', url)
        window.location.href = url
      }
    }

  const navigateToSpeakerDetailsFactory = ({ parentUrl, parentSpeakerFragment, parentTicketFragment, parentTicket }: SpeakerDetailsFactoryParams) =>
    (uri: string) => {
      if (parentUrl && parentSpeakerFragment) {
        let url = parentUrl

        let ticketFragment
        if (parentTicketFragment && parentTicket) {
          ticketFragment = parentTicketFragment.replace('{ticket}', parentTicket)
        } else {
          ticketFragment = ''
        }
        console.info('- using ticket fragment:', ticketFragment)
        url = url.replace('{ticket_fragment}', ticketFragment)

        const speakerFragment = parentSpeakerFragment.replace('{uri}', uri)
        console.info('- using speaker fragment:', speakerFragment)
        url = url.replace('{entity_fragment}', speakerFragment)

        console.info('- preparing parent URL:', url)
        window.location.href = url
      }
    }

  const navigateToSessionDetailsFactory = ({ parentUrl, parentSessionFragment, parentTicketFragment, parentTicket }: SessionDetailsFactoryParams) =>
    (uri: string) => {
      if (parentUrl && parentSessionFragment) {
        let url = parentUrl

        let ticketFragment
        if (parentTicketFragment && parentTicket) {
          ticketFragment = parentTicketFragment.replace('{ticket}', parentTicket)
        } else {
          ticketFragment = ''
        }
        console.info('- using ticket fragment:', ticketFragment)
        url = url.replace('{ticket_fragment}', ticketFragment)

        const sessionFragment = parentSessionFragment.replace('{uri}', uri)
        console.info('- using session fragment:', sessionFragment)
        url = url.replace('{entity_fragment}', sessionFragment)

        console.info('- preparing parent URL:', url)
        window.location.href = url
      }
    }

  return {
    isWidgetMode,
    isWidgetModeRef,
    navigateToEventDetailsFactory,
    navigateToSpeakerDetailsFactory,
    navigateToSessionDetailsFactory
  }
}
