<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteSystemRole, fetchSystemPermissions, fetchSystemRoles, saveSystemRole } from '../api/modules'
import { hasPermission } from '../utils/auth'

const list = ref([])
const permissions = ref([])
const formRef = ref()
const drawerVisible = ref(false)
const page = reactive({ current: 1, size: 6, total: 0 })
const query = reactive({ keyword: '', status: undefined })
const canWrite = hasPermission('system:role:write')
const canDelete = hasPermission('system:role:delete')
const form = reactive({
  id: null,
  roleCode: '',
  roleName: '',
  status: 1,
  permissionIds: []
})
const rules = {
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  permissionIds: [{ type: 'array', required: true, message: '至少选择一个权限', trigger: 'change' }]
}
const permissionGroups = computed(() => {
  const groups = {}
  permissions.value.forEach((item) => {
    if (!groups[item.moduleName]) {
      groups[item.moduleName] = []
    }
    groups[item.moduleName].push(item)
  })
  return Object.entries(groups)
})

const resetForm = () => {
  Object.assign(form, { id: null, roleCode: '', roleName: '', status: 1, permissionIds: [] })
}

const openCreate = () => {
  resetForm()
  drawerVisible.value = true
}

const loadData = async () => {
  const [roleData, permissionData] = await Promise.all([
    fetchSystemRoles({ ...query, current: page.current, size: page.size }),
    fetchSystemPermissions({ current: 1, size: 200 })
  ])
  list.value = roleData.records
  page.total = roleData.total
  permissions.value = permissionData.records
}

const editRow = (row) => {
  Object.assign(form, row)
  drawerVisible.value = true
}

const submit = async () => {
  await formRef.value.validate()
  await saveSystemRole(form)
  ElMessage.success('角色保存成功')
  resetForm()
  drawerVisible.value = false
  loadData()
}

const removeRow = async (id) => {
  await ElMessageBox.confirm('删除角色后，绑定该角色的账号将失去对应权限，确认删除吗？', '删除角色', { type: 'warning' })
  await deleteSystemRole(id)
  ElMessage.success('角色删除成功')
  loadData()
}

onMounted(loadData)
</script>

<template>
  <section>
    <div class="page-header">
      <div>
        <h2>角色管理</h2>
        <p>维护角色与权限集合，用于 RBAC 授权。</p>
      </div>
      <el-button v-if="canWrite" type="primary" @click="openCreate">新增角色</el-button>
    </div>
    <div class="panel">
      <div class="toolbar">
        <el-input v-model="query.keyword" placeholder="搜索角色编码/名称" clearable />
        <el-select v-model="query.status" placeholder="状态" clearable style="width: 140px">
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
        <el-button type="primary" @click="page.current = 1; loadData()">查询</el-button>
      </div>
      <el-table :data="list" border>
        <el-table-column prop="roleCode" label="编码" />
        <el-table-column prop="roleName" label="名称" />
        <el-table-column label="权限">
          <template #default="{ row }">{{ row.permissionNames?.join('、') || '-' }}</template>
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

    <el-drawer v-if="canWrite" v-model="drawerVisible" :title="form.id ? '编辑角色' : '新增角色'" size="560px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="88px">
        <el-form-item label="编码" prop="roleCode"><el-input v-model="form.roleCode" /></el-form-item>
        <el-form-item label="名称" prop="roleName"><el-input v-model="form.roleName" /></el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="权限" prop="permissionIds">
          <div class="permission-groups" style="width: 100%;">
            <div v-for="[moduleName, items] in permissionGroups" :key="moduleName" class="permission-group">
              <strong>{{ moduleName }}</strong>
              <el-checkbox-group v-model="form.permissionIds">
                <el-checkbox v-for="item in items" :key="item.id" :label="item.id">{{ item.permissionName }}</el-checkbox>
              </el-checkbox-group>
            </div>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submit">保存</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-drawer>
  </section>
</template>
