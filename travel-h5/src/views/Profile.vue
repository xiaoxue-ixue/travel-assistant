<template>
  <div class="profile-page page-container">
    <div class="profile-header">
      <div class="header-bg"></div>
      <div class="user-info">
        <div class="avatar">
          <span>🧳</span>
        </div>
        <div class="user-detail">
          <h2 class="user-name">旅游爱好者</h2>
          <p class="user-sign">世界那么大，我想去看看 🌍</p>
        </div>
      </div>

      <div class="stats-row">
        <div class="stat-item">
          <div class="stat-num">{{ stats.plans }}</div>
          <div class="stat-label">生成行程</div>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <div class="stat-num">{{ stats.cities }}</div>
          <div class="stat-label">探索城市</div>
        </div>
        <div class="stat-divider"></div>
        <div class="stat-item">
          <div class="stat-num">{{ stats.chats }}</div>
          <div class="stat-label">AI对话</div>
        </div>
      </div>
    </div>

    <div class="section-card">
      <div class="section-title">我的行程</div>
      <div v-if="currentPlan" class="current-plan-card" @click="$router.push('/detail')">
        <div class="plan-card-left">
          <div class="plan-city">{{ currentPlan.city }}</div>
          <div class="plan-meta">
            <span>{{ currentPlan.days }}天</span>
            <span class="dot">·</span>
            <span>¥{{ formatNumber(currentPlan.totalBudget) }}</span>
          </div>
        </div>
        <div class="plan-card-right">
          <van-button type="primary" size="mini" round plain>
            查看详情
          </van-button>
        </div>
      </div>
      <div v-else class="no-plan">
        <van-empty description="还没有行程，去生成一个吧～">
          <van-button type="primary" round size="small" @click="$router.push('/')">
            立即规划
          </van-button>
        </van-empty>
      </div>
    </div>

    <div class="section-card">
      <div class="section-title">功能菜单</div>
      <van-cell-group inset :border="false">
        <van-cell
          title="行程规划"
          icon="location-o"
          is-link
          @click="$router.push('/')"
        >
          <template #icon>
            <div class="menu-icon" style="background: linear-gradient(135deg, #1989fa, #5ac8fa);">
              <van-icon name="location-o" color="#fff" />
            </div>
          </template>
        </van-cell>
        <van-cell
          title="AI 咨询"
          is-link
          @click="$router.push('/chat')"
        >
          <template #icon>
            <div class="menu-icon" style="background: linear-gradient(135deg, #07c160, #5ed097);">
              <van-icon name="chat-o" color="#fff" />
            </div>
          </template>
        </van-cell>
        <van-cell
          title="热门目的地"
          is-link
          @click="showHotDestinations"
        >
          <template #icon>
            <div class="menu-icon" style="background: linear-gradient(135deg, #ff976a, #ffb347);">
              <van-icon name="fire-o" color="#fff" />
            </div>
          </template>
        </van-cell>
        <van-cell
          title="旅行清单"
          is-link
          @click="showFeatureSoon('旅行清单')"
        >
          <template #icon>
            <div class="menu-icon" style="background: linear-gradient(135deg, #7232dd, #a86ddf);">
              <van-icon name="orders-o" color="#fff" />
            </div>
          </template>
        </van-cell>
      </van-cell-group>
    </div>

    <div class="section-card">
      <div class="section-title">其他</div>
      <van-cell-group inset :border="false">
        <van-cell
          title="使用指南"
          is-link
          @click="showGuide"
        >
          <template #icon>
            <div class="menu-icon" style="background: linear-gradient(135deg, #00c4b6, #5ce1d7);">
              <van-icon name="info-o" color="#fff" />
            </div>
          </template>
        </van-cell>
        <van-cell
          title="关于我们"
          is-link
          @click="showAbout"
        >
          <template #icon>
            <div class="menu-icon" style="background: linear-gradient(135deg, #ee0a24, #ff6034);">
              <van-icon name="friends-o" color="#fff" />
            </div>
          </template>
        </van-cell>
      </van-cell-group>
    </div>

    <div class="app-version">v1.0.0 · 智能旅游助手</div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { showDialog, showToast } from 'vant'
import { useChatStore } from '@/stores/chat'

const chatStore = useChatStore()

const stats = reactive({
  plans: 0,
  cities: 0,
  chats: 0
})

const currentPlan = computed(() => chatStore.currentPlan)

const formatNumber = (n) => {
  if (n === null || n === undefined) return '0'
  return Number(n).toLocaleString()
}

const showGuide = () => {
  showDialog({
    title: '使用指南',
    message: '1. 在首页选择目的地、预算和天数，点击开始规划\n2. AI会为您生成详细的旅游行程\n3. 在详情页可查看每日行程、预算明细、温馨提示等\n4. 在AI咨询页可随时提问任何旅游相关问题\n5. 问题回复支持流式显示，体验更好',
    confirmButtonText: '知道了',
    showCancelButton: false
  })
}

const showAbout = () => {
  showDialog({
    title: '关于我们',
    message: '智能旅游助手 v1.0.0\n\n基于大语言模型的智能旅游规划平台，为用户提供个性化的旅游行程推荐和实时旅游咨询服务。\n\n技术栈：Vue 3 + Vant UI + Spring Boot 3',
    confirmButtonText: '知道了',
    showCancelButton: false,
    allowHtml: false
  })
}

const showHotDestinations = () => {
  showToast('即将前往热门目的地推荐')
}

const showFeatureSoon = (name) => {
  showToast(`「${name}」功能即将上线，敬请期待～`)
}
</script>

<style scoped>
.profile-page {
  padding-bottom: 40px;
}

.profile-header {
  position: relative;
  padding: 40px 20px 0;
  margin-bottom: 20px;
  overflow: hidden;
}

.header-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
  border-bottom-left-radius: 24px;
  border-bottom-right-radius: 24px;
}

.user-info {
  position: relative;
  z-index: 2;
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 10px 0 24px;
}

.avatar {
  width: 66px;
  height: 66px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.25);
  backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  border: 2px solid rgba(255, 255, 255, 0.4);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.user-detail {
  flex: 1;
  color: #fff;
}

.user-name {
  font-size: 20px;
  font-weight: 700;
  margin: 0 0 4px;
}

.user-sign {
  font-size: 13px;
  opacity: 0.85;
  margin: 0;
}

.stats-row {
  position: relative;
  z-index: 2;
  display: flex;
  background: #fff;
  border-radius: 16px;
  padding: 18px 10px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  margin-bottom: -10px;
}

.stat-item {
  flex: 1;
  text-align: center;
}

.stat-num {
  font-size: 22px;
  font-weight: 700;
  color: #323233;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #969799;
}

.stat-divider {
  width: 1px;
  background: #ebedf0;
  align-self: stretch;
  margin: 4px 0;
}

.menu-icon {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 4px;
}

.current-plan-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px;
  background: linear-gradient(135deg, #f5f7ff 0%, #e8efff 100%);
  border-radius: 12px;
  border: 1px solid #d4e0ff;
  cursor: pointer;
}

.plan-city {
  font-size: 16px;
  font-weight: 600;
  color: #1989fa;
  margin-bottom: 4px;
}

.plan-meta {
  font-size: 13px;
  color: #646566;
}

.dot {
  margin: 0 4px;
  color: #c8c9cc;
}

.no-plan {
  padding: 20px 0 10px;
}

.app-version {
  text-align: center;
  font-size: 12px;
  color: #c8c9cc;
  margin-top: 30px;
  padding-bottom: 10px;
}
</style>
