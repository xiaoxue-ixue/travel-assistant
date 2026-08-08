<template>
  <div class="bubble-row" :class="[role]">
    <div v-if="role === 'ai'" class="avatar avatar-ai">
      <span class="avatar-ring"><span class="avt-ic">🤖</span></span>
    </div>
    <div class="bubble" :class="{ loading: isStreaming && !content }">
      <div v-if="isStreaming && !content" class="dots">
        <span></span><span></span><span></span>
      </div>
      <div v-else class="bubble-text">{{ content }}</div>
      <div v-if="role === 'ai' && !isStreaming" class="bubble-actions">
        <span title="复制" @click="onCopy">📋 复制</span>
        <span title="赞">👍</span>
      </div>
    </div>
    <div v-if="role === 'user'" class="avatar avatar-user">
      <span class="avt-ic">🧑</span>
    </div>
  </div>
</template>

<script setup>
import { showToast } from 'vant'
defineProps({
  role:    { type: String, default: 'ai' },
  content: { type: String, default: '' },
  isStreaming: Boolean
})
const onCopy = async () => {
  try {
    await navigator.clipboard.writeText(arguments[0]?.content || '')
    showToast('已复制 ✨')
  } catch {}
}
</script>

<style scoped>
.bubble-row {
  display: flex;
  align-items: flex-end;
  gap: 8px;
  margin-bottom: 14px;
}
.bubble-row.user { flex-direction: row-reverse; }

.avatar { width: 32px; height: 32px; flex-shrink: 0; }
.avatar-ai .avatar-ring {
  width: 100%; height: 100%;
  border-radius: 50%;
  padding: 2px;
  background: conic-gradient(#FF6F91, #FFC75F, #84FAB0, #4DD0E1, #845EC2, #FF6F91);
  animation: spin 5s linear infinite;
  display: block;
}
@keyframes spin { to { transform: rotate(360deg); } }
.avt-ic {
  width: 100%; height: 100%;
  border-radius: 50%;
  background: #fff;
  display: flex; align-items: center; justify-content: center;
  font-size: 17px;
}
.avatar-user .avt-ic {
  background: linear-gradient(135deg, #FFF4F6, #F0F7FF);
  box-shadow: 0 4px 10px rgba(132, 94, 194, 0.22);
}

.bubble {
  max-width: 76%;
  padding: 10px 14px;
  border-radius: 18px;
  font-size: 14px;
  line-height: 1.65;
  word-break: break-word;
  white-space: pre-wrap;
  box-shadow: 0 6px 16px rgba(0,0,0,0.08);
  position: relative;
}
.bubble-row.ai .bubble {
  background: linear-gradient(160deg, #fff 0%, #F4F8FF 100%);
  color: #2c3e50;
  border-top-left-radius: 6px;
  border: 1px solid #ecf0fb;
}
.bubble-row.user .bubble {
  background: linear-gradient(120deg, #FF6F91 0%, #FF9671 40%, #FFC75F 100%);
  background-size: 200% 200%;
  animation: shift 6s ease infinite;
  color: #fff;
  border-top-right-radius: 6px;
  box-shadow: 0 10px 22px rgba(255, 111, 145, 0.3);
}
@keyframes shift {
  0%, 100% { background-position: 0 50%; }
  50%      { background-position: 100% 50%; }
}

.dots {
  display: flex; gap: 4px; padding: 4px 0;
}
.dots span {
  width: 8px; height: 8px; border-radius: 50%;
  animation: bounce 1.2s ease-in-out infinite;
}
.dots span:nth-child(1) { background: linear-gradient(135deg, #FF6F91, #FFC75F); }
.dots span:nth-child(2) { background: linear-gradient(135deg, #FFC75F, #84FAB0); animation-delay: .15s; }
.dots span:nth-child(3) { background: linear-gradient(135deg, #4DD0E1, #845EC2); animation-delay: .3s; }
@keyframes bounce {
  0%, 80%, 100% { transform: translateY(0) scale(0.8); opacity: 0.6; }
  40%           { transform: translateY(-8px) scale(1.1); opacity: 1; }
}

.bubble-actions {
  margin-top: 8px;
  display: flex; gap: 10px;
  font-size: 11px;
  color: #8a8f99;
  opacity: 0.7;
  transition: opacity .2s;
}
.bubble:hover .bubble-actions { opacity: 1; }
.bubble-actions span { cursor: pointer; user-select: none; }
.bubble-actions span:hover { color: #FF6F91; }
</style>