import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getMe } from '@/api/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/views/Login.vue'),
      meta: { public: true }
    },
    {
      path: '/',
      component: () => import('@/layout/Layout.vue'),
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('@/views/Dashboard.vue'),
          meta: { title: '统计仪表盘' }
        },
        {
          path: 'users',
          name: 'Users',
          component: () => import('@/views/admin/UserManage.vue'),
          meta: { title: '用户管理', roles: ['ADMIN'] }
        },
        {
          path: 'residents',
          name: 'Residents',
          component: () => import('@/views/resident/ResidentList.vue'),
          meta: { title: '居民档案', roles: ['ADMIN', 'DOCTOR'] }
        },
        {
          path: 'residents/:id',
          name: 'ResidentDetail',
          component: () => import('@/views/resident/ResidentDetail.vue'),
          meta: { title: '档案详情', roles: ['ADMIN', 'DOCTOR'] }
        },
        {
          path: 'chronic',
          name: 'Chronic',
          component: () => import('@/views/chronic/ChronicManage.vue'),
          meta: { title: '慢病管理', roles: ['ADMIN', 'DOCTOR'] }
        },
        {
          path: 'followup',
          name: 'Followup',
          component: () => import('@/views/followup/FollowupManage.vue'),
          meta: { title: '随访记录', roles: ['ADMIN', 'DOCTOR'] }
        },
        {
          path: 'my-profile',
          name: 'MyProfile',
          component: () => import('@/views/resident/MyProfile.vue'),
          meta: { title: '我的档案', roles: ['RESIDENT'] }
        },
        {
          path: 'alerts',
          name: 'Alerts',
          component: () => import('@/views/alert/AlertList.vue'),
          meta: { title: '健康预警', roles: ['ADMIN', 'DOCTOR'] }
        },
        {
          path: 'oplog',
          name: 'OpLog',
          component: () => import('@/views/admin/OpLog.vue'),
          meta: { title: '操作日志', roles: ['ADMIN'] }
        }
      ]
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/'
    }
  ]
})

router.beforeEach(async (to) => {
  if (to.meta.public) return true

  const userStore = useUserStore()
  if (!userStore.token) return '/login'

  if (!userStore.userInfo) {
    try {
      const info = await getMe() as any
      userStore.setUserInfo(info)
    } catch {
      userStore.logout()
      return '/login'
    }
  }

  const roles = to.meta.roles as string[] | undefined
  if (roles && roles.length > 0) {
    const role = userStore.userInfo?.role
    if (!role || !roles.includes(role)) {
      return '/dashboard'
    }
  }

  return true
})

export default router
