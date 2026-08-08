<template>
  <div class="chat-page page-container">
    <div class="chat-header">
      <div class="hdr-bg"></div>
      <div class="hdr-row">
        <van-icon
          name="arrow-left"
          size="22"
          class="hdr-back"
          @click="$router.back()"
        />
        <div class="hdr-title">
          <div class="hdr-bot">
            <span class="avatar-ring">
              <span class="avatar-inner">🤖</span>
              <span class="avatar-dot"></span>
            </span>
            <div class="hdr-text">
              <div class="hdr-name">AI 旅行顾问</div>
              <div class="hdr-status">
                <em class="stat-live"></em>在线 · 随时为您规划
              </div>
            </div>
          </div>
        </div>
        <van-icon
          name="delete-o"
          size="20"
          class="hdr-clear"
          @click="clearChat"
        />
      </div>
    </div>

    <div class="msg-scroll" ref="scrollRef">
      <div v-if="messages.length === 0" class="welcome-wrap">
        <div class="welcome-card pop-in d1">
          <div class="welcome-hero">
            <div class="wh-ic">✨</div>
            <h2>你好！我是你的 AI 旅行小助手</h2>
            <p>随时问我关于旅游的任何问题，我都能帮你解答～</p>
          </div>

          <div class="quick-titles">🔥 快速提问</div>
          <div class="quick-grid">
            <div
              v-for="(q,i) in quickQuestions"
              :key="q.text"
              :class="['quick-btn pop-in', 'd' + ((i%6)+1)]"
              :style="qStyle(i)"
              @click="sendQuick(q.text)"
            >
              <span class="qb-ic">{{ q.ic }}</span>
              <span class="qb-txt">{{ q.text }}</span>
            </div>
          </div>

          <div class="quick-titles mt20">💡 使用提示</div>
          <ul class="hint-list">
            <li>可以让我帮你做「3 天成都 2000 元预算」规划</li>
            <li>可以问我「三亚冬天能下海吗？」</li>
            <li>还可以让我推荐某个城市的美食和避坑</li>
          </ul>
        </div>
      </div>

      <div v-for="(m, i) in messages" :key="i" class="msg-row" :class="m.role">
        <div v-if="m.role === 'ai'" class="avatar-sm">
          <span class="avatar-inner sm">🤖</span>
        </div>
        <div class="bubble" :class="{ streaming: m.streaming }">
          <div v-if="m.role === 'ai' && !m.content" class="thinking">
            <span class="dot"></span>
            <span class="dot d2"></span>
            <span class="dot d3"></span>
          </div>
          <div v-else class="bubble-content">{{ m.content }}</div>
        </div>
        <div v-if="m.role === 'user'" class="avatar-sm user">
          <span class="avatar-inner sm u">🧑</span>
        </div>
      </div>
    </div>

    <div class="chat-footer">
      <div class="quick-chips">
        <span
          v-for="c in chips"
          :key="c.t"
          class="chip"
          :style="chipStyle(c.c)"
          @click="appendChip(c.t)"
        >{{ c.ic }} {{ c.t }}</span>
      </div>

      <div class="composer">
        <div
          class="input-wrap"
          :class="{ focused: isFocused }"
        >
          <span class="input-glow"></span>
          <van-field
            v-model="inputText"
            :border="false"
            placeholder="问我任何旅游问题吧～"
            class="msg-input"
            @focus="isFocused = true"
            @blur="isFocused = false"
            @keydown.enter.exact="handleSend"
          />
          <van-icon
            v-if="inputText"
            name="clear"
            size="18"
            class="clear-input"
            @click="inputText = ''"
          />
        </div>
        <van-button
          round
          type="primary"
          class="send-btn"
          :loading="sending"
          :disabled="!inputText.trim() && !sending"
          @click="handleSend"
        >
          <van-icon name="send-o" size="16" />
        </van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick } from 'vue'
import { showConfirmDialog, showToast } from 'vant'
import { useChatStore } from '@/stores/chat'

const chatStore = useChatStore()
const messages = chatStore.messages

const scrollRef = ref(null)
const inputText = ref('')
const isFocused = ref(false)
const sending = ref(false)

const quickQuestions = [
  { ic: '🗺️', text: '推荐一个适合 3 天短途旅行的城市' },
  { ic: '🍜', text: '成都有哪些必吃的美食？' },
  { ic: '🏖️', text: '三亚 5 天亲子游怎么安排？' },
  { ic: '💰', text: '学生党 1000 元穷游攻略' },
  { ic: '🏛️', text: '西安 4 天预算 3000 元行程' },
  { ic: '🧳', text: '第一次出国游需要准备什么？' }
]

const chips = [
  { ic: '📍', t: '推荐目的地', c: ['#FF6F91', '#FFC75F'] },
  { ic: '🍴', t: '当地美食',   c: ['#FF9671', '#FF7F7F'] },
  { ic: '🏨', t: '住宿推荐',   c: ['#845EC2', '#C398E1'] },
  { ic: '🚆', t: '交通路线',   c: ['#4DD0E1', '#5E81F4'] },
  { ic: '🌤️', t: '天气穿衣',   c: ['#00C9A7', '#84FAB0'] },
  { ic: '🎟️', t: '景点门票',   c: ['#FFC75F', '#F9F871'] }
]

const qStyle = (i) => {
  const arr = [
    ['#FF6F91', '#FFC75F'],
    ['#84FAB0', '#00C9A7'],
    ['#4DD0E1', '#5E81F4'],
    ['#C398E1', '#845EC2'],
    ['#FF9671', '#FF6F91'],
    ['#FFC75F', '#FF9671']
  ]
  const [a, b] = arr[i % arr.length]
  return { '--c1': a, '--c2': b }
}
const chipStyle = (arr) => ({ '--c1': arr[0], '--c2': arr[1] })

const scrollBottom = async () => {
  await nextTick()
  const el = scrollRef.value
  if (el) el.scrollTop = el.scrollHeight
}

watch(messages, () => scrollBottom(), { deep: true })
onMounted(scrollBottom)

const appendChip = (t) => {
  inputText.value = inputText.value ? (inputText.value + '，' + t) : t
}

const sendQuick = (t) => {
  inputText.value = t
  handleSend()
}

const handleSend = async () => {
  const text = inputText.value.trim()
  if (!text || sending.value) return
  sending.value = true
  inputText.value = ''

  chatStore.pushMessage({ role: 'user', content: text })
  const aiMsg = chatStore.pushMessage({ role: 'ai', content: '', streaming: true })

  try {
    const resp = await fetch(`${import.meta.env.VITE_API_BASE_URL || ''}/api/chat/stream`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ message: text, history: messages.value.slice(0, -1).map(m => ({ role: m.role, content: m.content })) })
    })

    if (!resp.ok || !resp.body) throw new Error('API 无响应')
    const reader = resp.body.getReader()
    const decoder = new TextDecoder('utf-8')
    let buf = ''
    let full = ''

    while (true) {
      const { value, done } = await reader.read()
      if (done) break
      buf += decoder.decode(value, { stream: true })
      const lines = buf.split('\n')
      buf = lines.pop() || ''
      for (const line of lines) {
        const t = line.trim()
        if (!t || !t.startsWith('data:')) continue
        const payload = t.slice(5).trim()
        if (payload === '[DONE]') continue
        try {
          const json = JSON.parse(payload)
          const chunk = json.content || json.data || json.chunk || json.delta || ''
          if (chunk) {
            full += chunk
            aiMsg.content = full
          }
        } catch {}
      }
    }

    if (!full) aiMsg.content = '好问题！让我为您查一查～（后端未返回内容，请检查 SSE 流式响应）'
    aiMsg.streaming = false
  } catch (e) {
    aiMsg.streaming = false
    aiMsg.content = `⚠️ 网络请求失败：${e.message || e}\n当前部署环境（GitHub Pages）不支持后端 API，您可以本地启动前端（vite）+ 后端（Spring Boot）体验完整功能。`
  } finally {
    sending.value = false
  }
}

const clearChat = async () => {
  try {
    await showConfirmDialog({
      title: '🗑️ 清空对话？',
      message: '确认清空当前会话吗？这个操作不可恢复哦',
      confirmButtonText: '清空',
      cancelButtonText: '再想想',
      confirmButtonColor: '#FF6F91'
    })
    chatStore.clearMessages()
    showToast('对话已清空 ✨')
  } catch {}
}
</script>

<style scoped>
.chat-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, #FDF7FF 0%, #F0F7FF 40%, #FFF8F0 100%);
}

/* ==== Header ==== */
.chat-header {
  position: relative;
  overflow: hidden;
  padding: 52px 16px 14px;
  color: #fff;
  z-index: 10;
}
.hdr-bg {
  position: absolute; inset: 0;
  background: linear-gradient(120deg,
    #845EC2 0%, #5E81F4 30%, #4DD0E1 55%, #84FAB0 75%, #FFC75F 100%);
  background-size: 300% 300%;
  animation: hdrShift 10s ease infinite;
}
.hdr-bg::after {
  content: '';
  position: absolute; inset: 0;
  background:
    radial-gradient(circle at 10% 20%, #ffffff30 0, transparent 40%),
    radial-gradient(circle at 90% 80%, #ffffff28 0, transparent 45%);
}
@keyframes hdrShift {
  0%, 100% { background-position: 0% 50%; }
  50%      { background-position: 100% 50%; }
}
.hdr-row {
  position: relative; z-index: 2;
  display: flex; align-items: center; gap: 10px;
}
.hdr-back, .hdr-clear {
  width: 38px; height: 38px;
  flex-shrink: 0;
  display: flex; align-items: center; justify-content: center;
  border-radius: 12px;
  background: rgba(255,255,255,0.25);
  backdrop-filter: blur(6px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
  color: #fff;
}

.hdr-title { flex: 1; min-width: 0; }
.hdr-bot { display: flex; align-items: center; gap: 12px; }

.avatar-ring {
  position: relative;
  width: 46px; height: 46px;
  border-radius: 50%;
  background: conic-gradient(#FF6F91, #FFC75F, #84FAB0, #4DD0E1, #845EC2, #FF6F91);
  padding: 3px;
  display: block;
  animation: spinRing 6s linear infinite;
  box-shadow: 0 6px 18px rgba(132, 94, 194, 0.3);
}
@keyframes spinRing { to { transform: rotate(360deg); } }
.avatar-inner {
  width: 100%; height: 100%;
  border-radius: 50%;
  background: #fff;
  display: flex; align-items: center; justify-content: center;
  font-size: 22px;
  animation: spinRing 6s linear infinite reverse;
}
.avatar-inner.sm { font-size: 18px; }
.avatar-inner.sm.u { background: linear-gradient(135deg, #FFF4F6, #F0F7FF); }

.avatar-dot {
  position: absolute;
  right: 0; bottom: 0;
  width: 12px; height: 12px;
  background: #00C9A7;
  border: 2px solid #fff;
  border-radius: 50%;
  box-shadow: 0 0 0 3px #00C9A733;
  animation: livePulse 1.8s ease-in-out infinite;
}
@keyframes livePulse {
  0%, 100% { box-shadow: 0 0 0 3px #00C9A733; }
  50%      { box-shadow: 0 0 0 7px #00C9A710; }
}

.hdr-text { min-width: 0; }
.hdr-name { font-size: 16px; font-weight: 800; line-height: 1.2; }
.hdr-status {
  margin-top: 2px;
  font-size: 11px;
  opacity: 0.95;
  display: flex; align-items: center; gap: 5px;
}
.stat-live {
  display: inline-block; width: 6px; height: 6px;
  background: #84FAB0;
  border-radius: 50%;
  box-shadow: 0 0 6px #84FAB0;
}

/* ==== 消息滚动区 ==== */
.msg-scroll {
  flex: 1;
  overflow-y: auto;
  padding: 16px 14px 18px;
}

.welcome-card {
  background: rgba(255,255,255,0.75);
  backdrop-filter: blur(14px);
  border-radius: 24px;
  padding: 18px 16px 20px;
  border: 1px solid rgba(255,255,255,0.9);
  box-shadow: 0 14px 40px rgba(132, 94, 194, 0.16);
}
.welcome-hero {
  background: linear-gradient(120deg,
    #FF6F91 0%, #FF9671 30%, #FFC75F 55%, #84FAB0 78%, #4DD0E1 100%);
  background-size: 200% 200%;
  animation: btnShift 7s ease infinite;
  color: #fff;
  border-radius: 18px;
  padding: 16px 14px;
  margin-bottom: 16px;
  text-align: center;
  box-shadow: 0 10px 26px rgba(255, 111, 145, 0.28);
}
@keyframes btnShift {
  0%, 100% { background-position: 0% 50%; }
  50%      { background-position: 100% 50%; }
}
.wh-ic {
  font-size: 36px;
  margin-bottom: 6px;
  filter: drop-shadow(0 4px 10px rgba(0,0,0,0.18));
}
.welcome-hero h2 {
  font-size: 17px;
  font-weight: 800;
  margin: 0 0 6px;
}
.welcome-hero p {
  font-size: 12px;
  opacity: 0.95;
  margin: 0;
}

.quick-titles {
  font-size: 13px;
  font-weight: 800;
  color: #2c3e50;
  margin-bottom: 10px;
}
.mt20 { margin-top: 18px; }

.quick-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}
.quick-btn {
  --c1: #FF6F91; --c2: #FFC75F;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 10px;
  border-radius: 16px;
  background: linear-gradient(145deg,
    color-mix(in srgb, var(--c1) 14%, white) 0%,
    color-mix(in srgb, var(--c2) 14%, white) 100%);
  border: 1px solid color-mix(in srgb, var(--c1) 20%, white);
  transition: transform .2s;
}
.quick-btn:active { transform: scale(0.97); }
.qb-ic {
  width: 30px; height: 30px;
  border-radius: 10px;
  background: linear-gradient(135deg, var(--c1), var(--c2));
  display: flex; align-items: center; justify-content: center;
  font-size: 15px;
  flex-shrink: 0;
  box-shadow: 0 4px 10px color-mix(in srgb, var(--c1) 35%, transparent);
}
.qb-txt {
  font-size: 12px;
  line-height: 1.35;
  font-weight: 600;
  color: #2c3e50;
}

.hint-list {
  margin: 0;
  padding-left: 18px;
  font-size: 12px;
  line-height: 1.9;
  color: #4a4f5c;
}
.hint-list li::marker { color: #FF6F91; }

/* ==== 消息气泡 ==== */
.msg-row {
  display: flex;
  gap: 8px;
  margin-bottom: 14px;
  align-items: flex-end;
}
.msg-row.user { flex-direction: row-reverse; }
.avatar-sm { width: 32px; height: 32px; flex-shrink: 0; }
.avatar-sm.user { align-self: flex-end; }

.bubble {
  max-width: 76%;
  padding: 10px 14px;
  border-radius: 18px;
  font-size: 14px;
  line-height: 1.65;
  word-break: break-word;
  white-space: pre-wrap;
  position: relative;
  box-shadow: 0 6px 18px rgba(0,0,0,0.08);
}
.bubble.streaming::after {
  content: '▋';
  display: inline-block;
  margin-left: 2px;
  animation: cursor 1s steps(2) infinite;
  color: #845EC2;
}
@keyframes cursor { 50% { opacity: 0; } }

.msg-row.ai .bubble {
  background: linear-gradient(160deg, #fff 0%, #F4F8FF 100%);
  color: #2c3e50;
  border-top-left-radius: 6px;
  border: 1px solid #ecf0fb;
}
.msg-row.user .bubble {
  background: linear-gradient(120deg, #FF6F91 0%, #FF9671 40%, #FFC75F 100%);
  background-size: 200% 200%;
  animation: btnShift 6s ease infinite;
  color: #fff;
  border-top-right-radius: 6px;
  box-shadow: 0 10px 22px rgba(255, 111, 145, 0.32);
  text-shadow: 0 1px 2px rgba(0,0,0,0.12);
}

.bubble-content { position: relative; }
.thinking {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 0;
}
.thinking .dot {
  width: 8px; height: 8px; border-radius: 50%;
  background: linear-gradient(135deg, #FF6F91, #FFC75F);
  animation: bounce 1.2s ease-in-out infinite;
}
.thinking .dot.d2 { background: linear-gradient(135deg, #FFC75F, #84FAB0); animation-delay: 0.15s; }
.thinking .dot.d3 { background: linear-gradient(135deg, #4DD0E1, #845EC2); animation-delay: 0.3s; }
@keyframes bounce {
  0%, 80%, 100% { transform: translateY(0) scale(0.8); opacity: 0.6; }
  40%           { transform: translateY(-8px) scale(1.1); opacity: 1; }
}

/* ==== Footer 输入框 ==== */
.chat-footer {
  padding: 10px 12px calc(12px + env(safe-area-inset-bottom));
  background: linear-gradient(180deg, transparent 0%, rgba(255,255,255,0.85) 40%);
  backdrop-filter: blur(10px);
  border-top: 1px solid #f2edf8;
}

.quick-chips {
  display: flex;
  gap: 6px;
  margin-bottom: 10px;
  overflow-x: auto;
  padding-bottom: 2px;
  scrollbar-width: none;
}
.quick-chips::-webkit-scrollbar { display: none; }
.chip {
  --c1: #FF6F91; --c2: #FFC75F;
  flex-shrink: 0;
  padding: 6px 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
  background: linear-gradient(135deg,
    color-mix(in srgb, var(--c1) 18%, white),
    color-mix(in srgb, var(--c2) 18%, white));
  color: #2c3e50;
  border: 1px solid color-mix(in srgb, var(--c1) 25%, white);
  transition: transform .15s;
}
.chip:active { transform: scale(0.95); }

.composer {
  display: flex;
  align-items: center;
  gap: 10px;
}

.input-wrap {
  --ring: #FF6F91;
  flex: 1;
  position: relative;
  border-radius: 28px;
  padding: 3px;
  background: linear-gradient(135deg, #FF6F91, #FFC75F, #00C9A7, #4DD0E1, #845EC2);
  background-size: 250% 250%;
  animation: ringShift 6s ease infinite;
  transition: transform .2s;
}
.input-wrap.focused { transform: scale(1.01); }
@keyframes ringShift {
  0%, 100% { background-position: 0 50%; }
  50%      { background-position: 100% 50%; }
}
.input-glow {
  position: absolute;
  inset: -3px;
  border-radius: 30px;
  background: inherit;
  filter: blur(12px);
  opacity: 0.55;
  z-index: 0;
  animation: glow 2.6s ease-in-out infinite;
}
@keyframes glow {
  0%, 100% { opacity: 0.35; }
  50%      { opacity: 0.7; }
}
.msg-input {
  position: relative;
  z-index: 1;
  background: #fff;
  border-radius: 26px;
  min-height: 44px;
}
.msg-input :deep(.van-field__control) {
  font-weight: 500;
  font-size: 14px;
}
.clear-input {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  z-index: 2;
  color: #b5bac4;
  padding: 4px;
}

.send-btn {
  width: 46px;
  height: 46px;
  border-radius: 50% !important;
  padding: 0 !important;
  border: none !important;
  background: linear-gradient(135deg, #FF6F91, #FF9671 60%, #FFC75F) !important;
  box-shadow: 0 8px 20px rgba(255, 111, 145, 0.4);
  color: #fff !important;
  flex-shrink: 0;
  transition: transform .15s;
}
.send-btn:active { transform: scale(0.92); }
.send-btn:disabled {
  background: linear-gradient(135deg, #cfd5e3, #b8bfcf) !important;
  box-shadow: none;
}
</style>