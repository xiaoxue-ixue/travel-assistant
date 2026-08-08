<template>
  <div class="budget-table">
    <div
      v-for="(item, index) in budgetList"
      :key="index"
      class="budget-row"
    >
      <div class="budget-label">
        <span class="budget-icon">{{ item.icon }}</span>
        <span class="budget-name">{{ item.name }}</span>
      </div>
      <div class="budget-bar-wrap">
        <div class="budget-bar-bg">
          <div
            class="budget-bar"
            :style="{ width: item.percent + '%', background: item.color }"
          ></div>
        </div>
        <span class="budget-percent">{{ item.percent }}%</span>
      </div>
      <div class="budget-amount">¥{{ item.amount.toLocaleString() }}</div>
    </div>
    <div class="budget-total">
      <span class="total-label">预算总计</span>
      <span class="total-amount">¥{{ total.toLocaleString() }}</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  breakdown: {
    type: Object,
    required: true
  },
  total: {
    type: Number,
    default: 0
  }
})

const budgetList = computed(() => {
  const list = [
    { key: 'accommodation', name: '住宿', icon: '🏨', color: 'linear-gradient(90deg, #1989fa, #5ac8fa)' },
    { key: 'food', name: '餐饮', icon: '🍜', color: 'linear-gradient(90deg, #ff976a, #ffb347)' },
    { key: 'transportation', name: '交通', icon: '🚇', color: 'linear-gradient(90deg, #07c160, #5ed097)' },
    { key: 'tickets', name: '门票', icon: '🎫', color: 'linear-gradient(90deg, #7232dd, #a86ddf)' },
    { key: 'other', name: '其他', icon: '💡', color: 'linear-gradient(90deg, #ee0a24, #ff6034)' }
  ]

  const calcTotal = props.total || list.reduce((sum, item) => sum + (props.breakdown[item.key] || 0), 0)

  return list.map((item) => {
    const amount = props.breakdown[item.key] || 0
    const percent = calcTotal > 0 ? Math.round((amount / calcTotal) * 100) : 0
    return { ...item, amount, percent }
  })
})
</script>

<style scoped>
.budget-table {
  background: #fff;
}

.budget-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid #f2f3f5;
}

.budget-row:last-of-type {
  border-bottom: none;
}

.budget-label {
  width: 90px;
  display: flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
}

.budget-icon {
  font-size: 16px;
}

.budget-name {
  font-size: 14px;
  color: #323233;
  font-weight: 500;
}

.budget-bar-wrap {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.budget-bar-bg {
  flex: 1;
  height: 8px;
  background: #f2f3f5;
  border-radius: 4px;
  overflow: hidden;
}

.budget-bar {
  height: 100%;
  border-radius: 4px;
  transition: width 0.6s ease;
}

.budget-percent {
  font-size: 12px;
  color: #969799;
  width: 36px;
  text-align: right;
  flex-shrink: 0;
}

.budget-amount {
  font-size: 14px;
  font-weight: 600;
  color: #323233;
  width: 75px;
  text-align: right;
  flex-shrink: 0;
}

.budget-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 2px solid #ebedf0;
}

.total-label {
  font-size: 16px;
  font-weight: 600;
  color: #323233;
}

.total-amount {
  font-size: 20px;
  font-weight: 700;
  color: #ee0a24;
}
</style>
