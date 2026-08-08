import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useChatStore = defineStore('chat', () => {
  const messages = ref([])
  const isTyping = ref(false)
  const currentPlan = ref(null)

  const addMessage = (msg) => {
    messages.value.push({
      id: Date.now() + Math.random(),
      role: msg.role || 'user',
      content: msg.content || '',
      time: new Date().toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    })
  }

  const updateLastMessage = (content, isDone = false) => {
    if (messages.value.length > 0) {
      const lastMsg = messages.value[messages.value.length - 1]
      if (lastMsg.role === 'assistant') {
        lastMsg.content = content
      }
    }
  }

  const appendToLastMessage = (chunk) => {
    if (messages.value.length > 0) {
      const lastMsg = messages.value[messages.value.length - 1]
      if (lastMsg.role === 'assistant') {
        lastMsg.content += chunk
      }
    }
  }

  const clearMessages = () => {
    messages.value = []
  }

  const setCurrentPlan = (plan) => {
    currentPlan.value = plan
  }

  const clearCurrentPlan = () => {
    currentPlan.value = null
  }

  return {
    messages,
    isTyping,
    currentPlan,
    addMessage,
    updateLastMessage,
    appendToLastMessage,
    clearMessages,
    setCurrentPlan,
    clearCurrentPlan
  }
})
