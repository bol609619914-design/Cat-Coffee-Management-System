import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import DashboardView from '../views/DashboardView.vue'
import CatView from '../views/CatView.vue'
import DrinkView from '../views/DrinkView.vue'
import TableView from '../views/TableView.vue'
import ReservationView from '../views/ReservationView.vue'
import OrderView from '../views/OrderView.vue'
import PointCenterView from '../views/PointCenterView.vue'
import CouponCenterView from '../views/CouponCenterView.vue'
import ReviewCenterView from '../views/ReviewCenterView.vue'
import ActivityCenterView from '../views/ActivityCenterView.vue'
import LoginView from '../views/LoginView.vue'
import UserManageView from '../views/UserManageView.vue'
import RoleManageView from '../views/RoleManageView.vue'
import PermissionManageView from '../views/PermissionManageView.vue'
import { fetchCurrentUser } from '../api/modules'
import { clearAuth, getHomePath, getToken, hasPermission, setUserInfo } from '../utils/auth'

const routes = [
  { path: '/', redirect: '/dashboard' },
  { path: '/login', component: LoginView, meta: { public: true } },
  { path: '/dashboard', component: DashboardView, meta: { permission: 'dashboard:view', label: '经营看板' } },
  { path: '/cats', component: CatView, meta: { permission: 'cat:read', label: '猫咪管理' } },
  { path: '/drinks', component: DrinkView, meta: { permission: 'drink:read', label: '饮品管理' } },
  { path: '/tables', component: TableView, meta: { permission: 'table:read', label: '桌台管理' } },
  { path: '/reservations', component: ReservationView, meta: { permission: 'reservation:read', label: '预约管理' } },
  { path: '/orders', component: OrderView, meta: { permission: 'order:read', label: '订单管理' } },
  { path: '/member/points', component: PointCenterView, meta: { permission: 'points:read', label: '会员积分' } },
  { path: '/member/coupons', component: CouponCenterView, meta: { permission: 'coupon:read', label: '优惠券中心' } },
  { path: '/member/reviews', component: ReviewCenterView, meta: { permission: 'review:read', label: '评价管理' } },
  { path: '/marketing/activities', component: ActivityCenterView, meta: { permission: 'activity:read', label: '活动营销' } },
  { path: '/system/users', component: UserManageView, meta: { permission: 'system:user:read', label: '用户管理' } },
  { path: '/system/roles', component: RoleManageView, meta: { permission: 'system:role:read', label: '角色管理' } },
  { path: '/system/permissions', component: PermissionManageView, meta: { permission: 'system:permission:read', label: '权限管理' } }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

const ensureUserProfile = async () => {
  const user = await fetchCurrentUser()
  setUserInfo(user)
  return user
}

router.beforeEach(async (to, from, next) => {
  if (to.meta.public) {
    if (to.path === '/login' && getToken()) {
      next(getHomePath())
      return
    }
    next()
    return
  }

  if (!getToken()) {
    next('/login')
    return
  }

  try {
    await ensureUserProfile()
  } catch (error) {
    clearAuth()
    ElMessage.error('登录状态已失效，请重新登录')
    next('/login')
    return
  }

  if (to.meta.permission && !hasPermission(to.meta.permission)) {
    ElMessage.error('当前账号没有访问该页面的权限')
    next(from.path && from.path !== '/login' ? from.fullPath : getHomePath())
    return
  }

  next()
})

export default router
