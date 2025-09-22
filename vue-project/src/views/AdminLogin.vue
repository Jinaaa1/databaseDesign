<script setup>
import Header from '../components/administrator/AdminHeader.vue'
import {useRouter} from "vue-router";
import {reactive, ref} from "vue";
import {inject} from "vue";
import {ElMessage} from "element-plus";

const router = useRouter()
const form = ref({
  account: '',
  password: '',
})

const rules = reactive({
  account: [
    {
      required: true,
      message: '请输入账号',
      trigger: 'blur',
    }
  ],
  password: [
    {
      required: true,
      message: '请输入密码',
      trigger: 'blur'
    },
    {
      min: 6,
      max: 15,
      message: '密码长度应该为6-15位',
      trigger: 'blur'
    }
  ]
})
const ruleFormRef = ref()

const axios = inject('axios')

async function login(formEl) {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      console.log('submit', fields)
      axios.post('/administrator/login', form.value)
          .then(function (response) {
            //console.log(response)
            if (response.state === 1) {
              sessionStorage.setItem('token', response.data.authorization)
              router.push({
                name: 'AdminIndex'
              })
            } else {
              ElMessage({
                showClose: true,
                message: response.message,
                type: 'error'
              })
            }
          })
          .catch(function (error) {
            console.log(error)
          })
    } else {
      console.log('error submit!', fields)
      ElMessage({
        showClose: true,
        message: '输入的信息不符合要求',
        type: 'error'
      })
    }
  })
}
</script>
<template>
    <el-container>
    <el-header>
      <Header/>
    </el-header>

    <el-main>
      <router-link to="/" class="my-router-link">
        <el-icon>
          <Back/>
        </el-icon>
        返回初始页
      </router-link>
      <div class="border-div">
        <el-form :model="form" :rules="rules" ref="ruleFormRef" class="my-form">
          <el-form-item class="my-form-item" label="账号" prop="account">
            <el-input v-model="form.account"/>
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="form.password" type="password" show-password maxlength="25"/>
          </el-form-item>
          <el-form-item>
            <el-button
                type="primary"
                @click="login(ruleFormRef)"
                style="margin: auto"
            >登录
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-main>
  </el-container>
</template>

<style scoped>
.my-form {
  text-align: center;
  text-align: -webkit-center;
  align-items: center;
  border: 1px solid var(--el-border-color);
  border-radius: 8px;
  padding: 8px;
  margin: auto auto;
  width: 20rem;
  height: 150px;
  display: block;
  background-color: rgba(255, 255, 255, 0.89);
  opacity: 0.92;
}

html.dark .my-form {
  background-color: rgba(34, 34, 35, 0.8);
}

.border-div {
  display: block;
  text-align: center;
  margin-top: 8rem;
}

.my-router-link {
  color: #d3dce6;
  text-decoration: none;
}
</style>