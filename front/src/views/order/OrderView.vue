<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

interface Product {
  id: number
  name: string
  category: string
  price: number
  commission: number
  stock: number
  sales: number
  imageUrl: string | null
  status: string
}

interface Distributor {
  id: number
  realName: string
  phone: string
  level: string | null
  parentId: number
  totalSales: number
  totalCommission: number
  status: string
  createdTime: string
}

interface Order {
  id: number
  orderNumber: string
  productID: string
  distributorID: string
  orderNum: number
  totalAmount: number
  totalcommission: number
  status: string
  createdTime: string
}

const statusOptions = ['已完成', '待结算', '待发货', '已取消', '已退款']

const createEmptyForm = (): Order => ({
  id: 0,
  orderNumber: '',
  productID: '',
  distributorID: '',
  orderNum: 0,
  totalAmount: 0,
  totalcommission: 0,
  status: '待发货',
  createdTime: ''
})

const products = ref<Product[]>([])
const distributors = ref<Distributor[]>([])
const orders = ref<Order[]>([])

const getProductName = (id: string) => {
  if (!id) return '-'
  return products.value.find(p => String(p.id) === String(id))?.name || id
}

const getDistributorName = (id: string) => {
  if (!id) return '-'
  return distributors.value.find(d => String(d.id) === String(id))?.realName || id
}

onMounted(async () => {
  await Promise.all([refresh(), refreshProducts(), refreshDistributors()])
})

const refresh = async () => {
  const response = await request.get('/orders/all')
  if (response.data.code === 200) {
    orders.value = response.data.data
  } else {
    ElMessage.error('刷新失败')
  }
}

const refreshProducts = async () => {
  const response = await request.get('/product/all')
  if (response.data.code === 200) {
    products.value = response.data.data
  }
}

const refreshDistributors = async () => {
  const response = await request.get('/distributor/all')
  if (response.data.code === 200) {
    distributors.value = response.data.data
  }
}

// 搜索订单
const searchKeyword = ref('')
const filterStatus = ref('')

const filteredOrders = computed(() => {
  return orders.value.filter(o => {
    const keyword = searchKeyword.value.toLowerCase()
    const matchKeyword = !keyword ||
      o.orderNumber.toLowerCase().includes(keyword) ||
      getProductName(o.productID).toLowerCase().includes(keyword) ||
      getDistributorName(o.distributorID).toLowerCase().includes(keyword)
    const matchStatus = !filterStatus.value || o.status === filterStatus.value
    return matchKeyword && matchStatus
  })
})

/**
 * 分页功能
 */
const total = computed(() => filteredOrders.value.length)
const pageSize = ref(10)
const currentPage = ref(1)
const pageSizeOptions = [5, 6, 7, 8, 9, 10]

const paginatedOrders = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredOrders.value.slice(start, end)
})

const handleSizeChange = () => {
  currentPage.value = 1
}

const handlePageChange = async (page: number) => {
  currentPage.value = page
}

/**
 * 对话框（新增/编辑共用）
 */
const dialogVisible = ref(false)
const isEdit = ref(false)
const Form = ref<Order>(createEmptyForm())

const generateOrderNumber = () => {
  const now = new Date()
  const pad = (n: number) => String(n).padStart(2, '0')
  const dateStr = `${now.getFullYear()}${pad(now.getMonth() + 1)}${pad(now.getDate())}`
  const timeStr = `${pad(now.getHours())}${pad(now.getMinutes())}${pad(now.getSeconds())}`
  const random = String(Math.floor(Math.random() * 10000)).padStart(4, '0')
  return `ORD${dateStr}${timeStr}${random}`
}

const recalcAmount = () => {
  const product = products.value.find(p => String(p.id) === String(Form.value.productID))
  if (product) {
    const num = Form.value.orderNum || 0
    Form.value.totalAmount = Math.round(product.price * num * 100) / 100
    Form.value.totalcommission = Math.round(product.commission * num * 100) / 100
  }
}

const onProductChange = () => {
  if (!isEdit.value && Form.value.orderNum === 0) {
    Form.value.orderNum = 1
  }
  recalcAmount()
}

const openAdd = () => {
  isEdit.value = false
  Form.value = createEmptyForm()
  Form.value.orderNumber = generateOrderNumber()
  dialogVisible.value = true
}

const openView = (id: number) => {
  const order = orders.value.find(p => p.id === id)
  if (!order) return
  isEdit.value = true
  Form.value = { ...order }
  dialogVisible.value = true
}

const submitForm = async () => {
  try {
    const response = await request.post('/orders', Form.value)
    if (response.data.code === 200) {
      ElMessage.success('新增成功')
      dialogVisible.value = false
      await refresh()
    } else {
      ElMessage.error('新增失败')
    }
  } catch {
    ElMessage.error('新增失败')
  }
}

/**
 * 删除订单
 */
const del = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该订单吗？删除后不可恢复。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const response = await request.delete(`/orders/${id}`)
    if (response.data.code === 200) {
      ElMessage.success('删除成功')
      await refresh()
    } else {
      ElMessage.error('删除失败')
    }
  } catch {
    ElMessage.error('删除失败')
  }
}
</script>

<template>
  <div class="order-page">
    <h2 class="page-title">订单管理</h2>

    <div class="filter-bar">
      <input v-model="searchKeyword" type="text" placeholder="搜索订单号/商品名/分销商名" class="search-input" />
      <select v-model="filterStatus" class="filter-select">
        <option value="">全部状态</option>
        <option v-for="s in statusOptions" :key="s" :value="s">{{ s }}</option>
      </select>
      <button class="add-btn" @click="openAdd">新增订单</button>
    </div>

    <div class="table-card">
      <table class="data-table">
        <thead>
          <tr>
            <th>订单号</th>
            <th>商品名称</th>
            <th>分销商名称</th>
            <th>商品数量</th>
            <th>商品金额</th>
            <th>商品佣金</th>
            <th>状态</th>
            <th>下单时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="order in paginatedOrders" :key="order.orderNumber">
            <td>{{ order.orderNumber }}</td>
            <td>{{ getProductName(order.productID) }}</td>
            <td>{{ getDistributorName(order.distributorID) }}</td>
            <td>{{ order.orderNum }}</td>
            <td>¥{{ order.totalAmount }}</td>
            <td>¥{{ order.totalcommission }}</td>
            <td>
              <span class="status-tag" :class="{
                'status-done': order.status === '已完成',
                'status-pending': order.status === '待结算',
                'status-shipping': order.status === '待发货',
                'status-cancel': order.status === '已取消',
                'status-refund': order.status === '已退款'
              }">{{ order.status }}</span>
            </td>
            <td>{{ order.createdTime }}</td>
            <td class="actions">
              <button class="action-btn view" @click="openView(order.id)">查看</button>
              <button class="action-btn delete" @click="del(order.id)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>

      <div class="pagination">
        <div class="page-left">
          <span class="page-info">共 {{ total }} 条记录</span>
          <select v-model="pageSize" @change="handleSizeChange()" class="size-select">
            <option v-for="size in pageSizeOptions" :key="size" :value="size">{{ size }} 条/页</option>
          </select>
        </div>
        <div class="page-controls">
          <button class="page-btn" :disabled="currentPage === 1" @click="handlePageChange(1)">首页</button>
          <button class="page-btn" :disabled="currentPage === 1" @click="handlePageChange(currentPage - 1)">上一页</button>
          <span class="page-current">第 {{ currentPage }} / {{ Math.ceil(total / pageSize) || 1 }} 页</span>
          <button class="page-btn" :disabled="currentPage >= Math.ceil(total / pageSize)" @click="handlePageChange(currentPage + 1)">下一页</button>
          <button class="page-btn" :disabled="currentPage >= Math.ceil(total / pageSize)" @click="handlePageChange(Math.ceil(total / pageSize))">末页</button>
        </div>
      </div>
    </div>

    <!-- 新增/查看订单对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '查看订单' : '新增订单'" width="500px">
      <el-form :model="Form" label-width="80px">
        <el-form-item label="订单号">
          <el-input v-model="Form.orderNumber" disabled />
        </el-form-item>
        <el-form-item label="商品">
          <el-select v-model="Form.productID" placeholder="请选择商品" :disabled="isEdit" @change="onProductChange">
            <el-option v-for="p in products" :key="p.id" :label="p.name" :value="String(p.id)" />
          </el-select>
        </el-form-item>
        <el-form-item label="分销商">
          <el-select v-model="Form.distributorID" placeholder="请选择分销商" :disabled="isEdit">
            <el-option v-for="d in distributors" :key="d.id" :label="d.realName" :value="String(d.id)" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品数量">
          <el-input-number v-model="Form.orderNum" :min="1" :disabled="isEdit" @change="recalcAmount" />
        </el-form-item>
        <el-form-item label="商品金额">
          <el-input-number v-model="Form.totalAmount" :min="0" :precision="2" :controls="false" class="readonly-input" readonly />
        </el-form-item>
        <el-form-item label="商品佣金">
          <el-input-number v-model="Form.totalcommission" :min="0" :precision="2" :controls="false" class="readonly-input" readonly />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button v-if="!isEdit" @click="dialogVisible = false">取消</el-button>
        <el-button v-if="!isEdit" type="primary" @click="submitForm">确定</el-button>
        <el-button v-if="isEdit" type="primary" @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.order-page {
  max-width: 1200px;
}

.page-title {
  margin: 0 0 20px;
  font-size: 22px;
  color: #1d1e2c;
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
  width: 260px;
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

.status-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status-done { background: #f0f9eb; color: #67c23a; }
.status-pending { background: #fdf6ec; color: #e6a23c; }
.status-shipping { background: #ecf5ff; color: #409eff; }
.status-cancel { background: #f5f7fa; color: #909399; }
.status-refund { background: #fef0f0; color: #f56c6c; }

.actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 4px 10px;
  border: none;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
}

.action-btn.view {
  background: #ecf5ff;
  color: #409eff;
}

.action-btn.delete {
  background: #fef0f0;
  color: #f56c6c;
}

.pagination {
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-info {
  font-size: 13px;
  color: #999;
}

.page-controls {
  display: flex;
  gap: 4px;
  align-items: center;
}

.page-btn {
  min-width: 32px;
  height: 32px;
  padding: 0 8px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background: #fff;
  color: #606266;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s;
}

.page-btn:hover:not(:disabled):not(.active) {
  color: #409eff;
  border-color: #409eff;
}

.page-btn.active {
  background: #409eff;
  color: #fff;
  border-color: #409eff;
}

.page-btn:disabled {
  color: #c0c4cc;
  border-color: #e4e7ed;
  cursor: not-allowed;
}

.page-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.size-select {
  height: 32px;
  padding: 0 8px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background: #fff;
  color: #606266;
  font-size: 13px;
  outline: none;
  cursor: pointer;
}

.size-select:focus {
  border-color: #409eff;
}

.page-current {
  font-size: 13px;
  color: #606266;
  padding: 0 8px;
}

.add-btn {
  margin-left: auto;
  height: 36px;
  padding: 0 16px;
  background: #409eff;
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
}

.add-btn:hover {
  background: #3a8ee6;
}

.readonly-input :deep(.el-input__inner) {
  background: #f5f7fa;
  color: #999;
  cursor: not-allowed;
}
</style>
