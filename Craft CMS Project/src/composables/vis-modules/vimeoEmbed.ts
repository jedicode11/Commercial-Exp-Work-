declare global {
  interface Window {
    DIMEDIS: any // eslint-disable-line
  }
}

export interface VimeoOEmbedData {
  title: string
  text: string
  thumbImgUrl: string
}

// calls vimeo oEmbed service, uses cached data from localStorage if available
export async function getVimeoOEmbed (videoId: string, width?: number, height?: number, clearCache?: boolean): Promise<VimeoOEmbedData | Error> {
  if (window.DIMEDIS) {
    return window.DIMEDIS.vis.vimeo.$fn.getVideoEmbedPromise(videoId, width, height, clearCache)
  }
  return Promise.reject(new Error('Missing window.DIMEDIS'))
}
