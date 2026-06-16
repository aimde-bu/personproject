import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/layout/Layout.vue'
import LoginView from '@/views/LoginView.vue'
import RegisterView from '@/views/RegisterView.vue'
import DashboardView from '@/views/dashboard/DashboardView.vue'
import DistributorView from '@/views/distributor/DistributorView.vue'
import OrderView from '@/views/order/OrderView.vue'
import CommissionView from '@/views/commission/CommissionView.vue'
import ProductView from '@/views/product/ProductView.vue'
import TeamView from '@/views/team/TeamView.vue'


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: LoginView,
      meta: { requiresAuth: false },
    },
    {
      path: '/register',
      name: 'register',
      component: RegisterView,
      meta: { requiresAuth: false },
    },
    {
      path: '/',
      component: Layout,
      redirect: '/dashboard',
      meta: { requiresAuth: true },
      children: [
        {
          path: 'dashboard',
          name: 'dashboard',
          component: DashboardView,
        },
        {
          path: 'distributor',
          name: 'distributor',
          component: DistributorView,
        },
        {
          path: 'order',
          name: 'order',
          component: OrderView,
        },
        {
          path: 'commission',
          name: 'commission',
          component: CommissionView,
        },
        {
          path: 'product',
          name: 'product',
          component: ProductView,
        },
        {
          path: 'team',
          name: 'team',
          component: TeamView,
        },
      ],
    },
  ],
})

// 导航守卫：未登录时跳转到登录页
// router.beforeEach((to) => {
//   const token = localStorage.getItem('dms_token')
//   if (to.matched.some(record => record.meta.requiresAuth) && !token) {
//     ElMessage('请先登录')
//     return { name: 'login' }
//   }
// })

export default router
