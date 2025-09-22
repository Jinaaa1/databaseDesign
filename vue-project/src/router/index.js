import { createRouter, createWebHashHistory } from 'vue-router'
import InitHome from '../views/InitHome.vue'
import UserPage from '@/views/UserPage.vue'
import UserIndex from "../components/user/UserIndex.vue"


const routes = [
  {
    path: '/',
    name: 'home',
    component: InitHome
  },
  {
    path: '/user/register',
    name: 'UserRegister',
    component: () => import('../views/UserRegister.vue')
  },
  {
    path: '/user/login',
    name: 'UserLogin',
    component: () => import('../views/UserLogin.vue')
  },
  {
    path: '/user/:account',
    name: 'UserPage',
    component: UserPage,
    children:[
      {
        path: 'index',
        name: 'UserIndex',
        component: UserIndex,
        alias: ['']
      },
      {
        path: 'information',
        name: 'UserInformation',
        component: () => import('../components/user/UserInfomation.vue')
      },
      {
        path: 'privilege',
        name: 'UserPrivilege',
        component: () => import('../components/user/UserPrivilege.vue')
      },
      {
        path: 'coupon',
        name: 'UserCoupon',
        component: () => import('../components/user/UserCoupon.vue')
      },
      {
        path: 'redeem',
        name: 'UserRedeem',
        component: () => import('../components/user/UserRedeem.vue')
      },
      {
        path: 'commodity',
        name: 'UserCommodity',
        component: () => import('../components/user/UserCommodity.vue')
      },
      {
        path: 'order',
        name: 'UserOrder',
        component: () => import('../components/user/UserOrder.vue')
      },
      {
        path: 'cashFlow',
        name: 'UserCashFlow',
        component: () => import('../components/user/UserCashFlow.vue')
      },
  ]
  },
  {
        path: '/administrator/login',
        name: 'AdminLogin',
        component: () => import('../views/AdminLogin.vue')
  },
  {
            path: '/administrator',
            name: 'AdminPage',
            component: () => import('../views/AdminPage.vue'),
            children: [
                {
                    path: 'index',
                    name: 'AdminIndex',
                    component: () => import('../components/administrator/AdminIndex.vue')
                },
                {
                    path: 'user_management',
                    name: 'AdminUserManagement',
                    component: () => import('../components/administrator/AdminUserManagement.vue')
                },
                {
                    path: 'order',
                    name: 'AdminOrder',
                    component: () => import('../components/administrator/AdminOrder.vue')
                },
                {
                    path: 'level',
                    name: 'AdminLevelManagement',
                    component: () => import('../components/administrator/AdminLevelManagement.vue')
                },
                {
                    path: 'coupon',
                    name: 'AdminCouponManagement',
                    component: () => import('../components/administrator/AdminCoupon.vue')
                },
                {
                    path: 'commodity',
                    name: 'AdminCommodityManagement',
                    component: () => import('../components/administrator/AdminCommodityManagement.vue')
                }
            ]
        },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

export default router  // 只导出路由，不在这里挂载应用