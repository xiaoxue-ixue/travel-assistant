<template>
  <div class="detail-page page-container">
    <van-nav-bar
      title="行程详情"
      left-text="返回"
      left-arrow
      @click-left="$router.back()"
    >
      <template #right>
        <van-icon name="chat-o" size="20" @click="goToChat" />
      </template>
    </van-nav-bar>

    <div v-if="plan" class="detail-content">
      <div class="overview-banner">
        <div class="overview-bg"></div>
        <div class="overview-info">
          <div class="city-name">
            <span class="city-emoji">📍</span>
            <h2>{{ plan.city }}</h2>
          </div>
          <div class="overview-tags">
            <span class="overview-tag">
              <van-icon name="calendar-o" /> {{ plan.days }}天行程
            </span>
            <span class="overview-tag">
              <van-icon name="balance-pay" /> 预算¥{{ formatNumber(plan.totalBudget) }}
            </span>
          </div>
        </div>
      </div>

      <div class="section-card">
        <div class="section-title">每日行程</div>
        <van-collapse v-model="activeNames" accordion>
          <van-collapse-item
            v-for="(day, index) in plan.dailyItinerary"
            :key="index"
            :title="day.date || `第${day.day}天`"
            :name="day.day"
          >
            <template #title>
              <div class="day-title">
                <span class="day-badge">D{{ day.day }}</span>
                <span>{{ day.date || `第${day.day}天` }}</span>
              </div>
            </template>

            <div class="day-content">
              <div class="period-section">
                <div class="period-label morning">
                  <span>☀️</span> 上午
                </div>
                <SpotItem :item="day.morning || {}" period="morning" />
              </div>

              <div class="period-section">
                <div class="period-label afternoon">
                  <span>🌤️</span> 下午
                </div>
                <SpotItem :item="day.afternoon || {}" period="afternoon" />
              </div>

              <div class="period-section">
                <div class="period-label evening">
                  <span>🌙</span> 晚上
                </div>
                <SpotItem :item="day.evening || {}" period="evening" />
              </div>
            </div>
          </van-collapse-item>
        </van-collapse>
      </div>

      <div class="section-card">
        <div class="section-title">预算明细</div>
        <BudgetTable
          :breakdown="plan.budgetBreakdown || {}"
          :total="plan.totalBudget"
        />
      </div>

      <div v-if="plan.tips && plan.tips.length" class="section-card">
        <div class="section-title">
          <span style="color: #07c160;">💡</span> 温馨提示
        </div>
        <ul class="tips-list">
          <li v-for="(tip, i) in plan.tips" :key="i">
            <span class="tip-num">{{ i + 1 }}</span>
            <span class="tip-text">{{ tip }}</span>
          </li>
        </ul>
      </div>

      <div v-if="plan.warnings && plan.warnings.length" class="section-card">
        <div class="section-title">
          <span style="color: #ff976a;">⚠️</span> 注意事项
        </div>
        <ul class="warnings-list">
          <li v-for="(warning, i) in plan.warnings" :key="i">
            <span class="warning-icon">!</span>
            <span class="warning-text">{{ warning }}</span>
          </li>
        </ul>
      </div>

      <div class="bottom-actions">
        <van-button block round size="large" type="primary" @click="goToChat">
          <van-icon name="chat-o" /> 咨询 AI 助手
        </van-button>
        <van-button block round plain size="large" class="mt-12" @click="regenerate">
          <van-icon name="replay" /> 重新规划
        </van-button>
      </div>
    </div>

    <div v-else class="empty-state">
      <div class="empty-icon">🗺️</div>
      <p class="empty-text">暂无行程数据</p>
      <van-button type="primary" round @click="$router.push('/')">
        去生成行程
      </van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showLoadingToast, closeToast } from 'vant'
import { useChatStore } from '@/stores/chat'
import request from '@/utils/request'
import SpotItem from '@/components/SpotItem.vue'
import BudgetTable from '@/components/BudgetTable.vue'

const router = useRouter()
const chatStore = useChatStore()

const activeNames = ref([1])

const plan = computed(() => chatStore.currentPlan)

onMounted(() => {
  if (plan.value && plan.value.dailyItinerary && plan.value.dailyItinerary.length > 0) {
    activeNames.value = [plan.value.dailyItinerary[0].day]
  }
})

const formatNumber = (n) => {
  if (n === null || n === undefined) return '0'
  return Number(n).toLocaleString()
}

const goToChat = () => {
  router.push('/chat')
}

const regenerate = async () => {
  if (!plan.value) return
  showLoadingToast({ message: '正在重新规划...', forbidClick: true })
  try {
    const res = await request.post('/api/travel/recommend', {
      city: plan.value.city,
      budget: plan.value.totalBudget,
      days: plan.value.days
    })
    if (res.data) {
      chatStore.setCurrentPlan(res.data)
      showToast('已重新生成行程')
      if (res.data.dailyItinerary?.length) {
        activeNames.value = [res.data.dailyItinerary[0].day]
      }
    }
  } catch (e) {
    console.error(e)
  } finally {
    closeToast()
  }
}
</script>

<style scoped>
.detail-page {
  padding-bottom: 140px;
}

.overview-banner {
  position: relative;
  margin: 12px 16px;
  border-radius: 16px;
  overflow: hidden;
  padding: 24px 20px;
}

.overview-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 60%, #f093fb 100%);
  opacity: 0.95;
}

.overview-info {
  position: relative;
  z-index: 2;
  color: #fff;
}

.city-name {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.city-emoji {
  font-size: 24px;
}

.city-name h2 {
  font-size: 26px;
  font-weight: 700;
  color: #fff;
  margin: 0;
}

.overview-tags {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.overview-tag {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(10px);
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
}

.day-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 500;
}

.day-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 22px;
  background: linear-gradient(135deg, #1989fa, #5ac8fa);
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  border-radius: 6px;
}

.day-content {
  padding-top: 4px;
}

.period-section {
  margin-bottom: 14px;
}

.period-section:last-child {
  margin-bottom: 0;
}

.period-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 8px;
  padding: 4px 0;
}

.period-label.morning {
  color: #ff8a00;
}

.period-label.afternoon {
  color: #1565c0;
}

.period-label.evening {
  color: #5e35b1;
}

.tips-list,
.warnings-list {
  padding: 0;
  margin: 0;
  list-style: none;
}

.tips-list li {
  display: flex;
  gap: 10px;
  padding: 10px 0;
  border-bottom: 1px solid #f2f3f5;
  line-height: 1.6;
}

.tips-list li:last-child {
  border-bottom: none;
}

.tip-num {
  flex-shrink: 0;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: linear-gradient(135deg, #e8f5e9, #c8e6c9);
  color: #07c160;
  font-size: 12px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 2px;
}

.tip-text {
  font-size: 14px;
  color: #323233;
  flex: 1;
}

.warnings-list li {
  display: flex;
  gap: 10px;
  padding: 10px 0;
  border-bottom: 1px solid #f2f3f5;
  line-height: 1.6;
}

.warnings-list li:last-child {
  border-bottom: none;
}

.warning-icon {
  flex-shrink: 0;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: linear-gradient(135deg, #fff3e0, #ffe0b2);
  color: #ff976a;
  font-size: 13px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 2px;
}

.warning-text {
  font-size: 14px;
  color: #323233;
  flex: 1;
}

.bottom-actions {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 12px 16px calc(env(safe-area-inset-bottom, 0) + 12px);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 -2px 12px rgba(0, 0, 0, 0.08);
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 120px 40px;
  gap: 20px;
}

.empty-icon {
  font-size: 80px;
  opacity: 0.5;
}

.empty-text {
  font-size: 15px;
  color: #969799;
}
</style>
