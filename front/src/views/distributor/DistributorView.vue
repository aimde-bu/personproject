<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'

interface Order {
  distributorID: string
  totalAmount: number
  totalcommission: number
}

const orders = ref<Order[]>([])

const refreshOrders = async () => {
  const response = await request.get('/orders/all')
  if (response.data.code === 200) {
    orders.value = response.data.data
  }
}

// 根据订单计算分销商总销售额
const calcTotalSales = (distributorId: number) => {
  return orders.value
    .filter(o => String(o.distributorID) === String(distributorId))
    .reduce((sum, o) => sum + (o.totalAmount || 0), 0)
}

// 分销商自己的直接佣金（来自自己的订单）
const getDirectCommission = (distributorId: number) => {
  return orders.value
    .filter(o => String(o.distributorID) === String(distributorId))
    .reduce((sum, o) => sum + (o.totalcommission || 0), 0)
}

// 获取所有下级分销商（递归）
const getDescendants = (distributorId: number): number[] => {
  const children = distributors.value
    .filter(d => String(d.parentId) === String(distributorId))
    .map(d => d.id)
  const result = [...children]
  for (const childId of children) {
    result.push(...getDescendants(childId))
  }
  return result
}

// 根据订单计算分销商总佣金（自己的 + 每个下级佣金的5%）
const calcTotalCommission = (distributorId: number) => {
  let total = getDirectCommission(distributorId)
  const descendants = getDescendants(distributorId)
  for (const descId of descendants) {
    total += getDirectCommission(descId) * 0.05
  }
  return Math.round(total * 100) / 100
}

onMounted(async () => {
  await Promise.all([refresh(), refreshOrders()])
})

//刷新页面
const refresh = async () =>{
  const response = await request.get('/distributor/all')
  if (response.data.code === 200) {
    distributors.value = response.data.data
  }else{
    ElMessage.error('刷新失败')
  }
}

interface Distributor {
  id: number
  realName: string
  phone: string
  level: string
  parentId: string
  totalSales: number
  totalCommission: number
  status: string
  createdTime: string
}

//初始化分销商数据
const Form = ref<Distributor>({
  id: 0,
  realName: '',
  phone: '',
  level: '',
  parentId: '',
  totalSales: 0,
  totalCommission: 0,
  status: '',
  createdTime: ''
})

const searchKeyword = ref('')
const filterStatus = ref('')
const distributors = ref<Distributor[]>([])

const getParentName = (parentId: string) => {
  if (!parentId) return '-'
  return distributors.value.find(p => String(p.id) === String(parentId))?.realName || parentId
}

const getLevel = (totalSales: number) => {
  if (totalSales >= 10000) return '金牌'
  if (totalSales >= 54200) return '银牌'
  return '铜牌'
}



const filteredDistributors = computed(() => {
  return distributors.value.filter(p => {
    const matchKeyword = !searchKeyword.value || p.realName.includes(searchKeyword.value) || p.phone.includes(searchKeyword.value)
    const matchStatus = !filterStatus.value || p.status === filterStatus.value
    return matchKeyword && matchStatus
  })
})

/**
 * 分页功能
 */
// 总页数
const total = computed(() => {
  return distributors.value.length
})
const pageSize = ref(10) // 每页数量
const currentPage = ref(1) // 当前页码
const pageSizeOptions = [5, 6, 7, 8, 9, 10]// 每页数量选项
// 当前页的数据
const paginatedOrders = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredDistributors.value.slice(start, end)
})
// 每页数量变化时，重置到第一页
const handleSizeChange = () => {
  currentPage.value = 1
}
// 页码变化
const handlePageChange = async (page: number) => {
  currentPage.value = page
}

/**
 * 编辑商品
 */
const editDialogVisible = ref(false)

const edit = async (id: number) => {
  const distributor = distributors.value.find(p => p.id === id)
  if (!distributor) return
  Form.value = { ...distributor }
  editDialogVisible.value = true
}

const submitEdit = async () => {
  try {
    const response = await request.put('/distributor', Form.value)
    if (response.data.code === 200) {
      ElMessage.success('编辑成功')
      editDialogVisible.value = false 
      await refresh()
    }else{
      ElMessage.error('编辑失败')
    }
  } catch {
    ElMessage.error('编辑失败')
  }
}

/**
 * 删除商品
 */
const del = async (id: number) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？删除后不可恢复。', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const response = await request.delete(`/distributor/${id}`)
    if (response.data.code === 200) {
      ElMessage.success('删除成功')
      await refresh()
    }else{
      ElMessage.error('删除失败')
    }
  } catch {
    ElMessage.error('删除失败')
  }
}

/**
 * 新增商品
 */
const addDialogVisible = ref(false)
const add = async () => {
  Form.value = {
    id: 0,
    realName: '',
    phone: '',
    level: '',
    parentId: '',
    totalSales: 0,
    totalCommission: 0,
    status: '',
    createdTime: ''
  }
  addDialogVisible.value = true
}
const submitAdd = async () => {
  try {
    const response = await request.post('/distributor', Form.value)
    if (response.data.code === 200) {
      ElMessage.success('新增成功')
      addDialogVisible.value = false 
      await refresh()
    }else{
      ElMessage.error('新增失败')
    }
  } catch {
    ElMessage.error('新增失败')
  }
}

</script>

<template>
  <div class="distributor-page">
    <h2 class="page-title">分销商管理</h2>
    <div class="filter-bar">
      <input v-model="searchKeyword" type="text" placeholder="搜索姓名/手机号" class="search-input" />
      <select v-model="filterStatus" class="filter-select">
        <option value="">全部状态</option>
        <option value="正常">正常</option>
        <option value="冻结">冻结</option>
      </select>
      <button class="add-btn" @click="add">+ 新增分销商</button>
           <!-- 新增分销商对话框 -->
    <el-dialog v-model="addDialogVisible" title="新增分销商" width="500px">
      <el-form :model="Form" label-width="80px">
        <el-form-item label="姓名">
          <el-input v-model="Form.realName" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="Form.phone" />
        </el-form-item>
        <el-form-item label="上级分销商">
          <el-select v-model="Form.parentId" placeholder="请选择上级分销商" clearable>
            <el-option v-for="d in distributors" :key="d.id" :label="d.realName" :value="String(d.id)" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
            <el-select v-model="Form.status" placeholder="请选择状态">
            <el-option label="正常" value="正常" />
            <el-option label="冻结" value="冻结" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdd">确定</el-button>
      </template>
    </el-dialog>
    </div>
    
    <div class="table-card">
      <table class="data-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>姓名</th>
            <th>手机号</th>
            <th>等级</th>
            <th>上级</th>
            <th>累计销售</th>
            <th>累计佣金</th>
            <th>状态</th>
            <th>加入日期</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="d in paginatedOrders" :key="d.id">
            <td>{{ d.id }}</td>
            <td>{{ d.realName }}</td>
            <td>{{ d.phone }}</td>
            <td><span class="level-tag" :class="calcTotalSales(d.id) >= 10000 ? 'level-gold' : calcTotalSales(d.id) >= 54200 ? 'level-silver' : 'level-bronze'">
              {{getLevel(calcTotalSales(d.id))}}</span></td>  
            <td>
              {{ getParentName(d.parentId) }}
            </td>
            <td>¥{{ calcTotalSales(d.id) }}</td>
            <td>¥{{ calcTotalCommission(d.id) }}</td>
            <td><span class="status-tag" :class="d.status === '正常' ? 'status-active' : 'status-frozen'">{{ d.status }}</span></td>
            <td>{{ d.createdTime }}</td>
            <td class="actions">
              <button class="action-btn edit" @click="edit(d.id)">编辑</button>
              <button class="action-btn delete" @click="del(d.id)">删除</button>
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

    <!-- 编辑分销商对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑分销商" width="500px">
      <el-form :model="Form" label-width="80px">
        <el-form-item label="姓名">
          <el-input v-model="Form.realName" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="Form.phone" />
        </el-form-item>
        <el-form-item label="上级分销商">
          <el-select v-model="Form.parentId" placeholder="请选择上级分销商" clearable>
            <el-option v-for="d in distributors" :key="d.id" :label="d.realName" :value="String(d.id)" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="Form.status" placeholder="请选择状态">
            <el-option label="正常" value="正常" />
            <el-option label="冻结" value="冻结" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-current {
  font-size: 13px;
  color: #606266;
  padding: 0 8px;
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

.page-controls {
  display: flex;
  gap: 4px;
  align-items: center;
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

.page-info {
  font-size: 13px;
  color: #999;
}

.page-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.pagination {
  padding: 12px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.distributor-page {
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

.level-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.level-gold { background: #fdf6ec; color: #e6a23c; }
.level-silver { background: #ecf5ff; color: #409eff; }
.level-bronze { background: #f5f7fa; color: #909399; }

.status-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status-active { background: #f0f9eb; color: #67c23a; }
.status-frozen { background: #fef0f0; color: #f56c6c; }

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

.action-btn.edit {
  background: #ecf5ff;
  color: #409eff;
}

.action-btn.delete {
  background: #fef0f0;
  color: #f56c6c;
}
</style>
