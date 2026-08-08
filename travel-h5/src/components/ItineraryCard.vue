<template>
  <div class="it-card pop-in" :style="cardColor">
    <div class="it-glow"></div>
    <div class="it-head">
      <div class="it-day">
        <span class="it-day-num">D{{ day }}</span>
        <span class="it-day-name">{{ title }}</span>
      </div>
      <div v-if="highlight" class="it-hl">{{ highlight }}</div>
    </div>

    <div class="it-body">
      <div
        v-for="(it, i) in items"
        :key="i"
        class="act-row"
      >
        <span class="act-bullet" :style="dotColor(i)"></span>
        <span class="act-time">{{ it.time || '' }}</span>
        <div class="act-main">
          <div class="act-name">
            {{ it.icon || '📍' }} {{ it.name || it.activity || it }}
          </div>
          <div v-if="it.location || it.address" class="act-place">
            📍 {{ it.location || it.address }}
          </div>
          <div v-if="it.tip || it.note" class="act-tip">💡 {{ it.tip || it.note }}</div>
        </div>
      </div>
    </div>

    <div class="it-foot">
      <span v-if="transport" class="it-chip tc1">🚆 {{ transport }}</span>
      <span v-if="hotel" class="it-chip tc2">🏨 {{ hotel }}</span>
      <span v-if="weather" class="it-chip tc3">🌤️ {{ weather }}</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
const props = defineProps({
  day:       { type: [Number, String], default: 1 },
  title:     { type: String, default: '' },
  items:     { type: Array,  default: () => [] },
  transport: String,
  hotel:     String,
  weather:   String,
  highlight: String,
  variant:   { type: Number, default: 0 }
})
const palette = [
  ['#FF6F91', '#FFC75F'],
  ['#84FAB0', '#00C9A7'],
  ['#4DD0E1', '#5E81F4'],
  ['#C398E1', '#845EC2'],
  ['#FF9671', '#FF6F91'],
  ['#FFC75F', '#84FAB0'],
  ['#00C9A7', '#4DD0E1'],
  ['#845EC2', '#FF6F91']
]
const pair = computed(() => palette[(Number(props.day) - 1) % palette.length])
const cardColor = computed(() => ({
  '--c1': pair.value[0],
  '--c2': pair.value[1]
}))
const dotColor = (i) => {
  const arr = ['#FF6F91','#FFC75F','#00C9A7','#4DD0E1','#845EC2','#FF9671']
  return { background: `linear-gradient(135deg, ${arr[i % arr.length]}, #ffffffaa)`,
           boxShadow: `0 0 0 3px ${arr[i % arr.length]}22` }
}
</script>

<style scoped>
.it-card {
  --c1: #FF6F91; --c2: #FFC75F;
  position: relative;
  padding: 16px 14px 14px;
  border-radius: 20px;
  background: linear-gradient(155deg,
    color-mix(in srgb, var(--c1) 12%, white) 0%,
    color-mix(in srgb, var(--c2) 12%, white) 100%);
  border: 1px solid color-mix(in srgb, var(--c1) 20%, white);
  overflow: hidden;
  transition: transform .25s, box-shadow .25s;
  box-shadow: 0 8px 22px color-mix(in srgb, var(--c1) 18%, transparent);
}
.it-card:hover { transform: translateY(-3px); box-shadow: 0 14px 32px color-mix(in srgb, var(--c1) 28%, transparent); }

.it-glow {
  position: absolute;
  top: -40px; right: -40px;
  width: 120px; height: 120px;
  background: radial-gradient(circle, var(--c1) 0%, transparent 70%);
  opacity: 0.25;
  pointer-events: none;
}

.it-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}
.it-day { display: flex; align-items: baseline; gap: 8px; }
.it-day-num {
  font-size: 20px;
  font-weight: 900;
  background: linear-gradient(135deg, var(--c1), var(--c2));
  -webkit-background-clip: text;
  background-clip: text;
  color: transparent;
}
.it-day-name {
  font-size: 14px;
  font-weight: 800;
  color: #2c3e50;
}
.it-hl {
  font-size: 10px;
  padding: 3px 8px;
  border-radius: 999px;
  background: linear-gradient(135deg, var(--c1), var(--c2));
  color: #fff;
  font-weight: 700;
}

.it-body { display: flex; flex-direction: column; gap: 4px; }

.act-row {
  display: grid;
  grid-template-columns: 12px auto 1fr;
  gap: 8px 10px;
  align-items: flex-start;
  padding: 8px 0;
  border-bottom: 1px dashed color-mix(in srgb, var(--c1) 20%, white);
}
.act-row:last-child { border-bottom: none; }

.act-bullet {
  margin-top: 6px;
  width: 10px; height: 10px;
  border-radius: 50%;
}
.act-time {
  font-size: 11px;
  font-weight: 800;
  color: var(--c1);
  padding-top: 3px;
  min-width: 44px;
}
.act-name {
  font-size: 13px;
  font-weight: 700;
  color: #2c3e50;
}
.act-place {
  font-size: 11px;
  color: #6b7080;
  margin-top: 2px;
}
.act-tip {
  font-size: 11px;
  color: var(--c1);
  margin-top: 4px;
  padding: 4px 8px;
  border-radius: 10px;
  background: color-mix(in srgb, var(--c1) 12%, white);
  font-weight: 600;
}

.it-foot {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 12px;
}
.it-chip {
  font-size: 11px;
  padding: 3px 10px;
  border-radius: 999px;
  color: #fff;
  font-weight: 700;
}
.tc1 { background: linear-gradient(135deg, #5E81F4, #4DD0E1); }
.tc2 { background: linear-gradient(135deg, #FF9671, #FF6F91); }
.tc3 { background: linear-gradient(135deg, #00C9A7, #84FAB0); color: #2c3e50; }
</style>