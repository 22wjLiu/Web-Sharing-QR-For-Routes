<template>
  <div class="main-container-home">
    <section class="home-hero">
      <div class="home-hero__inner width-range">
        <div class="hero-left">
          <div class="typed-wrapper">
            <h1 class="hero-title" ref="heroTitleRef"></h1>
          </div>

          <p class="hero-text">
            我们把京剧、昆曲、豫剧的发源地、剧场与文化地标一一落在地图上，
            设计成可导航、可打卡、可分享的主题路线
          </p>
          <p class="hero-text">
            借助高德地图的一键导航，从查找戏院、规划行程，到现场打卡、记录足迹，
            都可以在这张地图里完成，让“听说过”真正变成“亲自去看过”
          </p>

          <div class="hero-actions">
            <button class="btn-primary" @click="router.push('/routes')">开启戏曲之旅</button>
          </div>
        </div>

        <div class="hero-right">
          <div class="img-glow"></div>
          <img class="character-img" :src="Actress1" />
        </div>
      </div>
    </section>

    <section class="home-intro width-range">
      <div class="home-intro__inner">
        <div class="left">
          <Vue3Lottie :animationData="LocationJson" :height="'100%'" :width="'100%'" :speed="0.8" />
        </div>
        <div class="right">
          <h2 class="intro-title">我们的初衷</h2>
          <p class="intro-text">
            “良辰美景奈何天，赏心乐事谁家院”，当昆曲的婉转遇上园林的雅致；
            “刘大哥讲话理太偏”，当豫剧的豪迈撞上中原的厚重；
            “海岛冰轮初转腾”，当京剧的华丽邂逅京城的庄严——
            戏曲作为中华传统文化的活态载体，承载着千年的历史记忆与人文精神。
          </p>
          <p class="intro-text">
            然而在快节奏的现代生活中，这门古老的艺术却渐渐与大众尤其是年轻群体产生距离，
            许多人对戏曲的认知停留在“听说过”，却从未真正走近。
          </p>
          <p class="intro-text">
            我们希望打破“戏曲只在剧场里”的固有认知，
            以高德地图为载体，将戏曲发源地、核心院团、文化地标转化为
            可导航、可打卡的旅游点位，让戏曲从“书本上”“舞台上”走进“旅途中”，
            真正“活”在每个人的旅程里。
          </p>
        </div>
      </div>
    </section>

    <section class="home-scan width-range">
      <div class="scan-header">
        <h2 class="title center">跟着一张二维码，走进一出戏</h2>
        <p class="subtitle">扫码即刻出发，体验不一样的文化苦旅</p>
      </div>

      <div class="scan-content">
        <div class="features-list">
          <div class="feature-card">
            <div class="icon-box">🗺️</div>
            <h3>即扫即开路线</h3>
            <p>自动跳转高德，看到完整路线与每一站的打卡点。</p>
          </div>
          <div class="feature-card">
            <div class="icon-box">📍</div>
            <h3>站点信息一目了然</h3>
            <p>剧场、街巷、故居，历史背景与演出信息尽在掌握。</p>
          </div>
          <div class="feature-card">
            <div class="icon-box">🤝</div>
            <h3>一键分享给同伴</h3>
            <p>生成专属二维码，邀请好友共赴这场“戏路”之旅。</p>
          </div>
        </div>

        <div class="scan-visual">
          <Vue3Lottie :animationData="Scan" :height="'100%'" :width="'100%'" />
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { Vue3Lottie } from 'vue3-lottie';
import Actress1 from '@/assets/img/actress1.png';
import LocationJson from '@/assets/json/Location.json';
import Scan from '@/assets/json/Scan.json';
import Typed from 'typed.js';
import { ref, onMounted, onBeforeUnmount } from 'vue';
import router from '@/router';

const heroTitleRef = ref<HTMLElement | null>(null);
let typed: Typed | null = null;

onMounted(() => {
  if (!heroTitleRef.value) return;

  typed = new Typed(heroTitleRef.value, {
    strings: ['让戏曲从舞台走进旅途', '让每一次打卡都有"戏"'],
    typeSpeed: 80,
    backSpeed: 40,
    loop: true,
    showCursor: false,
  });
});

onBeforeUnmount(() => {
  typed?.destroy();
});
</script>

<style scoped lang="less">
.width-range {
  width: 100%;
  max-width: 1200px;
  min-width: 900px;
}

.main-container-home {
  display: flex;
  flex-direction: column;
  align-items: center;

  & > .home-hero {
    width: 100%;
    padding: 10px 24px;
    padding-bottom: 0;
    color: #f9fafb;
    position: relative;
    display: flex;
    justify-content: center;

    &::before {
      content: '';
      position: absolute;
      inset: 0;
      background: linear-gradient(
        90deg,
        rgba(8, 16, 32, 0.85) 0%,
        rgba(8, 16, 32, 0.65) 30%,
        rgba(8, 16, 32, 0.2) 55%,
        transparent 100%
      );
      pointer-events: none;
      z-index: 0;
    }

    & > .home-hero__inner {
      width: 100%;
      height: 560px;
      display: flex;
      justify-content: space-between;
      align-items: center;

      & > .hero-left {
        position: relative;
        z-index: 1;
        max-width: 380px;

        & > .typed-wrapper {
          min-height: 36.5px;

          & > .hero-title {
            font-size: 26px;
            font-weight: 600;
            font-family: 'Noto Serif SC', 'Songti SC', 'STSong', serif;
            margin-bottom: 16px;
          }
        }

        & > .hero-text {
          font-size: 14px;
          line-height: 1.9;
          opacity: 0.94;
          margin-bottom: 10px;
          text-align: justify;
        }

        & > .hero-actions {
          display: flex;
          gap: 16px;

          @media (max-width: 768px) {
            justify-content: center;
          }

          button {
            padding: 12px 32px;
            border-radius: 50px;
            font-size: 16px;
            cursor: pointer;
            transition: all 0.3s ease;
            font-weight: 500;

            background: linear-gradient(
              135deg,
              rgba(185, 28, 28, 0.5) 0%,
              rgba(37, 99, 235, 0.5) 80%,
              rgba(211, 211, 211, 0.5) 100%
            );
            border: none;
            color: white;
            box-shadow: 0 4px 15px rgba(79, 70, 229, 0.4);

            &:hover {
              transform: translateY(-2px);
              box-shadow: 0 6px 20px rgba(79, 70, 229, 0.6);
            }
          }
        }

        & > .hero-title,
        & > .hero-text {
          text-shadow: 0 2px 8px rgba(0, 0, 0, 0.6);
        }
      }

      & > .hero-right {
        display: flex;
        align-items: end;
        height: 100%;
        .img-glow {
          position: absolute;
          width: 400px;
          height: 400px;
          background: radial-gradient(circle, rgba(255, 255, 255, 0.484) 0%, transparent 70%);
          top: 50%;
          left: 50%;
          transform: translate(-50%, -50%);
          z-index: -1;
        }

        .character-img {
          height: 100%;
          object-fit: contain;
          filter: drop-shadow(0 0 20px rgba(0, 0, 0, 0.4));
        }
      }
    }
  }

  & > .home-intro {
    margin-top: 40px;

    & > .home-intro__inner {
      display: flex;
      gap: 40px;
      height: 350px;
      padding: 40px;
      background: rgba(255, 255, 255, 0.2);
      box-shadow: 0 20px 40px rgba(0, 0, 0, 0.08);
      backdrop-filter: blur(20px);
      border-radius: 24px;

      & > .left {
        height: 100%;
        flex: 2;
      }

      & > .right {
        display: flex;
        flex-direction: column;
        flex: 4;

        & > .intro-title {
          margin: 0 0 14px;
          font-size: 32px;
          font-weight: 700;
          color: #1f2937;
          font-family: 'Noto Serif SC', 'Songti SC', serif;
        }

        & > .intro-text {
          margin: 0 0 10px;
          font-size: 14px;
          line-height: 1.9;
          color: #475569;
        }
      }
    }
  }

  .home-scan {
    margin-top: 40px;

    .scan-header {
      .title {
        font-size: 32px;
        font-weight: 700;
        color: #1f2937;
        margin-bottom: 16px;
        font-family: 'Noto Serif SC', 'Songti SC', serif;

        &.center {
          text-align: center;
        }
      }

      .subtitle {
        text-align: center;
        color: #6b7280;
      }
    }

    .scan-content {
      display: flex;
      align-items: center;
      gap: 60px;

      @media (max-width: 900px) {
        flex-direction: column-reverse;
      }

      .features-list {
        flex: 2;
        display: flex;
        gap: 24px;

        .feature-card {
          background: #fff;
          border: 1px solid #f3f4f6;
          padding: 24px;
          border-radius: 16px;
          transition: all 0.3s ease;
          display: flex;
          flex-direction: column;
          align-items: flex-start;

          &:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
            border-color: #e0e7ff;

            .icon-box {
              transform: scale(1.1) rotate(5deg);
              background: #4f46e5;
              color: #fff;
            }
          }

          .icon-box {
            width: 48px;
            height: 48px;
            background: #e0e7ff;
            color: #4f46e5;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
            margin-bottom: 16px;
            transition: all 0.3s ease;
          }

          h3 {
            font-size: 18px;
            font-weight: 700;
            color: #111827;
            margin-bottom: 8px;
          }

          p {
            font-size: 14px;
            color: #6b7280;
            line-height: 1.6;
          }
        }
      }

      .scan-visual {
        flex: 1;
        height: 350px;
      }
    }
  }
}
</style>
