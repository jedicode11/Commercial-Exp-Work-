export enum FinderEvents {
  dataEditComplete = 'dimedis.finder.dataEditComplete',
  closeAllModals = 'dimedis.finder.closeAllModals',
  modalOpen = 'dimedis.finder.modalOpen',
  closeModal = 'dimedis.finder.closeModal',
  stopVideos = 'dimedis.finder.stopVideos',
  showModalPage1 = 'dimedis.finder.showModalPage1',
  showModalPage2 = 'dimedis.finder.showModalPage2'
}

export const dispatchFinderEvent = (type: string, eventInit?: Record<string, unknown>): void => {
  window.dispatchEvent(
    new CustomEvent(type, eventInit)
  )
}
