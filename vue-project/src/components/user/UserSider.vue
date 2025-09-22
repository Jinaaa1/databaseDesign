<template>
        <el-menu
        :default-active="userLocation"
        class="el-menu-vertical-demo"
        router >
        <el-menu-item index="index">
          <el-icon><House /></el-icon>
          <span>首页</span>
        </el-menu-item>

        <el-sub-menu index="2">
          <template #title>
            <el-icon><User /></el-icon>
            <span>账号管理</span>
          </template>
            <el-menu-item index="information">个人信息</el-menu-item>
            <el-menu-item index="privilege">会员权益</el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="3">
          <template #title>
            <el-icon><Present /></el-icon>
            <span>您的满减券</span>
          </template>
            <el-menu-item index="coupon">查看满减券</el-menu-item>
            <el-menu-item index="redeem">兑换满减券</el-menu-item>
        </el-sub-menu>

        <el-menu-item index="order">
          <el-icon><Box /></el-icon>
          <span>订单查询</span>
        </el-menu-item>

        <el-menu-item index="cashFlow">
          <el-icon><Coin /></el-icon>
          <span>流水查询</span>
        </el-menu-item>

        <el-menu-item index="commodity">
          <el-icon><ShoppingBag /></el-icon>
          <span>逛逛商品</span>
        </el-menu-item>

    </el-menu>
</template>

<script setup>
import {useRoute} from 'vue-router'
// 引入Vue的computed函数
import {computed} from "vue";
import { userInfo } from '@/Info/userInfo';

const route = useRoute()


/**
 * 计算当前激活的菜单项
 * 从路由路径中提取关键部分作为激活索引
 * 例如：/user/info → info
 */
const userLocation = computed(() => {
  let path = route.path  // 获取完整路径，如"/user/info"
  path = path.substring(6)  // 去掉前6个字符"/user/"
  console.log(path.substring(path.indexOf('/') + 1))
  console.log('当前路径:', path)
  console.log('User:',userInfo.value.user); // 访问user属性
  
  return path.substring(path.indexOf('/') + 1)  // 获取第一个斜杠后的内容
})


</script>

<style>
.el-menu-vertical-demo {
  opacity: 0.8;  /* 设置透明度 */
  height: 100%;  /* 建议添加，确保填满容器 */
  transition: opacity 0.3s;  /* 建议添加透明过渡效果 */
}
</style>