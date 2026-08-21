export const saveSession = ({ token, user }) => {
  localStorage.setItem('token', token)
  localStorage.setItem('user', JSON.stringify(user))
}

export const saveUser = (user) => {
  localStorage.setItem('user', JSON.stringify(user))
}

export const clearSession = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
}

export const getUser = () => {
  try {
    return JSON.parse(localStorage.getItem('user') || 'null')
  } catch {
    return null
  }
}

export const isLoggedIn = () => Boolean(localStorage.getItem('token'))
