<script setup>
import Header from '../components/administrator/AdminHeader.vue'
import Sider from '../components/administrator/AdminSider.vue'

</script>

<template>
  <el-container class="my-el-container">
    <el-header>
      <Header/>
    </el-header>

    <el-container class="my-el-container">
      <el-aside width="12.5rem" class="my-el-aside">
        <Sider/>
      </el-aside>
      <el-main class="my-el-main">
        <router-view/>
      </el-main>
    </el-container>
  </el-container>
</template>


<style lang="scss" scoped>
.my-el-aside {
  height: auto;
  min-height: 336px;
  background-color: rgba(205, 214, 222, 0.6);
  border-radius: 5px;
}

.my-el-main {
  border-radius: 10px;
  background-color: rgba(205, 214, 222, 0.6);
  margin: 10px;
  padding: 25px;
  height: auto;
}

.my-el-container {
  height: 100%;
}
</style>