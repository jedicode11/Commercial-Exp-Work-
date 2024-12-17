import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'

import Slider from '@/components/elements/Slider.vue'
import SpeakerOverview from '@/components/elements/SpeakerOverview.vue'
import {
  EventsView,
  EventDetails,
  SearchView,
  SpeakerDetails,
  SessionDetails
} from './../components'

export {}

declare module 'vue-router' {
  interface RouteMeta {
    requiresAuth: boolean
    roles?: string[]
  }
}

const routes: Array<RouteRecordRaw> = [
  {
    path: '/events',
    name: 'events',
    component: Slider
  },
  {
    path: '/speakers/:text?',
    name: 'speakers',
    component: SpeakerOverview
  },
  {
    path: '/',
    name: 'home',
    component: EventsView
  },
  {
    path: '/details/:uri',
    name: 'details',
    component: EventDetails
  },
  {
    path: '/speaker/:uri',
    name: 'speaker',
    component: SpeakerDetails
  },
  {
    path: '/session/:uri',
    name: 'session',
    component: SessionDetails
  },
  {
    path: '/search/:text?',
    name: 'search',
    component: SearchView
  }
  // {
  //   path: '/about',
  //   name: 'about',
  //   // route level code-splitting
  //   // this generates a separate chunk (about.[hash].js) for this route
  //   // which is lazy-loaded when the route is visited.
  //   component: () => import(/* webpackChunkName: "about" */ '../views/AboutView.vue')
  // }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

export default router
