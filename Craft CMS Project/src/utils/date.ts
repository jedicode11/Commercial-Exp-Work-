import { useStore } from '@/store'
/**
 * Converts utc string to locale date, otherwise.
 * @param {string} date - Date to convert to locale date
 * @returns Locale date
 */
function makeLocaleDate (date: string): Date {
  const dt = date[date.length - 1] === 'Z' ? date.substring(0, date.length - 1) : date
  return new Date(dt)
}

/**
 * Basic format function
 * @param {string} date - Date to format
 * @param {Record<string, unknown>} options - Options for toLocaleString
 * @returns {string} Formatted date string
 */
function format (date: string, options: Record<string, unknown>): string {
  if (!date) {
    return ''
  }

  const store = useStore()
  const dt = makeLocaleDate(date)
  return dt.toLocaleString(store.langCode, options)
}

/**
 * Formats the given date as dd. MMM
 * @param {string} date - Date to format
 * @returns {string} Formatted date string as dd. MMM, e.g. 14. Okt, 2. Mär
 */
export function formatDateShort (date: string): string {
  return format(date, { day: 'numeric', month: 'short' })
}

/**
 * Formats the given date as dd. MMM
 * @param {string} date - Date to format
 * @returns {string} Formatted date string as dd. MMM, e.g. 14. Okt, 2. Mär
 */
export function formatDateYearShort (date: string): string {
  return format(date, { day: 'numeric', month: 'short', year: '2-digit' })
}

/**
 * Formats the given date as HH:mm
 * @param {string} date - Date to format
 * @returns {string} Formatted time string as HH:mm, e.g. 23:15, 08:30
 */
export function formatTime (date: string): string {
  return format(date, { hour: '2-digit', minute: '2-digit' })
}

/**
 * Checks if dateToCheck is between dateFrom and dateTo.
 * @param {string} dateFrom - Start date
 * @param {string} dateTo - End date
 * @param {string} dateToCheck - Date to check for
 * @returns Returns true if dateToCheck is between dateFrom and dateTo, otherwise false.
 */
export function isBetween (dateFrom: string, dateTo: string, dateToCheck: string): boolean {
  if (!dateFrom || !dateTo || !dateToCheck) {
    return false
  }

  const dtFrom = makeLocaleDate(dateFrom)
  const dtTo = makeLocaleDate(dateTo)
  const dtToCheck = makeLocaleDate(dateToCheck)
  return dtFrom <= dtToCheck && dtToCheck < dtTo
}

export function convertDatetimeToDate (date = new Date(), utc = true): Date {
  // console.log('🚀 ~ file: date.ts:75 ~ convertDatetimeToDate ~ date', date)
  // // date.toISOString()
  // console.log('🚀 ~ file: date.ts:77 ~ convertDatetimeToDate ~ date.toISOString()', date.toISOString())
  return utc
    ? new Date(Date.UTC(
      date.getUTCFullYear(),
      date.getUTCMonth(),
      date.getUTCDate()
    ))
    : new Date(
      date.getFullYear(),
      date.getMonth(),
      date.getDate()
    )
}

// export function convertDatetimeToDEDate (date: Date): Date {
//   // console.log('🚀 ~ file: date.ts:75 ~ convertDatetimeToDate ~ date', date)
//   // // date.toISOString()
//   // console.log('🚀 ~ file: date.ts:77 ~ convertDatetimeToDate ~ date.toISOString()', date.toISOString())
//   const m = moment(date)
//   m.tz("Europe/Berlin")
//   m.startOf("day")
// }
