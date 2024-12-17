<template>
  <label>
    <span :for="option">{{ option }}</span>
    <input
      :x-id="option"
      v-model="isChecked"
      type="checkbox"
      :value="option"
      @change="onChange()"
    >
    <span class="checkmark" />
  </label>
</template>

<script lang="ts">
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'CheckBox',
  emits: ['change'],
  props: {
    option: {
      type: String,
      required: false,
      default: () => ''
    },
    checked: {
      type: Boolean,
      required: false,
      default: () => false
    }
  },
  data () {
    return {
      isChecked: false
    }
  },
  watch: {
    checked (val) {
      this.parseChecked(val)
    }
  },
  methods: {
    onChange () {
      this.$emit('change', this.isChecked)
    },
    parseChecked (chedked: boolean) {
      // console.log('🚀 ~ parseChecked ~ chedked:', chedked)
      this.isChecked = chedked
    }
  },
  mounted () {
    this.parseChecked(this.checked)
  }
})
</script>

<style lang="scss" scoped>
/* stylelint-disable */
label {
  display: block;
  position: relative;
  padding-left: 25px;
  /* margin-bottom: 12px; */
  margin: 3px 2px;
  cursor: pointer;
  font-size: 1em;
  /* height: 20px; */
  line-height: 1.1em;
  -webkit-user-select: none;
  -moz-user-select: none;
  -ms-user-select: none;
  user-select: none;
  display: flex;
  align-items: center;

  input {
    position: absolute;
    opacity: 0;
    cursor: pointer;
    height: 0;
    width: 0;
  }

  .checkmark {
    position: absolute;
    top: 0;
    left: 0;
    height: 20px;
    width: 20px;
    background-color: var(--finder-color-primary-5);
    border: 1px solid #949494;
  }

  &:hover input ~ .checkmark {
    background-color: #EDECEC;
    border: 1px solid var(--finder-color-primary);
  }

  & input:checked ~ .checkmark {
    background-color: var(--finder-color-primary);
  }

  .checkmark:after {
    content: "";
    position: absolute;
    display: none;
  }

  & input:checked ~ .checkmark:after {
    display: block;
  }

  & .checkmark:after {
    left: 7px;
    top: 3px;
    width: 5px;
    height: 10px;
    border: solid white;
    border-width: 0 3px 3px 0;
    -webkit-transform: rotate(45deg);
    -ms-transform: rotate(45deg);
    transform: rotate(45deg);
  }
}
</style>
