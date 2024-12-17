<template>
  <div v-if="!(isActive === 'show')" style="margin: 1rem;" class="finder-normalize">
    <slot></slot>
  </div>
  <finder-template :add-wrapper="addWrapper" v-else>
    <slot></slot>
  </finder-template>
</template>

<script lang="ts">
import { defineComponent, toRef } from 'vue'
import FinderTemplate from './FinderTemplate.vue'
import { useStore, testState } from '@/store'

export default defineComponent({
  name: 'FinderTheme',
  props: {
    isActive: { type: String, required: false, default: 'hide' },
    isEditMode: { type: String, required: false, default: 'false' },
    addWrapper: { type: Boolean, required: false, default: true }
  },
  components: {
    FinderTemplate
  },
  setup (props) {
    const store = useStore()
    const editMode = toRef(props, 'isEditMode')
    store.$patch(testState) // set store's test state explicitly
    store.$state.editMode = editMode.value === 'true'
    return {}
  }
})
</script>
