import { reactive } from 'vue'

export interface UserInfo {
  username: string
  nickname: string
  role: string
}

interface UserState {
  isLoggedIn: boolean
  userInfo: UserInfo | null
  token: string
}

const STORAGE_KEY = 'dms_user'
const TOKEN_KEY = 'dms_token'

function loadState(): UserState {
  try {
    const saved = localStorage.getItem(STORAGE_KEY)
    const token = localStorage.getItem(TOKEN_KEY)
    if (saved && token) {
      const userInfo = JSON.parse(saved) as UserInfo
      return { isLoggedIn: true, userInfo, token }
    }
  } catch {
    // ignore
  }
  return { isLoggedIn: false, userInfo: null, token: '' }
}

const state = reactive<UserState>(loadState())

// 模拟用户数据库（注册的用户会存入这里）
const registeredUsers = reactive<Array<{ username: string; password: string; nickname: string; role: string }>>([
  { username: 'admin', password: '123456', nickname: '管理员', role: 'admin' },
])

function login(username: string, password: string): { success: boolean; message: string } {
  const user = registeredUsers.find(u => u.username === username && u.password === password)
  if (!user) {
    return { success: false, message: '用户名或密码错误' }
  }
  state.isLoggedIn = true
  state.token = 'token_' + Date.now()
  state.userInfo = { username: user.username, nickname: user.nickname, role: user.role }
  localStorage.setItem(STORAGE_KEY, JSON.stringify(state.userInfo))
  localStorage.setItem(TOKEN_KEY, state.token)
  return { success: true, message: '登录成功' }
}

function register(username: string, password: string, nickname: string): { success: boolean; message: string } {
  if (!username || !password || !nickname) {
    return { success: false, message: '请填写完整信息' }
  }
  if (username.length < 3) {
    return { success: false, message: '用户名至少3个字符' }
  }
  if (password.length < 6) {
    return { success: false, message: '密码至少6个字符' }
  }
  const exists = registeredUsers.find(u => u.username === username)
  if (exists) {
    return { success: false, message: '用户名已存在' }
  }
  registeredUsers.push({ username, password, nickname, role: 'distributor' })
  return { success: true, message: '注册成功，请登录' }
}

function logout() {
  state.isLoggedIn = false
  state.userInfo = null
  state.token = ''
  localStorage.removeItem(STORAGE_KEY)
  localStorage.removeItem(TOKEN_KEY)
}

export function useUserStore() {
  return {
    state,
    login,
    register,
    logout,
  }
}
