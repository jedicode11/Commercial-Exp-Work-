import { config } from '@/../app.config'

export const sps: unique symbol = Symbol('app.scriptParamsService')

export interface ScriptParams {
    scriptElement: HTMLOrSVGScriptElement,
    selector: string | null,
    siteId: string | null,
    lang: string | null,
    authBearer: string | null,
    startPage: string | null,
    pageSegment: string | null,
    searchPhrase: string | null,
    searchProductCategories: string | null,
    searchDate: string | null,
    searchSpeaker: string | null,
    searchForums: string | null,
    permittedForums: string | null,
    forumBasedOnEntry: string | null,
    searchResultOrder: string | null,
    detailsId: string | null,
    searchFieldIsHide: string | null,
    speakerFilterIsHide: string | null,
    tagFilterIsHide: string | null,
    widgetMode: string | null,
    tagCategoryParentId: string | null,
    eventAllSearchParams: string | null,
    querySearchSections: string | null,
    parentUrl: string | null,
    parentTicketFragment: string | null,
    parentTicket: string | null,
    parentEventFragment: string | null,
    parentSpeakersFragment: string | null,
    parentTagFragment: string | null,
    parentSpeakerFragment: string | null,
    parentSessionFragment: string | null,
    parentFilterFragment: string | null,
    parentFilterDateFragment: string | null,
    parentFilterCurrentEventsFragment: string | null,
    parentFilterTimeOfDayFragment: string | null,
    parentFilterSpeakerFragment: string | null,
    parentFilterLangFragment: string | null,
    parentFilterForumFragment: string | null,
    parentFilterTagFragment: string | null,
    websessionAddUrl: string | null,
    websessionGetUrl: string | null,
    organizerBookmarkAddUrl: string | null,
    organizerBookmarksGetUrl: string | null,
    organizerBookmarkDeleteUrl:string | null,
    organizerTicket:string | null,
    organizerHeaders:string | null,
    locationHallPlanUrl:string | null,
    locationHallPlanActivated:string | null,
    limit: string | number,
    offset: string | number,

    src: string | null,
}

export interface ScriptParamError {
    paramName: string
    message: string
}

export class ScriptParamsService {
    private _params: ScriptParams
    private _paramsError: Array<ScriptParamError> = []

    constructor (
      params: ScriptParams
    ) {
      this._params = { ...params }
      this._validateParams()
    }

    get isSteleMode (): boolean {
      return this._params.widgetMode === 'stele'
    }

    get startPage (): string | null {
      return this._params.startPage
    }

    get pageSegment (): string | null {
      return this._params.pageSegment
    }

    get locationHallPlanActivated (): boolean | undefined {
      if (this._params.locationHallPlanActivated?.toLowerCase() === 'true') {
        return true
      } else {
        return false
      }
    }

    get websessionAddUrl (): string | null {
      return this._params.websessionAddUrl
    }

    get websessionGetUrl (): string | null {
      return this._params.websessionGetUrl
    }

    get hasError (): boolean {
      return !!this._paramsError.length
    }

    get getErrors (): ScriptParamError[] {
      // TODO
      return this._paramsError
    }

    get limit (): number {
      return Number(this._params.limit)
    }

    get offset (): number {
      return Number(this._params.offset)
    }

    public getParamByAttributeName (attributeName: string): string | null {
      return this._params.scriptElement.getAttribute(attributeName)
    }

    public getErrorsByParamName (paramName: String): ScriptParamError[] | null {
      // TODO
      const errosAsKVP = this._paramsError.reduce((val: any, acc: any) => {
      return acc[val.paramName] = val
    }, {})
      return errosAsKVP[paramName as keyof typeof errosAsKVP] || null
    }

    private _validateParams (): void {
      // Start Page
      if (this._params.startPage || this._params.startPage === '') {
        if (!(config.scriptParams.allowedDataStartPageValues as string[]).includes(this._params.startPage)) {
          this._paramsError.push({
            paramName: 'startPage',
            message: `Script attribute 'data-start-page' has is an invalid value!
            See app.config.ts`
          })
        }
      }
      
      // Limit
      if (this._params.limit !== null && this.limit <= 0) {
        this._paramsError.push({
          paramName: 'eventLimit',
          message: 'Event limit must be a positive integer.'
        })
      }

      // Offset
      if (this._params.offset !== null && this.offset < 0) {
        this._paramsError.push({
        paramName: 'eventOffset',
        message: 'Event offset must be a non-negative integer or null.'
      })
    }
  }
}
