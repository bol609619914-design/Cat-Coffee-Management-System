<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteSystemUser, fetchSystemRoles, fetchSystemUsers, resetSystemUserPassword, saveSystemUser } from '../api/modules'
import { hasPermission } from '../utils/auth'

const list = ref([])
const roles = ref([])
const formRef = ref()
const drawerVisible = ref(false)
const page = reactive({ current: 1, size: 6, total: 0 })
const query = reactive({ keyword: '', status: undefined })
const canWrite = hasPermission('system:user:write')
const canDelete = hasPermission('system:user:delete')
const form = reactive({
  id: null,
  username: '',
  password: '',
  nickname: '',
  status: 1,
  roleIds: []
})
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  password: [{
    validator: (_, value, callback) => {
      if (!form.id && !value) {
        callback(new Error('新增用户必须填写密码'))
        return
      }
      if (value && value.length < 6) {
        callback(new Error('密码至少 6 位'))
        return
      }
      callback()
    },
    trigger: 'blur'
  }],
  roleIds: [{ type: 'array', required: true, message: '至少选择一个角色', trigger: 'change' }]
}

const resetForm = () => {
  Object.assign(form, { id: null, username: '', password: '', nickname: '', status: 1, roleIds: [] })
}

const openCreate = () => {
  resetForm()
  drawerVisible.value = true
}

const loadData = async () => {
  const [userData, roleData] = await Promise.all([
    fetchSystemUsers({ ...query, current: page.current, size: page.size }),
    fetchSystemRoles({ current: 1, size: 100 })
  ])
  list.value = userData.records
  page.total = userData.total
  roles.value = roleData.records
}

const editRow = (row) => {
  Object.assign(form, { ...row, password: '' })
  drawerVisible.value = true
}

const submit = async () => {
  await formRef.value.validate()
  await saveSystemUser(form)
  ElMessage.success('用户保存成功')
  resetForm()
  drawerVisible.value = false
  loadData()
}

const removeRow = async (id) => {
  await ElMessageBox.confirm('删除后该账号将无法继续登录，确认删除吗？', '删除用户', { type: 'warning' })
  await deleteSystemUser(id)
  ElMessage.success('用户删除成功')
  loadData()
}

const resetPassword = async (row) => {
  const { value } = await ElMessageBox.prompt(`给用户 ${row.username} 设置新密码`, '重置密码', {
    inputPlaceholder: '请输入新密码',
    inputValidator: (input) => {
      if (!input || input.trim().length < 6) {
        return '密码至少 6 位'
      }
      return true
    }
  })
  await resetSystemUserPassword(row.id, { newPassword: value.trim() })
  ElMessage.success('密码重置成功')
}

onMounted(loadData)
</script>

<template>
  <section>
    <div class="page-header">
      <div>
        <h2>用户管理</h2>
        <p>维护系统账号、登录状态和角色归属。</p>
      </div>
      <el-button v-if="canWrite" type="primary" @click="openCreate">新增用户</el-button>
    </div>
    <div class="panel">
      <div class="toolbar">
        <el-input v-model="query.keyword" placeholder="搜索用户名/昵称" clearable />
        <el-select v-model="query.status" placeholder="状态" clearable style="width: 140px">
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
        <el-button type="primary" @click="page.current = 1; loadData()">查询</el-button>
      </div>
      <el-table :data="list" border>
        <el-table-column prop="username" label="用户名" />
        <el-table-column prop="nickname" label="昵称" />
        <el-table-column label="角色">
          <template #default="{ row }">{{ row.roleNames?.join('、') || '-' }}</template>
        </el-table-column>
        <el-table-column label="状态">
          <template #default="{ row }">{{ row.status === 1 ? '启用' : '禁用' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <div class="table-actions">
              <el-button v-if="canWrite" link type="primary" @click="editRow(row)">编辑</el-button>
              <el-button v-if="canWrite" link @click="resetPassword(row)">重置密码</el-button>
              <el-button v-if="canDelete" link type="danger" @click="removeRow(row.id)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrap">
        <el-pagination v-model:current-page="page.current" v-model:page-size="page.size" layout="total, prev, pager, next" :total="page.total" @current-change="loadData" />
      </div>
    </div>

    <el-drawer v-if="canWrite" v-model="drawerVisible" :title="form.id ? '编辑用户' : '新增用户'" size="520px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="88px">
        <el-form-item label="用户名" prop="username"><el-input v-model="form.username" /></el-form-item>
        <el-form-item label="密码" prop="password"><el-input v-model="form.password" type="password" show-password placeholder="编辑时可留空" /></el-form-item>
        <el-form-item label="昵称" prop="nickname"><el-input v-model="form.nickname" /></el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="角色" prop="roleIds">
          <el-select v-model="form.roleIds" multiple placeholder="选择角色">
            <el-option v-for="role in roles" :key="role.id" :label="role.roleName" :value="role.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submit">保存</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-drawer>
  </section>
</template>
