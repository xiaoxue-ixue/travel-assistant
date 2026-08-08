<template>
  <div class="chat-page">
    <van-nav-bar
      title="AI 旅游咨询"
      left-text="返回"
      left-arrow
      @click-left="$router.back()"
    >
      <template #right>
        <van-icon name="delete-o" size="20" @click="clearAll" />
      </template>
    </van-nav-bar>

    <div ref="messagesContainer" class="messages-container">
      <div v-if="messages.length === 0" class="welcome-section">
        <div class="welcome-icon">🤖</div>
        <h3 class="welcome-title">你好，我是AI旅游助手</h3>
        <p class="welcome-desc">有任何旅游相关的问题都可以问我哦～</p>

        <div class="quick-questions">
          <div class="qq-title">试试这些问题：</div>
          <div
            v-for="(q, index) in quickQuestions"
            :key="index"
            class="qq-item"
            @click="sendMessage(q)"
          >
            <span class="qq-icon">💬</span>
            <span class="qq-text">{{ q }}</span>
          </div>
        </div>
      </div>

      <ChatBubble
        v-for="msg in messages"
        :key="msg.id"
        :message="msg"
      />
      <ChatBubble
        v-if="isTyping"
        :message="{ role: 'assistant', content: '', time: '' }"
        :is-typing="true"
      />

      <div ref="bottomAnchor"></div>
    </div>

    <div v-if="messages.length > 0" class="suggestion-bar" v-show="!isTyping">
      <div class="suggestion-scroll">
        <span
          v-for="(s, i) in suggestions"
          :key="i"
          class="suggestion-tag"
          @click="sendMessage(s)"
        >
          {{ s }}
        </span>
      </div>
    </div>

    <div class="input-bar">
      <van-field
        v-model="inputText"
        class="message-input"
        placeholder="输入你的问题..."
        maxlength="500"
        :border="false"
        @keyup.enter="handleSend"
      />
      <van-button
        class="send-btn"
        type="primary"
        round
        size="small"
        :disabled="!inputText.trim() || isTyping"
        :loading="isTyping"
        loading-text="思考中"
        @click="handleSend"
      >
        发送
      </van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted, watch } from 'vue'
import { showDialog, showConfirmDialog } from 'vant'
import { useChatStore } from '@/stores/chat'
import { streamRequest } from '@/utils/request'
import ChatBubble from '@/components/ChatBubble.vue'

const chatStore = useChatStore()

const messagesContainer = ref(null)
const bottomAnchor = ref(null)
const inputText = ref('')

const messages = chatStore.messages
const isTyping = chatStore.isTyping

const quickQuestions = [
  '北京有哪些必去的景点？',
  '去三亚旅游要带什么？',
  '成都美食推荐',
  '西安3天旅游攻略'
]

const suggestions = [
  '推荐亲子游目的地',
  '预算2000去哪玩？',
  '情侣浪漫旅行地',
  '小众旅游城市推荐',
  '国庆旅游好去处'
]

const scrollToBottom = async () => {
  await nextTick()
  if (bottomAnchor.value) {
    bottomAnchor.value.scrollIntoView({ behavior: 'smooth' })
  }
}

watch(
  () => messages.value.length,
  () => scrollToBottom()
)

onMounted(() => {
  scrollToBottom()
})

const clearAll = async () => {
  try {
    await showConfirmDialog({
      title: '清空对话',
      message: '确定要清空所有对话记录吗？',
      confirmButtonText: '清空',
      cancelButtonText: '取消'
    })
    chatStore.clearMessages()
  } catch (e) {
    // cancelled
  }
}

const handleSend = () => {
  const text = inputText.value.trim()
  if (!text || isTyping.value) return
  sendMessage(text)
}

const sendMessage = async (text) => {
  if (isTyping.value) return

  const userMsg = text.trim()
  if (!userMsg) return

  inputText.value = ''
  chatStore.addMessage({ role: 'user', content: userMsg })
  chatStore.isTyping = true

  chatStore.addMessage({ role: 'assistant', content: '' })

  let fullContent = ''

  await streamRequest('/api/travel/chat/stream', { message: userMsg }, {
    onChunk: (chunk) => {
      fullContent += chunk
      chatStore.updateLastMessage(fullContent)
      scrollToBottom()
    },
    onDone: () => {
      chatStore.isTyping = false
      scrollToBottom()
    },
    onError: (error) => {
      chatStore.isTyping = false
      if (!fullContent) {
        chatStore.updateLastMessage(`抱歉，出错了：${error || '未知错误'}`)
      }
      showDialog({
        title: '请求失败',
        message: error || '请稍后重试',
        showCancelButton: false
      })
    }
  })
}
</script>

<style scoped>
.chat-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  height: 100dvh;
  background: #f5f7fa;
}

.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 16px 14px calc(env(safe-area-inset-bottom, 0) + 16px);
  -webkit-overflow-scrolling: touch;
}

.welcome-section {
  text-align: center;
  padding: 30px 10px 20px;
}

.welcome-icon {
  font-size: 60px;
  margin-bottom: 16px;
  animation: wave 3s ease-in-out infinite;
}

@keyframes wave {
  0%, 100% { transform: rotate(0); }
  20% { transform: rotate(-10deg); }
  40% { transform: rotate(10deg); }
  60% { transform: rotate(-5deg); }
  80% { transform: rotate(5deg); }
}

.welcome-title {
  font-size: 18px;
  font-weight: 600;
  color: #323233;
  margin-bottom: 6px;
}

.welcome-desc {
  font-size: 14px;
  color: #969799;
  margin-bottom: 24px;
}

.quick-questions {
  text-align: left;
  max-width: 360px;
  margin: 0 auto;
}

.qq-title {
  font-size: 13px;
  color: #646566;
  margin-bottom: 12px;
  padding-left: 4px;
}

.qq-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 13px 14px;
  background: #fff;
  border-radius: 12px;
  margin-bottom: 10px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
  cursor: pointer;
  transition: all 0.2s;
}

.qq-item:active {
  transform: scale(0.98);
  background: #e8f3ff;
}

.qq-icon {
  font-size: 18px;
}

.qq-text {
  flex: 1;
  font-size: 14px;
  color: #323233;
}

.suggestion-bar {
  padding: 0 12px 8px;
  background: transparent;
}

.suggestion-scroll {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding: 4px 2px;
  scrollbar-width: none;
}

.suggestion-scroll::-webkit-scrollbar {
  display: none;
}

.suggestion-tag {
  flex-shrink: 0;
  padding: 7px 14px;
  background: #fff;
  border-radius: 18px;
  font-size: 13px;
  color: #1989fa;
  border: 1px solid #d4ebff;
  white-space: nowrap;
  cursor: pointer;
}

.suggestion-tag:active {
  background: #e8f3ff;
}

.input-bar {
  display: flex;
  align-items: flex-end;
  gap: 10px;
  padding: 10px 12px calc(env(safe-area-inset-bottom, 0) + 10px);
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(10px);
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
  border-top: 1px solid #ebedf0;
}

.message-input {
  flex: 1;
  background: #f2f3f5;
  border-radius: 20px;
  padding: 8px 14px;
  min-height: 40px;
}

.message-input :deep(.van-field__control) {
  font-size: 15px;
}

.send-btn {
  height: 40px;
  padding: 0 20px;
  flex-shrink: 0;
  background: linear-gradient(135deg, #1989fa, #5ac8fa);
  border: none;
}

.send-btn:disabled {
  opacity: 0.5;
}
</style>
