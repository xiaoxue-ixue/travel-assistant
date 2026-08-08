<template>
  <div class="profile-page page-container">

    <!-- 彩虹渐变头部 + 用户卡片悬浮弹出 -->
    <div class="pf-header">
      <div class="pf-bg"></div>
      <div class="pf-blobs" aria-hidden>
        <span class="pfb p1">✈️</span>
        <span class="pfb p2">🌍</span>
        <span class="pfb p3">🌴</span>
        <span class="pfb p4">🗺️</span>
        <span class="pfb p5">🎒</span>
        <span class="pfb p6">🗽</span>
        <span class="pfb p7">🏖️</span>
      </div>

      <div class="pf-user-card pop-in">
        <div class="pf-avatar-wrap">
          <div class="pf-avatar-ring">
            <div class="pf-avatar">🧳</div>
          </div>
          <div class="pf-badge">Lv.{{ level }}</div>
        </div>
        <div class="pf-user-meta">
          <div class="pf-name">
            <span>{{ user.nickname }}</span>
            <span class="pf-vip">👑 旅行达人</span>
          </div>
          <div class="pf-id">ID: {{ user.id }}</div>
          <div class="pf-slogan">🌈 走遍世界的每一个角落</div>
        </div>
      </div>

      <!-- 统计卡玻璃拟态 -->
      <div class="pf-stats pop-in d1">
        <div class="stat-card sc1">
          <div class="st-ic">🗺️</div>
          <div class="st-num">{{ stats.plans }}</div>
          <div class="st-label">生成行程</div>
        </div>
        <div class="stat-card sc2">
          <div class="st-ic">💬</div>
          <div class="st-num">{{ stats.chats }}</div>
          <div class="st-label">AI 对话</div>
        </div>
        <div class="stat-card sc3">
          <div class="st-ic">🏙️</div>
          <div class="st-num">{{ stats.cities }}</div>
          <div class="st-label">去过城市</div>
        </div>
        <div class="stat-card sc4">
          <div class="st-ic">⭐</div>
          <div class="st-num">{{ stats.days }}</div>
          <div class="st-label">旅行天数</div>
        </div>
      </div>
    </div>

    <!-- 升级进度条 -->
    <div class="section-card level-card pop-in d2">
      <div class="level-head">
        <div>
          <div class="lh-title">升级进度 · 距离 Lv.{{ level + 1 }}</div>
          <div class="lh-sub">再生成 {{ left }} 个行程即可升级 🎉</div>
        </div>
        <div class="lh-pct">{{ pct }}%</div>
      </div>
      <div class="level-bar">
        <div class="level-fill" :style="{ width: pct + '%' }">
          <div class="level-shine"></div>
        </div>
      </div>
      <div class="level-tags">
        <span class="lt lt1">🎯 当前经验：{{ exp }}/100</span>
        <span class="lt lt2">💡 每生成一个行程 +10 经验</span>
      </div>
    </div>

    <!-- 菜单 - 多彩渐变图标 -->
    <div class="section-card menu-card pop-in d2">
      <div class="section-title">🎨 我的服务</div>
      <div class="menu-grid">
        <div
          v-for="(m, i) in menus"
          :key="m.label"
          class="menu-item pop-in"
          :class="'d' + (((i+1)%6)+1)"
          @click="onMenu(m)"
          :style="menuStyle(i)"
        >
          <div class="mi-bg"></div>
          <div class="mi-ic">
            <van-icon :name="m.icon" size="22" color="#fff" />
          </div>
          <div class="mi-label">{{ m.label }}</div>
          <div class="mi-sub">{{ m.sub }}</div>
          <div v-if="m.tag" class="mi-tag">{{ m.tag }}</div>
        </div>
      </div>
    </div>

    <!-- 历史规划列表 -->
    <div class="section-card pop-in d3">
      <div class="sec-head">
        <div class="section-title" style="margin:0">📒 最近规划</div>
        <div class="more" @click="onAll">查看全部 <van-icon name="arrow" size="12" /></div>
      </div>
      <div v-if="plans.length === 0" class="empty-box">
        还没有规划记录，去首页生成一个吧 ✈️
      </div>
      <div v-else class="plan-list">
        <div
          v-for="(p, i) in plans"
          :key="i"
          class="plan-item pop-in"
          :class="'d' + (((i%5)+1))"
          @click="onPlan(p)"
          :style="planStyle(i)"
        >
          <div class="pl-left">
            <div class="pl-emoji">{{ p.emoji }}</div>
          </div>
          <div class="pl-mid">
            <div class="pl-title">{{ p.city }} · {{ p.days }}天</div>
            <div class="pl-tags">
              <span class="plt">💰 ¥{{ p.budget.toLocaleString() }}</span>
              <span class="plt plt2">🗓️ {{ p.date }}</span>
            </div>
            <div class="pl-bottom">
              <span class="pl-chip">{{ p.theme }}</span>
              <span class="pl-score">⭐ {{ p.score }}</span>
            </div>
          </div>
          <van-icon name="arrow" color="#C398E1" />
        </div>
      </div>
    </div>

    <!-- 设置/关于/退出 -->
    <div class="section-card pop-in d3" style="margin-bottom:36px">
      <div class="row-item" v-for="r in rows" :key="r.label" @click="onRow(r)">
        <div class="ri-left">
          <span class="ri-ic" :style="rowIconStyle(r.c)"><van-icon :name="r.icon" color="#fff" size="16" /></span>
          <span class="ri-label">{{ r.label }}</span>
        </div>
        <div class="ri-right">
          <span v-if="r.sub" class="ri-sub">{{ r.sub }}</span>
          <van-icon name="arrow" size="14" color="#b5bac4" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { showToast } from 'vant'

const router = useRouter()

const user = { nickname: '爱旅行的小雪', id: '20260808' }
const stats = { plans: 12, chats: 68, cities: 8, days: 24 }
const exp = 42
const level = computed(() => Math.floor((stats.plans * 10 + 22) / 100) + 1)
const pct = computed(() => (exp % 100))
const left = computed(() => Math.max(0, 100 - pct.value))

const menus = [
  { label: '我的收藏', sub: '4 个目的地', icon: 'star-o',     tag: 'HOT' },
  { label: '足迹地图', sub: '点亮 8 座城', icon: 'location-o', tag: 'NEW' },
  { label: '好友分享', sub: '分享得积分', icon: 'friends-o' },
  { label: 'AI 咨询',   sub: '随时问问题', icon: 'chat-o' },
  { label: '主题推荐', sub: '亲子/蜜月/毕业', icon: 'photograph' },
  { label: '意见反馈', sub: '告诉我们想法', icon: 'comment-o' }
]
const planColors = [
  ['#FF6F91','#FFC75F'],
  ['#84FAB0','#00C9A7'],
  ['#4DD0E1','#5E81F4'],
  ['#C398E1','#845EC2'],
  ['#FF9671','#FF6F91']
]
const menuStyle = (i) => {
  const [c1, c2] = planColors[i % planColors.length]
  return { '--c1': c1, '--c2': c2 }
}
const planStyle = (i) => {
  const [c1, c2] = planColors[i % planColors.length]
  return { '--c1': c1, '--c2': c2 }
}

const plans = [
  { city: '北京', days: 4, budget: 3200, date: '2026-08-01', theme: '亲子游',   emoji: '🏛️', score: 96 },
  { city: '成都', days: 3, budget: 2100, date: '2026-07-20', theme: '美食探',   emoji: '🐼', score: 94 },
  { city: '三亚', days: 5, budget: 6800, date: '2026-07-05', theme: '海岛度假', emoji: '🌴', score: 98 }
]

const rows = [
  { label: '消息通知', sub: '已开启',      icon: 'bell',     c: ['#FF6F91','#FFC75F'] },
  { label: '深色模式', sub: '跟随系统',    icon: 'night',    c: ['#5E81F4','#845EC2'] },
  { label: '语言',     sub: '简体中文',    icon: 'wap-nav',  c: ['#00C9A7','#4DD0E1'] },
  { label: '关于我们', sub: 'v1.0.0',      icon: 'info-o',   c: ['#FF9671','#FFC75F'] },
  { label: '退出登录',                    icon: 'log-out',  c: ['#FF7F7F','#FF6F91'] }
]
const rowIconStyle = (arr) => ({
  background: `linear-gradient(135deg, ${arr[0]}, ${arr[1]})`
})

const onMenu = (m) => {
  if (m.icon === 'chat-o') return router.push('/chat')
  showToast(`已点击「${m.label}」✨`)
}
const onPlan = (p) => {
  showToast(`打开 ${p.city} ${p.days}天 行程 🗺️`)
}
const onAll = () => showToast('查看全部规划 📒')
const onRow = (r) => {
  if (r.label === '退出登录') showToast('已退出登录 👋')
  else showToast(`「${r.label}」敬请期待 ✨`)
}
</script>

<style scoped>
.profile-page { padding-bottom: 40px; }

/* ==== Header ==== */
.pf-header {
  position: relative;
  padding: 54px 16px 24px;
  overflow: hidden;
  padding-bottom: 96px;
}
.pf-bg {
  position: absolute; inset: 0;
  background: linear-gradient(125deg,
    #FF6F91 0%,
    #FF9671 20%,
    #FFC75F 40%,
    #84FAB0 60%,
    #4DD0E1 80%,
    #845EC2 100%);
  background-size: 300% 300%;
  animation: hdrWave 14s ease-in-out infinite;
}
.pf-bg::before {
  content: '';
  position: absolute; inset: 0;
  background:
    radial-gradient(circle at 15% 20%, #ffffff40 0, transparent 40%),
    radial-gradient(circle at 85% 80%, #ffffff28 0, transparent 45%);
}
@keyframes hdrWave {
  0%, 100% { background-position: 0% 50%; }
  50%      { background-position: 100% 50%; }
}

.pf-blobs { position: absolute; inset: 0; pointer-events: none; }
.pfb {
  position: absolute;
  font-size: 22px;
  opacity: 0.55;
  animation: floaty 9s ease-in-out infinite;
}
.pfb.p1 { top: 10%; left: 8%;  animation-delay: -1s; }
.pfb.p2 { top: 14%; right: 10%; font-size: 28px; animation-delay: -2s; }
.pfb.p3 { top: 42%; left: 4%; font-size: 26px; animation-delay: -4s; }
.pfb.p4 { top: 48%; right: 6%; animation-delay: -3s; }
.pfb.p5 { bottom: 10%; left: 16%; animation-delay: -0.5s; }
.pfb.p6 { bottom: 12%; right: 18%; font-size: 26px; animation-delay: -5s; }
.pfb.p7 { top: 60%; left: 46%; animation-delay: -2.5s; }
@keyframes floaty {
  0%, 100% { transform: translate(0, 0) rotate(-4deg); }
  50%      { transform: translate(6px, -14px) rotate(6deg); }
}

/* 用户卡 */
.pf-user-card {
  position: relative;
  z-index: 3;
  display: flex;
  gap: 14px;
  align-items: center;
  padding: 16px 16px;
  background: rgba(255,255,255,0.22);
  backdrop-filter: blur(18px);
  -webkit-backdrop-filter: blur(18px);
  border-radius: 24px;
  border: 1px solid rgba(255,255,255,0.45);
  box-shadow: 0 14px 36px rgba(0,0,0,0.14);
  color: #fff;
  text-shadow: 0 2px 6px rgba(0,0,0,0.2);
}

.pf-avatar-wrap { position: relative; flex-shrink: 0; }
.pf-avatar-ring {
  width: 66px; height: 66px;
  border-radius: 50%;
  padding: 3px;
  background: conic-gradient(#FFC75F, #84FAB0, #4DD0E1, #C398E1, #FF6F91, #FFC75F);
  animation: spinRing 7s linear infinite;
  box-shadow: 0 8px 22px rgba(255,255,255,0.3);
}
@keyframes spinRing { to { transform: rotate(360deg); } }
.pf-avatar {
  width: 100%; height: 100%;
  border-radius: 50%;
  background: linear-gradient(145deg, #fff, #fff8f0);
  display: flex; align-items: center; justify-content: center;
  font-size: 32px;
  animation: spinRing 7s linear infinite reverse;
}

.pf-badge {
  position: absolute;
  right: -4px; bottom: -4px;
  padding: 2px 8px;
  font-size: 10px;
  font-weight: 800;
  border-radius: 999px;
  background: linear-gradient(135deg, #FFC75F, #FF6F91);
  color: #fff;
  border: 2px solid #fff;
  box-shadow: 0 4px 10px rgba(255, 111, 145, 0.4);
}

.pf-user-meta { flex: 1; min-width: 0; }
.pf-name {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 800;
  margin-bottom: 2px;
}
.pf-vip {
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 999px;
  background: linear-gradient(135deg, #FFC75F, #FF9671);
  color: #fff;
  font-weight: 800;
  text-shadow: none;
}
.pf-id { font-size: 11px; opacity: 0.9; margin-bottom: 2px; }
.pf-slogan { font-size: 12px; opacity: 0.95; }

/* Stats */
.pf-stats {
  position: relative; z-index: 3;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
  margin: 18px -2px 0;
}
.stat-card {
  padding: 14px 6px 12px;
  border-radius: 18px;
  background: rgba(255,255,255,0.72);
  backdrop-filter: blur(14px);
  -webkit-backdrop-filter: blur(14px);
  border: 1px solid rgba(255,255,255,0.85);
  box-shadow: 0 10px 26px rgba(132, 94, 194, 0.18);
  text-align: center;
  position: relative;
  overflow: hidden;
}
.stat-card::before {
  content: '';
  position: absolute;
  top: -16px; right: -16px;
  width: 54px; height: 54px;
  border-radius: 50%;
  opacity: 0.3;
}
.stat-card.sc1::before { background: radial-gradient(circle, #FF6F91, transparent 70%); }
.stat-card.sc2::before { background: radial-gradient(circle, #4DD0E1, transparent 70%); }
.stat-card.sc3::before { background: radial-gradient(circle, #FFC75F, transparent 70%); }
.stat-card.sc4::before { background: radial-gradient(circle, #845EC2, transparent 70%); }
.st-ic { font-size: 22px; margin-bottom: 4px; }
.st-num {
  font-size: 20px;
  font-weight: 900;
  background: linear-gradient(135deg, #FF6F91, #845EC2);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  line-height: 1.1;
}
.stat-card.sc1 .st-num { background: linear-gradient(135deg, #FF6F91, #FFC75F); -webkit-background-clip: text; background-clip: text; color: transparent; }
.stat-card.sc2 .st-num { background: linear-gradient(135deg, #00C9A7, #5E81F4); -webkit-background-clip: text; background-clip: text; color: transparent; }
.stat-card.sc3 .st-num { background: linear-gradient(135deg, #FFC75F, #FF9671); -webkit-background-clip: text; background-clip: text; color: transparent; }
.stat-card.sc4 .st-num { background: linear-gradient(135deg, #C398E1, #845EC2); -webkit-background-clip: text; background-clip: text; color: transparent; }
.st-label {
  font-size: 11px;
  color: #6b7080;
  margin-top: 2px;
  font-weight: 600;
}

/* Level card */
.level-card {
  margin-top: -62px;
  position: relative;
  z-index: 6;
}
.level-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}
.lh-title { font-size: 14px; font-weight: 800; color: #2c3e50; }
.lh-sub   { font-size: 11px; color: #8a8f99; margin-top: 2px; }
.lh-pct {
  font-size: 20px;
  font-weight: 900;
  background: linear-gradient(135deg, #FF6F91, #FFC75F, #845EC2);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}
.level-bar {
  height: 14px;
  background: #f1eef9;
  border-radius: 999px;
  overflow: hidden;
  position: relative;
}
.level-fill {
  height: 100%;
  background: linear-gradient(90deg, #FF6F91 0%, #FFC75F 35%, #84FAB0 65%, #4DD0E1 100%);
  background-size: 200% 100%;
  border-radius: 999px;
  position: relative;
  overflow: hidden;
  animation: shift 5s ease infinite;
  transition: width .8s cubic-bezier(.2,1.2,.3,1);
  box-shadow: 0 4px 12px rgba(255, 111, 145, 0.35);
}
@keyframes shift {
  0%, 100% { background-position: 0 0; }
  50%      { background-position: 100% 0; }
}
.level-shine {
  position: absolute;
  top: 0; bottom: 0;
  left: -40%;
  width: 40%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.65), transparent);
  animation: shine 2.2s ease-in-out infinite;
}
@keyframes shine {
  0%   { left: -40%; }
  60%  { left: 110%; }
  100% { left: 110%; }
}

.level-tags {
  margin-top: 10px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
.lt {
  padding: 4px 10px;
  font-size: 11px;
  border-radius: 999px;
  font-weight: 700;
  color: #fff;
}
.lt.lt1 { background: linear-gradient(135deg, #5E81F4, #845EC2); }
.lt.lt2 { background: linear-gradient(135deg, #00C9A7, #4DD0E1); }

.section-title {
  font-size: 16px;
  font-weight: 800;
  color: #2c3e50;
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 14px;
}

/* Menus */
.menu-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}
.menu-item {
  --c1: #FF6F91; --c2: #FFC75F;
  position: relative;
  padding: 16px 8px 12px;
  border-radius: 18px;
  background: linear-gradient(160deg,
    color-mix(in srgb, var(--c1) 12%, white) 0%,
    color-mix(in srgb, var(--c2) 12%, white) 100%);
  border: 1px solid color-mix(in srgb, var(--c1) 20%, white);
  overflow: hidden;
  transition: transform .2s;
}
.menu-item:active { transform: scale(0.97); }
.mi-bg {
  position: absolute;
  top: -30px; right: -30px;
  width: 70px; height: 70px;
  border-radius: 50%;
  background: radial-gradient(circle, var(--c1) 0%, transparent 70%);
  opacity: 0.3;
  pointer-events: none;
}
.mi-ic {
  width: 44px; height: 44px;
  border-radius: 14px;
  background: linear-gradient(135deg, var(--c1), var(--c2));
  display: flex; align-items: center; justify-content: center;
  margin-bottom: 8px;
  box-shadow: 0 6px 14px color-mix(in srgb, var(--c1) 35%, transparent);
  position: relative;
}
.mi-label {
  font-size: 13px;
  font-weight: 800;
  color: #2c3e50;
}
.mi-sub {
  font-size: 10px;
  color: #8a8f99;
  margin-top: 2px;
}
.mi-tag {
  position: absolute;
  top: 8px; right: 8px;
  font-size: 9px;
  padding: 2px 6px;
  border-radius: 999px;
  color: #fff;
  font-weight: 800;
  background: linear-gradient(135deg, #FF6F91, #FF9671);
}
.mi-tag + .mi-tag { display: none; }
.menu-item:nth-child(2) .mi-tag { background: linear-gradient(135deg, #4DD0E1, #5E81F4); }

/* 历史规划列表 */
.sec-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}
.more {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  font-size: 12px;
  color: #845EC2;
  font-weight: 700;
}

.plan-list { display: flex; flex-direction: column; gap: 10px; }
.plan-item {
  --c1: #FF6F91; --c2: #FFC75F;
  display: grid;
  grid-template-columns: 52px 1fr auto;
  align-items: center;
  gap: 12px;
  padding: 12px 12px;
  border-radius: 18px;
  background: linear-gradient(135deg,
    color-mix(in srgb, var(--c1) 12%, white),
    color-mix(in srgb, var(--c2) 12%, white));
  border: 1px solid color-mix(in srgb, var(--c1) 20%, white);
  transition: transform .18s;
}
.plan-item:active { transform: scale(0.98); }
.pl-left {
  width: 52px; height: 52px;
  border-radius: 16px;
  background: linear-gradient(135deg, var(--c1), var(--c2));
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 6px 16px color-mix(in srgb, var(--c1) 35%, transparent);
}
.pl-emoji { font-size: 24px; }
.pl-title {
  font-size: 14px;
  font-weight: 800;
  color: #2c3e50;
  margin-bottom: 4px;
}
.pl-tags {
  display: flex; gap: 8px;
  font-size: 11px;
  color: #6b7080;
  margin-bottom: 6px;
}
.plt {
  padding: 2px 8px;
  border-radius: 999px;
  background: rgba(255, 111, 145, 0.12);
  color: #FF6F91;
  font-weight: 700;
}
.plt.plt2 { background: rgba(94, 129, 244, 0.12); color: #5E81F4; }
.pl-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.pl-chip {
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 999px;
  background: linear-gradient(135deg, var(--c1), var(--c2));
  color: #fff;
  font-weight: 700;
}
.pl-score {
  font-size: 11px;
  color: #FF9671;
  font-weight: 800;
}
.empty-box {
  padding: 26px;
  text-align: center;
  font-size: 12px;
  color: #8a8f99;
  background: linear-gradient(135deg, #FFF4F6, #E4F2FF);
  border-radius: 14px;
}

/* 行列表 */
.row-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 4px;
  border-bottom: 1px dashed #f0ecf8;
}
.row-item:last-child { border-bottom: none; }
.ri-left {
  display: flex;
  align-items: center;
  gap: 10px;
}
.ri-ic {
  width: 30px; height: 30px;
  border-radius: 10px;
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 4px 10px rgba(132, 94, 194, 0.2);
}
.ri-label {
  font-size: 14px;
  font-weight: 600;
  color: #2c3e50;
}
.ri-right {
  display: flex;
  align-items: center;
  gap: 8px;
}
.ri-sub {
  font-size: 12px;
  color: #8a8f99;
  font-weight: 500;
}
</style>