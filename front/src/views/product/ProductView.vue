<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import request from '@/utils/request'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { UploadProps } from 'element-plus'

onMounted(async () => {
  await refresh()
})

//刷新页面
const refresh = async () =>{
  const response = await request.get('/product/all')
  if (response.data.code === 200) {
    products.value = response.data.data
  }else{
    ElMessage.error('刷新失败')
  }
}

interface Product {
  id: number
  name: string
  category: string
  price: number
  commission: number
  stock: number
  sales: number
  imageUrl: string
  status: string
}

const Form = ref<Product>({
  id: 0,
  name: '',
  category: '',
  price: 0,
  commission: 0,
  stock: 0,
  sales: 0,
  imageUrl: '',
  status: ''
})

const searchKeyword = ref('')
const filterCategory = ref('')
const filterStatus = ref('')
const products = ref<Product[]>([])

const filteredProducts = computed(() => {
  return products.value.filter(p => {
    const matchKeyword = !searchKeyword.value || p.name.includes(searchKeyword.value)
    const matchCategory = !filterCategory.value || p.category === filterCategory.value
    const matchStatus = !filterStatus.value || p.status === filterStatus.value
    return matchKeyword && matchCategory && matchStatus
  })
})

/**
 * 分页功能
 */
// 总页数
const total = computed(() => {
  return products.value.length
})
const pageSize = ref(10) // 每页数量
const currentPage = ref(1) // 当前页码
const pageSizeOptions = [5, 6, 7, 8, 9, 10]// 每页数量选项
// 当前页的数据
const paginatedOrders = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredProducts.value.slice(start, end)
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
  const product = products.value.find(p => p.id === id)
  if (!product) return
  Form.value = { ...product }
  editDialogVisible.value = true
}

const submitEdit = async () => {
  try {
    const response = await request.put('/product', Form.value)
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
    const response = await request.delete(`/product/${id}`)
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
    name: '',
    category: '',
    price: 0,
    commission: 0,
    stock: 0,
    sales: 0,
    imageUrl: '',
    status: ''
  }
  addDialogVisible.value = true

}
const submitAdd = async () => {
  try {
    const response = await request.post('/product', Form.value)
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

/**
 * 图片上传
 */
const uploadUrl = '/api/file/upload'
const headers = computed(() => {
  const token = localStorage.getItem('token')
  return token ? { Authorization: token } : {}
})

const handleUploadSuccess: UploadProps['onSuccess'] = (response) => {
  if (response.code === 200) {
    Form.value.imageUrl = response.data
    ElMessage.success('图片上传成功')
  } else {
    ElMessage.error('图片上传失败')
  }
}

const beforeUpload: UploadProps['beforeUpload'] = (rawFile) => {
  const isImage = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'].includes(rawFile.type)
  const isLt2M = rawFile.size / 1024 / 1024 < 2
  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}
</script>

<template>
  <div class="product-page">
    <h2 class="page-title">商品管理</h2>

    <div class="filter-bar">
      <input v-model="searchKeyword" type="text" placeholder="搜索商品名称" class="search-input" />
      <select v-model="filterCategory" class="filter-select">
        <option value="">全部分类</option>
        <option value="美妆">美妆</option>
        <option value="保健">保健</option>
        <option value="酒水">酒水</option>
        <option value="食品">食品</option>
        <option value="茶饮">茶饮</option>
      </select>
      <select v-model="filterStatus" class="filter-select">
        <option value="">全部状态</option>
        <option value="上架">上架</option>
        <option value="下架">下架</option>
        <option value="缺货">缺货</option>
      </select>
      <button class="add-btn" @click="add">+ 新增商品</button>

       <!-- 新增商品对话框 -->
    <el-dialog v-model="addDialogVisible" title="新增商品" width="500px">
      <el-form :model="Form" label-width="80px">
        <el-form-item label="商品名称">
          <el-input v-model="Form.name" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="Form.category" placeholder="请选择分类">
            <el-option label="美妆" value="美妆" />
            <el-option label="保健" value="保健" />
            <el-option label="酒水" value="酒水" />
            <el-option label="食品" value="食品" />
            <el-option label="茶饮" value="茶饮" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="Form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="佣金">
          <el-input-number v-model="Form.commission" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="Form.stock" :min="0" />
        </el-form-item>
        <el-form-item label="销售量">
          <el-input-number v-model="Form.sales" :min="0" />
        </el-form-item>
        <el-form-item label="图片">
          <el-upload
            class="image-uploader"
            :action="uploadUrl"
            :headers="headers"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
          >
            <img v-if="Form.imageUrl" :src="Form.imageUrl" class="upload-preview" />
            <el-icon v-else class="upload-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="Form.status" placeholder="请选择状态">
            <el-option label="上架" value="上架" />
            <el-option label="下架" value="下架" />
            <el-option label="缺货" value="缺货" />
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
            <th>商品名称</th>
            <th>分类</th>
            <th>价格</th>
            <th>佣金</th>
            <th>库存</th>
            <th>销售量</th>
            <th>图片</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="d in paginatedOrders" :key="d.id">
            <td>{{ d.id }}</td>
            <td>{{ d.name }}</td>
            <td>{{ d.category }}</td>
            <td>¥{{ d.price }}</td>
            <td>¥{{d.commission}}</td>
            <td>{{d.stock}}</td>
            <td>{{d.sales}}</td>
            <td>
              <el-image v-if="d.imageUrl" :src="d.imageUrl" :preview-src-list="[d.imageUrl]" style="width: 50px; height: 50px" fit="cover" />
              <span v-else>暂无图片</span>
            </td>
            <td><span class="status-tag" :class="d.status === '上架' ? 'status-active' : 'status-frozen'">{{ d.status }}</span></td>
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

    <!-- 编辑商品对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑商品" width="500px">
      <el-form :model="Form" label-width="80px">
        <el-form-item label="商品名称">
          <el-input v-model="Form.name" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="Form.category" placeholder="请选择分类">
            <el-option label="美妆" value="美妆" />
            <el-option label="保健" value="保健" />
            <el-option label="酒水" value="酒水" />
            <el-option label="食品" value="食品" />
            <el-option label="茶饮" value="茶饮" />
          </el-select>
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="Form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="佣金">
          <el-input-number v-model="Form.commission" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="Form.stock" :min="0" />
        </el-form-item>
        <el-form-item label="销售量">
          <el-input-number v-model="Form.sales" :min="0" />
        </el-form-item>
        <el-form-item label="图片">
          <el-upload
            class="image-uploader"
            :action="uploadUrl"
            :headers="headers"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :before-upload="beforeUpload"
          >
            <img v-if="Form.imageUrl" :src="Form.imageUrl" class="upload-preview" />
            <el-icon v-else class="upload-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="Form.status" placeholder="请选择状态">
            <el-option label="上架" value="上架" />
            <el-option label="下架" value="下架" />
            <el-option label="缺货" value="缺货" />
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

.image-uploader :deep(.el-upload) {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 100px;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: border-color 0.3s;
}

.image-uploader :deep(.el-upload:hover) {
  border-color: #409eff;
}

.upload-preview {
  width: 100px;
  height: 100px;
  object-fit: cover;
  display: block;
}

.upload-icon {
  font-size: 28px;
  color: #8c939d;
}
</style>
