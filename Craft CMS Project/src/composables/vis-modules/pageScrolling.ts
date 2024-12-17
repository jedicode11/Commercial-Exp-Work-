import { get as _get } from 'lodash-es'

declare global {
  interface Window {
    DIMEDIS: any // eslint-disable-line
  }
}

export enum PSOffsetIds {
  metabar = 'finderVueMetabar'
}

const getPs = () => {
  const ps = _get(window.DIMEDIS, 'modules.pageScrolling')
  if (ps) {
    return ps
  }
  console.warn('vis-module "PageScrolling" is not available')
  return null
}

export const setPsOffset = (offsetId: string, value: number): void => {
  const ps = getPs()
  if (ps) {
    ps.setOffset(offsetId, value)
  }
}

export const getPsOffsetUp = (): number => {
  const ps = getPs()
  if (ps) {
    return window.DIMEDIS.modules.pageScrolling.getOffsetUp()
  }
  return 0
}

export const getPsOffsetDown = (): number => {
  const ps = getPs()
  if (ps) {
    return window.DIMEDIS.modules.pageScrolling.getOffsetDown()
  }
  return 0
}

export const scrollToElement = (element: HTMLElement, animate?: boolean): void => {
  const ps = getPs()
  if (ps) {
    window.DIMEDIS.modules.pageScrolling.scrollToElement(element, animate)
  }
}

export const scrollToSelector = (selector: string, animate?: boolean): void => {
  const ps = getPs()
  if (ps) {
    window.DIMEDIS.modules.pageScrolling.scrollToSelector(selector, animate)
  }
}
