<template>
  <button ref="btn" class="heart-button" @click="handleClick">
    <svg class="heart" viewBox="0 0 24 24">
      <path
        d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5
                                             2 5.42 4.42 3 7.5 3
                                             c1.74 0 3.41 0.81 4.5 2.09
                                             C13.09 3.81 14.76 3 16.5 3
                                             19.58 3 22 5.42 22 8.5
                                             c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"
      />
    </svg>

    <div ref="burst" class="burst"></div>
  </button>
</template>

<script setup lang="ts">
import { getCurrentInstance, ref } from 'vue';

const btn = ref<HTMLButtonElement | null>(null);
const burst = ref<HTMLDivElement | null>(null);
const instance = getCurrentInstance();
const ownScopeId = (instance?.type as { __scopeId?: string } | undefined)?.__scopeId;

const explode = () => {
  if (!burst.value) return;
  burst.value.innerHTML = '';
  const colors = ['#60a5fa', '#34d399', '#f59e0b', '#ef4444', '#a78bfa', '#f472b6'];
  const N = 16;
  for (let i = 0; i < N; i++) {
    const dot = document.createElement('div');
    dot.className = 'dot';
    if (ownScopeId) dot.setAttribute(ownScopeId, '');
    const ang = ((Math.PI * 2) / N) * i;
    const dist = 36 + Math.random() * 18;
    dot.style.setProperty('--tx', `${Math.cos(ang) * dist}px`);
    dot.style.setProperty('--ty', `${Math.sin(ang) * dist}px`);
    dot.style.setProperty('--c', colors[i % colors.length] ?? '');
    dot.style.left = '50%';
    dot.style.top = '50%';
    dot.style.transform = 'translate(-50%,-50%)';
    burst.value.appendChild(dot);
    requestAnimationFrame(() => {
      const delay = Math.random() * 0.05;
      dot.style.animation = `heart-pop .8s ${delay}s cubic-bezier(.2,.8,.2,1) forwards`;
    });
  }
};

const handleClick = () => {
  if (!btn.value) return;
  btn.value.classList.toggle('liked');
  explode();
};
</script>

<style scoped>
.heart-button {
  position: relative;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  place-items: center;
  cursor: pointer;
  user-select: none;
  border: none;
  transition: transform 0.15s ease;
}
.heart-button:hover {
  transform: scale(1.05);
}
.heart {
  width: 18px;
  height: 36px;
  fill: none;
  stroke: #94a3b8;
  stroke-width: 2.5;
  transition: all 0.25s ease;
}
.heart-button.liked .heart {
  fill: #ef4444;
  stroke: #ef4444;
  filter: drop-shadow(0 0 8px rgba(239, 68, 68, 0.6));
  transform: scale(1.15);
}

/* 粒子 */
.burst {
  position: absolute;
  inset: 0;
  pointer-events: none;
}
.dot {
  position: absolute;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--c, #60a5fa);
  opacity: 0;
  transform: translate(0, 0) scale(0.7);
}
</style>
