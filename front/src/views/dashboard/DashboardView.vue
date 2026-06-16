<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import request from '@/utils/request'

interface Distributor {
  id: number
  realName: string
  parentId: string
}

interface Order {
  id: number
  orderNumber: string
  distributorID: string
  totalAmount: number
  totalcommission: number
  status: string
  createdTime: string
}

const distributors = ref<Distributor[]>([])
const orders = ref<Order[]>([])

const fetchData = async () => {
  const [distRes, orderRes] = await Promise.all([
    request.get('/distributor/all'),
    request.get('/orders/all')
  ])
  if (distRes.data.code === 200) distributors.value = distRes.data.data
  if (orderRes.data.code === 200) orders.value = orderRes.data.data
}

onMounted(fetchData)

// 统计卡片
const stats = computed(() => [
  { label: '分销商总数', value: distributors.value.length, unit: '人', color: '#409eff' },
  { label: '本月订单', value: currentMonthOrders.value.length, unit: '笔', color: '#67c23a' },
  { label: '本月佣金', value: Math.round(currentMonthOrders.value.reduce((s, o) => s + (o.totalcommission || 0), 0)), unit: '元', color: '#e6a23c' },
  { label: '本月销售额', value: Math.round(currentMonthOrders.value.reduce((s, o) => s + (o.totalAmount || 0), 0)), unit: '元', color: '#f56c6c' },
])

// 本月订单
const currentMonthOrders = computed(() => {
  const now = new Date()
  const ym = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
  return orders.value.filter(o => o.createdTime && o.createdTime.startsWith(ym))
})

// 最近5笔订单
const recentOrders = computed(() => {
  return [...orders.value]
    .sort((a, b) => (b.createdTime || '').localeCompare(a.createdTime || ''))
    .slice(0, 5)
    .map(o => ({
      id: o.orderNumber,
      distributor: getDistributorName(o.distributorID),
      amount: o.totalAmount,
      commission: o.totalcommission,
      status: o.status,
      time: o.createdTime
    }))
})

// 分销商排行（按销售额降序取前5）
const topDistributors = computed(() => {
  return distributors.value
    .map(d => {
      const distOrders = orders.value.filter(o => String(o.distributorID) === String(d.id))
      const sales = distOrders.reduce((s, o) => s + (o.totalAmount || 0), 0)
      const orderCount = distOrders.length
      return { name: d.realName, level: getLevel(sales), sales, orders: orderCount }
    })
    .sort((a, b) => b.sales - a.sales)
    .slice(0, 5)
})

const getDistributorName = (id: string) => {
  if (!id) return '-'
  return distributors.value.find(d => String(d.id) === String(id))?.realName || id
}

const getLevel = (totalSales: number) => {
  if (totalSales >= 10000) return '金牌'
  if (totalSales >= 54200) return '银牌'
  return '铜牌'
}

function formatMoney(value: number): string {
  return value.toLocaleString()
}

function statusClass(status: string): string {
  switch (status) {
    case '已完成': return 'status-done'
    case '待结算': return 'status-pending'
    case '待发货': return 'status-shipping'
    case '已取消': return 'status-cancel'
    case '已退款': return 'status-refund'
    default: return ''
  }
}
</script>

<template >
  <div class="dashboard">
    <h2 class="page-title">数据概览</h2>

    <div class="stats-grid">
      <div v-for="stat in stats" :key="stat.label" class="stat-card" :style="{ borderLeftColor: stat.color }">
        <div class="stat-value" :style="{ color: stat.color }">{{ formatMoney(stat.value) }}</div>
        <div class="stat-label">{{ stat.label }}（{{ stat.unit }}）</div>
      </div>
    </div>

    <div class="section-grid">
      <div class="section-card">
        <h3 class="section-title">最近订单</h3>
        <table class="data-table">
          <thead>
            <tr>
              <th>订单号</th>
              <th>分销商</th>
              <th>金额</th>
              <th>佣金</th>
              <th>状态</th>
              <th>时间</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="order in recentOrders" :key="order.id">
              <td>{{ order.id }}</td>
              <td>{{ order.distributor }}</td>
              <td>¥{{ formatMoney(order.amount) }}</td>
              <td>¥{{ formatMoney(order.commission) }}</td>
              <td><span class="status-tag" :class="statusClass(order.status)">{{ order.status }}</span></td>
              <td>{{ order.time }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div class="section-card">
        <h3 class="section-title">分销商排行</h3>
        <table class="data-table">
          <thead>
            <tr>
              <th>排名</th>
              <th>姓名</th>
              <th>等级</th>
              <th>销售额</th>
              <th>订单数</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(d, index) in topDistributors" :key="d.name">
              <td><span class="rank-badge" :class="'rank-' + (index + 1)">{{ index + 1 }}</span></td>
              <td>{{ d.name }}</td>
              <td><span class="level-tag">{{ d.level }}</span></td>
              <td>¥{{ formatMoney(d.sales) }}</td>
              <td>{{ d.orders }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dashboard {
  max-width: 1200px;
}

.page-title {
  margin: 0 0 20px;
  font-size: 22px;
  color: #1d1e2c;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  border-left: 4px solid;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 14px;
  color: #999;
}

.section-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.section-card {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.section-title {
  margin: 0 0 16px;
  font-size: 16px;
  color: #333;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.data-table th {
  text-align: left;
  padding: 8px 10px;
  background: #f5f7fa;
  color: #666;
  font-weight: 500;
  border-bottom: 1px solid #ebeef5;
}

.data-table td {
  padding: 10px 10px;
  border-bottom: 1px solid #ebeef5;
  color: #333;
}

.status-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status-done {
  background: #f0f9eb;
  color: #67c23a;
}

.status-pending {
  background: #fdf6ec;
  color: #e6a23c;
}

.status-cancel {
  background: #fef0f0;
  color: #f56c6c;
}

.status-shipping {
  background: #ecf5ff;
  color: #409eff;
}

.status-refund {
  background: #fef0f0;
  color: #f56c6c;
}

.level-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  background: #ecf5ff;
  color: #409eff;
}

.rank-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  font-size: 12px;
  font-weight: 700;
  color: #fff;
  background: #c0c4cc;
}

.rank-1 { background: #f56c6c; }
.rank-2 { background: #e6a23c; }
.rank-3 { background: #409eff; }
</style>
