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
  padding: 40px 16px;

  background:
    radial-gradient(circle at 0 0, rgba(185, 28, 28, 0.06), transparent 55%),
    radial-gradient(circle at 100% 100%, rgba(37, 99, 235, 0.06), transparent 60%), #f3f4f6;

  & > .login-register-card-container {
    margin: 0 auto;
    width: 65%;
    max-width: 1200px;
    min-width: 750px;
    aspect-ratio: 1.618 / 1;
    background:
      radial-gradient(circle at 0 0, rgba(185, 28, 28, 0.08), transparent 55%),
      radial-gradient(circle at 100% 100%, rgba(37, 99, 235, 0.08), transparent 60%), #ffffff;
    box-shadow: 0 24px 60px rgba(15, 23, 42, 0.12);
    border-radius: 10px;
  }
}
</style>
