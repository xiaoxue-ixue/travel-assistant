<template>
  <div class="chat-bubble" :class="roleClass">
    <div class="avatar">
      <span v-if="message.role === 'user'">🙂</span>
      <span v-else>🤖</span>
    </div>
    <div class="bubble-wrap">
      <div class="bubble-content">
        <p v-if="!isTyping && message.content">{{ formattedContent }}</p>
        <p v-if="isTyping" class="typing-dots">
          <span></span><span></span><span></span>
        </p>
      </div>
      <div class="bubble-time">{{ message.time }}</div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  message: {
    type: Object,
    required: true
  },
  isTyping: {
    type: Boolean,
    default: false
  }
})

const roleClass = computed(() => ({
  'is-user': props.message.role === 'user',
  'is-assistant': props.message.role !== 'user'
}))

const formattedContent = computed(() => {
  return props.message.content || ''
})
</script>

<style scoped>
.chat-bubble {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
  align-items: flex-start;
}

.chat-bubble.is-user {
  flex-direction: row-reverse;
}

.avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.is-assistant .avatar {
  background: linear-gradient(135deg, #1989fa 0%, #5ac8fa 100%);
  box-shadow: 0 2px 8px rgba(25, 137, 250, 0.3);
}

.is-user .avatar {
  background: linear-gradient(135deg, #07c160 0%, #5ed097 100%);
  box-shadow: 0 2px 8px rgba(7, 193, 96, 0.3);
}

.bubble-wrap {
  max-width: 75%;
  display: flex;
  flex-direction: column;
}

.is-user .bubble-wrap {
  align-items: flex-end;
}

.bubble-content {
  padding: 12px 14px;
  border-radius: 14px;
  font-size: 15px;
  line-height: 1.65;
  word-break: break-word;
  white-space: pre-wrap;
}

.is-assistant .bubble-content {
  background: #fff;
  color: #323233;
  border-top-left-radius: 4px;
  box-shadow: 0 1px 6px rgba(0, 0, 0, 0.06);
}

.is-user .bubble-content {
  background: linear-gradient(135deg, #1989fa 0%, #5ac8fa 100%);
  color: #fff;
  border-top-right-radius: 4px;
  box-shadow: 0 2px 8px rgba(25, 137, 250, 0.25);
}

.is-assistant .bubble-content p {
  margin: 0;
}

.is-user .bubble-content p {
  margin: 0;
}

.bubble-time {
  font-size: 11px;
  color: #c8c9cc;
  margin-top: 4px;
  padding: 0 4px;
}

.typing-dots {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  height: 20px;
  margin: 0;
}

.typing-dots span {
  width: 6px;
  height: 6px;
  background: #969799;
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out;
}

.typing-dots span:nth-child(1) {
  animation-delay: 0s;
}

.typing-dots span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-dots span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
    opacity: 0.4;
  }
  30% {
    transform: translateY(-4px);
    opacity: 1;
  }
}
</style>
