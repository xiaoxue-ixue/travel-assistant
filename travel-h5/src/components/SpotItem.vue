<template>
  <div class="spot-item">
    <div class="spot-header">
      <div class="spot-icon" :class="iconClass">
        <span>{{ iconText }}</span>
      </div>
      <div class="spot-info">
        <h4 class="spot-name">{{ item.spot || '未安排' }}</h4>
        <div class="spot-meta">
          <span v-if="item.duration" class="meta-tag">
            <van-icon name="clock-o" /> {{ item.duration }}
          </span>
          <span v-if="item.ticket" class="meta-tag">
            <van-icon name="balance-pay" /> {{ item.ticket }}
          </span>
          <span v-if="item.transportation" class="meta-tag">
            <van-icon name="logistics" /> {{ item.transportation }}
          </span>
        </div>
      </div>
    </div>
    <p v-if="item.description" class="spot-desc">{{ item.description }}</p>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  item: {
    type: Object,
    default: () => ({})
  },
  period: {
    type: String,
    default: 'morning'
  }
})

const iconClass = computed(() => {
  const map = {
    morning: 'icon-morning',
    afternoon: 'icon-afternoon',
    evening: 'icon-evening'
  }
  return map[props.period] || 'icon-morning'
})

const iconText = computed(() => {
  const map = {
    morning: '☀️',
    afternoon: '🌤️',
    evening: '🌙'
  }
  return map[props.period] || '☀️'
})
</script>

<style scoped>
.spot-item {
  background: #fafbfc;
  border-radius: 12px;
  padding: 12px;
  margin-bottom: 10px;
  border-left: 3px solid #1989fa;
}

.spot-item:last-child {
  margin-bottom: 0;
}

.spot-header {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.spot-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.icon-morning {
  background: linear-gradient(135deg, #fff3e0 0%, #ffe0b2 100%);
}

.icon-afternoon {
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
}

.icon-evening {
  background: linear-gradient(135deg, #ede7f6 0%, #d1c4e9 100%);
}

.spot-info {
  flex: 1;
  min-width: 0;
}

.spot-name {
  font-size: 15px;
  font-weight: 600;
  color: #323233;
  margin-bottom: 6px;
}

.spot-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.meta-tag {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  font-size: 12px;
  color: #646566;
  background: #fff;
  padding: 3px 8px;
  border-radius: 10px;
  border: 1px solid #ebedf0;
}

.spot-desc {
  margin-top: 10px;
  font-size: 13px;
  color: #646566;
  line-height: 1.6;
  padding-top: 10px;
  border-top: 1px dashed #ebedf0;
}
</style>
