const wrapClass = 'msg-wrap'

function ensureWrap() {
  let wrap = document.querySelector(`.${wrapClass}`)
  if (!wrap) {
    wrap = document.createElement('div')
    wrap.className = wrapClass
    document.body.appendChild(wrap)
  }
  return wrap
}

function show(text, type) {
  if (!text) return
  const el = document.createElement('div')
  el.className = `msg-item msg-${type}`
  el.textContent = text
  ensureWrap().appendChild(el)
  setTimeout(() => {
    el.classList.add('out')
    setTimeout(() => el.remove(), 220)
  }, 2400)
}

export const message = {
  error: (text) => show(text, 'error'),
  success: (text) => show(text, 'success'),
  info: (text) => show(text, 'info'),
}
