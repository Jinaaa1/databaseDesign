<script setup>
import { userInfo } from '@/Info/userInfo';
// import axios from 'axios';
import { ElMessage, ElMessageBox } from 'element-plus';
import {computed, inject} from 'vue'

const axios = inject ('axios')

const level = computed(() => {
  const membershipLevel = JSON.parse(sessionStorage.getItem("membership_level_all"))
  return membershipLevel[userInfo.value.user.level - 1].name
})

function recharge(){
  ElMessageBox.prompt("请输入要充值的金额", "提示", {
    confirmButtonText: "充值",
    cancelButtonText: "取消",
    inputPattern: /^([1-9]\d*)+(.\d{1,2})?$/,
    inputErrorMessage: "无效金额",
  }).then(({value}) => {
    const rechargeAmount = Number(value)
    if (rechargeAmount < 1 || rechargeAmount > 999999999){
      ElMessage({
        showClose: true,
        type: 'error',
        message: '金额需要在1~999999999元之间'
      })
      return
    }
    axios.post('/user/recharge',{amount: rechargeAmount})
        .then(function (response){
          if (response.state === 1){
            userInfo.value.user.balance += rechargeAmount
            ElMessage({
              showClose: true,
              type: 'success',
              message: `成功充值${value}元`
            })
          } else {
            ElMessage({
              showClose: true,
              type: 'error',
              message: '充值失败'
            })
          }
        })
  }).catch(() => {
    ElMessage({
      type: 'info',
      message: '充值取消',
    })
  })
}
</script>

<script>

export default{
  data(){
    return {
      couponNumber: null,
      loading: true
    }
  },
  created(){
    this.fecthData()
  },
  methods: {
    fecthData(){
      const that = this
      this.axios.get('/user_coupon/user')
      .then(function (response){
        let temp = 0
        for (const responseElement of response.data){
          temp += responseElement.quantity
        }
        that.couponNumber = temp
        that.loading = false
      })
      .catch(function (error){
        console.log(error)
      })
    }
  }
}
</script>

<template>
  <el-row>
    <el-col :span="7">
      <el-card class="box-card">
        <template #header>
          <div class="card-header">
            <span class="text">你的卡号</span>
            <div style="height: 65px"/>
          </div>
        </template>
        <div class="card-content">
          {{ userInfo.user.account }}
        </div>
      </el-card>
    </el-col>

    <el-col :span="1"/>
    <el-col :span="7">
      <el-card class="box-card">
        <template #header>
          <div class="card-header">
            <span class="text">你的余额</span>
            <el-button 
            class="button"
            plain text
            size="large"
            @click="recharge">
              充值
              </el-button>
          </div>
        </template>
        <div class="card-content">
          {{ userInfo.user.balance.toFixed(2) }}
        </div>
        </el-card>
        </el-col>

        <el-col :span="1"/>
    <el-col :span="7">
      <el-card class="box-card" :class="'level' + userInfo.user.level">
        <template #header>
          <div class="card-header">
            <span class="text">你的等级</span>
            <div style="height: 65px"/>
          </div>
        </template>
        <div class="card-content">
          {{ level }}会员
        </div>
      </el-card>
    </el-col>
  </el-row>

  <div style="height: 20px"/>
  <el-row>
    <el-col :span="3"/>
    <el-col :span="7">
      <el-card class="box-card">
        <template #header>
          <div class="card-header">
            <span class="text">你的积分</span>
            <div style="height: 65px"/>
          </div>
        </template>
        <div class="card-content">
          {{ userInfo.user.points }}
        </div>
      </el-card>
    </el-col>
    <el-col :span="3"/>
    <el-col :span="7">
      <el-card class="box-card" v-loading="loading">
        <template #header>
          <div class="card-header">
            <span class="text">你的满减券</span>
            <div style="height: 65px"/>
          </div>
        </template>
        <div class="card-content">
          {{ couponNumber }}张
        </div>
      </el-card>
    </el-col>
  </el-row>


</template>

<style lang="scss" scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.box-card {
  background-color: #af98e8;
  border-radius: 30px;
}
.text {
  font-size: 1.5rem;
}
.button {
  font-weight: bold;
}
div.card-content {
  font-weight: bolder;
  font-size: 2.5rem;
  margin-right: 0;
  text-align: right;
  width: 100%;
}
@import "../../assets/membership_level.scss"
</style>