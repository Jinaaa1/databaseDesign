<template>
    <el-container>
      <el-header>
        <UserHeader/>
      </el-header>
      <el-main>
        <router-link to="/" class="router_link">
        <el-icon>
          <Back/>
        </el-icon>
        返回初始页
        </router-link>
        <div class= "border">
                <el-form ref="formRef" style="max-width: 600px" 
                    :model="form" 
                    :rules="rules" 
                    label-width="auto"
                    class="form">
                    <el-form-item label="卡号" prop="account">
                        <el-input v-model="form.account" />
                    </el-form-item>
                    
                    <el-form-item label="密码" prop="password">
                        <el-input v-model="form.password" type="password" show-password maxlength="15"/>
                    </el-form-item>
                    <el-form-item>
                        <div style="width: 25px;"></div>
                        <el-button type="primary" @click="login(formRef)" style="margin: auto">登录</el-button>
                    </el-form-item>
                </el-form>
            </div>

      </el-main>

    </el-container>



</template>


<script setup>
import UserHeader from '@/components/user/UserHeader.vue';
import { reactive, ref, inject } from 'vue';
import {ElMessage} from 'element-plus'
import {useRouter} from 'vue-router'
import { userInfo } from '@/Info/userInfo';


const axios = inject('axios') //获取一个全局共享的 Axios 实例

const router = useRouter()

const form = ref({
  account: '',
  password: '',
})

const formRef = ref()

// defineExpose({
//   name: 'UserLogin'
// })


async function login(formEl) {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      console.log('submit!',fields)
      axios.post('/user/login', form.value)
          .then(function (response) {
            // console.log(response)
            if (response.state === 1){
              sessionStorage.setItem('user',JSON.stringify(response.data.user))
              sessionStorage.setItem('token',response.data.authorization)
              console.log(JSON.stringify(response.data.user)
            )
              //获得会员信息
              axios.get('/membership_level/all')
              .then(function (levelResponse){
                // console.error(levelResponse)
                if(levelResponse.state === 1){
                  sessionStorage.setItem('membership_level_all', JSON.stringify(levelResponse.data))
                  userInfo.value.user = response.data.user
                  userInfo.value.membershipLevel = levelResponse.data
                  console.log("Membership:",levelResponse.data )
                  router.push({
                    name: 'UserIndex',
                    params:{
                      account: response.data.user.account
                    }
                  })
                }else{
                  ElMessage({
                    showClose: true,
                    message: response.message,
                    type: 'error'
                  })
                }
              })
              .catch(function(error){
              console.log(error)
            })
            }else {
              ElMessage({
                showClose: true,
                message: response.message,
                type: 'error'
              })
            }
          })
          .catch(function (error){
            console.log(error)
          })
    }else{
      console.log('error submit!',fields)
      ElMessage({
        showClose: true,
        message: '您输入的信息不符合要求',
        type: 'error'

      })
    }
  })
}

const rules = reactive({
    account: [{
        required: true,
        message: '请输入卡号',
        trigger: 'blur',
    },
    {
        min: 8,
        max: 8,
        message: '卡号长度应为8位',
        trigger: 'blur'
    }],
    password: [{
        required: true,
        message: '请输入密码',
        trigger: 'blur'
    },
    {
        min: 6,
        max: 15,
        message: '密码长度应在6至15位',
        trigger: 'blur'
    }]
})



</script>

<style>
.router_link {
    color: #d3dce6;
    text-decoration: none;
}
.border {
  display: block;
  margin: auto;
  width: 40%;
  text-align: center;
}
.form{
    display: block;
    max-width: 40rem;
    width: 20rem;
    margin: auto;
    border: 1px solid var(--el-border-color);
    border-radius: 8px;
    background-color: rgba(255, 255, 255, 0.89);
    opacity: 0.92;
    padding:  40px 40px 30px 35px;;
}

</style>