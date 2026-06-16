import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import zhCn from 'element-plus/es/locale/lang/zh-cn'

const app = createApp(App)
app.use(router)
app.mount('#app')

import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
app.use(ElementPlus, {
  locale: zhCn
})

