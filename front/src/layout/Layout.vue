<script setup lang="ts">
import { ref, computed } from 'vue'
import { RouterLink, RouterView, useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const sidebarCollapsed = ref(false)

const displayName = computed(() => {
  return userStore.state.userInfo?.nickname || '用户'
})

const menuItems = [
  { path: '/dashboard', label: '数据概览'},
  { path: '/distributor', label: '分销商管理'},
  { path: '/product', label: '商品管理'},
  { path: '/order', label: '订单管理'},
  { path: '/team', label: '团队管理'}
]

function toggleSidebar() {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

function isActive(path: string): boolean {
  return route.path.startsWith(path)
}

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>

<template>
  <div class="layout" :class="{ 'sidebar-collapsed': sidebarCollapsed }">
    <aside class="sidebar">
      <div class="sidebar-header">
        <h2 v-if="!sidebarCollapsed">分销管理系统</h2>
        <h2 v-else>分销</h2>
      </div>
      <nav class="sidebar-nav">
        <RouterLink
          v-for="item in menuItems"
          :key="item.path"
          :to="item.path"
          class="nav-item"
          :class="{ active: isActive(item.path) }"
        >
          <span v-if="!sidebarCollapsed" class="nav-label">{{ item.label }}</span>
        </RouterLink>
      </nav>
    </aside>

    <div class="main-area">
      <header class="topbar">
        <button class="toggle-btn" @click="toggleSidebar">
          {{ sidebarCollapsed ? '▶' : '◀' }}
        </button>
        <div class="topbar-right">
          <span class="username">{{ displayName }}</span>
          <a class="logout-btn" @click="handleLogout">退出登录</a>
        </div>
      </header>
      <main class="content">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<style scoped>
.layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.sidebar {
  width: 220px;
  background: #1d1e2c;
  color: #fff;
  display: flex;
  flex-direction: column;
  transition: width 0.3s;
  flex-shrink: 0;
}

.sidebar-collapsed .sidebar {
  width: 64px;
}

.sidebar-header {
  padding: 20px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  text-align: center;
}

.sidebar-header h2 {
  margin: 0;
  font-size: 18px;
  white-space: nowrap;
  overflow: hidden;
}

.sidebar-nav {
  flex: 1;
  padding: 8px 0;
  overflow-y: auto;
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 12px 20px;
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  transition: all 0.2s;
  white-space: nowrap;
}

.sidebar-collapsed .nav-item {
  justify-content: center;
  padding: 12px 0;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.08);
  color: #fff;
}

.nav-item.active {
  background: #409eff;
  color: #fff;
}

.nav-icon {
  font-size: 18px;
  flex-shrink: 0;
}

.nav-label {
  margin-left: 12px;
  font-size: 14px;
}

.main-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: #f0f2f5;
}

.topbar {
  height: 56px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  flex-shrink: 0;
}

.toggle-btn {
  background: none;
  border: none;
  font-size: 16px;
  cursor: pointer;
  color: #666;
  padding: 4px 8px;
  border-radius: 4px;
}

.toggle-btn:hover {
  background: #f0f0f0;
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.username {
  font-size: 14px;
  color: #333;
}

.logout-btn {
  font-size: 14px;
  color: #f56c6c;
  text-decoration: none;
  cursor: pointer;
}

.logout-btn:hover {
  text-decoration: underline;
}

.content {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}
</style>
