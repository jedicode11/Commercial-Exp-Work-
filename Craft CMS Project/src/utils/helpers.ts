/* eslint-disable @typescript-eslint/no-explicit-any */

export const replaceObjKey = (arrayOfObj: any[], oldKey: string, newKey: string): any[] => {
  for (let i = 0; i < arrayOfObj.length; i++) {
    const o = arrayOfObj[i]
    o[newKey] = o[oldKey]
    delete o[oldKey]
  }
  return arrayOfObj
}

// eslint-disable-next-line @typescript-eslint/explicit-module-boundary-types
export const contentIsEmpty = (content: any): boolean => {
  let isEmpty = true
  const type = Object.prototype.toString.call(content)
  if (type === '[object String]') {
    isEmpty = isEmpty && content === ''
  } else if (type === '[object Array]') {
    content.forEach(function (item: any) {
      isEmpty = isEmpty && contentIsEmpty(item)
    })
    isEmpty = content.length === 0
  } else if (type === '[object Object]') {
    const keys = Object.keys(content)
    keys.forEach(key => {
      isEmpty = isEmpty && contentIsEmpty(content[key])
    })
  }
  return isEmpty
}

export const stripHTML = (value: string): string => {
  const div = document.createElement('div')
  div.innerHTML = value
  const text = div.textContent || div.innerText || ''
  return text
}

export const timeout = async (ms: number): Promise<void> => {
  return new Promise(resolve => setTimeout(resolve, ms))
}
