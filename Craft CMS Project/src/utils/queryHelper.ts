import { config } from '@/../app.config'
import { QuerySearchEventResponseModel } from '@/models/QuerySearchEventResponseModel'

export const handleSpecialCarecters = (searchText: string): string => {
  const transformedSearchText = searchText
    .replaceAll('ä', 'ae')
    .replaceAll('ö', 'oe')
    .replaceAll('ü', 'ue')
    .replaceAll('ß', 'ss')
    .replaceAll('Ä', 'AE')
    .replaceAll('Ö', 'OE')
    .replaceAll('Ü', 'UE')

  return transformedSearchText
}

export const getEventSection = (sections: string[]): string => {
  if (sections.includes('event')) {
    return config.sectionHandle.event
  }

  return ''
}

export const getSessionSection = (sections: string[]): string => {
  if (sections.includes('session')) {
    return config.sectionHandle.session
  }

  return ''
}

export class QueryHandler {
  private _initialLimit: number
  private _subsequentLimit: number
  private _offset = 0
  private _sections: string[]
  private _currentLimit: number
  private _loadCount = 0
  private _pointer = 0
  private _autoLoad = false

  private _searchText: string | undefined

  constructor (initialLimit: number, subsequentLimit: number, querySections: string[]) {
    this._initialLimit = initialLimit
    this._subsequentLimit = subsequentLimit
    this._sections = [...querySections, 'relatedBySpeaker']
    this._setCurrentLimit()
  }

  get next (): boolean {
    return !!this._sections[this._pointer]
  }

  get autoLoad (): boolean {
    return this._autoLoad && this.next
  }

  set searchText (st: string) {
    this._searchText = st
  }

  public queryResponse (qr: QuerySearchEventResponseModel) {
    if (qr) {
      if (this._sections[this._pointer] === 'event') {
        const loadEntriesCount = qr.events.length
        if (loadEntriesCount < this._currentLimit) {
          this._changePointer()
          this._offset = 0
          this._currentLimit = this._currentLimit - loadEntriesCount
          this._autoLoad = true
        } else {
          this._offset += this._currentLimit
          this._autoLoad = false
        }
      } else if (this._sections[this._pointer] === 'session') {
        const loadEntriesCount = qr.sessions.length
        if (loadEntriesCount < this._currentLimit) {
          this._changePointer()
          this._offset = 0
          this._currentLimit = this._currentLimit - loadEntriesCount
          this._autoLoad = true
        } else {
          this._offset += this._currentLimit
          this._autoLoad = false
        }
      } else if (this._sections[this._pointer] === 'relatedBySpeaker')  {
        const loadRelatedBySpeakerCount = qr.relatedBySpeaker.length
        if (loadRelatedBySpeakerCount < this._currentLimit) {
          this._changePointer()
          this._offset = 0
          this._currentLimit = this._currentLimit - loadRelatedBySpeakerCount
          this._autoLoad = true
        } else {
          this._offset += this._currentLimit
          this._autoLoad = false
        }
      }

      this._increaseLoadCount()
      this._setCurrentLimit()
    }
  }

  public reset () {
    this._loadCount = 0
    this._currentLimit = this._initialLimit
    this._offset = 0
    this._pointer = 0
    this._autoLoad = false
  }

  public getNewValue (): {
    eventSection: string,
    sessionSection: string
    relatedBySpeakerSection: string[]
    eventLimit: number,
    eventOffset: number,
    sessionLimit: number,
    sessionOffset: number,
    relatedBySpeakerLimit: number,
    relatedBySpeakerOffset: number,
    eventCountSection: string,
    sessionCountSection: string,
    relatedBySpeakerCountSection: string[]
    } {
    if (this._sections[this._pointer] === 'event') {
      return {
        eventSection: 'event',
        sessionSection: '',
        relatedBySpeakerSection: [],

        eventLimit: this._currentLimit,
        eventOffset: this._offset,

        sessionLimit: 0,
        sessionOffset: 0,

        relatedBySpeakerLimit: 0,
        relatedBySpeakerOffset: 0,

        eventCountSection: this._getEventCountSection(),
        sessionCountSection: this._getSessionCountSection(),
        relatedBySpeakerCountSection: this._getRelatedBySpeakerSection()
      }
    }
    if (this._sections[this._pointer] === 'session') {
      return {
        eventSection: '',
        sessionSection: 'session',
        relatedBySpeakerSection: [],

        eventLimit: 0,
        eventOffset: 0,

        sessionLimit: this._currentLimit,
        sessionOffset: this._offset,

        relatedBySpeakerLimit: 0,
        relatedBySpeakerOffset: 0,

        eventCountSection: this._getEventCountSection(),
        sessionCountSection: this._getSessionCountSection(),
        relatedBySpeakerCountSection: this._getRelatedBySpeakerSection()
      }
    }
    if (this._sections[this._pointer] === 'relatedBySpeaker' && this._searchText) {
      return {
        eventSection: '',
        sessionSection: '',
        relatedBySpeakerSection: this._getRelatedBySpeakerSection(),

        eventLimit: 0,
        eventOffset: 0,

        sessionLimit: 0,
        sessionOffset: this._offset,

        relatedBySpeakerLimit: this._currentLimit,
        relatedBySpeakerOffset: this._offset,

        eventCountSection: this._getEventCountSection(),
        sessionCountSection: this._getSessionCountSection(),
        relatedBySpeakerCountSection: this._getRelatedBySpeakerSection()
      }
    }
    return {
      eventSection: '',
      sessionSection: '',
      relatedBySpeakerSection: [],

      eventLimit: 0,
      eventOffset: 0,

      sessionLimit: 0,
      sessionOffset: 0,

      relatedBySpeakerLimit: 0,
      relatedBySpeakerOffset: 0,

      eventCountSection: this._getEventCountSection(),
      sessionCountSection: this._getSessionCountSection(),
      relatedBySpeakerCountSection: this._getRelatedBySpeakerSection()
    }
  }

  private _changePointer () {
    this._pointer += 1
  }

  private _increaseLoadCount () {
    this._loadCount += 1
  }

  private _getEventCountSection (): string {
    return this._sections.includes('event') ? 'event' : ''
  }

  private _getSessionCountSection (): string {
    return this._sections.includes('session') ? 'session' : ''
  }

  private _getRelatedBySpeakerSection (): string[] {
    if (this._sections.includes('event') && this._sections.includes('session')) {
      return ['event', 'session']
    } else if (this._sections.includes('session')) {
      return ['session']
    } else if (this._sections.includes('event')) {
      return ['event']
    } else {
      return []
    }
  }

  private _setCurrentLimit () {
    if (this._loadCount) {
      this._currentLimit = this._subsequentLimit
    } else {
      this._currentLimit = this._initialLimit
    }
  }
}
