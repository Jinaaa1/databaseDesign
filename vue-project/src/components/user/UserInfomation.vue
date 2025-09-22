<script setup>
import { userInfo } from "../../Info/userInfo";
import { computed, ref, inject} from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import {addDays, format, } from 'date-fns'
import axiosT from "../../utils/requestT.js";

const dialogVisible = ref(false);
const userData = userInfo.value.user

const weekConsumptionData =  ref([])
const totalConsumption = ref(0)


const axios = inject('axios') //获取一个全局共享的 Axios 实例

const levelName = computed(() => {
  const membershipLevel = JSON.parse(sessionStorage.getItem("membership_level_all"))
  return membershipLevel[userInfo.value.user.level - 1 ].name
})

axiosT.get('/order/user/weekAmount', {
  params: {
    startDate: format(addDays(new Date(), -7), 'yyyy-MM-dd'),
    endDate: format(addDays(new Date(), 1), 'yyyy-MM-dd')  
  }
}, (data) => {
  weekConsumptionData.value = data
  console.log("Data", data)
  console.log("UserWeekConsumptionData", weekConsumptionData.value)
})


axiosT.get('/order/user/totalAmount', null, (data) => {
  totalConsumption.value = data.toFixed(2)
})



const weekConsumptionSum = computed(() => {
  let sum = 0
  for (const consumption of weekConsumptionData.value) {
    sum += consumption.paymentAmount
  }
   return sum.toFixed(2)
})



function show(){
  dialogVisible.value  = !dialogVisible.value
}

function showLevelUp(totalConsumption){
    ElMessageBox.confirm(
    '您确定要升级吗，请先确保您的消费金额满足条件！',
    '升级会员等级',
    {
      confirmButtonText: '我确认',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(() => {
      axiosT.get("/user/levelUp", {
        params: {
          totalConsumption: totalConsumption
        }
      }, () => {
          ElMessage.success('升级成功，请重新登录')   
      })
    })
    .catch(() => {
      ElMessage.info('已取消')
    })
}

const form = ref({
  oldPassword: '',
  newPassword: '',
  confirmNewPassword: '',
});

const onSubmit = (formEl) => {
  if (!formEl) return
  formEl.validate((valid) => {
    if (valid) {
      console.log('submit!')
      axios.post('/user/change_password',form.value)
      .then(function (response){
        if (response.state === 1) {
          cancel()
          ElMessage({
            type: 'success',
            message: '您已成功修改密码'
          })
        } else {
          ElMessageBox.alert(response.message)
        }
        formEl.resetFileds()
      })
      .catch(function (error){
        console.log(error)
      })
    } else {
      console.log('error submit!')
      return false
    }
  })
}

function cancel(){
  dialogVisible.value = !dialogVisible.value
  form.value.oldPassword = '';
  form.value.newPassword = '';
  form.value.confirmNewPassword ='';
}

const checkNewPassword = (rule, value, callback) => {
  if (value !== form.value.newPassword){
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = ref({
  oldPassword: [
  {
    required: true,
    message: '请输入旧密码',
    trigger: 'blur'
  },
  {
    min: 6,
    max: 15,
    message:'密码长度应在6-15位之间',
    trigger: 'blur'
  }
],
  newPassword: [
  {
    required: true,
    message: '请输入新密码',
    trigger: 'blur'
  },
  {
    min: 6,
    max: 15,
    message:'密码长度应在6-15位之间',
    trigger: 'blur'
  }
],
confirmNewPassword: [
  {
    required: true,
    message: '请再次输入新密码',
    trigger: 'blur'
  },
  {
     validator: checkNewPassword,
      trigger: 'blur'
  }
]
})

const ruleFormRef = ref()
</script>


<template>
  <div class="profile-container">
    <div class="profile-content">
      <el-card class="profile-card">
        <template #header>
          <div class="profile-header">
            <div class="profile-info">
              <div class="avatar">
                <el-icon :size="32" color="white"><User /></el-icon>
              </div>
              <div>
                <h1>{{ userData.name }}</h1>
                <el-tag type="success" effect="dark" class="pro-badge">
                    会员等级
                    {{ userData.level }}
                  </el-tag>
                  </div>
            </div>

            
            <div class="profile-meta">
              <span>
                {{ levelName }}
              </span>
            </div>
          </div>
        </template>

        <div class="card-body">

          <div class="metrics-grid">
            <!--展示会员卡号信息 -->
            <div class="metric-card">
                <div class="metric-label">会员卡号</div>
                <div class="metric-value" style="color:#34d399">{{ userData.account }}</div>
            </div>

            <!--展示会员身份证号信息 -->
            <div class="metric-card">
                <div class="metric-label">身份证号</div>
                <div class="metric-value" style="color:#fbbf24">{{ userData.idNumber }}</div>
            </div>

            <!--展示会员余额信息 -->
            <div class="metric-card">
                <div class="metric-label">卡内余额</div>
                <div class="metric-value" style="color:#34d399">{{ userData.balance.toFixed(2) }}</div>
            </div>

            <!--展示会员积分信息 -->
            <div class="metric-card">
                <div class="metric-label">剩余积分</div>
                <div class="metric-value" style="color:#34d399">{{ userData.points }}</div>
            </div>

            <!--展示会员手机号码信息 -->
            <div class="metric-card">
                <div class="metric-label">手机号码</div>
                <div class="metric-value" style="color:#34d399">{{ userData.phoneNumber }}</div>
            </div>

            <!--展示会员等级信息 -->
            <div class="metric-card">
                <div class="metric-label">会员等级</div>
                <div class="metric-value" style="color:#34d399">{{ userData.level }}</div>
            </div>
          </div>
             

          <div class="recent-performance">
            <h3>
              <el-icon :size="20" color="#a78bfa"><Medal /></el-icon>
              最近动态
            </h3>

            <div class="performance-grid">
              <div class="performance-card">
                <div class="performance-label">最近七天消费</div>
                <div class="performance-value win">{{ weekConsumptionSum }}</div>
              </div>


              <div class="performance-card">
                <div class="performance-label">消费总额</div>
                <div class="performance-value win">{{ totalConsumption }}</div>
              </div>

              <!-- <div class="performance-card">
                <div class="performance-value streak">感谢您的信任与支持。愿您在此收获美好体验！</div>
              </div> -->
            </div>
          </div>

          <div class="action-buttons">
            <el-button type="primary" class="outline-button"
            @click="showLevelUp(totalConsumption)">
              <el-icon class="button-icon"><Top /></el-icon>
              提升等级
            </el-button>

            <el-button type="primary" class="profile-button"
            @click="show">
              <el-icon class="button-icon"><User /></el-icon>
              更改密码
            </el-button>
          </div>
        </div>
      </el-card>

         <!--下为更改密码所用的弹窗信息 -->
      <el-dialog draggable
          v-model="dialogVisible"
          title="修改密码"
          style="width: 28%;"
          >
        <el-form :rules = "rules" ref="ruleFormRef" :model="form" label-width="100px">

          <el-form-item label="原密码" prop="oldPassword">
            <el-input v-model="form.oldPassword" type="password" show-password style="width: 220px;" />
          </el-form-item>

           <el-form-item label="新密码" prop="newPassword">
            <el-input v-model="form.newPassword" type="password" show-password style="width: 220px;"/>
          </el-form-item>

          <el-form-item label="确认新密码" prop="confirmNewPassword">
            <el-input v-model="form.confirmNewPassword" type="password" show-password style="width: 220px;" />
          </el-form-item>

        <div class="dialog-button">
          <el-button class="button1" type="primary" @click="onSubmit(ruleFormRef)">确认</el-button>
          <el-button @click="cancel">取消</el-button>
        </div>

        </el-form>
        </el-dialog>

    </div>
  </div>
</template>


<style scoped>
.profile-container {
  min-height: 100vh;
  background: white;
  padding: 1rem;
}

.profile-content {
  max-width: 56rem;
  margin: 0 auto;
}

.profile-card {
  background: rgb(255, 255, 255);
  border: 1px solid #e5e7eb;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.profile-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-bottom: 1rem;
}

.profile-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.avatar {
  width: 4rem;
  height: 4rem;
  background: linear-gradient(to bottom right, #3b82f6, #9333ea);
  border-radius: 9999px;
  display: flex;
  align-items: center;
  justify-content: center;
}

h1 {
  font-size: 1.875rem;
  font-weight: 700;
  color: #111827;
  margin: 0;
}

.username {
  color: #6b7280;
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin: 0;
}

.pro-badge {
  background-color: rgba(59, 130, 246, 0.1);
  color: #2563eb;
  border-color: rgba(59, 130, 246, 0.2);
}

.profile-meta {
  text-align: right;
  font-size: 25px;
  font-weight: 700;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #6b7280;
  margin-bottom: 0.25rem;
}


.metrics-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
}

.metric-card {
  padding: 1rem;
  border-radius: 0.5rem;
  border: 1px solid #e5e7eb;
  background: #f9fafb;
}

.metric-label {
  color: #6b7280;
  font-size: 0.875rem;
  font-weight: 500;
  margin-bottom: 0.25rem;
}

.metric-value {
  font-size: 1.5rem;
  font-weight: 700;
}

.metric-progress {
  margin-top: 0.5rem;
}

.recent-performance {
  margin-top: 1.5rem;
}


.performance-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
}

.performance-card {
  padding: 1rem;
  background: #f9fafb;
  border-radius: 0.5rem;
  border: 1px solid #e5e7eb;
}

.performance-label {
  color: #6b7280;
  font-size: 0.875rem;
}

.performance-value {
  font-size: 1.25rem;
  font-weight: 700;
  margin: 0.25rem 0;
}

.performance-value.win {
  color: #10b981;
}

.performance-value.streak {
  color: #3b82f6;
}

.action-buttons {
  display: flex;
  gap: 0.75rem;
  padding-top: 1rem;
}

.profile-button {
  background-color: #2563eb;
  color: white;
}

.profile-button:hover {
  background-color: #1d4ed8;
}

.outline-button {
  border-color: #d1d5db;
  color: #4b5563;
  background-color: white;
}

.outline-button:hover {
  background-color: #f3f4f6;
  border-color: #9ca3af;
}

.button-icon {
  margin-right: 0.5rem;
}

.dialog-button {

  display: flex;
  justify-content: center;
  gap: 12px;
  margin-top: 15px;
}

</style>