<template>
  <div id="app-container">
    <router-view v-slot="{ Component }">
      <transition name="slide-fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </div>
</template>

<script setup>
</script>

<style lang="css">
#app-container {
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

#app-container::before,
#app-container::after {
  content: '';
  position: fixed;
  border-radius: 50%;
  filter: blur(60px);
  opacity: 0.35;
  pointer-events: none;
  z-index: 0;
  animation: floatBg 18s ease-in-out infinite;
}
#app-container::before {
  width: 320px; height: 320px;
  top: -60px; left: -80px;
  background: radial-gradient(circle, #FF6F91, transparent 70%);
}
#app-container::after {
  width: 280px; height: 280px;
  bottom: -80px; right: -60px;
  background: radial-gradient(circle, #845EC2, transparent 70%);
  animation-delay: -7s;
}
@keyframes floatBg {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50%      { transform: translate(20px, -30px) scale(1.12); }
}

.slide-fade-enter-active {
  transition: all 0.4s cubic-bezier(.2,1.2,.3,1);
}
.slide-fade-leave-active {
  transition: all 0.25s cubic-bezier(.5,.3,.8,1);
}
.slide-fade-enter-from {
  transform: translateX(28px) scale(.96);
  opacity: 0;
  filter: blur(4px);
}
.slide-fade-leave-to {
  transform: translateX(-18px) scale(.98);
  opacity: 0;
  filter: blur(2px);
}
</style>