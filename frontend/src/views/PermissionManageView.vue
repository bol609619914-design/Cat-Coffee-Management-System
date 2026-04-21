<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteSystemPermission, fetchSystemPermissions, saveSystemPermission } from '../api/modules'
import { hasPermission } from '../utils/auth'

const list = ref([])
const formRef = ref()
const drawerVisible = ref(false)
const page = reactive({ current: 1, size: 8, total: 0 })
const query = reactive({ keyword: '', status: undefined })
const canWrite = hasPermission('system:permission:write')
const canDelete = hasPermission('system:permission:delete')
const form = reactive({
  id: null,
  permissionCode: '',
  permissionName: '',
  moduleName: '',
  status: 1
})
const rules = {
  moduleName: [{ required: true, message: '请输入模块名称', trigger: 'blur' }],
  permissionCode: [{ required: true, message: '请输入权限编码', trigger: 'blur' }],
  permissionName: [{ required: true, message: '请输入权限名称', trigger: 'blur' }]
}

const resetForm = () => {
  Object.assign(form, { id: null, permissionCode: '', permissionName: '', moduleName: '', status: 1 })
}

const openCreate = () => {
  resetForm()
  drawerVisible.value = true
}

const loadData = async () => {
  const data = await fetchSystemPermissions({ ...query, current: page.current, size: page.size })
  list.value = data.records
  page.total = data.total
}

const editRow = (row) => {
  Object.assign(form, row)
  drawerVisible.value = true
}

const submit = async () => {
  await formRef.value.validate()
  await saveSystemPermission(form)
  ElMessage.success('权限保存成功')
  resetForm()
  drawerVisible.value = false
  loadData()
}

const removeRow = async (id) => {
  await ElMessageBox.confirm('删除权限后，绑定该权限的角色授权会同步失效，确认删除吗？', '删除权限', { type: 'warning' })
  await deleteSystemPermission(id)
  ElMessage.success('权限删除成功')
  loadData()
}

onMounted(loadData)
</script>

<template>
  <section>
    <div class="page-header">
      <div>
        <h2>权限管理</h2>
        <p>维护接口权限编码、模块归属和启停状态。</p>
      </div>
      <el-button v-if="canWrite" type="primary" @click="openCreate">新增权限</el-button>
    </div>
    <div class="panel">
      <div class="toolbar">
        <el-input v-model="query.keyword" placeholder="搜索权限编码/名称" clearable />
        <el-select v-model="query.status" placeholder="状态" clearable style="width: 140px">
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
        <el-button type="primary" @click="page.current = 1; loadData()">查询</el-button>
      </div>
      <el-table :data="list" border>
        <el-table-column prop="moduleName" label="模块" />
        <el-table-column prop="permissionCode" label="编码" min-width="180" />
        <el-table-column prop="permissionName" label="名称" />
        <el-table-column label="状态">
          <template #default="{ row }">{{ row.status === 1 ? '启用' : '禁用' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button v-if="canWrite" link type="primary" @click="editRow(row)">编辑</el-button>
            <el-button v-if="canDelete" link type="danger" @click="removeRow(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrap">
        <el-pagination v-model:current-page="page.current" v-model:page-size="page.size" layout="total, prev, pager, next" :total="page.total" @current-change="loadData" />
      </div>
    </div>

    <el-drawer v-if="canWrite" v-model="drawerVisible" :title="form.id ? '编辑权限' : '新增权限'" size="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="88px">
        <el-form-item label="模块" prop="moduleName"><el-input v-model="form.moduleName" /></el-form-item>
        <el-form-item label="编码" prop="permissionCode"><el-input v-model="form.permissionCode" /></el-form-item>
        <el-form-item label="名称" prop="permissionName"><el-input v-model="form.permissionName" /></el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submit">保存</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-drawer>
  </section>
</template>
