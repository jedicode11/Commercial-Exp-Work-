/**
 * unfortunately, the functions provided by the body-scroll-lock library are not able to prevent background page
 * scrolling on CMS pages, when e.g. a modal is opened in the foreground
 * for some reason, setting 'document.body.style.overflow' to 'hidden' (this is the body-scroll-lock library's approach)
 * is not sufficient here - we also have to set 'document.documentElement.style.overflow' to 'hidden'
 * so please only use the functions provided below instead of directly using body-scroll-lock functions
 */
import { BodyScrollOptions, clearAllBodyScrollLocks, disableBodyScroll, enableBodyScroll } from 'body-scroll-lock'

let previousDocumentOverflowSetting: string | undefined
let locks: Array<HTMLElement | Element> = []

const setOverflowHidden = () => {
  if (previousDocumentOverflowSetting === undefined) {
    previousDocumentOverflowSetting = document.documentElement.style.overflow
    document.documentElement.style.overflow = 'hidden'
  }
}

const restoreOverflowSetting = () => {
  if (previousDocumentOverflowSetting !== undefined) {
    document.documentElement.style.overflow = previousDocumentOverflowSetting
    previousDocumentOverflowSetting = undefined
  }
}

export const clearAllScrollLocks = (): void => {
  clearAllBodyScrollLocks()
  restoreOverflowSetting()
  locks = []
}

export const removeScrollLock = (targetElement: HTMLElement | Element): void => {
  enableBodyScroll(targetElement)
  locks = locks.filter((existingLock) => existingLock !== targetElement)
  if (!locks.length) {
    restoreOverflowSetting()
  }
}

export const addScrollLock = (targetElement: HTMLElement | Element, options?: BodyScrollOptions): void => {
  disableBodyScroll(targetElement, options)
  locks.push(targetElement)
  setOverflowHidden()
}
