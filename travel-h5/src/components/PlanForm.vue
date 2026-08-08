<template>
  <div class="plan-form glass-card">
    <h3 class="form-title">
      <span class="title-bar"></span>
      ✈️ 开始新的旅程
    </h3>

    <div class="field-group">
      <label class="field-label">
        <span class="dot dot-pink"></span>
        目的地城市
      </label>
      <div class="city-picker" @click="handlePickCity">
        <span class="picker-emoji">{{ cityEmoji }}</span>
        <input
          class="picker-input"
          :value="form.city"
          readonly
          placeholder="点击选择目的地"
        />
        <van-icon name="arrow" color="#C398E1" />
      </div>
    </div>

    <div class="field-group">
      <div class="field-label-row">
        <label class="field-label">
          <span class="dot dot-yellow"></span>
          总预算 (元)
        </label>
        <div class="budget-badge">¥ {{ form.budget.toLocaleString() }}</div>
      </div>
      <van-slider
        v-model="form.budget"
        :min="500" :max="20000" :step="100"
        bar-height="8px"
        class="dopamine-slider"
      />
      <div class="slider-range">
        <span>¥500</span><span>¥10,000</span><span>¥20,000</span>
      </div>
    </div>

    <div class="field-group">
      <div class="field-label-row">
        <label class="field-label">
          <span class="dot dot-mint"></span>
          出游天数
        </label>
        <div class="days-hint">{{ daysHint }}</div>
      </div>
      <van-stepper
        v-model="form.days"
        :min="1" :max="30"
        input-width="80px"
        button-size="32px"
        theme="round"
        class="days-stepper"
      />
    </div>

    <van-button
      block round type="primary" size="large"
      class="submit-btn"
      :loading="loading"
      @click="$emit('submit', form)"
    >
      🚀 生成智能行程
    </van-button>
  </div>
</template>

<script setup>
import { computed, reactive } from 'vue'

const props = defineProps({
  loading: Boolean,
  modelValue: { type: Object, default: () => ({ city: '', budget: 3000, days: 3 }) }
})
const emit = defineEmits(['submit', 'update:modelValue', 'pick-city'])

const form = reactive({ ...props.modelValue })
const cityEmoji = computed(() => {
  const map = { 北京:'🏛️',上海:'🌆',成都:'🐼',西安:'🏺',杭州:'🍃',
    三亚:'🌴',重庆:'🌶️',厦门:'🌊',苏州:'🏡',南京:'🏮',广州:'🌺',
    深圳:'🏙️',青岛:'⚓',大连:'🐚',桂林:'⛰️',丽江:'🎐' }
  return map[form.city] || '📍'
})
const daysHint = computed(() => {
  const d = form.days
  if (d <= 2) return '短假快闪 🌿'
  if (d <= 5) return '完美行程 ⭐'
  if (d <= 10) return '深度漫游 🚞'
  return '史诗旅程 🗺️'
})
const handlePickCity = () => emit('pick-city')
</script>

<style scoped>
.plan-form { padding: 20px; border-radius: 22px; }
.form-title {
  font-size: 17px;
  font-weight: 800;
  color: #2c3e50;
  margin: 0 0 18px;
  display: flex;
  align-items: center;
  gap: 10px;
}
.title-bar {
  width: 5px; height: 20px;
  border-radius: 4px;
  background: linear-gradient(180deg, #FF6F91, #FFC75F, #845EC2);
}

.field-group { margin-bottom: 18px; }
.field-label, .field-label-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 13px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 10px;
}
.field-label-row { margin-bottom: 6px; }

.dot { width: 10px; height: 10px; border-radius: 50%; margin-right: 6px; }
.dot-pink   { background: linear-gradient(135deg, #FF6F91, #FFC75F); }
.dot-yellow { background: linear-gradient(135deg, #FFC75F, #84FAB0); }
.dot-mint   { background: linear-gradient(135deg, #00C9A7, #4DD0E1); }

.city-picker {
  display: grid;
  grid-template-columns: 40px 1fr 20px;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 16px;
  background: linear-gradient(135deg, #FFF8FC, #F0F7FF);
  border: 1px solid #f3eafb;
}
.picker-emoji { font-size: 22px; text-align: center; }
.picker-input {
  border: none; background: transparent;
  font-size: 14px; font-weight: 600; color: #2c3e50;
  outline: none; width: 100%;
}
.picker-input::placeholder { color: #b5bac4; font-weight: 500; }

.budget-badge {
  font-size: 14px;
  font-weight: 800;
  padding: 3px 10px;
  border-radius: 999px;
  background: linear-gradient(135deg, #FF6F91, #FFC75F);
  color: #fff;
}

.dopamine-slider :deep(.van-slider__bar) {
  background: linear-gradient(90deg, #FF6F91 0%, #FFC75F 40%, #4DD0E1 100%) !important;
  border-radius: 999px;
}
.dopamine-slider :deep(.van-slider__button) {
  background: #fff !important;
  border: 3px solid #FF6F91 !important;
  width: 22px !important; height: 22px !important;
  box-shadow: 0 4px 10px rgba(255, 111, 145, 0.4);
}

.slider-range {
  margin-top: 6px;
  display: flex;
  justify-content: space-between;
  font-size: 10px;
  color: #8a8f99;
  font-weight: 500;
}

.days-hint {
  font-size: 12px;
  font-weight: 800;
  background: linear-gradient(135deg, #00C9A7, #5E81F4);
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}
.days-stepper { margin-top: 4px; }
.days-stepper :deep(.van-stepper__minus),
.days-stepper :deep(.van-stepper__plus) {
  background: linear-gradient(135deg, #84FAB0, #4DD0E1);
  color: #fff;
  border: none;
  font-weight: 800;
}
.days-stepper :deep(.van-stepper__input) {
  font-weight: 800;
  color: #2c3e50;
}

.submit-btn {
  margin-top: 10px;
  height: 50px;
  border: none !important;
  background: linear-gradient(120deg,
    #FF6F91 0%, #FF9671 30%, #FFC75F 55%, #4DD0E1 80%, #845EC2 100%) !important;
  background-size: 200% 200%;
  animation: btnShift 5s ease infinite;
  border-radius: 28px !important;
  font-weight: 800;
  font-size: 15px;
  box-shadow: 0 12px 28px rgba(255, 111, 145, 0.35);
}
@keyframes btnShift {
  0%, 100% { background-position: 0 50%; }
  50%      { background-position: 100% 50%; }
}
</style>