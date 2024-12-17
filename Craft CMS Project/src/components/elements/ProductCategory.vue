<template>
  <div class="field-wrapper">
  </div>
  <cta-button v-if="showButton" @click="showModalCategories()">{{ buttonText }}</cta-button>
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue'
import { SvgIconName } from '@/components/helpers/SvgIcon.vue'
import CtaButton from '@/components/elements/CtaButton.vue'

export default defineComponent({
  name: 'CategoryButton',
  components: {
    CtaButton
  },
  props: {
    showIcon: Boolean,
    showButton: Boolean,
    buttonText: String,
    backgroundColor: String,
    color: String,
    text: {
      type: String as PropType<string | null>
    },
    allowedChars: {
      type: Array as PropType<Array<string>>,
      required: true,
      // default: () => ['[a-zA-Z0-9äöüÄÖÜßа-яА-Я]']
      default: () => ['[a-zA-Z0-9]']
    },
    placeholder: String
  },
  data () {
    return {
      value: '',
      SvgIconName,
      allowedCharsRE: new RegExp('.*')
    }
  },
  computed: {
    isValid() {
      return !this.value || this.allowedCharsRE.test(this.value)
    }
  },
  watch: {
    text(val) {
      this.value = val ?? ''
    },
    allowedChars(val) {
      this.allowedCharsRE = new RegExp(`^${val}*$`)
    }
  },
  // created () {

  // },
  mounted() {
    this.value = this.text ?? ''
    this.allowedCharsRE = new RegExp(`^${this.allowedChars}*$`)
  },
  methods: {
    clear() {
      this.value = ''
    },
    showModalCategories() {
      if (this.value && this.isValid) {
        this.$emit('category', this.value)
      }
    }
  }
})
</script>

  <style lang="scss" scoped>
  .field-wrapper {
    display: flex;
    gap: 16px;

    .field {
      flex-grow: 1;
      height: 3rem;
      border-radius: 1.5rem;
      line-height: 3rem;

      // box-shadow: rgba(0, 0, 0, 0.16) 0px 1px 4px;
      background-color: #ededed;
      border: 0px solid #ededed;
      padding: 4px 0.75rem;

      display: flex;
      align-items: center;

      input {
        width: 100%;
        height: 2.5rem;
        font-size: 1rem;
        margin: 0 4px;
        line-height: normal;

        border-style: hidden;
        background-color: transparent;

        &:focus {
          outline: none;
        }
      }
    }
  }
  </style>
