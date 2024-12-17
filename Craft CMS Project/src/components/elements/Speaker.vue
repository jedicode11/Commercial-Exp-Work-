<template>
  <div class="speaker">
    <div class="speaker__frame">
      <img :src="speaker.speakerAvatar?.length ? speaker.speakerAvatar[0].url : hostname + 'img/no_image_icon.png'"
        :alt="speaker.speakerAvatar?.length ? speaker.speakerAvatar[0].alt : ''"
        :title="speaker.speakerAvatar?.length ? speaker.speakerAvatar[0].alt : ''" />
    </div>
    <div class="speaker__details">
      <!-- <router-link :to="`/speaker/${speaker.id}`" @click="$event.preventDefault()">{{ name }}</router-link><br /> -->
      <div class="speaker__details__company" @click="$event.stopPropagation()">{{ speaker.company }}</div>
      <div class="speaker__details__name">{{ speaker.speakerTitle ? speaker.speakerTitle : ' ' }} {{ speaker.speakerName }}
      </div>
      <div class="speaker__details__title" @click="$event.stopPropagation()">{{ speaker.speakerJobTitle }}</div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType, computed, inject } from 'vue'
import { useWidgetMode } from '@/composables/useWidgetMode'
import { SpeakerModel } from '@/models/SpeakerModel'

export default defineComponent({
  name: 'SpeakerComponent',
  props: {
    speaker: {
      type: Object as PropType<SpeakerModel>,
      required: true
    }
  },
  setup (props) {
    const { isWidgetMode } = useWidgetMode()
    const origin = inject('app.origin')

    const hostname = computed(() => {
      if (isWidgetMode) {
        return origin + '/'
      }
      return '/'
    })
    const name = computed(() => {
      return `${props.speaker.speakerTitle ? props.speaker.speakerTitle : ''} ${props.speaker.speakerName}`
    })

    return {
      hostname,
      name
    }
  }
})
</script>
<style lang="scss" scoped>
.speaker {
  display: flex;
  flex-direction: column;
}

.speaker__frame {
  border: 2px solid #d7d7d7;
  border-radius: 10px;
  width: 204px;
  height: 204px;
  position: relative;
  &::after {
    content: '';
    display: block;
    padding-bottom: 56%;
  }

  img {
    position: absolute;
    width: 100%;
    height: 100%;
    object-fit: cover;
    cursor: pointer;
  }
}

img {
  border-radius: 8px;
  max-width: 100%;
  max-height: 100%;
  object-fit: cover;
}

.speaker__details {
  line-height: 100%;
  text-align: left;

  .speaker__details__title {
    margin-top: 5px;
    font-family: 'Source Sans Pro', sans-serif;
    font-style: normal;
    font-size: 16px;
    font-weight: 400;
    line-height: 19px;
    color: rgba(24, 24, 24, .5);
  }
  .speaker__details__name {
    margin-top: 5px;
    font-family: 'Source Sans Pro', sans-serif;
    font-size: 16px;
    font-weight: 700;
    line-height: 19px;
    // white-space: nowrap;
    cursor: pointer;
  }
  .speaker__details__company {
    margin-top: 5px;
    font-family: 'Source Sans Pro', sans-serif;
    font-style: normal;
    font-size: 12px;
    font-weight: 400;
    line-height: 120%;
    text-transform: uppercase;
    color: rgba(24, 24, 24, .5);
  }
}
@media (max-width: 480px) {
  .speaker {
    flex-direction: column;
  }

  .speaker__frame {
    width: 80px;
    height: 80px;
    margin-right: 10px;
    overflow: hidden;
  }

  .speaker__details {
    flex: 1;
    margin-top: 0;
    display: flex;
    flex-direction: column;
    width: 57%;
  }

  .speaker__details__name {
    order: 1;
  }

  .speaker__details__title {
    order: 2;
    margin-top: 5px;
    display: none;
  }

  .speaker__details__company {
    order: 3;
    margin-top: 5px;
  }

  img {
    max-width: 100%;
    max-height: 100%;
    object-fit: cover;
    width: 80px;
    height: 80px;
    border-radius: 8px;
    border: 1px;
  }
}
</style>
