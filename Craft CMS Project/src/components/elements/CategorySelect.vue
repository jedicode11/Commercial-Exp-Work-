<template>
  <div>
    <div class="checkbox-form">
      <div class="items">
        <div class="item" v-for="(category, i) of categories" :key="i" :x-key="category.id">
          <CheckBox :option="category.title" x-v-model="category" :checked="isCategoryChecked(category.id)"
            @change="handleSelectionChange($event, category)"></CheckBox>
          <div class="sub-items">
            <CategorySelectComponent
              v-if="!isCategoryChecked(category.id) && category.children && category.children.length > 0"
              :categories="category.children" :checkedCategories="checkedCategories"
              y-selectionChange="handleCategoryChildrenSelectionChange($event, category)"
              @change="handleChildSelectionChange($event, category)"></CategorySelectComponent>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { EventCategoryModel } from '@/models/EventCategoryModel'
import { defineComponent, PropType } from 'vue'
import CheckBox from './checkbox/CheckBox.vue'

export default defineComponent({
  name: 'CategorySelectComponent',
  emits: ['change', 'selectionChange'],
  components: {
    CheckBox
  },
  props: {
    categories: {
      type: Array as PropType<Array<EventCategoryModel>>,
      required: false,
      default: () => []
    },
    checkedCategories: {
      type: Array as PropType<Array<number | string>>,
      required: false,
      default: () => []
    }
  },
  data() {
    return {
      selectedCategories: [] as Array<EventCategoryModel>,
      selectedChildrenCategories: {} as Record<number | string, Array<number | string>>
    }
  },
  watch: {
    checkedCategories(val) {
      this.parseCheckedCategories(val)
    }
  },
  methods: {
    handleSelectionChange(checked: boolean, category: EventCategoryModel) {
      // console.log('🚀 ~ handleSelectionChange ~ category, checked:', category, checked)
      // if (checked) {
      //   this.selectedCategories.push(category)
      //   this.selectedChildrenCategories[category.id] = []
      // } else {
      //   this.selectedCategories = this.selectedCategories.filter(cat => cat.id !== category.id)
      // }
      // this.emitSelected()
      this.emitChange(checked, category)
    },
    handleCategoryChildrenSelectionChange(checkedChildren: Array<number | string>, category: EventCategoryModel) {
      // console.log('🚀 ~ handleCategoryChildrenSelectionChange ~ category, checkedChildren:', category, checkedChildren)
      this.selectedChildrenCategories[category.id] = checkedChildren
      this.emitSelected()
    },
    handleChildSelectionChange({ checked, category }: { checked: boolean, category: EventCategoryModel }, parentCategory: EventCategoryModel) {
      // if (checked) {
      //   this.selectedChildrenCategories[parentCategory.id].push(category.id)
      // } else {
      //   this.selectedChildrenCategories[parentCategory.id] = this.selectedChildrenCategories[parentCategory.id].filter(catId => catId !== category.id)
      // }
      // this.emitSelected()
      this.emitChange(checked, category)
    },
    isCategoryChecked(categoryId: number | string) {
      return this.selectedCategories.map(cat => cat.id).includes(categoryId)
    },
    emitSelected() {
      const selectedChildrenCategories = Object.keys(this.selectedChildrenCategories).map(catId => this.selectedChildrenCategories[catId]).flat()
      // console.log('🚀 ~ emitSelected ~ this.selectedCategories, selectedChildrenCategories:', this.selectedCategories.map(cat => cat.id), selectedChildrenCategories)
      this.$emit('selectionChange', Array.from(new Set(this.selectedCategories.map(cat => cat.id).concat(selectedChildrenCategories))))
    },
    emitChange(checked: boolean, category: EventCategoryModel) {
      this.$emit('change', { checked, category })
    },
    parseCheckedCategories(checkedCategories: Array<number | string>) {
      this.selectedCategories = this.categories.filter(cat => checkedCategories.includes(cat.id))
      this.categories.filter(cat => cat.children && cat.children.length > 0).forEach(cat => {
        this.selectedChildrenCategories[cat.id] = cat.children.map(cat => cat.id).filter(catId => checkedCategories.includes(catId))
      })
    }
  },
  mounted() {
    this.parseCheckedCategories(this.checkedCategories)
  }
})
</script>

<style lang="scss" scoped>
.checkbox-form {
  display: flex;
  flex-direction: column;
  align-items: center;

  .items {
    display: flex;
    flex-direction: column;
    align-items: left;
    width: 100%;
  }

  .sub-items {
    margin-left: 30px;
  }
}
</style>
