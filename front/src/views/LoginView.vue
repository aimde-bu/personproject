<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
const router = useRouter()
const username = ref('')
const password = ref('')
const errorMsg = ref('')

async function handleLogin() {
  errorMsg.value = ''

  if (!username.value || !password.value) {
    errorMsg.value = '请输入用户名和密码'
    return
  }
  
  const result = await request.post('/login', {
    username: username.value,
    password: password.value
  })
  console.log(result.data)
  if (result.data.code === 200) {
    localStorage.setItem('token', result.data.data)
    router.push('/dashboard')
  } else {
    errorMsg.value = result.data.message
    ElMessage.error('请先登录')
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-card">
      <h1 class="login-title">分销管理系统</h1>
      <p class="login-subtitle">Distribution Management System</p>
      <form class="login-form" @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="username">用户名</label>
          <input
            id="username"
            v-model="username"
            type="text"
            placeholder="请输入用户名"
            autocomplete="username"
          />
        </div>
        <div class="form-group">
          <label for="password">密码</label>
          <input
            id="password"
            v-model="password"
            type="password"
            placeholder="请输入密码"
            autocomplete="current-password"
          />
        </div>
        <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>
        <button type="submit" class="login-btn">登 录</button>
        <p class="switch-text">
          还没有账号？
          <RouterLink to="/register" class="switch-link">立即注册</RouterLink>
        </p>
      </form>
    </div>
  </div>
</template>

<style scoped>

.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1d1e2c 0%, #3a3d5c 100%);
}

.login-card {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.login-title {
  text-align: center;
  margin: 0 0 4px;
  font-size: 28px;
  color: #1d1e2c;
}

.login-subtitle {
  text-align: center;
  margin: 0 0 32px;
  font-size: 13px;
  color: #999;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-group label {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.form-group input {
  height: 42px;
  padding: 0 12px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s;
}

.form-group input:focus {
  border-color: #409eff;
}

.error-msg {
  margin: 0;
  font-size: 13px;
  color: #f56c6c;
}

.login-btn {
  height: 42px;
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.2s;
}

.login-btn:hover {
  background: #3a8ee6;
}

.switch-text {
  text-align: center;
  font-size: 14px;
  color: #999;
  margin: 0;
}

.switch-link {
  color: #409eff;
  text-decoration: none;
}

.switch-link:hover {
  text-decoration: underline;
}
</style>
