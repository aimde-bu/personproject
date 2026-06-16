<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import request from '@/utils/request'

interface TeamMember {
  id: number
  realName: string
  phone: string
  level: string | null
  parentId: number
  totalSales: number
  totalCommission: number
  status: string
  createdTime: string
  children: TeamMember[]
}

onMounted(() => {
  getDistributors()
})

const teamMembers = ref<TeamMember[]>([])
const searchKeyword = ref('')
const expandedIds = ref<Set<number>>(new Set())

const getDistributors = async () => {
  const response = await request.get('/Teams')
  teamMembers.value = response.data.data
}

// 搜索过滤：递归匹配
function filterMembers(members: TeamMember[]): TeamMember[] {
  if (!searchKeyword.value) return members
  return members
    .map(m => {
      const selfMatch = m.realName.includes(searchKeyword.value) || m.phone.includes(searchKeyword.value)
      const filteredChildren = filterMembers(m.children)
      if (selfMatch || filteredChildren.length > 0) {
        return { ...m, children: filteredChildren }
      }
      return null
    })
    .filter((m): m is TeamMember => m !== null)
}

const filteredMembers = computed(() => filterMembers(teamMembers.value))

// 顶层成员（parentId === 0）
const topLevelMembers = computed(() => filteredMembers.value)

// 团队总人数（递归计算）
function countMembers(members: TeamMember[]): number {
  return members.reduce((sum, m) => sum + 1 + countMembers(m.children), 0)
}

// 团队总销售额（递归计算）
function sumSales(members: TeamMember[]): number {
  return members.reduce((sum, m) => sum + m.totalSales + sumSales(m.children), 0)
}

const totalPeople = computed(() => countMembers(teamMembers.value))
const topLevelCount = computed(() => teamMembers.value.length)
const totalSales = computed(() => sumSales(teamMembers.value))

// 展开/折叠
function toggleExpand(id: number) {
  if (expandedIds.value.has(id)) {
    expandedIds.value.delete(id)
  } else {
    expandedIds.value.add(id)
  }
}
function isExpanded(id: number) { return expandedIds.value.has(id) }
function hasChildren(m: TeamMember) { return m.children && m.children.length > 0 }

function formatMoney(v: number) {
  return v >= 10000 ? (v / 10000).toFixed(1) + '万' : v.toLocaleString()
}
</script>

<template>
  <div class="team-page">
    <h2 class="page-title">团队管理</h2>

    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-value">{{ totalPeople }}</div>
        <div class="stat-label">团队总人数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ topLevelCount }}</div>
        <div class="stat-label">一级分销商</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">¥{{ formatMoney(totalSales) }}</div>
        <div class="stat-label">团队总销售额</div>
      </div>
    </div>

    <div class="filter-bar">
      <input v-model="searchKeyword" type="text" placeholder="搜索姓名/手机号" class="search-input" />
    </div>

    <div class="tree-card">
      <div class="tree-header">
        <span class="col-name">分销商</span>
        <span class="col-phone">手机号</span>
        <span class="col-sales">销售额</span>
        <span class="col-commission">佣金</span>
        <span class="col-status">状态</span>
        <span class="col-date">加入日期</span>
      </div>

      <div class="tree-body">
        <template v-for="member in topLevelMembers" :key="member.id">
          <div class="tree-row level-0" @click="hasChildren(member) && toggleExpand(member.id)">
            <span class="expand-icon">
              <template v-if="hasChildren(member)">{{ isExpanded(member.id) ? '▼' : '▶' }}</template>
              <template v-else>&nbsp;</template>
            </span>
            <span class="col-name">{{ member.realName }}</span>
            <span class="col-phone">{{ member.phone }}</span>
            <span class="col-sales">¥{{ formatMoney(member.totalSales) }}</span>
            <span class="col-commission">¥{{ formatMoney(member.totalCommission) }}</span>
            <span class="col-status">{{ member.status }}</span>
            <span class="col-date">{{ member.createdTime }}</span>
          </div>

          <template v-if="isExpanded(member.id) && hasChildren(member)">
            <div
              v-for="child in member.children"
              :key="child.id"
              class="tree-row level-1"
              @click="hasChildren(child) && toggleExpand(child.id)"
            >
              <span class="expand-icon">
                <template v-if="hasChildren(child)">{{ isExpanded(child.id) ? '▼' : '▶' }}</template>
                <template v-else>&nbsp;</template>
              </span>
              <span class="col-name">{{ child.realName }}</span>
              <span class="col-phone">{{ child.phone }}</span>
              <span class="col-sales">¥{{ formatMoney(child.totalSales) }}</span>
              <span class="col-commission">¥{{ formatMoney(child.totalCommission) }}</span>
              <span class="col-status">{{ child.status }}</span>
              <span class="col-date">{{ child.createdTime }}</span>
            </div>
          </template>
        </template>
      </div>
    </div>
  </div>
</template>

<style scoped>
.team-page { max-width: 1200px; }
.page-title { margin: 0 0 20px; font-size: 22px; color: #1d1e2c; }

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  margin-bottom: 20px;
}

.stat-card {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  border-left: 4px solid #409eff;
}

.stat-value { font-size: 24px; font-weight: 700; color: #333; margin-bottom: 4px; }
.stat-label { font-size: 14px; color: #999; }

.filter-bar { display: flex; gap: 12px; margin-bottom: 16px; }

.search-input {
  height: 36px;
  padding: 0 12px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
  width: 220px;
}

.search-input:focus { border-color: #409eff; }

.tree-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.tree-header {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: #f5f7fa;
  font-size: 13px;
  color: #666;
  font-weight: 500;
  border-bottom: 1px solid #ebeef5;
}

.tree-row {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #ebeef5;
  font-size: 13px;
  color: #333;
  cursor: pointer;
  transition: background 0.15s;
}

.tree-row:hover { background: #f5f7fa; }
.tree-row.level-1 { padding-left: 48px; }

.expand-icon { width: 20px; font-size: 10px; color: #999; flex-shrink: 0; }

.col-name { flex: 1; min-width: 80px; }
.col-phone { width: 120px; }
.col-sales { width: 100px; }
.col-commission { width: 80px; }
.col-status { width: 60px; text-align: center; }
.col-date { width: 100px; }
</style>
