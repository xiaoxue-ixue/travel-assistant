<template>
  <div class="detail-page page-container">

    <!-- 顶部渐变头部：城市名 + 日期 + 预算彩虹条 -->
    <div class="detail-hero">
      <div class="hero-bg"></div>
      <van-icon
        name="arrow-left"
        size="22"
        class="back-btn"
        @click="goBack"
      />
      <div class="hero-top-tags">
        <span class="hero-chip chip-city">
          <van-icon name="location-o" /> {{ planInfo.city }}
        </span>
        <span class="hero-chip chip-days">
          <van-icon name="calendar-o" /> {{ planInfo.days }} 天
        </span>
      </div>
      <h1 class="hero-heading">我的{{ planInfo.city }}之旅</h1>
      <p class="hero-sub">
        ✨ 预算 <b>¥{{ formatMoney(planInfo.budget) }}</b>
        · 共 <b>{{ planInfo.days }}</b> 天行程
      </p>

      <div class="budget-rainbow">
        <div class="br-tip">预算健康指数</div>
        <div class="br-fill" :style="{ width: budgetRatio + '%' }"></div>
        <div class="br-meta">
          <span>舒适 💚</span>
          <span>¥{{ formatMoney(planInfo.budget) }}</span>
          <span>奢华 💜</span>
        </div>
      </div>
    </div>

    <!-- 总体评分 + AI 总结卡 -->
    <div class="section-card pop-in d1 summary-card">
      <div class="summary-header">
        <div class="summary-title">🎯 行程总览</div>
        <div class="score-ring" :style="scoreRingStyle">
          <svg viewBox="0 0 64 64">
            <circle cx="32" cy="32" r="26" class="ring-bg" />
            <circle cx="32" cy="32" r="26" class="ring-fg" />
          </svg>
          <div class="score-num">{{ overallScore }}</div>
        </div>
      </div>
      <p class="summary-text">{{ planInfo.summary || 'AI 精心为您规划，张弛有度，预算可控，祝您旅途愉快！🌞' }}</p>
      <div class="summary-tags">
        <span v-for="(t,i) in planTags" :key="t" :class="['summary-tag', 'st'+((i%6)+1)]">
          {{ t }}
        </span>
      </div>
    </div>

    <!-- 预算分配 3D 彩色饼图 -->
    <div class="section-card pop-in d2 budget-card">
      <div class="section-title">💰 预算分配</div>
      <div class="budget-wrapper">
        <div class="pie-chart" :style="pieStyleVars">
          <div class="pie-layer"></div>
          <div class="pie-inner">
            <div class="pie-total-label">总预算</div>
            <div class="pie-total">¥{{ formatShort(planInfo.budget) }}</div>
          </div>
        </div>
        <div class="pie-legend">
          <div
            v-for="(item,i) in budgetList"
            :key="item.category"
            class="legend-item pop-in"
            :class="'d' + ((i%6)+1)"
          >
            <span class="legend-dot" :style="dotStyle(item.color)"></span>
            <span class="legend-name">{{ item.category }}</span>
            <span class="legend-pct">{{ item.pct }}%</span>
            <span class="legend-val">¥{{ formatShort(item.value) }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 时间轴 - 彩色竖条 -->
    <div class="section-card pop-in d2">
      <div class="section-title">🗓️ 每日行程</div>
      <div v-if="!planInfo.itinerary || planInfo.itinerary.length === 0" class="empty-box">
        暂未生成每日行程 🌼
      </div>
      <div v-else class="timeline">
        <div
          v-for="(day, idx) in planInfo.itinerary"
          :key="idx"
          :class="['tl-item pop-in', 'd' + (((idx+1) % 8) + 1)]"
        >
          <div class="tl-dot-wrap" :style="dotColor(idx)">
            <div class="tl-dot"></div>
            <div class="tl-line"></div>
            <div class="tl-day-badge">第{{ day.day || idx + 1 }}天</div>
          </div>
          <div class="tl-content">
            <div class="tl-card">
              <h4 class="tl-title">
                {{ day.title || day.theme || 'Day ' + (idx+1) }}
              </h4>
              <ul class="tl-list">
                <li v-for="(a,ai) in (day.activities || day.items || [])" :key="ai">
                  <span class="tl-bullet" :style="dotColor(ai)"></span>
                  <span class="tl-act-time">{{ a.time || '' }}</span>
                  <span class="tl-act-name">{{ a.activity || a.name || a }}</span>
                  <span v-if="a.location" class="tl-act-place">📍 {{ a.location }}</span>
                </li>
              </ul>
              <div v-if="day.transport || day.hotel" class="tl-tags">
                <span v-if="day.transport" class="tl-tag tt1">🚆 {{ day.transport }}</span>
                <span v-if="day.hotel" class="tl-tag tt2">🏨 {{ day.hotel }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 贴士卡：彩虹玻璃拟态 -->
    <div v-if="planInfo.tips && planInfo.tips.length > 0" class="section-card pop-in d3">
      <div class="section-title">💡 贴心贴士</div>
      <div class="tips-grid">
        <div
          v-for="(t, i) in planInfo.tips.slice(0, 6)"
          :key="i"
          :class="['tip-card pop-in', 'd' + (((i+1) % 6) + 1)]"
          :style="tipColorVars(i)"
        >
          <div class="tip-ic">{{ tipIcon(i) }}</div>
          <div class="tip-text">{{ t }}</div>
        </div>
      </div>
    </div>

    <!-- 底部操作按钮 -->
    <div class="bottom-actions">
      <van-button
        class="act-btn act-edit"
        icon="edit"
        round
        @click="goEdit"
      >调整行程</van-button>
      <van-button
        class="act-btn act-ask"
        icon="chat-o"
        round
        @click="goChat"
      >问 AI</van-button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useChatStore } from '@/stores/chat'

const router = useRouter()
const chatStore = useChatStore()

const defaultPlan = {
  city: '上海',
  budget: 3000,
  days: 3,
  summary: '',
  budgetBreakdown: { 交通: 800, 住宿: 1200, 餐饮: 600, 门票: 300, 购物: 100 },
  itinerary: [
    { day: 1, title: '初探魔都', items: [
      { activity: '抵达上海', location: '虹桥站' },
      { activity: '外滩夜景', location: '中山东一路' },
      { activity: '南京路步行街', location: '南京东路' }
    ], transport: '地铁', hotel: '黄浦区精品酒店' },
    { day: 2, title: '深度漫游', items: [
      { activity: '豫园', location: '福佑路' },
      { activity: '田子坊', location: '泰康路' },
      { activity: '陆家嘴三件套', location: '世纪大道' }
    ], transport: '公交+地铁', hotel: '黄浦区精品酒店' },
    { day: 3, title: '文艺收尾', items: [
      { activity: '迪士尼一日游', location: '川沙' },
      { activity: '返程', location: '虹桥/浦东机场' }
    ], transport: '打车' }
  ],
  tips: [
    '提前一周订机票/酒店价格更优',
    '高峰期景点尽量预约或早去',
    '随身携带充电宝和水',
    '使用大众点评搜当地美食',
    '地铁卡/乘车码提前准备',
    '天气查询避免雨天影响行程'
  ]
}

const planInfo = computed(() => chatStore.currentPlan || defaultPlan)

const budgetList = computed(() => {
  const bd = planInfo.value.budgetBreakdown || {}
  const total = Object.values(bd).reduce((s, v) => s + (Number(v) || 0), 0) || 1
  const colors = ['#FF6F91','#FFC75F','#00C9A7','#4DD0E1','#845EC2','#FF9671']
  return Object.entries(bd).map(([k, v], i) => {
    const value = Number(v) || 0
    return {
      category: k,
      value,
      pct: Math.round(value / total * 100),
      color: colors[i % colors.length]
    }
  })
})

const overallScore = computed(() => {
  const b = planInfo.value.budget
  const d = planInfo.value.days
  let score = 82
  if (d >= 3 && d <= 7) score += 5
  if (b >= 1500 && b <= 10000) score += 5
  return Math.min(99, score)
})
const scoreRingStyle = computed(() => ({ '--score': overallScore.value }))

const budgetRatio = computed(() => {
  return Math.min(100, Math.round((planInfo.value.budget / 5000) * 100))
})

const planTags = computed(() => ['AI 智能推荐', '预算可控', '张弛有度', '亲子友好', '网红打卡', '美食之旅'])

const pieStyleVars = computed(() => {
  const list = budgetList.value
  let vars = {}
  let acc = 0
  list.forEach((it, i) => {
    vars[`--seg${i + 1}-from`] = `${acc}%`
    acc += it.pct
    vars[`--seg${i + 1}-to`] = `${acc}%`
    vars[`--seg${i + 1}-color`] = it.color
  })
  return vars
})

const dotStyle = (color) => ({
  background: `linear-gradient(135deg, ${color}, #ffffffcc)`,
  boxShadow: `0 0 0 2px ${color}33`
})

const dotColor = (i) => {
  const colors = ['#FF6F91','#FFC75F','#00C9A7','#4DD0E1','#845EC2','#FF9671','#84FAB0','#C398E1']
  return { '--dot-c': colors[i % colors.length] }
}

const tipIcon = (i) => ['🎈','🌂','🔋','🍜','🚇','🌤️'][i % 6]

const tipColorVars = (i) => {
  const cs = [
    ['#FF6F91','#FFC75F'],
    ['#84FAB0','#00C9A7'],
    ['#4DD0E1','#5E81F4'],
    ['#FFC75F','#FF9671'],
    ['#C398E1','#845EC2'],
    ['#FF9671','#FF6F91']
  ]
  return { '--c1': cs[i % 6][0], '--c2': cs[i % 6][1] }
}

const formatMoney = (n) => Number(n || 0).toLocaleString()
const formatShort = (n) => {
  n = Number(n) || 0
  if (n >= 10000) return (n / 10000).toFixed(1) + 'w'
  if (n >= 1000)  return (n / 1000).toFixed(1) + 'k'
  return n.toFixed(0)
}

const goBack = () => router.back()
const goEdit = () => router.push('/')
const goChat = () => {
  chatStore.pushMessage({
    role: 'user',
    content: `我生成的${planInfo.value.city}${planInfo.value.days}天行程，你有什么建议？`
  })
  router.push('/chat')
}
</script>

<style scoped>
.detail-page { padding-bottom: 110px; }

/* ==== Hero ==== */
.detail-hero {
  position: relative;
  padding: 54px 20px 34px;
  overflow: hidden;
  border-bottom-left-radius: 34px;
  border-bottom-right-radius: 34px;
  color: #fff;
}
.hero-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg,
    #845EC2 0%, #FF6F91 25%, #FF9671 50%, #FFC75F 75%, #4DD0E1 100%);
  background-size: 300% 300%;
  animation: heroWave 14s ease infinite;
}
.hero-bg::before {
  content: '';
  position: absolute; inset: 0;
  background:
    radial-gradient(circle at 20% 20%, #ffffff40 0, transparent 40%),
    radial-gradient(circle at 80% 70%, #ffffff30 0, transparent 45%);
}
@keyframes heroWave {
  0%, 100% { background-position: 0 50%; }
  50%      { background-position: 100% 50%; }
}

.back-btn {
  position: relative; z-index: 3;
  color: #fff;
  width: 38px; height: 38px;
  background: rgba(255,255,255,0.25);
  backdrop-filter: blur(6px);
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 4px 14px rgba(0,0,0,0.15);
}

.hero-top-tags {
  position: relative; z-index: 3;
  display: flex; gap: 8px;
  margin-top: 14px;
  flex-wrap: wrap;
}
.hero-chip {
  padding: 6px 12px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
  background: rgba(255,255,255,0.28);
  border: 1px solid rgba(255,255,255,0.45);
  backdrop-filter: blur(4px);
  display: inline-flex; align-items: center; gap: 4px;
}
.chip-city   { background: rgba(255, 111, 145, 0.55); }
.chip-days   { background: rgba(255, 199, 95, 0.55); }

.hero-heading {
  position: relative; z-index: 3;
  margin-top: 18px;
  font-size: 28px;
  font-weight: 800;
  letter-spacing: 1px;
  text-shadow: 0 4px 14px rgba(0,0,0,0.18);
}
.hero-sub {
  position: relative; z-index: 3;
  margin-top: 6px;
  font-size: 14px;
  opacity: 0.96;
}
.hero-sub b { font-weight: 800; }

.budget-rainbow {
  position: relative; z-index: 3;
  margin-top: 22px;
  padding: 12px 14px 10px;
  background: rgba(255,255,255,0.22);
  border: 1px solid rgba(255,255,255,0.38);
  border-radius: 16px;
  backdrop-filter: blur(8px);
}
.br-tip { font-size: 11px; font-weight: 600; opacity: 0.95; margin-bottom: 8px; }
.br-fill {
  height: 8px;
  border-radius: 999px;
  background: linear-gradient(90deg, #84FAB0 0%, #FFC75F 40%, #FF6F91 75%, #845EC2 100%);
  box-shadow: 0 2px 10px rgba(255, 111, 145, 0.45);
  transition: width .6s cubic-bezier(.2,1.2,.3,1);
}
.br-meta {
  margin-top: 6px;
  display: flex; justify-content: space-between;
  font-size: 11px; font-weight: 600; opacity: 0.9;
}

/* ==== 行程总览卡 ==== */
.summary-card { margin-top: -18px; position: relative; z-index: 5; }
.summary-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}
.section-title, .summary-title {
  font-size: 17px;
  font-weight: 800;
  color: #2c3e50;
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 14px;
}

.score-ring {
  --score: 85;
  width: 62px; height: 62px;
  position: relative;
  flex-shrink: 0;
}
.score-ring svg {
  width: 100%; height: 100%;
  transform: rotate(-90deg);
}
.ring-bg {
  fill: none;
  stroke: #f2eef9;
  stroke-width: 6;
}
.ring-fg {
  fill: none;
  stroke-width: 6;
  stroke-linecap: round;
  stroke: url(#dash);
  stroke: #FF6F91;
  stroke-dasharray: calc(var(--score) * 1.6336) 163.36;
  filter: drop-shadow(0 2px 6px rgba(255, 111, 145, 0.5));
  transition: stroke-dasharray .8s cubic-bezier(.2,1.2,.3,1);
  stroke: linear-gradient(90deg, #FF6F91, #FFC75F, #4DD0E1);
}
.score-ring::before {
  position: absolute; inset: 0;
  content: '';
  background: conic-gradient(
    #FF6F91 0%,
    #FFC75F 30%,
    #00C9A7 60%,
    #845EC2 100%
  );
  border-radius: 50%;
  mask: radial-gradient(transparent 52%, black 54%);
  -webkit-mask: radial-gradient(transparent 52%, black 54%);
  opacity: 0.8;
}
.score-num {
  position: absolute; inset: 0;
  display: flex; align-items: center; justify-content: center;
  font-size: 20px; font-weight: 900;
  background: linear-gradient(135deg, #FF6F91, #845EC2);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.summary-text {
  font-size: 13px;
  line-height: 1.7;
  color: #4a4f5c;
  padding: 10px 14px;
  background: linear-gradient(135deg, #FFF4F6 0%, #E4F2FF 100%);
  border-radius: 14px;
  margin-bottom: 14px;
}

.summary-tags { display: flex; flex-wrap: wrap; gap: 8px; }
.summary-tag {
  padding: 5px 12px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 700;
  color: #fff;
  box-shadow: 0 4px 10px rgba(0,0,0,0.12);
}
.summary-tag.st1 { background: linear-gradient(135deg, #FF6F91, #FFC75F); }
.summary-tag.st2 { background: linear-gradient(135deg, #84FAB0, #00C9A7); }
.summary-tag.st3 { background: linear-gradient(135deg, #4DD0E1, #5E81F4); }
.summary-tag.st4 { background: linear-gradient(135deg, #FFC75F, #FF9671); }
.summary-tag.st5 { background: linear-gradient(135deg, #C398E1, #845EC2); }
.summary-tag.st6 { background: linear-gradient(135deg, #FF9671, #FF7F7F); }

/* ==== 预算分配 ==== */
.budget-wrapper {
  display: flex;
  gap: 20px;
  align-items: center;
}
.pie-chart {
  --seg1-from:0%; --seg1-to:25%; --seg1-color:#FF6F91;
  --seg2-from:25%; --seg2-to:55%; --seg2-color:#FFC75F;
  --seg3-from:55%; --seg3-to:75%; --seg3-color:#00C9A7;
  --seg4-from:75%; --seg4-to:88%; --seg4-color:#4DD0E1;
  --seg5-from:88%; --seg5-to:100%; --seg5-color:#845EC2;
  width: 140px; height: 140px;
  flex-shrink: 0;
  position: relative;
  border-radius: 50%;
  background: conic-gradient(
    var(--seg1-color) var(--seg1-from) var(--seg1-to),
    var(--seg2-color) var(--seg2-from) var(--seg2-to),
    var(--seg3-color) var(--seg3-from) var(--seg3-to),
    var(--seg4-color) var(--seg4-from) var(--seg4-to),
    var(--seg5-color) var(--seg5-from) var(--seg5-to),
    #eef0f5 var(--seg5-to) 100%
  );
  box-shadow: 0 14px 34px rgba(132, 94, 194, 0.25),
              inset 0 -6px 14px rgba(255,255,255,0.5);
  transform: perspective(600px) rotateX(22deg);
  transition: transform .4s;
}
.pie-chart:hover { transform: perspective(600px) rotateX(10deg) scale(1.04); }

.pie-layer {
  position: absolute; inset: 6px;
  border-radius: 50%;
  background:
    radial-gradient(circle at 30% 30%, #ffffffaa 0%, #ffffff 55%);
  mask: radial-gradient(transparent 54%, black 56%);
  -webkit-mask: radial-gradient(transparent 54%, black 56%);
  filter: drop-shadow(0 8px 14px rgba(255,255,255,0.6));
}
.pie-inner {
  position: absolute; inset: 22px;
  border-radius: 50%;
  background: linear-gradient(145deg, #ffffff, #f7f3ff);
  box-shadow:
    inset 3px 3px 10px #e7e0f4,
    inset -3px -3px 10px #ffffff;
  display: flex; flex-direction: column;
  align-items: center; justify-content: center;
}
.pie-total-label { font-size: 10px; color: #8a8f99; font-weight: 600; }
.pie-total {
  margin-top: 2px;
  font-size: 18px; font-weight: 900;
  background: linear-gradient(135deg, #FF6F91, #845EC2);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}

.pie-legend { flex: 1; min-width: 0; }
.legend-item {
  display: grid;
  grid-template-columns: 14px 1fr auto auto;
  gap: 8px;
  align-items: center;
  padding: 6px 0;
  border-bottom: 1px dashed #f1eefa;
  font-size: 12px;
}
.legend-item:last-child { border-bottom: none; }
.legend-dot {
  width: 12px; height: 12px;
  border-radius: 4px;
  transform: rotate(45deg);
  box-shadow: 0 2px 4px rgba(0,0,0,0.12);
}
.legend-name { color: #3b4050; font-weight: 600; }
.legend-pct  { color: #4a4f5c; font-weight: 700; }
.legend-val  { color: #FF6F91; font-weight: 800; }

/* ==== 时间轴 ==== */
.timeline { position: relative; padding-left: 14px; }

.tl-item {
  display: grid;
  grid-template-columns: 74px 1fr;
  gap: 10px;
  margin-bottom: 18px;
  position: relative;
}
.tl-item:last-child .tl-line { display: none; }

.tl-dot-wrap {
  --dot-c: #FF6F91;
  position: relative;
  padding-top: 4px;
}
.tl-day-badge {
  position: absolute;
  left: -4px; top: 0;
  font-size: 11px;
  font-weight: 800;
  padding: 4px 8px;
  border-radius: 10px;
  background: linear-gradient(135deg, var(--dot-c), #fff);
  color: var(--dot-c);
  box-shadow: 0 2px 6px color-mix(in srgb, var(--dot-c) 30%, transparent);
  white-space: nowrap;
}
.tl-dot {
  position: absolute;
  top: 36px; left: 30px;
  width: 14px; height: 14px;
  border-radius: 50%;
  background: #fff;
  border: 3px solid var(--dot-c);
  box-shadow: 0 0 0 4px color-mix(in srgb, var(--dot-c) 25%, transparent),
              0 2px 6px rgba(0,0,0,0.18);
  animation: pulse 2.2s ease-in-out infinite;
}
@keyframes pulse {
  0%, 100% { box-shadow: 0 0 0 4px color-mix(in srgb, var(--dot-c) 25%, transparent), 0 2px 6px rgba(0,0,0,0.18); }
  50%      { box-shadow: 0 0 0 8px color-mix(in srgb, var(--dot-c) 8%, transparent), 0 2px 6px rgba(0,0,0,0.18); }
}
.tl-line {
  position: absolute;
  left: 36px; top: 54px; bottom: -20px;
  width: 3px;
  border-radius: 999px;
  background: linear-gradient(180deg, var(--dot-c), color-mix(in srgb, var(--dot-c) 30%, transparent));
}

.tl-card {
  margin-top: 32px;
  padding: 14px 14px 12px;
  border-radius: 18px;
  background: linear-gradient(160deg, #fff 0%, #fdf8ff 100%);
  border: 1px solid color-mix(in srgb, var(--dot-c) 20%, white);
  box-shadow: 0 6px 18px color-mix(in srgb, var(--dot-c) 15%, transparent);
}
.tl-title {
  font-size: 15px;
  font-weight: 800;
  margin-bottom: 8px;
  color: var(--dot-c);
}
.tl-list { list-style: none; padding: 0; margin: 0; }
.tl-list li {
  display: grid;
  grid-template-columns: 10px auto 1fr auto;
  gap: 6px 10px;
  align-items: center;
  padding: 7px 0;
  border-bottom: 1px dashed #f0ebf7;
  font-size: 13px;
  color: #3b4050;
}
.tl-list li:last-child { border-bottom: none; }
.tl-bullet {
  width: 7px; height: 7px;
  border-radius: 50%;
  background: var(--dot-c);
  box-shadow: 0 0 0 3px color-mix(in srgb, var(--dot-c) 20%, transparent);
}
.tl-act-time { font-weight: 700; color: var(--dot-c); font-size: 12px; min-width: 42px; }
.tl-act-name { font-weight: 600; }
.tl-act-place { font-size: 11px; color: #8a8f99; }

.tl-tags { display: flex; gap: 6px; margin-top: 10px; flex-wrap: wrap; }
.tl-tag {
  font-size: 11px;
  padding: 3px 10px;
  border-radius: 999px;
  color: #fff;
  font-weight: 700;
}
.tl-tag.tt1 { background: linear-gradient(135deg, #4DD0E1, #5E81F4); }
.tl-tag.tt2 { background: linear-gradient(135deg, #FF9671, #FF6F91); }

/* ==== 贴士卡片 ==== */
.tips-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}
.tip-card {
  --c1: #FF6F91; --c2: #FFC75F;
  position: relative;
  padding: 14px 12px 12px 52px;
  min-height: 74px;
  border-radius: 16px;
  background: linear-gradient(145deg,
    color-mix(in srgb, var(--c1) 16%, white) 0%,
    color-mix(in srgb, var(--c2) 16%, white) 100%);
  border: 1px solid color-mix(in srgb, var(--c1) 20%, white);
  overflow: hidden;
  transition: transform .25s;
}
.tip-card:active { transform: scale(0.97); }
.tip-card::before {
  content: '';
  position: absolute;
  left: 0; top: 0; bottom: 0;
  width: 4px;
  background: linear-gradient(180deg, var(--c1), var(--c2));
  box-shadow: 3px 0 12px color-mix(in srgb, var(--c1) 30%, transparent);
}
.tip-ic {
  position: absolute;
  left: 12px; top: 14px;
  width: 30px; height: 30px;
  border-radius: 10px;
  background: linear-gradient(135deg, var(--c1), var(--c2));
  display: flex; align-items: center; justify-content: center;
  font-size: 16px;
  color: #fff;
  box-shadow: 0 4px 10px color-mix(in srgb, var(--c1) 40%, transparent);
}
.tip-text {
  font-size: 12px;
  line-height: 1.55;
  color: #3b4050;
  font-weight: 500;
}

.empty-box {
  padding: 30px;
  text-align: center;
  color: #8a8f99;
  background: linear-gradient(135deg, #FFF4F6, #E4F2FF);
  border-radius: 16px;
  font-size: 13px;
}

/* ==== 底部按钮 ==== */
.bottom-actions {
  position: fixed;
  bottom: 0; left: 0; right: 0;
  padding: 12px 16px calc(12px + env(safe-area-inset-bottom));
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
  background: linear-gradient(180deg, transparent 0%, #ffffffcc 40%, #fffffff2 100%);
  backdrop-filter: blur(8px);
  z-index: 50;
}
.act-btn {
  height: 48px;
  font-weight: 800;
  border-radius: 26px !important;
  font-size: 14px;
  border: none !important;
}
.act-edit {
  background: linear-gradient(135deg, #FFF4F6, #F0F7FF) !important;
  color: #845EC2 !important;
  box-shadow: 0 8px 22px rgba(132, 94, 194, 0.18);
}
.act-ask {
  background: linear-gradient(120deg, #FF6F91, #FF9671, #FFC75F) !important;
  background-size: 200% 200%;
  animation: btnShift 5s ease infinite;
  color: #fff !important;
  box-shadow: 0 10px 28px rgba(255, 111, 145, 0.38);
}
@keyframes btnShift {
  0%, 100% { background-position: 0 50%; }
  50%      { background-position: 100% 50%; }
}
</style>