
export const triggerExhGetInTouch = (): void => {
  window.document.dispatchEvent(new CustomEvent('exhGetInTouch.start'))
}
