import { useCssVar } from '@vueuse/core'

export enum BreakpointNames {
  xs = 'xs',
  sm = 'sm',
  md = 'md',
  lg = 'lg',
  xl = 'xl',
  xxl = 'xxl'
}

// read breakpoint definition from css variables (defined at src/assets/styles/01-abstracts/_variables.scss)
export const getBreakpoints = (): Record<string, number> => {
  const breakpoints = {} as Record<string, number>
  Object.values<string>(BreakpointNames).forEach(function (name) {
    breakpoints[name] = parseInt(useCssVar('--finder-breakpoint-' + name, window.document.body).value, 10)
  })
  return breakpoints
}
