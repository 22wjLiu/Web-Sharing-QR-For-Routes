<template>
  <main class="main-container">
    <section class="login-register-card-container">
      <RouterView />
    </section>
  </main>
</template>

<style scoped lang="less">
.main-container {
  width: 100vw;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;

  & > .login-register-card-container {
    margin: 0 auto;
    width: 90%;
    max-width: 1200px;
    min-width: 600px;
    aspect-ratio: 1.618 / 1;
    background-color: #ffffff;
    border-radius: 10px;
    padding: 10px;
  }
}
</style>
