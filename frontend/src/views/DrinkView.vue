<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { deleteDrink, fetchDrinks, saveDrink } from '../api/modules'
import { ElMessage } from 'element-plus'
import { hasPermission, hasRole } from '../utils/auth'

const list = ref([])
const drawerVisible = ref(false)
const query = reactive({ keyword: '', status: undefined })
const page = reactive({ current: 1, size: 5, total: 0 })
const canWrite = hasPermission('drink:write')
const canDelete = hasPermission('drink:delete')
const isCustomerUser = hasRole('user')
const pageTitle = computed(() => isCustomerUser ? '饮品菜单' : '饮品管理')
const pageDescription = computed(() => isCustomerUser ? '浏览当前可点单的饮品和库存状态。' : '维护饮品菜单、库存、推荐状态和销售基础数据。')
const form = reactive({
  id: null,
  name: '',
  category: '咖啡',
  price: 0,
  stock: 0,
  isRecommended: 0,
  description: '',
  status: 1
})

const resetForm = () => {
  Object.assign(form, {
    id: null,
    name: '',
    category: '咖啡',
    price: 0,
    stock: 0,
    isRecommended: 0,
    description: '',
    status: 1
  })
}

const openCreate = () => {
  resetForm()
  drawerVisible.value = true
}

const loadData = async () => {
  const data = await fetchDrinks({ ...query, current: page.current, size: page.size })
  list.value = data.records
  page.total = data.total
}

const submit = async () => {
  await saveDrink(form)
  ElMessage.success('保存成功')
  resetForm()
  drawerVisible.value = false
  loadData()
}

const editRow = (row) => {
  Object.assign(form, row)
  drawerVisible.value = true
}

const removeRow = async (id) => {
  await deleteDrink(id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>

<template>
  <section>
    <div class="page-header">
      <div>
        <h2>{{ pageTitle }}</h2>
        <p>{{ pageDescription }}</p>
      </div>
      <el-button v-if="canWrite" type="primary" @click="openCreate">新增饮品</el-button>
    </div>

    <div class="panel">
      <div class="toolbar">
        <el-input v-model="query.keyword" placeholder="搜索饮品名" clearable />
        <el-select v-model="query.status" placeholder="状态" clearable style="width: 140px">
          <el-option label="上架" :value="1" />
          <el-option label="下架" :value="0" />
        </el-select>
        <el-button type="primary" @click="page.current = 1; loadData()">查询</el-button>
      </div>
      <el-table :data="list" border>
        <el-table-column prop="name" label="名称" />
        <el-table-column prop="category" label="分类" />
        <el-table-column prop="price" label="售价" />
        <el-table-column prop="stock" label="库存" />
        <el-table-column prop="sales" label="销量" />
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <div class="table-actions">
              <el-button v-if="canWrite" link type="primary" @click="editRow(row)">编辑</el-button>
              <el-button v-if="canDelete" link type="danger" @click="removeRow(row.id)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="page.current"
          v-model:page-size="page.size"
          layout="total, prev, pager, next"
          :total="page.total"
          @current-change="loadData"
        />
      </div>
    </div>

    <el-drawer v-if="canWrite" v-model="drawerVisible" :title="form.id ? '编辑饮品' : '新增饮品'" size="520px">
      <el-form label-width="88px">
        <el-form-item label="名称"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="分类"><el-input v-model="form.category" /></el-form-item>
        <el-form-item label="价格"><el-input-number v-model="form.price" :min="0" :precision="2" /></el-form-item>
        <el-form-item label="库存"><el-input-number v-model="form.stock" :min="0" /></el-form-item>
        <el-form-item label="推荐">
          <el-switch v-model="form.isRecommended" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="描述"><el-input v-model="form.description" type="textarea" :rows="3" /></el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submit">保存</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-drawer>
  </section>
</template>
