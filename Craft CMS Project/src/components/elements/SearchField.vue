<template>
  <div class="search-field">
    <h4 v-if="title" class="search-field-title">{{ title }}</h4>
    <div class="field-wrapper">
      <div class="field" :style="{ backgroundColor, color }">
        <!-- <svg-icon v-if="showIcon && !iconAsSuffix" :icon-name="SvgIconName.search" /> -->
        <input :placeholder="placeholder" v-model="value" @keypress.enter="emitSearch()" />
        <a v-if="value" class="icon-button clear" @click="clear()">
          <svg-icon :is-small="true" :icon-name="SvgIconName.close" />
        </a>
        <a class="icon-button search" :disabled="!value" @click="emitSearch()">
          <svg-icon v-if="showIcon && iconAsSuffix" :is-small="true" :icon-name="SvgIconName.search" />
        </a>
      </div>
      <cta-button class="button-search" :icon-name="SvgIconName.search" :isFullWidth="true" :isDisabled="!isValid" @click="emitSearch()"></cta-button>
    </div>
    <div class="field-error" v-if="!isValid">{{ $t('components_events_search_error') }}</div>
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue'
import SvgIcon, { SvgIconName } from '@/components/helpers/SvgIcon.vue'
import CtaButton from '@/components/elements/CtaButton.vue'

export default defineComponent({
  name: 'SearchFieldComponent',
  components: {
    SvgIcon,
    CtaButton
  },
  props: {
    title: String,
    showIcon: Boolean,
    showButton: Boolean,
    buttonText: String,
    iconAsSuffix: Boolean,
    backgroundColor: String,
    color: String,
    text: {
      type: String as PropType<string | null>
    },
    allowedChars: {
      type: Array as PropType<Array<string>>,
      required: false,
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
    isValid () {
      // return !this.value || this.allowedCharsRE.test(this.value)
      return true
    }
  },
  watch: {
    text (val) {
      this.value = val ?? ''
    },
    allowedChars (val) {
      this.allowedCharsRE = new RegExp(`^${val}*$`)
    }
  },
  // created () {

  // },
  mounted () {
    this.value = this.text ?? ''
    this.allowedCharsRE = new RegExp(`^${this.allowedChars}*$`)
  },
  methods: {
    clear () {
      this.value = ''
    },
    emitSearch () {
      if (this.isValid) {
        this.$emit('search', this.value)
      }
    }
  }
})
</script>

<style lang="scss" scoped>
.search-field {
  @media (max-width: 480px) {
    display: flex;
    flex-direction: column;
    margin-bottom: 20px;
  }
  background: #1818180d;
  border-radius: 2mm;
  padding: 20px;

  .search-field-title {
    font-size: 1rem;
    margin-bottom: 0.5rem;
    font-weight: 900;
    height: 1.5rem;
    display: block;
  }

  .button-search {
    display: none;
    @media (max-width: 480px) {
      display: block;
      margin-top: 0px;
      width: 3rem;
      height: 3rem;
      align-items: center;
      justify-content: center;
      color: #ffffff;
    }
  }

  .field-wrapper {
    display: flex;

    @media (max-width: 480px) {
      display: flex;
      gap: 16px;
      padding: -4px;
      margin: 0px;
    }

    gap: 16px;

    .field {
      flex-grow: 1;
      height: 3rem;
      border-radius: 6px;
      line-height: 3rem;

      background-color: #ffffff;
      border: 1px solid lightgrey;
      padding: 4px 0.75rem 4px 0;

      display: flex;
      align-items: center;

      @media (max-width: 480px) {
        font-size: 0.875rem;
      }

      @media (min-width: 481px) {
        height: 3rem;
        font-size: 1rem;
      }

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

      &:hover,
      &:has(input:focus) {
        border-color: #8b8a8a;
      }

      .icon-button {
        flex-basis: 25px;
        cursor: pointer;
        width: 24px;
        height: 24px;
        line-height: normal;
        color: #8b8b8b;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 4px;
        @media (max-width: 480px) {
          display: none;
        }

        &[disabled='true'] {
          cursor: default;
        }

        &:not([disabled='true']):hover {
          background-color: #e8e8e8;
        }
      }

      .icon-button + .icon-button {
        margin-inline-start: 4px;
      }
    }
  }

  .field-error {
    padding: 2px 8px;
    color: #861531;
    color: var(--finder-color-error);
    background: #f0e1e5;
    background: var(--finder-color-error-background);
    margin: 8px 0px;
    border-radius: 4px;
    border: 1px solid #debfc7;
    border: 1px solid var(--finder-color-error-disabled);
    font-size: smaller;
  }
}
</style>
