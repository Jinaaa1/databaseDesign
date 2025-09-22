<template>
     <el-container class="my_container">
        <el-header>
        <UserHeader/>
        </el-header>

      <el-container class="my_container">
      <el-aside width="12.5rem" class="my_aside">
          <UserSider/>
       </el-aside>

       <el-main class="my_main">
          <router-view/>
       </el-main>
      </el-container>
     </el-container>
</template>

<script setup>
import UserHeader from '@/components/user/UserHeader.vue';
import UserSider from '@/components/user/UserSider.vue'
import { userInfo } from '@/Info/userInfo';
import {onUnmounted} from "vue";

onUnmounted(() => {
  sessionStorage.clear()
  userInfo.value.user = null
  userInfo.value.membershipLevel = null
  console.log('注销成功')
})
</script>

<style  lang="scss" scoped>
.my_container{
     height: 100%;
}
.my_aside {
  height: auto;
  min-height: 336px;
  background-color: rgba(205, 214, 222, 0.6);
  border-radius: 5px;
}
.my_main {
  border-radius: 10px;
  background-color: rgba(205, 214, 222, 0.6);
  margin: 10px;
  padding: 25px;
  height: auto;
}
</style>