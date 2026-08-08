<template>
  <div class="home-page page-container">

    <!-- 多巴胺彩虹渐变 Hero -->
    <div class="hero-section">
      <div class="hero-blob blob1"></div>
      <div class="hero-blob blob2"></div>
      <div class="hero-blob blob3"></div>

      <div class="hero-content">
        <div class="hero-logo floaty">✈️</div>
        <h1 class="hero-title">智能旅游助手</h1>
        <p class="hero-subtitle">🌈 AI 为您定制专属旅游行程</p>
        <div class="hero-tag">
          <span class="tag-chip tag-pink">🗺️ 个性化</span>
          <span class="tag-chip tag-yellow">💰 控预算</span>
          <span class="tag-chip tag-mint">⏰ 省时间</span>
        </div>
      </div>

      <div class="hero-decoration" aria-hidden>
        <span class="deco-item floaty">🏖️</span>
        <span class="deco-item floaty d1">🗼</span>
        <span class="deco-item floaty d2">🌺</span>
        <span class="deco-item floaty d3">🏔️</span>
        <span class="deco-item floaty d4">🎈</span>
        <span class="deco-emoji e1">🐳</span>
        <span class="deco-emoji e2">🦋</span>
        <span class="deco-emoji e3">🌸</span>
      </div>
    </div>

    <div class="section-card form-card pop-in d1">
      <div class="section-title">🎯 开始规划</div>

      <div class="form-item">
        <label class="form-label">
          <span class="label-dot dot-pink"></span>
          <van-icon name="location-o" /> 目的地城市
        </label>
        <van-field
          v-model="formData.city"
          is-link
          readonly
          placeholder="请选择目的地"
          class="form-field"
          @click="showCityPicker = true"
        />
      </div>

      <div class="form-item pop-in d2">
        <label class="form-label">
          <span class="label-dot dot-yellow"></span>
          <van-icon name="balance-pay" /> 总预算 (元)
        </label>
        <div class="budget-display">
          <span class="budget-num">¥ {{ formData.budget.toLocaleString() }}</span>
          <span class="budget-tip">{{ budgetLevel }}</span>
        </div>
        <van-slider
          v-model="formData.budget"
          :min="100"
          :max="50000"
          :step="100"
          class="budget-slider"
          bar-height="6px"
        />
        <div class="slider-labels">
          <span>¥100 · 穷游</span>
          <span>¥25,000 · 奢华</span>
        </div>
      </div>

      <div class="form-item pop-in d3">
        <label class="form-label">
          <span class="label-dot dot-mint"></span>
          <van-icon name="calendar-o" /> 出游天数
        </label>
        <div class="days-stepper-wrap">
          <van-stepper
            v-model="formData.days"
            :min="1"
            :max="30"
            input-width="80px"
            button-size="34px"
            theme="round"
          />
          <div class="days-hint">
            <em v-if="formData.days <= 2">短假快闪 🌿</em>
            <em v-else-if="formData.days <= 5">正正好 ⭐</em>
            <em v-else-if="formData.days <= 10">深度漫游 🚞</em>
            <em v-else>史诗旅程 🗺️</em>
          </div>
        </div>
      </div>

      <van-button
        block
        round
        type="primary"
        size="large"
        class="submit-btn"
        :loading="submitting"
        loading-text="AI 规划中 ·"
        @click="handleSubmit"
      >
        <span class="btn-inner">🚀 开始规划我的旅程</span>
      </van-button>
    </div>

    <div class="section-card pop-in d2">
      <div class="section-title">🔥 热门目的地</div>
      <div class="hot-cities">
        <div
          v-for="(city, i) in hotCities"
          :key="city.name"
          :class="['city-card pop-in', 'd' + ((i % 8) + 1)]"
          :style="cityColorVars(city.color)"
          @click="selectHotCity(city.name)"
        >
          <div class="city-glow" aria-hidden></div>
          <div class="city-emoji">{{ city.emoji }}</div>
          <div class="city-name">{{ city.name }}</div>
          <div class="city-tag">{{ city.tag }}</div>
        </div>
      </div>
    </div>

    <div class="section-card quick-actions pop-in d3">
      <div class="section-title">✨ 快捷功能</div>
      <div class="action-grid">
        <div class="action-item" @click="$router.push('/chat')">
          <div class="action-icon ai-gradient">
            <van-icon name="chat-o" size="22" color="#fff" />
          </div>
          <span>AI 咨询</span>
        </div>
        <div class="action-item" @click="$router.push('/profile')">
          <div class="action-icon me-gradient">
            <van-icon name="user-o" size="22" color="#fff" />
          </div>
          <span>个人中心</span>
        </div>
        <div class="action-item" @click="showTips">
          <div class="action-icon info-gradient">
            <van-icon name="info-o" size="22" color="#fff" />
          </div>
          <span>使用指南</span>
        </div>
        <div class="action-item" @click="scrollToTop">
          <div class="action-icon top-gradient">
            <van-icon name="arrow-up" size="22" color="#fff" />
          </div>
          <span>回到顶部</span>
        </div>
      </div>
    </div>

    <van-popup v-model:show="showCityPicker" round position="bottom" :style="{ height: '60%' }">
      <div class="picker-header">
        <span class="picker-title">🌈 选择目的地</span>
        <van-icon name="cross" size="22" @click="showCityPicker = false" />
      </div>
      <div class="city-search">
        <van-search
          v-model="searchKeyword"
          shape="round"
          placeholder="搜索城市名，如：成都"
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
          <span class="list-dot"></span>
          <van-icon name="location-o" color="#FF6F91" />
          <span>{{ city }}</span>
          <van-icon v-if="formData.city === city" name="success" color="#00C9A7" />
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
  { name: '北京', emoji: '🏛️', color: ['#FF6F91', '#FFC75F'], tag: '千年古都' },
  { name: '上海', emoji: '🌆', color: ['#5E81F4', '#4DD0E1'], tag: '魔都繁华' },
  { name: '成都', emoji: '🐼', color: ['#84FAB0', '#00C9A7'], tag: '慢生活' },
  { name: '西安', emoji: '🏺', color: ['#FF9671', '#FF7F7F'], tag: '盛唐文化' },
  { name: '杭州', emoji: '🍃', color: ['#4DD0E1', '#84FAB0'], tag: '西湖天堂' },
  { name: '三亚', emoji: '🌴', color: ['#FFC75F', '#F9F871'], tag: '海岛度假' },
  { name: '重庆', emoji: '🌶️', color: ['#FF7F7F', '#845EC2'], tag: '8D魔幻' },
  { name: '厦门', emoji: '🌊', color: ['#C398E1', '#5E81F4'], tag: '文艺海岛' }
]

const formData = reactive({
  city: '',
  budget: 3000,
  days: 3
})

const showCityPicker = ref(false)
const searchKeyword = ref('')
const submitting = ref(false)

const budgetLevel = computed(() => {
  const b = formData.budget
  if (b < 1000) return '穷游党'
  if (b < 3000) return '小清新'
  if (b < 8000) return '品质派'
  if (b < 20000) return '豪享玩'
  return '奢华行'
})

const filteredCities = computed(() => {
  if (!searchKeyword.value) return popularCities
  const kw = searchKeyword.value.toLowerCase()
  return popularCities.filter(c => c.toLowerCase().includes(kw))
})

const cityColorVars = (colors) => ({
  '--city-from': colors[0],
  '--city-to':   colors[1]
})

const selectCity = (city) => {
  formData.city = city
  showCityPicker.value = false
}

const selectHotCity = (city) => {
  formData.city = city
  showToast(`已选择 · ${city} 🎉`)
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
    title: '🌈 使用指南',
    message: '1. 选择您想去的城市\n2. 拖动滑块设置旅游预算\n3. 增减按钮选择出游天数\n4. 点击「开始规划我的旅程」\n5. 等待 AI 生成专属行程\n6. 遇到问题去 AI 咨询随时问！',
    confirmButtonText: '我知道啦 🎉',
    confirmButtonColor: '#FF6F91',
    showCancelButton: false
  })
}

const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}
</script>

<style scoped>
.home-page { padding-bottom: 60px; }

.hero-section {
  position: relative;
  padding: 48px 20px 92px;
  overflow: hidden;
  background: linear-gradient(135deg,
    #FF6F91 0%, #FF9671 18%, #FFC75F 36%, #84FAB0 55%, #4DD0E1 75%, #845EC2 100%);
  background-size: 300% 300%;
  animation: heroWave 12s ease-in-out infinite;
  border-bottom-left-radius: 38px;
  border-bottom-right-radius: 38px;
  box-shadow: 0 14px 44px rgba(132, 94, 194, 0.28);
}
@keyframes heroWave {
  0%, 100% { background-position: 0% 50%; }
  50%      { background-position: 100% 50%; }
}

.hero-blob {
  position: absolute;
  border-radius: 50%;
  filter: blur(40px);
  opacity: 0.45;
  pointer-events: none;
}
.hero-blob.blob1 {
  width: 240px; height: 240px;
  left: -60px; top: -60px;
  background: #F9F871;
  animation: blobFloat 11s ease-in-out infinite;
}
.hero-blob.blob2 {
  width: 200px; height: 200px;
  right: -40px; top: 30%;
  background: #C398E1;
  animation: blobFloat 9s ease-in-out infinite reverse;
}
.hero-blob.blob3 {
  width: 160px; height: 160px;
  left: 30%; bottom: -40px;
  background: #84FAB0;
  animation: blobFloat 13s ease-in-out infinite;
}
@keyframes blobFloat {
  0%, 100% { transform: translate(0,0) scale(1); }
  50%      { transform: translate(14px, -18px) scale(1.08); }
}

.hero-content {
  position: relative;
  z-index: 3;
  text-align: center;
  color: #fff;
  text-shadow: 0 4px 16px rgba(0,0,0,0.18);
}
.hero-logo {
  width: 72px; height: 72px;
  margin: 0 auto 14px;
  background: rgba(255,255,255,0.28);
  border: 2px solid rgba(255,255,255,0.55);
  border-radius: 24px;
  display: flex; align-items: center; justify-content: center;
  font-size: 38px;
  backdrop-filter: blur(8px);
  box-shadow: 0 8px 28px rgba(0,0,0,0.15);
}
.hero-title {
  font-size: 30px;
  font-weight: 800;
  margin-bottom: 8px;
  letter-spacing: 2px;
}
.hero-subtitle {
  font-size: 15px;
  opacity: 0.95;
  margin-bottom: 18px;
}
.hero-tag {
  display: flex;
  justify-content: center;
  gap: 8px;
  flex-wrap: wrap;
}
.tag-chip {
  display: inline-block;
  padding: 5px 14px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
  color: #fff;
  backdrop-filter: blur(6px);
  background: rgba(255,255,255,0.22);
  border: 1px solid rgba(255,255,255,0.4);
}
.tag-pink   { background: rgba(255, 111, 145, 0.65); }
.tag-yellow { background: rgba(255, 199, 95, 0.7); }
.tag-mint   { background: rgba(0, 201, 167, 0.65); }

.hero-decoration { position: absolute; inset: 0; pointer-events: none; z-index: 2; }
.deco-item {
  position: absolute;
  font-size: 26px;
  opacity: 0.6;
}
.deco-item:nth-child(1) { top: 12%; left: 8%; font-size: 30px; }
.deco-item:nth-child(2) { top: 18%; right: 10%; font-size: 24px; }
.deco-item:nth-child(3) { bottom: 30%; left: 12%; font-size: 20px; }
.deco-item:nth-child(4) { top: 55%; right: 14%; font-size: 22px; }
.deco-item:nth-child(5) { bottom: 20%; right: 20%; font-size: 24px; }
.deco-emoji {
  position: absolute;
  font-size: 18px;
  opacity: 0.5;
  animation: bubble 7s ease-in-out infinite;
}
.deco-emoji.e1 { top: 34%; left: 42%; animation-delay: 1.5s; }
.deco-emoji.e2 { bottom: 26%; left: 48%; animation-delay: 2.5s; }
.deco-emoji.e3 { top: 8%;  left: 50%; animation-delay: 0.7s; }
@keyframes bubble {
  0%, 100% { transform: translateY(0) scale(1); opacity: .5; }
  50%      { transform: translateY(-20px) scale(1.2); opacity: .8; }
}

.form-card {
  margin-top: -52px;
  position: relative;
  z-index: 10;
  border: 1px solid rgba(255,255,255,0.9);
}

.form-item { margin-bottom: 20px; }
.form-label {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 10px;
}
.label-dot {
  display: inline-block;
  width: 10px; height: 10px;
  border-radius: 50%;
  box-shadow: 0 2px 6px rgba(0,0,0,0.18);
}
.dot-pink   { background: linear-gradient(135deg, #FF6F91, #FFC75F); }
.dot-yellow { background: linear-gradient(135deg, #FFC75F, #84FAB0); }
.dot-mint   { background: linear-gradient(135deg, #00C9A7, #4DD0E1); }

.form-field {
  background: linear-gradient(135deg, #FFF 0%, #FFF8FC 100%);
  border-radius: 16px !important;
}

.budget-display {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  padding: 10px 16px 6px;
  background: linear-gradient(135deg, #FFF4F6, #F0F7FF);
  border-radius: 14px;
  margin-bottom: 8px;
}
.budget-num {
  font-size: 22px;
  font-weight: 800;
  background: linear-gradient(135deg, #FF6F91, #845EC2);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}
.budget-tip {
  font-size: 12px;
  padding: 3px 10px;
  border-radius: 999px;
  background: linear-gradient(135deg, #FFC75F, #FF9671);
  color: #fff;
  font-weight: 700;
}

.budget-slider { margin: 4px 6px 6px; }
.budget-slider :deep(.van-slider__bar) {
  background: linear-gradient(90deg, #FF6F91 0%, #FFC75F 40%, #4DD0E1 100%) !important;
  border-radius: 999px;
  height: 6px;
}
.budget-slider :deep(.van-slider__button) {
  background: #fff !important;
  border: 3px solid #FF6F91;
  width: 22px; height: 22px;
  box-shadow: 0 4px 14px rgba(255, 111, 145, 0.5);
}

.slider-labels {
  display: flex;
  justify-content: space-between;
  font-size: 11px;
  color: #8a8f99;
  padding: 0 4px;
  font-weight: 500;
}

.days-stepper-wrap {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 14px;
  background: linear-gradient(135deg, #E9FFF3, #E4F2FF);
  border-radius: 16px;
}
.days-hint em {
  font-style: normal;
  font-weight: 800;
  background: linear-gradient(135deg, #00C9A7, #5E81F4);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
  font-size: 13px;
}
.days-stepper-wrap :deep(.van-stepper__minus),
.days-stepper-wrap :deep(.van-stepper__plus) {
  background: linear-gradient(135deg, #84FAB0, #4DD0E1);
  color: #fff;
  border: none;
  font-weight: 800;
  box-shadow: 0 4px 10px rgba(77, 208, 225, 0.35);
}
.days-stepper-wrap :deep(.van-stepper__input) {
  font-weight: 800;
  color: #2c3e50;
}

.submit-btn {
  margin-top: 10px;
  height: 52px;
  font-size: 16px;
  border: none;
  background: linear-gradient(120deg,
    #FF6F91 0%, #FF9671 25%, #FFC75F 50%, #4DD0E1 75%, #845EC2 100%);
  background-size: 200% 200%;
  animation: btnShift 5s ease infinite;
  color: #fff;
  border-radius: 28px;
  font-weight: 800;
  box-shadow: 0 12px 30px rgba(255, 111, 145, 0.35);
  transition: transform .15s ease, box-shadow .25s ease;
}
.submit-btn:active {
  transform: scale(0.97);
  box-shadow: 0 6px 16px rgba(132, 94, 194, 0.35);
}
.btn-inner { letter-spacing: 1px; }
@keyframes btnShift {
  0%, 100% { background-position: 0% 50%; }
  50%      { background-position: 100% 50%; }
}

.hot-cities {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.city-card {
  --city-from: #FF6F91;
  --city-to:   #FFC75F;
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 14px 4px 12px;
  border-radius: 18px;
  background: linear-gradient(160deg,
    color-mix(in srgb, var(--city-from) 16%, white) 0%,
    color-mix(in srgb, var(--city-to)   14%, white) 100%);
  border: 1px solid color-mix(in srgb, var(--city-from) 18%, white);
  overflow: hidden;
  cursor: pointer;
  transition: all .25s cubic-bezier(.2,.9,.3,1);
}
.city-card::before {
  content: '';
  position: absolute;
  top: -50%; right: -50%;
  width: 100px; height: 100px;
  background: radial-gradient(circle, var(--city-from) 0%, transparent 70%);
  opacity: 0.28;
  transition: opacity .3s;
}
.city-card:hover { transform: translateY(-4px) scale(1.03); }
.city-card:active { transform: scale(0.96); }
.city-card:hover::before { opacity: 0.45; }

.city-glow {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg,
    color-mix(in srgb, var(--city-from) 22%, transparent),
    color-mix(in srgb, var(--city-to)   22%, transparent));
  opacity: 0.0;
  transition: opacity .3s;
  pointer-events: none;
}
.city-card:hover .city-glow { opacity: 1; }

.city-emoji {
  font-size: 28px;
  margin-bottom: 6px;
  filter: drop-shadow(0 4px 8px rgba(0,0,0,0.15));
  transition: transform .3s;
  position: relative;
  z-index: 1;
}
.city-card:hover .city-emoji { transform: scale(1.2) rotate(-6deg); }

.city-name {
  font-size: 13px;
  color: #2c3e50;
  font-weight: 800;
  z-index: 1;
}
.city-tag {
  margin-top: 2px;
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 999px;
  background: linear-gradient(135deg, var(--city-from), var(--city-to));
  color: #fff;
  font-weight: 700;
  z-index: 1;
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
  transition: transform .2s;
}
.action-item:active { transform: scale(0.95); }

.action-icon {
  width: 52px; height: 52px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6px 18px rgba(0,0,0,0.12);
  position: relative;
  transition: transform .25s ease, box-shadow .25s ease;
}
.action-item:hover .action-icon {
  transform: translateY(-4px) rotate(-4deg);
  box-shadow: 0 10px 22px rgba(132, 94, 194, 0.28);
}
.action-icon::after {
  content: '';
  position: absolute;
  inset: -3px;
  border-radius: 20px;
  background: inherit;
  filter: blur(10px);
  opacity: 0.4;
  z-index: -1;
}

.ai-gradient   { background: linear-gradient(135deg, #FF6F91, #845EC2); }
.me-gradient   { background: linear-gradient(135deg, #00C9A7, #4DD0E1); }
.info-gradient { background: linear-gradient(135deg, #FF9671, #FFC75F); }
.top-gradient  { background: linear-gradient(135deg, #5E81F4, #C398E1); }

.action-item span {
  font-size: 12px;
  color: #4a4f5c;
  font-weight: 600;
}

.picker-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 18px 14px;
  background: linear-gradient(120deg, #FFF4F6 0%, #F0F7FF 100%);
  font-weight: 800;
  font-size: 17px;
  color: #2c3e50;
}
.city-search { padding: 8px 16px; }
.city-list {
  max-height: calc(60vh - 160px);
  overflow-y: auto;
  padding-bottom: 20px;
}
.city-list-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 18px;
  border-bottom: 1px solid #f5f5f8;
  font-size: 15px;
  font-weight: 500;
  transition: background .15s;
}
.city-list-item:active {
  background: linear-gradient(135deg, #FFF4F6, #E4F2FF);
}
.city-list-item .van-icon:last-child { margin-left: auto; }
.list-dot {
  width: 8px; height: 8px; border-radius: 50%;
  background: linear-gradient(135deg, #FF6F91, #FFC75F);
}
</style>