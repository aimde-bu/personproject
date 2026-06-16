<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const username = ref('')
const password = ref('')
const confirmPassword = ref('')
const nickname = ref('')
const errorMsg = ref('')
const successMsg = ref('')

function handleRegister() {
  errorMsg.value = ''
  successMsg.value = ''

  if (!username.value || !password.value || !nickname.value) {
    errorMsg.value = '请填写完整信息'
    return
  }
  if (password.value !== confirmPassword.value) {
    errorMsg.value = '两次密码输入不一致'
    return
  }

  const result = userStore.register(username.value, password.value, nickname.value)
  if (result.success) {
    successMsg.value = result.message
    setTimeout(() => {
      router.push('/login')
    }, 1500)
  } else {
    errorMsg.value = result.message
  }
}
</script>

<template>
  <div class="register-page">
    <div class="register-card">
      <h1 class="page-title">注册账号</h1>
      <p class="page-subtitle">加入分销管理系统</p>
      <form class="register-form" @submit.prevent="handleRegister">
        <div class="form-group">
          <label for="username">用户名</label>
          <input
            id="username"
            v-model="username"
            type="text"
            placeholder="请输入用户名（至少3个字符）"
            autocomplete="username"
          />
        </div>
        <div class="form-group">
          <label for="nickname">昵称</label>
          <input
            id="nickname"
            v-model="nickname"
            type="text"
            placeholder="请输入昵称"
          />
        </div>
        <div class="form-group">
          <label for="password">密码</label>
          <input
            id="password"
            v-model="password"
            type="password"
            placeholder="请输入密码（至少6个字符）"
            autocomplete="new-password"
          />
        </div>
        <div class="form-group">
          <label for="confirmPassword">确认密码</label>
          <input
            id="confirmPassword"
            v-model="confirmPassword"
            type="password"
            placeholder="请再次输入密码"
            autocomplete="new-password"
          />
        </div>
        <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>
        <p v-if="successMsg" class="success-msg">{{ successMsg }}</p>
        <button type="submit" class="register-btn">注 册</button>
        <p class="switch-text">
          已有账号？
          <RouterLink to="/login" class="switch-link">立即登录</RouterLink>
        </p>
      </form>
    </div>
  </div>
</template>

<style scoped>
.register-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1d1e2c 0%, #3a3d5c 100%);
}

.register-card {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.page-title {
  text-align: center;
  margin: 0 0 4px;
  font-size: 28px;
  color: #1d1e2c;
}

.page-subtitle {
  text-align: center;
  margin: 0 0 28px;
  font-size: 13px;
  color: #999;
}

.register-form {
  display: flex;
  flex-direction: column;
  gap: 16px;
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

.success-msg {
  margin: 0;
  font-size: 13px;
  color: #67c23a;
}

.register-btn {
  height: 42px;
  background: #67c23a;
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.2s;
}

.register-btn:hover {
  background: #5daf34;
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
