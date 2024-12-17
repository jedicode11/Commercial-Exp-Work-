<template>
  <section class="separator finder-normalize finder-content-wrapper">
    <hr />
    <div class="finder-content-wrapper__inner">
      <slot></slot>
    </div>
  </section>

</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'SectionSeparator'
})
</script>

<style lang="scss" scoped>
hr {
  background-color: var(--finder-color-primary);
  margin-top: 1rem;
  margin-bottom: 1rem;
}
</style>
