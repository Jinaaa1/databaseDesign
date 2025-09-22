<template>
    <!-- 欢迎信息 -->
    <div class="welcome">
        <h2 style="font-size: 1.5rem; font-weight: 700; margin-bottom: 0.5rem;">欢迎回来，管理员！</h2>
        <p style="color: #bfdbfe;">今天是 {{ formattedDate }}</p>
    </div>

<div class="stats-container">
    <div class="stats-row">
      <el-card shadow="hover" class="stat-card" v-loading="todayConsumptionLoading">
        <div class="card-header">
          <span class="card-title">今日会员消费</span>
          <el-icon class="card-icon"><Money/></el-icon>
        </div>
        <div class="card-content">
          <div class="card-value">{{ todayConsumptionSum }}</div>
        </div>
      </el-card>
    
    
    
      <el-card  shadow="hover" class="stat-card" v-loading="todayRechargeLoading">
        <div class="card-header">
          <span class="card-title">今日会员充值</span>
          <el-icon class="card-icon"><User/></el-icon>
        </div>
        <div class="card-content">
          <div class="card-value">{{ todayRechargeSum }}</div>
        </div>
      </el-card>
    </div>

    <div class="stats-row">
      <el-card shadow="hover" class="stat-card" v-loading="totalConsumptionLoading">
        <div class="card-header">
          <span class="card-title">会员消费总额</span>
          <el-icon class="card-icon"><Money/></el-icon>
        </div>
        <div class="card-content">
          <div class="card-value">{{ totalConsumption.toFixed(2) }}</div>
        </div>
      </el-card>

       <el-card shadow="hover" class="stat-card" v-loading="totalChargeAmountLoading">
        <div class="card-header">
          <span class="card-title">会员充值总额</span>
          <el-icon class="card-icon"><Money/></el-icon>
        </div>
        <div class="card-content">
          <div class="card-value">{{ totalChargeAmount.toFixed(2) }}</div>
        </div>
      </el-card>
    
    </div>
  </div>

  
</template>

<script setup>
import { ref, computed } from 'vue'
import axiosT from "../../utils/requestT.js";
import {addDays, format, } from 'date-fns'

const todayConsumptionData = ref([])
const todayConsumptionLoading = ref(true)

const todayRechargeData = ref([])
const todayRechargeLoading = ref(true)

const totalConsumption = ref(0)
const totalConsumptionLoading = ref(true)

const totalChargeAmount = ref(0)
const totalChargeAmountLoading = ref(true)

const formattedDate = computed(() => {
  return new Date().toLocaleDateString('zh-CN', { 
    year: 'numeric', 
    month: 'long', 
    day: 'numeric',
    weekday: 'long'
  })
})


const todayConsumptionSum = computed(() => {
  let sum = 0
  for (const consumption of todayConsumptionData.value) {
    sum += consumption.paymentAmount
  }
  return sum.toFixed(2)
})

axiosT.get('/order/administrator/between', {
  params: {
    startDate: format(new Date(), 'yyyy-MM-dd'),
    endDate: format(addDays(new Date(), 1), 'yyyy-MM-dd')
  }
}, (data) => {
  todayConsumptionData.value = data
  console.log(todayConsumptionData.value)
  todayConsumptionLoading.value = false
})

axiosT.get('/charge/administrator/between', {
  params: {
    startDate: format(new Date(), 'yyyy-MM-dd'),
    endDate: format(addDays(new Date(), 1), 'yyyy-MM-dd')
  }
}, (response) => {
  todayRechargeData.value = response
  todayRechargeLoading.value = false
})

axiosT.get('/order/administrator/totalAmount', null ,
  (response) => {
    totalConsumption.value = response
    totalConsumptionLoading.value = false
  }
)

axiosT.get('/charge/administrator/totalAmount', null ,
  (response) => {
    totalChargeAmount.value = response
    totalChargeAmountLoading.value = false
  }
)


const todayRechargeSum = computed(() => {
  let sum = 0;
  for (const data of todayRechargeData.value) {
    sum += data.amount
  }
  return sum.toFixed(2)
})

</script>

<style scoped>
.welcome {
    background: linear-gradient(to right, #2563eb, #1d4ed8);
    border-radius: 0.5rem;
    padding: 1.5rem;
    color: white;
}

.stats-container {
    margin-top: 1.5rem;
     display: flex;
    flex-direction: column;
    gap: 1.5rem;
}

.stats-row {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 1.5rem;
}

/* 卡片样式 */
.stat-card {
    border-radius: 0.5rem;
    transition: all 0.3s ease;
    height: 100%;
}

.stat-card:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 卡片头部 */
.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-bottom: 0.5rem;
    border-bottom: 1px solid #f0f0f0;
}

.card-title {
    font-size: 0.875rem;
    font-weight: 500;
    color: #666;
}

.card-icon {
    width: 1rem;
    height: 1rem;
    color: #999;
}

/* 卡片内容 */
.card-content {
    padding-top: 0.5rem;
}

.card-value {
    font-size: 1.5rem;
    font-weight: bold;
    margin: 0.5rem 0;
}

/* 变化指示 */
.card-change {
    display: flex;
    align-items: center;
    font-size: 0.75rem;
    color: #666;
}

.change-icon {
    width: 0.75rem;
    height: 0.75rem;
    margin-right: 0.25rem;
}

.increase {
    color: #67c23a;
}

.decrease {
    color: #f56c6c;
}

.change-desc {
    margin-left: 0.25rem;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .stats-row {
    grid-template-columns: 1fr;
  }
}
</style>