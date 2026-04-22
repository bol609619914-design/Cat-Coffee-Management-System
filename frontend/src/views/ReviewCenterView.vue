<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { deleteReview, fetchReviewOptions, fetchReviews, saveReview } from '../api/modules'
import { ElMessage, ElMessageBox } from 'element-plus'
import { hasPermission, hasRole } from '../utils/auth'

const list = ref([])
const options = ref([])
const drawerVisible = ref(false)
const page = reactive({ current: 1, size: 8, total: 0 })
const query = reactive({ rating: '' })
const form = reactive({
  id: null,
  orderId: null,
  drinkId: null,
  rating: 5,
  content: ''
})
const canWrite = hasPermission('review:write')
const isCustomerUser = hasRole('user')

const pageTitle = computed(() => isCustomerUser ? '我的评价' : '评价管理')
const pageDescription = computed(() => isCustomerUser
  ? '对已完成订单中的饮品进行真实评价。'
  : '汇总用户评价内容，为饮品优化和活动复盘提供依据。')

const resetForm = () => {
  Object.assign(form, {
    id: null,
    orderId: null,
    drinkId: null,
    rating: 5,
    content: ''
  })
}

const loadData = async () => {
  const [reviewData, reviewOptions] = await Promise.all([
    fetchReviews({ current: page.current, size: page.size, rating: query.rating || undefined }),
    canWrite ? fetchReviewOptions() : Promise.resolve([])
  ])
  list.value = reviewData.records
  page.total = reviewData.total
  options.value = reviewOptions
}

const openCreate = () => {
  resetForm()
  drawerVisible.value = true
}

const editRow = (row) => {
  Object.assign(form, row)
  drawerVisible.value = true
}

const submit = async () => {
  await saveReview(form)
  ElMessage.success('评价保存成功')
  drawerVisible.value = false
  resetForm()
  loadData()
}

const removeRow = async (id) => {
  await ElMessageBox.confirm('确认删除这条评价吗？', '删除评价', { type: 'warning' })
  await deleteReview(id)
  ElMessage.success('评价删除成功')
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
      <el-button v-if="canWrite" type="primary" @click="openCreate">新增评价</el-button>
    </div>

    <div class="panel">
      <div class="toolbar">
        <el-select v-model="query.rating" placeholder="星级评分" clearable style="width: 140px">
          <el-option v-for="score in 5" :key="score" :label="`${score} 星`" :value="score" />
        </el-select>
        <el-button type="primary" @click="page.current = 1; loadData()">查询</el-button>
      </div>

      <el-table :data="list" border>
        <el-table-column prop="nickname" label="用户" width="120" />
        <el-table-column prop="orderNo" label="订单号" min-width="160" />
        <el-table-column prop="drinkName" label="饮品" width="140" />
        <el-table-column prop="rating" label="评分" width="90" />
        <el-table-column prop="content" label="评价内容" min-width="260" />
        <el-table-column prop="createTime" label="评价时间" min-width="170" />
        <el-table-column v-if="canWrite" label="操作" width="140">
          <template #default="{ row }">
            <div class="table-actions">
              <el-button link type="primary" @click="editRow(row)">编辑</el-button>
              <el-button link type="danger" @click="removeRow(row.id)">删除</el-button>
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

    <el-drawer v-if="canWrite" v-model="drawerVisible" :title="form.id ? '编辑评价' : '新增评价'" size="520px">
      <el-form label-position="top">
        <el-form-item label="可评价订单项">
          <el-select
            v-model="form.drinkId"
            placeholder="请选择订单饮品"
            @change="(value) => {
              const current = options.find((item) => item.drinkId === value)
              form.orderId = current?.orderId || null
            }"
          >
            <el-option
              v-for="item in options"
              :key="`${item.orderId}-${item.drinkId}`"
              :label="`${item.orderNo} / ${item.drinkName}`"
              :value="item.drinkId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="评分">
          <el-rate v-model="form.rating" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input v-model="form.content" type="textarea" :rows="4" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submit">保存</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-drawer>
  </section>
</template>
