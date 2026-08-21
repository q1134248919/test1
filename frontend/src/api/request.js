import axios from 'axios'
import { message } from '../utils/message'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || 'http://39.107.33.81:8080/api',
  timeout: 10000,
})

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

request.interceptors.response.use(
  (res) => {
    const body = res.data
    if (body.code !== 200) {
      const msg = body.message || '请求失败'
      message.error(msg)
      return Promise.reject(new Error(msg))
    }
    return body
  },
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      if (!window.location.pathname.startsWith('/login')) {
        window.location.href = '/login'
      }
    }
    const msg = err.response?.data?.message || err.message || '网络错误'
    message.error(msg)
    return Promise.reject(new Error(msg))
  },
)

export default request
