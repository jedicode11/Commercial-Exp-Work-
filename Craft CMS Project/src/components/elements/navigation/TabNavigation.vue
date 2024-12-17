<template>
  <div class="tab-navigation" :class="[
      'tab-navigation',
      {'tab-navigation--small':isSmall}
      ]">
    <nav class="tab-navigation-primary">
      <ul ref="navWrapperElement" v-dragscroll.x="!isExpandable">
        <li v-for="(item, index) in navItems" :key="item.id"
            :ref="el => { navItemElements[item.id] = {el, id:item.id, index} }">
          <tab-button :class="[
                        'tab-navigation-primary__option-btn',
                        {'tab-navigation-primary__option-btn--invisible': isExpandable && !item.isPrimary}
                      ]"
                      :aria-hidden="!item.isPrimary || null"
                      :is-small="isSmall"
                      :is-selected="closestAnchorSelector === item.selector"
                      @click="onNavItemSelect(item.id)">{{ item.title }}
          </tab-button>
        </li>
      </ul>
    </nav>
    <div ref="subMenuTriggerElement" v-if="isExpandable && secondaryItems.length > 0"
         :style="{left:`${primaryNavBoundsRight}px`}"
         class="tab-navigation__expandable-menu">
      <sub-menu :items="secondaryItems"
                @itemSelect="onNavItemSelect"
                :align="SubMenuAlign.left"
                :is-small="isSmall"
                :selected-id="closestAnchorId">
        <template #default="subMenuProps">
          <icon-button :has-background="true"
                       :icon-name="SvgIconName.moreHoriz"
                       :text="t('components_tab_navigation_more_bt')"
                       :is-small="isSmall"
                       :is-small-on-mobile="false"
                       :is-active="subMenuProps.isOpen"/>
        </template>
      </sub-menu>
    </div>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, ref, toRef } from 'vue'
import { PageSectionAnchor } from '@/models/base'
import { useI18n } from 'vue-i18n'
import TabButton from '@/components/elements/navigation/TabButton.vue'
import IconButton from '@/components/elements/IconButton.vue'
import { SvgIconName } from '@/components/helpers/SvgIcon.vue'
import { useExpandableNav } from '@/composables/useExpandableNav'
import { useScrollTargetIndicator } from '@/composables/useScrollTargetIndicator'
import { useElementBounding } from '@vueuse/core'
import SubMenu, { SubMenuAlign, SubMenuItem } from '@/components/elements/navigation/SubMenu.vue'

export interface TabNavigationItem extends PageSectionAnchor {
  isPrimary: boolean
}

export default defineComponent({
  name: 'TabNavigation',
  emits: ['tabNavigationSelect'],
  components: {
    SubMenu,
    TabButton,
    IconButton
  },
  props: {

    /** Nav items */
    items: {
      type: Array as () => Array<PageSectionAnchor>,
      required: true
    },

    /** Nav items out of viewport are shown in expandable submenu (no scroller) */
    isExpandable: {
      type: Boolean,
      required: false,
      default: false
    },

    /** Scroll area = root for scroll target indicator's IntersectionObserver */
    scrollArea: {
      type: HTMLElement,
      required: false
    },

    /** Set component size 'small' (small on mobile devices by default) */
    isSmall: {
      type: Boolean,
      required: false,
      default: false
    }
  },
  setup (props, { emit }) {
    const { t } = useI18n()
    const isExpandable = toRef(props, 'isExpandable')
    const scrollArea = toRef(props, 'scrollArea')
    const subMenuTriggerElement = ref()
    const { width: subMenuTriggerElementWidth } = useElementBounding(subMenuTriggerElement)

    const items = toRef(props, 'items')
    const navItems = computed(() => {
      return items.value.map((item: PageSectionAnchor) => {
        const navItem = Object.assign({}, item) as TabNavigationItem
        navItem.isPrimary = isPrimaryItem(navItem.id)
        return navItem
      })
    })
    const secondaryItems = computed(() => {
      return navItems.value.filter(item => !item.isPrimary)
        .map(({ id, title }) => {
          return Object.assign({}, { id, title }) as SubMenuItem
        })
    })
    const targetAnchorSelectors = computed(() => {
      return items.value.map((item) => item.selector)
    })
    const { closestSelector: closestAnchorSelector } = useScrollTargetIndicator(targetAnchorSelectors, scrollArea.value)
    const {
      navItemElements,
      navWrapperElement,
      isPrimaryItem,
      primaryNavBoundsRight
    } = useExpandableNav(isExpandable, subMenuTriggerElementWidth)

    const onNavItemSelect = (id: string) => {
      emit('tabNavigationSelect', id)
    }

    const closestAnchorId = computed(() => {
      const [closestAnchor] = items.value.filter((item: PageSectionAnchor) => {
        return closestAnchorSelector.value === item.selector
      })
      if (closestAnchor) {
        return closestAnchor.id
      }
      return ''
    })

    return {
      t,
      SvgIconName,
      SubMenuAlign,
      navWrapperElement,
      primaryNavBoundsRight,
      subMenuTriggerElement,
      subMenuTriggerElementWidth,
      closestAnchorSelector,
      closestAnchorId,
      secondaryItems,
      onNavItemSelect,
      navItemElements,
      navItems
    }
  }
})
</script>

<style lang="scss" scoped>

.tab-navigation {
  --tab-navigation-height: var(--finder-metabar-height);
  position: relative;

  &__expandable-menu {
    position: absolute;
    top: 0;
  }

  &--small {
    --tab-navigation-height: var(--finder-metabar-height-small);

    .tab-navigation-primary__option-btn {
      margin-right: 16px;
    }
  }
}

.tab-navigation-primary {

  width: 100%;
  position: relative;

  & > ul {
    width: 100%;
    position: absolute;
    margin: 0;
    padding: 0;
    display: flex;
    flex-wrap: nowrap;
    overflow: hidden;
    flex-grow: 1;

    & > li {
      margin: 0;
      padding: 0;
      display: inline-block;
      list-style: none;
      position: relative;

      &:last-of-type {
        > * {
          margin-right: 0;
        }
      }
    }
  }

  &__option-btn {
    margin-right: 44px;
  }

  &__option-btn--invisible {
    visibility: hidden;
    pointer-events: none;
  }
}

</style>
