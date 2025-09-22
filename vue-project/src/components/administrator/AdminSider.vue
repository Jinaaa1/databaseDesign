<template>
  <el-menu :default-active="adminLocation" class="my-el-menu" router>
    <el-menu-item index="index">
      <el-icon>
        <Histogram/>
      </el-icon>
      <span>首页</span>
    </el-menu-item>
    <el-menu-item index="user_management">
      <el-icon>
        <User/>
      </el-icon>
      <span>用户管理</span>
    </el-menu-item>
    <el-menu-item index="order">
      <el-icon>
        <List/>
      </el-icon>
      <span>订单查询</span>
    </el-menu-item>
    <el-menu-item index="level">
      <el-icon>
        <Trophy/>
      </el-icon>
      <span>会员等级管理</span>
    </el-menu-item>
    <el-menu-item index="coupon">
      <el-icon>
        <Edit/>
      </el-icon>
      <span>满减券管理</span>
    </el-menu-item>
    <el-menu-item index="commodity">
      <el-icon>
        <Shop/>
      </el-icon>
      <span>商品管理</span>
    </el-menu-item>
  </el-menu>
</template>

<style scoped>
.my-el-menu {
  opacity: 0.8;
}
</style>

<script setup>
import {useRoute} from "vue-router";
import {computed} from "vue";

const route = useRoute()
const adminLocation = computed(() => {
  let path = route.path
  return path.substring(15)
})
</script>