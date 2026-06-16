<script setup lang="ts">
import { ref, computed } from 'vue'

interface CommissionRecord {
  id: number
  distributor: string
  orderNo: string
  amount: number
  rate: number
  commission: number
  type: string
  status: string
  time: string
}

const searchKeyword = ref('')
const filterType = ref('')
const filterStatus = ref('')

const commissions = ref<CommissionRecord[]>([
  { id: 1, distributor: '张三', orderNo: 'ORD20260601001', amount: 2680, rate: 10, commission: 268, type: '直接佣金', status: '已到账', time: '2026-06-01 14:30' },
  { id: 2, distributor: '李四', orderNo: 'ORD20260601002', amount: 1560, rate: 10, commission: 156, type: '直接佣金', status: '待结算', time: '2026-06-01 13:20' },
  { id: 3, distributor: '张三', orderNo: 'ORD20260601002', amount: 1560, rate: 3, commission: 46.8, type: '间接佣金', status: '待结算', time: '2026-06-01 13:20' },
  { id: 4, distributor: '王五', orderNo: 'ORD20260601003', amount: 3200, rate: 10, commission: 320, type: '直接佣金', status: '已到账', time: '2026-06-01 11:45' },
  { id: 5, distributor: '钱七', orderNo: 'ORD20260531005', amount: 4100, rate: 10, commission: 410, type: '直接佣金', status: '已到账', time: '2026-05-31 16:55' },
  { id: 6, distributor: '张三', orderNo: 'ORD20260531005', amount: 4100, rate: 3, commission: 123, type: '间接佣金', status: '已到账', time: '2026-05-31 16:55' },
  { id: 7, distributor: '孙八', orderNo: 'ORD20260530006', amount: 1040, rate: 10, commission: 104, type: '直接佣金', status: '待结算', time: '2026-05-30 10:30' },
  { id: 8, distributor: '李四', orderNo: 'ORD20260530006', amount: 1040, rate: 3, commission: 31.2, type: '间接佣金', status: '待结算', time: '2026-05-30 10:30' },
])

const totalCommission = computed(() => {
  return commissions.value.reduce((sum, c) => sum + c.commission, 0)
})

const settledCommission = computed(() => {
  return commissions.value.filter(c => c.status === '已到账').reduce((sum, c) => sum + c.commission, 0)
})

const pendingCommission = computed(() => {
  return commissions.value.filter(c => c.status === '待结算').reduce((sum, c) => sum + c.commission, 0)
})

const filteredCommissions = computed(() => {
  return commissions.value.filter(c => {
    const matchKeyword = !searchKeyword.value ||
      c.distributor.includes(searchKeyword.value) ||
      c.orderNo.includes(searchKeyword.value)
    const matchType = !filterType.value || c.type === filterType.value
    const matchStatus = !filterStatus.value || c.status === filterStatus.value
    return matchKeyword && matchType && matchStatus
  })
})

function formatMoney(value: number): string {
  return value.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
}

function statusClass(status: string): string {
  return status === '已到账' ? 'status-settled' : 'status-pending'
}

function typeClass(type: string): string {
  return type === '直接佣金' ? 'type-direct' : 'type-indirect'
}
</script>

<template>
  <div class="commission-page">
    <h2 class="page-title">佣金管理</h2>

    <div class="summary-grid">
      <div class="summary-card">
        <div class="summary-value">¥{{ formatMoney(totalCommission) }}</div>
        <div class="summary-label">佣金总额</div>
      </div>
      <div class="summary-card settled">
        <div class="summary-value">¥{{ formatMoney(settledCommission) }}</div>
        <div class="summary-label">已结算</div>
      </div>
      <div class="summary-card pending">
        <div class="summary-value">¥{{ formatMoney(pendingCommission) }}</div>
        <div class="summary-label">待结算</div>
      </div>
    </div>

    <div class="filter-bar">
      <input v-model="searchKeyword" type="text" placeholder="搜索分销商/订单号" class="search-input" />
      <select v-model="filterType" class="filter-select">
        <option value="">全部类型</option>
        <option value="直接佣金">直接佣金</option>
        <option value="间接佣金">间接佣金</option>
      </select>
      <select v-model="filterStatus" class="filter-select">
        <option value="">全部状态</option>
        <option value="已到账">已到账</option>
        <option value="待结算">待结算</option>
      </select>
      <button class="export-btn">导出记录</button>
    </div>

    <div class="table-card">
      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>分销商</th>
            <th>关联订单</th>
            <th>订单金额</th>
            <th>佣金比例</th>
            <th>佣金金额</th>
            <th>类型</th>
            <th>状态</th>
            <th>时间</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="c in filteredCommissions" :key="c.id">
            <td>{{ c.id }}</td>
            <td>{{ c.distributor }}</td>
            <td>{{ c.orderNo }}</td>
            <td>¥{{ formatMoney(c.amount) }}</td>
            <td>{{ c.rate }}%</td>
            <td class="commission-amount">¥{{ formatMoney(c.commission) }}</td>
            <td><span class="type-tag" :class="typeClass(c.type)">{{ c.type }}</span></td>
            <td><span class="status-tag" :class="statusClass(c.status)">{{ c.status }}</span></td>
            <td>{{ c.time }}</td>
          </tr>
        </tbody>
      </table>
      <div class="pagination">
        <span class="page-info">共 {{ filteredCommissions.length }} 条记录</span>
      </div>
    </div>
  </div>
</template>

<style scoped>
.commission-page {
  max-width: 1200px;
}

.page-title {
  margin: 0 0 20px;
  font-size: 22px;
  color: #1d1e2c;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.summary-card {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border-left: 4px solid #409eff;
}

.summary-card.settled {
  border-left-color: #67c23a;
}

.summary-card.pending {
  border-left-color: #e6a23c;
}

.summary-value {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin-bottom: 4px;
}

.summary-label {
  font-size: 14px;
  color: #999;
}

.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  align-items: center;
  flex-wrap: wrap;
}

.search-input {
  height: 36px;
  padding: 0 12px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
  width: 220px;
}

.search-input:focus {
  border-color: #409eff;
}

.filter-select {
  height: 36px;
  padding: 0 12px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
  background: #fff;
}

.export-btn {
  margin-left: auto;
  height: 36px;
  padding: 0 16px;
  background: #67c23a;
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
}

.export-btn:hover {
  background: #5daf34;
}

.table-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.data-table th {
  text-align: left;
  padding: 12px 12px;
  background: #f5f7fa;
  color: #666;
  font-weight: 500;
  border-bottom: 1px solid #ebeef5;
  white-space: nowrap;
}

.data-table td {
  padding: 12px 12px;
  border-bottom: 1px solid #ebeef5;
  color: #333;
  white-space: nowrap;
}

.commission-amount {
  font-weight: 600;
  color: #e6a23c;
}

.type-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.type-direct { background: #ecf5ff; color: #409eff; }
.type-indirect { background: #f0f9eb; color: #67c23a; }

.status-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status-settled { background: #f0f9eb; color: #67c23a; }
.status-pending { background: #fdf6ec; color: #e6a23c; }

.pagination {
  padding: 12px 16px;
  display: flex;
  justify-content: flex-end;
}

.page-info {
  font-size: 13px;
  color: #999;
}
</style>
