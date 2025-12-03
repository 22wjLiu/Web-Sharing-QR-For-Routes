<template>
  <footer class="footer">
    <div class="footer__inner">
      <section class="left footer__col" @click="router.push('/')">
        <div class="logo">
          <span class="logo-text">戏曲地图</span>
        </div>
        <p class="desc">跟着地图寻戏曲，从京剧到昆曲、豫剧， 让每一次打卡都多一点戏韵和故事。</p>
      </section>
      <section class="center footer__col">
        <h4 class="title">快速导航</h4>
        <ul class="nav-list">
          <li v-for="item in navItems" :key="item.path" class="nav-item">
            <button
              class="nav-link"
              :class="{ 'is-active': isActive(item) }"
              @click="go(item.path)"
            >
              {{ item.label }}
            </button>
          </li>
        </ul>
      </section>
      <section class="right footer__col">
        <h4 class="title">关于项目</h4>
        <p class="text">本站为课程项目 · 仅作学习与展示使用。</p>
      </section>
    </div>
    <div class="footer__bottom">
      <span> © {{ year }} 戏曲地图 · All rights reserved. </span>
    </div>
  </footer>
</template>

<script setup lang="ts">
import type { NavItem } from '@/types/layout';
import { useRouter, useRoute } from 'vue-router';

defineProps<{
  navItems: NavItem[];
}>();

const router = useRouter();
const route = useRoute();

const year = new Date().getFullYear();

const go = (path: string) => {
  if (path !== route.path) {
    router.push(path);
  }
};

const isActive = (item: { path: string }) => {
  if (item.path === '/') return route.path === '/';
  return route.path.startsWith(item.path);
};
</script>

<style scoped lang="less">
.footer {
  margin-top: 40px;
  padding: 24px 16px 18px;
  background: radial-gradient(circle at 0 0, #f1f5f9, #f9fafb);
  border-top: 1px solid rgba(148, 163, 184, 0.3);
  color: #475569;
  font-size: 13px;

  &__inner {
    max-width: 1120px;
    margin: 0 auto 12px;
    display: flex;
    gap: 32px;
    justify-content: space-between;
    flex-wrap: wrap;

    & > .left {
      cursor: pointer;

      & > .logo {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 8px;

        & > .logo-text {
          font-size: 16px;
          font-weight: 600;
          letter-spacing: 0.08em;
          color: #0f172a;
        }
      }

      & > .desc {
        margin: 0;
        margin-top: 4px;
        line-height: 1.6;
        color: #64748b;
      }
    }

    & > .center {
      & > .title {
        margin: 0 0 8px;
        font-size: 13px;
        font-weight: 600;
        letter-spacing: 0.08em;
        text-transform: uppercase;
        color: #94a3b8;
      }

      .nav-list {
        list-style: none;
        padding: 0;
        margin: 0;
        display: grid;
        gap: 4px;

        & > .nav-item {
          margin: 0;

          & > .nav-link {
            border: none;
            background: transparent;
            padding: 4px 0;
            font-size: 13px;
            color: #475569;
            cursor: pointer;
            text-align: left;
            position: relative;
            transition:
              color 0.2s ease,
              transform 0.15s ease;

            &::after {
              content: '';
              position: absolute;
              left: 0;
              bottom: 0;
              width: 18px;
              height: 2px;
              border-radius: 999px;
              background: linear-gradient(90deg, #ef4444, #3b82f6);
              transform: scaleX(0);
              transform-origin: left;
              transition: transform 0.2s ease;
            }

            &:hover {
              color: #0f172a;
              transform: translateX(2px);

              &::after {
                transform: scaleX(1);
              }
            }

            &.is-active {
              color: #0f172a;
              font-weight: 600;

              &::after {
                transform: scaleX(1);
              }
            }
          }
        }
      }
    }

    & > .right {
      & > .title {
        margin: 0 0 8px;
        font-size: 13px;
        font-weight: 600;
        letter-spacing: 0.08em;
        text-transform: uppercase;
        color: #94a3b8;
      }

      & > .text {
        margin: 0 0 4px;
        line-height: 1.6;
      }
    }
  }

  &__col {
    flex: 1 1 220px;
    min-width: 0;
  }

  &__bottom {
    max-width: 1120px;
    margin: 0 auto;
    padding-top: 8px;
    border-top: 1px solid rgba(148, 163, 184, 0.2);
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    color: #94a3b8;
  }

  /* 响应式 */
  @media (max-width: 768px) {
    &__inner {
      gap: 20px;
    }

    &__bottom {
      flex-direction: column;
      align-items: flex-start;
    }
  }
}
</style>
