import axios from 'axios'
import { showToast, showLoadingToast, closeToast } from 'vant'

const baseURL = import.meta.env.VITE_API_BASE_URL || ''

const request = axios.create({
  baseURL,
  timeout: 180000,
  headers: {
    'Content-Type': 'application/json'
  }
})

let loadingCount = 0

const showLoading = (msg = '加载中...') => {
  if (loadingCount === 0) {
    showLoadingToast({
      message: msg,
      forbidClick: true,
      loadingType: 'spinner'
    })
  }
  loadingCount++
}

const hideLoading = () => {
  loadingCount--
  if (loadingCount <= 0) {
    loadingCount = 0
    closeToast()
  }
}

request.interceptors.request.use(
  (config) => {
    if (config.showLoading !== false) {
      showLoading(config.loadingText || '加载中...')
    }
    return config
  },
  (error) => {
    hideLoading()
    return Promise.reject(error)
  }
)

request.interceptors.response.use(
  (response) => {
    hideLoading()
    const res = response.data
    if (res && res.code !== undefined && res.code !== 200) {
      showToast(res.message || '请求失败')
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    return res
  },
  (error) => {
    hideLoading()
    let message = '网络请求失败'
    if (error.response) {
      const status = error.response.status
      if (status === 400) {
        message = error.response.data?.message || '参数错误'
      } else if (status === 401) {
        message = '未授权，请重新登录'
      } else if (status === 404) {
        message = '请求的资源不存在'
      } else if (status === 500) {
        message = error.response.data?.message || '服务器内部错误'
      } else {
        message = `请求错误 (${status})`
      }
    } else if (error.code === 'ECONNABORTED') {
      message = '请求超时，请稍后重试'
    } else if (error.message === 'Network Error') {
      message = '网络连接失败，请检查网络'
    }
    showToast(message)
    return Promise.reject(error)
  }
)

export const streamRequest = async (url, data, callbacks) => {
  const { onChunk, onDone, onError } = callbacks
  const fullUrl = url.startsWith('http') ? url : baseURL + url

  try {
    const response = await fetch(fullUrl, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(data)
    })

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    const reader = response.body.getReader()
    const decoder = new TextDecoder('utf-8')
    let buffer = ''

    while (true) {
      const { done, value } = await reader.read()
      if (done) break

      buffer += decoder.decode(value, { stream: true })
      const lines = buffer.split('\n')
      buffer = lines.pop() || ''

      for (const line of lines) {
        const trimmed = line.trim()
        if (trimmed.startsWith('data:')) {
          const dataStr = trimmed.substring(5).trim()
          if (!dataStr) continue
          try {
            const parsed = JSON.parse(dataStr)
            if (parsed.type === 'chunk') {
              onChunk?.(parsed.content)
            } else if (parsed.type === 'done') {
              onDone?.()
            } else if (parsed.type === 'error') {
              onError?.(parsed.error || '未知错误')
            }
          } catch (e) {
            console.warn('解析SSE数据失败:', dataStr, e)
          }
        }
      }
    }
    onDone?.()
  } catch (error) {
    onError?.(error.message || '流式请求失败')
  }
}

export default request
