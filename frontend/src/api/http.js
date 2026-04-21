import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'
import { clearAuth, getRefreshToken, getToken, setAuth } from '../utils/auth'

const service = axios.create({
  baseURL: '/api/v1',
  timeout: 10000
})

let isRefreshing = false
let pendingRequests = []

service.interceptors.request.use((config) => {
  const token = getToken()
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

service.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求失败')
      return Promise.reject(new Error(res.message || 'Error'))
    }
    return res.data
  },
  async (error) => {
    const status = error.response?.status
    const originalRequest = error.config
    if (status === 401 && !originalRequest?.url?.includes('/auth/login') && !originalRequest?.url?.includes('/auth/refresh')) {
      const storedRefreshToken = getRefreshToken()
      if (!storedRefreshToken) {
        clearAuth()
        router.push('/login')
        ElMessage.error('登录已失效，请重新登录')
        return Promise.reject(error)
      }

      if (!isRefreshing) {
        isRefreshing = true
        try {
          const refreshResponse = await axios.post('/api/v1/auth/refresh', { refreshToken: storedRefreshToken })
          const refreshData = refreshResponse.data
          if (refreshData.code !== 200) {
            throw new Error(refreshData.message || '刷新令牌失败')
          }
          const data = refreshData.data
          setAuth(data)
          pendingRequests.forEach((callback) => callback(data.accessToken))
          pendingRequests = []
        } catch (refreshError) {
          clearAuth()
          pendingRequests = []
          router.push('/login')
          ElMessage.error('登录已失效，请重新登录')
          return Promise.reject(refreshError)
        } finally {
          isRefreshing = false
        }
      }

      return new Promise((resolve) => {
        pendingRequests.push((newAccessToken) => {
          originalRequest.headers.Authorization = `Bearer ${newAccessToken}`
          resolve(service(originalRequest))
        })
      })
    }
    if (status === 403) {
      ElMessage.error('当前账号没有操作权限')
      return Promise.reject(error)
    }
    ElMessage.error(error.message || '网络异常')
    return Promise.reject(error)
  }
)

export default service
