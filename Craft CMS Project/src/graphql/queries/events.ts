import gql from 'graphql-tag'
import { eventDefaultFragment } from '../fragments/event'
import { sessionDefaultFragment } from '../fragments/session'

export const GET_EVENTS = gql`
  query getEvents (
    $site: [String]!,
    $eventSection: [String]!,
    $sessionSection: [String]!,
    $relatedBySpeakerSection: [String]!
    $relatedTo: [QueryArgument],
    $relatedToCategories:  [EntryCriteriaInput],
    $textFilter: String,
    $timeStart: [QueryArgument],
    $timeEnd: [QueryArgument],
    $eventLimit: Int,
    $eventOffset: Int,
    $sessionLimit: Int,
    $sessionOffset: Int,
    $relatedBySpeakerLimit: Int,
    $relatedBySpeakerOffset: Int,
    $eventOrderBy: String
    $sessionOrderBy: String
    $eventCountSection: [String]
    $sessionCountSection: [String]
    $relatedBySpeakerCountSection: [String]
    ) {
    events: entries(
      site: $site,
      section: $eventSection,
      type: "default",
      relatedTo: $relatedTo,
      relatedToEntries:  $relatedToCategories,
      search: $textFilter,
      timeStart: $timeStart,
      timeEnd: $timeEnd,
      limit: $eventLimit,
      offset: $eventOffset,
      orderBy: $eventOrderBy,
      ) {
      ...EventDefault
    }
    sessions: entries(
      site: $site,
      section: $sessionSection,
      type: "default",
      relatedTo: $relatedTo,
      relatedToEntries:  $relatedToCategories,
      search: $textFilter,
      sessionStartTime: $timeStart,
      sessionEndTime: $timeEnd,
      limit: $sessionLimit,
      offset: $sessionOffset,
      orderBy: $sessionOrderBy,
      ) {
      ...SessionDefault
    }
    relatedBySpeaker: entries(
      site: $site
      section: $relatedBySpeakerSection
      type: "default"
      relatedTo: $relatedTo
      sessionStartTime: $timeStart
      sessionEndTime: $timeEnd
      limit: $relatedBySpeakerLimit
      offset: $relatedBySpeakerOffset
      orderBy: $sessionOrderBy
      relatedToEntries: [{site: $site section: "person" search: $textFilter}]
    ) {
      ...EventDefault
      ...SessionDefault
    }
    eventsCount: entryCount (
      site: $site,
      section: $eventCountSection,
      type: "default",
      relatedTo: $relatedTo,
      relatedToEntries:  $relatedToCategories,
      search: $textFilter,
      timeStart: $timeStart,
      timeEnd: $timeEnd,
    )
    sessionsCount: entryCount (
      site: $site,
      section: $sessionCountSection,
      type: "default",
      relatedTo: $relatedTo,
      relatedToEntries:  $relatedToCategories,
      search: $textFilter,
      sessionStartTime: $timeStart,
      sessionEndTime: $timeEnd,
    )
    relatedBySpeakerCount: entryCount(
      site: $site
      section: $relatedBySpeakerCountSection
      type: "default"
      relatedTo: $relatedTo
      relatedToEntries: [{site: $site section: "person" search: $textFilter}]
      sessionStartTime: $timeStart
      sessionEndTime: $timeEnd
    )
    allEntriesId: entries(
      site: $site
      section: $relatedBySpeakerCountSection
      type: "default"
      relatedTo: $relatedTo
      relatedToEntries: $relatedToCategories
      search: $textFilter
      timeStart: $timeStart
      timeEnd: $timeEnd
    ) {
      id
    }
    allEntriesRelatedBySpeakerId: entries(
      site: $site
      section: $relatedBySpeakerCountSection
      type: "default"
      relatedTo: $relatedTo
      relatedToEntries: [{site: $site, section: "person", search: $textFilter}]
      sessionStartTime: $timeStart
      sessionEndTime: $timeEnd
    ) {
      id
    }
  }
  ${sessionDefaultFragment.session}
  ${eventDefaultFragment.event}
`
