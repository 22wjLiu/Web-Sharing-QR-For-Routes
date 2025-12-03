<template>
  <main class="main-container-home">
    <section class="home-hero">
      <div class="home-hero__inner">
        <div class="hero-left">
          <h1 class="hero-title">让戏曲从舞台走进旅途</h1>

          <p class="hero-text">
            “良辰美景奈何天，赏心乐事谁家院”，当昆曲的婉转遇上园林的雅致；
            “刘大哥讲话理太偏”，当豫剧的豪迈撞上中原的厚重；
            “海岛冰轮初转腾”，当京剧的华丽邂逅京城的庄严。
          </p>
          <p class="hero-text">
            我们以高德地图为载体，将戏曲发源地、核心院团、文化地标转化为可导航、可打卡的旅途坐标，
            让戏曲文化从“书本上”“舞台上”走进“旅途中”，让戏曲活起来，让旅途有温度。
          </p>
        </div>

        <img class="character-img" src="@/assets/img/actress1.png" />
      </div>
    </section>
  </main>
</template>

<script setup lang="ts"></script>

<style scoped lang="less">
.main-container-home {
  display: flex;
  flex-direction: column;
  align-items: center;

  .home-hero {
    width: 100%;
    padding: 10px 24px;
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

    &__inner {
      width: 100%;
      height: 400px;
      max-width: 1200px;
      display: flex;
      justify-content: space-between;
      align-items: center;

      & > .hero-left {
        position: relative;
        z-index: 1;
        max-width: 380px;

        & > .hero-title {
          font-size: 26px;
          font-weight: 600;
          font-family: 'Noto Serif SC', 'Songti SC', 'STSong', serif;
          margin-bottom: 16px;
        }

        & > .hero-text {
          font-size: 14px;
          line-height: 1.9;
          opacity: 0.94;
          margin-bottom: 10px;
          text-align: justify;
        }

        & > .hero-title,
        & > .hero-text {
          text-shadow: 0 2px 8px rgba(0, 0, 0, 0.6);
        }
      }

      & > .character-img {
        height: 100%;
      }
    }
  }
}
</style>
