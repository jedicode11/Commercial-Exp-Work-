import { useFetch, UseFetchReturn } from '@vueuse/core'
import { VimeoOEmbedData } from '@/models/VimeoOEmbedData'

export function useFetchVimeoOEmbed (videoId: string): UseFetchReturn<VimeoOEmbedData> {
  const nocache = Date.now()
  const thumbSize = 480
  const url = `https://vimeo.com/api/oembed.json?url=https%3A//vimeo.com/${videoId}?width=${thumbSize}&height=${thumbSize}&nocache=${nocache}`

  return useFetch(url, {
    immediate: true,
    afterFetch (ctx) {
      const result = ctx.data
      ctx.data = {
        title: result.title, // The title of the video.
        authorName: result.author_name, // The owner of the video.
        accountType: result.account_type, // The video owner's membership type on Vimeo.
        width: result.width, // The width of the video in pixels.
        height: result.height, // The height of the video in pixels.
        duration: result.duration, // The duration of the video in seconds.
        description: result.description, // The description of the video.
        thumbnailUrl: result.thumbnail_url // The URL of the video's thumbnail image.
      } as VimeoOEmbedData
      return ctx
    }
  })
}
