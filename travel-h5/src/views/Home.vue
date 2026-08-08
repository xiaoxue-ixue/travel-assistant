<template>
  <div class="home-page page-container">
    <div class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">✈️ 智能旅游助手</h1>
        <p class="hero-subtitle">AI 为您定制专属旅游行程</p>
      </div>
      <div class="hero-decoration">
        <span class="deco-item" style="top: 10%; left: 10%; font-size: 28px;">🏖️</span>
        <span class="deco-item" style="top: 20%; right: 15%; font-size: 24px;">🗺️</span>
        <span class="deco-item" style="bottom: 20%; left: 15%; font-size: 22px;">🏔️</span>
        <span class="deco-item" style="bottom: 15%; right: 10%; font-size: 26px;">🌸</span>
      </div>
    </div>

    <div class="section-card form-card">
      <div class="section-title">开始规划</div>

      <div class="form-item">
        <label class="form-label">
          <van-icon name="location-o" /> 目的地城市
        </label>
        <van-field
          v-model="formData.city"
          is-link
          readonly
          placeholder="请选择目的地"
          @click="showCityPicker = true"
        />
      </div>

      <div class="form-item">
        <label class="form-label">
          <van-icon name="balance-pay" /> 总预算 (元)
        </label>
        <van-field
          v-model.number="formData.budget"
          type="number"
          placeholder="请输入预算，最低100元"
          :rules="[{ required: true, message: '请输入预算' }, { min: 100, message: '预算不能低于100元' }]"
        />
        <van-slider
          v-model="formData.budget"
          :min="100"
          :max="50000"
          :step="100"
          class="budget-slider"
          bar-height="4px"
          active-color="#1989fa"
        />
        <div class="slider-labels">
          <span>¥100</span>
          <span>¥50,000</span>
        </div>
      </div>

      <div class="form-item">
        <label class="form-label">
          <van-icon name="calendar-o" /> 出游天数
        </label>
        <van-stepper
          v-model="formData.days"
          :min="1"
          :max="30"
          input-width="60px"
          button-size="28px"
        />
      </div>

      <van-button
        block
        round
        type="primary"
        size="large"
        class="submit-btn"
        :loading="submitting"
        loading-text="AI正在规划中..."
        @click="handleSubmit"
      >
        🚀 开始规划
      </van-button>
    </div>

    <div class="section-card">
      <div class="section-title">热门目的地</div>
      <div class="hot-cities">
        <div
          v-for="city in hotCities"
          :key="city.name"
          class="city-card"
          @click="selectHotCity(city.name)"
        >
          <div class="city-emoji">{{ city.emoji }}</div>
          <div class="city-name">{{ city.name }}</div>
        </div>
      </div>
    </div>

    <div class="section-card quick-actions">
      <div class="section-title">快捷功能</div>
      <div class="action-grid">
        <div class="action-item" @click="$router.push('/chat')">
          <div class="action-icon" style="background: linear-gradient(135deg, #1989fa, #5ac8fa);">
            <van-icon name="chat-o" size="22" color="#fff" />
          </div>
          <span>AI 咨询</span>
        </div>
        <div class="action-item" @click="$router.push('/profile')">
          <div class="action-icon" style="background: linear-gradient(135deg, #07c160, #5ed097);">
            <van-icon name="user-o" size="22" color="#fff" />
          </div>
          <span>个人中心</span>
        </div>
        <div class="action-item" @click="showTips">
          <div class="action-icon" style="background: linear-gradient(135deg, #ff976a, #ffb347);">
            <van-icon name="info-o" size="22" color="#fff" />
          </div>
          <span>使用指南</span>
        </div>
        <div class="action-item" @click="scrollToTop">
          <div class="action-icon" style="background: linear-gradient(135deg, #7232dd, #a86ddf);">
            <van-icon name="arrow-up" size="22" color="#fff" />
          </div>
          <span>回到顶部</span>
        </div>
      </div>
    </div>

    <van-popup v-model:show="showCityPicker" round position="bottom" :style="{ height: '60%' }">
      <div class="picker-header">
        <span class="picker-title">选择城市</span>
        <van-icon name="cross" size="22" @click="showCityPicker = false" />
      </div>
      <div class="city-search">
        <van-search
          v-model="searchKeyword"
          shape="round"
          placeholder="搜索城市名"
          :show-action="false"
        />
      </div>
      <div class="city-list">
        <div
          v-for="city in filteredCities"
          :key="city"
          class="city-list-item"
          @click="selectCity(city)"
        >
          <van-icon name="location-o" color="#1989fa" />
          <span>{{ city }}</span>
          <van-icon v-if="formData.city === city" name="success" color="#07c160" />
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, computed, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showDialog } from 'vant'
import request from '@/utils/request'
import { useChatStore } from '@/stores/chat'

const router = useRouter()
const chatStore = useChatStore()

const popularCities = [
  '北京', '上海', '广州', '深圳', '成都', '杭州', '西安', '重庆',
  '南京', '武汉', '苏州', '长沙', '天津', '郑州', '济南', '青岛',
  '大连', '沈阳', '哈尔滨', '长春', '福州', '厦门', '南昌', '合肥',
  '昆明', '贵阳', '南宁', '桂林', '海口', '三亚', '丽江', '大理',
  '兰州', '乌鲁木齐', '拉萨', '呼和浩特', '太原', '石家庄'
]

const hotCities = [
  { name: '北京', emoji: '🏛️' },
  { name: '上海', emoji: '🌆' },
  { name: '成都', emoji: '🐼' },
  { name: '西安', emoji: '🏺' },
  { name: '杭州', emoji: '🍃' },
  { name: '三亚', emoji: '🌴' },
  { name: '重庆', emoji: '🌶️' },
  { name: '厦门', emoji: '🌊' }
]

const formData = reactive({
  city: '',
  budget: 3000,
  days: 3
})

const showCityPicker = ref(false)
const searchKeyword = ref('')
const submitting = ref(false)

const filteredCities = computed(() => {
  if (!searchKeyword.value) return popularCities
  const kw = searchKeyword.value.toLowerCase()
  return popularCities.filter(c => c.toLowerCase().includes(kw))
})

const selectCity = (city) => {
  formData.city = city
  showCityPicker.value = false
}

const selectHotCity = (city) => {
  formData.city = city
  showToast(`已选择${city}`)
}

const validate = () => {
  if (!formData.city) {
    showToast('请选择目的地城市')
    return false
  }
  if (!formData.budget || formData.budget < 100) {
    showToast('预算不能低于100元')
    return false
  }
  if (!formData.days || formData.days < 1 || formData.days > 30) {
    showToast('天数需在1-30天之间')
    return false
  }
  return true
}

const handleSubmit = async () => {
  if (!validate()) return
  submitting.value = true

  try {
    const res = await request.post('/api/travel/recommend', {
      city: formData.city,
      budget: formData.budget,
      days: formData.days
    })

    if (res.data) {
      chatStore.setCurrentPlan(res.data)
      router.push('/detail')
    } else {
      showToast('生成规划失败，请重试')
    }
  } catch (e) {
    console.error('规划请求失败:', e)
  } finally {
    submitting.value = false
  }
}

const showTips = () => {
  showDialog({
    title: '使用指南',
    message: '1. 选择您想去的城市\n2. 设置您的旅游预算\n3. 选择出游天数\n4. 点击"开始规划"，AI将为您生成专属行程\n5. 还可在AI咨询中随时提问旅游相关问题',
    confirmButtonText: '知道了',
    showCancelButton: false
  })
}

const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}
</script>

<style scoped>
.home-page {
  padding-bottom: 40px;
}

.hero-section {
  position: relative;
  background: linear-gradient(135deg, #1989fa 0%, #5ac8fa 60%, #a0d8f0 100%);
  padding: 50px 20px 70px;
  overflow: hidden;
  border-bottom-left-radius: 30px;
  border-bottom-right-radius: 30px;
}

.hero-content {
  position: relative;
  z-index: 2;
  text-align: center;
  color: #fff;
}

.hero-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 8px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.hero-subtitle {
  font-size: 15px;
  opacity: 0.9;
}

.hero-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.deco-item {
  position: absolute;
  opacity: 0.5;
  animation: float 4s ease-in-out infinite;
}

.deco-item:nth-child(2) { animation-delay: 0.5s; }
.deco-item:nth-child(3) { animation-delay: 1s; }
.deco-item:nth-child(4) { animation-delay: 1.5s; }

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

.form-card {
  margin-top: -30px;
  position: relative;
  z-index: 10;
}

.form-item {
  margin-bottom: 18px;
}

.form-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 600;
  color: #323233;
  margin-bottom: 8px;
  padding-left: 2px;
}

.budget-slider {
  margin: 12px 8px 6px;
}

.slider-labels {
  display: flex;
  justify-content: space-between;
  font-size: 11px;
  color: #969799;
  padding: 0 4px;
}

.submit-btn {
  margin-top: 10px;
  height: 48px;
  font-size: 16px;
  background: linear-gradient(135deg, #1989fa 0%, #5ac8fa 100%);
  border: none;
  box-shadow: 0 4px 16px rgba(25, 137, 250, 0.35);
}

.hot-cities {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.city-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 12px 4px;
  background: #fafbfc;
  border-radius: 12px;
  transition: all 0.2s;
  cursor: pointer;
}

.city-card:active {
  transform: scale(0.95);
  background: #e8f3ff;
}

.city-emoji {
  font-size: 26px;
  margin-bottom: 6px;
}

.city-name {
  font-size: 13px;
  color: #323233;
  font-weight: 500;
}

.quick-actions .action-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.action-icon {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.action-item span {
  font-size: 12px;
  color: #646566;
}

.picker-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border-bottom: 1px solid #ebedf0;
  font-weight: 600;
  font-size: 16px;
}

.city-search {
  padding: 8px 16px;
}

.city-list {
  max-height: calc(60vh - 160px);
  overflow-y: auto;
  padding-bottom: 20px;
}

.city-list-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 16px;
  border-bottom: 1px solid #f2f3f5;
  font-size: 15px;
}

.city-list-item .van-icon:last-child {
  margin-left: auto;
}
</style>
