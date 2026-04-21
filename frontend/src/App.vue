<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { clearAuth, getToken, getUserInfo, hasRole, setUserInfo } from './utils/auth'
import { changePassword, fetchCurrentUser, logout as logoutApi } from './api/modules'

const route = useRoute()
const router = useRouter()
const currentUser = ref(getUserInfo())

const menus = [
  { path: '/dashboard', label: '经营看板', permission: 'dashboard:view' },
  { path: '/cats', label: '猫咪管理', userLabel: '猫咪浏览', permission: 'cat:read' },
  { path: '/drinks', label: '饮品管理', userLabel: '饮品菜单', permission: 'drink:read' },
  { path: '/tables', label: '桌台管理', permission: 'table:read' },
  { path: '/reservations', label: '预约管理', userLabel: '我的预约', permission: 'reservation:read' },
  { path: '/orders', label: '订单管理', userLabel: '我的订单', permission: 'order:read' },
  { path: '/system/users', label: '用户管理', permission: 'system:user:read' },
  { path: '/system/roles', label: '角色管理', permission: 'system:role:read' },
  { path: '/system/permissions', label: '权限管理', permission: 'system:permission:read' }
]

const showShell = computed(() => route.path !== '/login')
const isCustomerUser = computed(() => hasRole('user'))
const visibleMenus = computed(() => {
  const permissions = currentUser.value?.permissions || []
  return menus
    .filter((item) => permissions.includes(item.permission))
    .map((item) => ({
      ...item,
      label: isCustomerUser.value && item.userLabel ? item.userLabel : item.label
    }))
})
const userInfo = computed(() => currentUser.value)
const checkingAuth = ref(true)

const syncCurrentUser = async () => {
  if (!getToken()) {
    currentUser.value = null
    checkingAuth.value = false
    return
  }

  try {
    const user = await fetchCurrentUser()
    setUserInfo(user)
    currentUser.value = user
  } catch (error) {
    clearAuth()
    currentUser.value = null
    if (route.path !== '/login') {
      router.push('/login')
    }
  } finally {
    checkingAuth.value = false
  }
}

const openChangePassword = async () => {
  const { value: values } = await ElMessageBox.prompt('', '修改密码', {
    distinguishCancelAndClose: true,
    confirmButtonText: '下一步',
    cancelButtonText: '取消',
    inputType: 'textarea',
    inputPlaceholder: '请输入：当前密码,新密码',
    inputValidator: (value) => {
      const parts = value.split(',').map((item) => item.trim())
      if (parts.length !== 2 || !parts[0] || !parts[1]) {
        return '请按“当前密码,新密码”格式输入'
      }
      if (parts[1].length < 6) {
        return '新密码至少 6 位'
      }
      return true
    }
  }).catch(() => ({ value: null }))

  if (!values) {
    return
  }

  const [oldPassword, newPassword] = values.split(',').map((item) => item.trim())
  await changePassword({ oldPassword, newPassword })
  ElMessage.success('密码已修改，请重新登录')
  clearAuth()
  currentUser.value = null
  router.push('/login')
}

const logout = async () => {
  await ElMessageBox.confirm('确认退出当前账号吗？', '退出登录', { type: 'warning' })
  try {
    await logoutApi()
  } finally {
    clearAuth()
    currentUser.value = null
    router.push('/login')
  }
}

onMounted(syncCurrentUser)

watch(
  () => route.fullPath,
  () => {
    currentUser.value = getUserInfo()
  }
)
</script>

<template>
  <div v-if="checkingAuth" class="app-loading">正在校验登录状态...</div>

  <router-view v-if="!showShell" />

  <div v-else-if="!checkingAuth" class="layout">
    <aside class="sidebar">
      <div class="brand">
        <img class="brand-logo" src="/assets/cat-cafe-logo.png" alt="Cat Cafe Logo" />
        <div>
          <h1>猫咖平台</h1>
          <p>Cat Coffee Console</p>
        </div>
      </div>

      <div class="user-panel">
        <strong>{{ userInfo?.nickname || '未登录用户' }}</strong>
        <small>{{ userInfo?.roles?.join(' / ') || 'guest' }}</small>
      </div>

      <el-menu
        :default-active="$route.path"
        class="menu"
        router
        background-color="transparent"
        text-color="#59493f"
        active-text-color="#fffaf3"
      >
        <el-menu-item v-for="item in visibleMenus" :key="item.path" :index="item.path">
          {{ item.label }}
        </el-menu-item>
      </el-menu>

      <div class="sidebar-footer">
        <div class="sidebar-actions">
          <el-button class="user-action-button" @click="openChangePassword">修改密码</el-button>
        </div>

        <el-button class="logout-button" @click="logout">退出登录</el-button>
      </div>
    </aside>

    <main class="content">
      <router-view />
    </main>
  </div>
</template>
