<template>
  <section class="stand finder-normalize finder-content-wrapper">
    <div class="finder-content-wrapper__inner">
      <h2 class="stand__module-title">{{ t('components_stand_title') }}</h2>
      <div class="stand__maps">
        <div class="stand__maps-item stand__maps-item--hall">
          <figure class="responsive-media-wrapper responsive-media-wrapper__16_9">
            <img :src="profileStore.profile.areaMap"/>
          </figure>
        </div>
        <div class="stand__maps-item stand__maps-item--event">
          <figure class="responsive-media-wrapper responsive-media-wrapper__16_9">
            <img :src="profileStore.profile.eventMap"/>
          </figure>
        </div>
      </div>
      <!-- <contact-person-list parentModule="stand"/> -->
    </div>
  </section>

</template>

<script lang="ts">
import { defineComponent } from 'vue'
import { useI18n } from 'vue-i18n'
import { useProfile } from '@/store/profile'

export default defineComponent({
  name: 'Stand',
  components: {
  },
  setup () {
    const { t } = useI18n()
    const profileStore = useProfile()

    profileStore.fetchProfile()

    return {
      t,
      profileStore
    }
  }
})
</script>
<style lang="scss" scoped>
@import 'src/assets/styles/04-common/module-title';
.stand {
  .stand__module-title {
    @include finder-module-title;
  }
  .stand__maps {
    display: flex;
    margin-bottom: $grid-gutter-width;
    img {
      border-radius: $border-radius;
    }
    .stand__maps-item {
      width: 50%;
      .stand__maps-item--hall {
        margin-right: $grid-gutter-width;
      }
      .stand__maps-item--event {
        img {
          object-fit: cover;
        }
      }
    }
    @media (max-width: mq(md, true)) {
      flex-direction: column;
      .stand__maps-item {
        width: 100%;
        .stand__maps-item--hall {
          margin: 0 0 $grid-gutter-width;
        }
      }
    }
  }
}
</style>
