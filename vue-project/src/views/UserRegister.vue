<template>
    <el-container>
        <el-header>
            <UserHeader />
        </el-header>
        <el-main>
            <router-link to="/" class=router_link>
                <el-icon>
                    <Back />
                </el-icon>
                已有账号？返回初始页
            </router-link>
            <br>
            <br>
            <br>
            <br>
            <br>
            <br>
            <div class= "border">
                <el-form ref="formRef" style="max-width: 600px" 
                    :model="form" 
                    :rules="rules" 
                    label-width="auto"
                    class="form">
                    <el-form-item label="姓名" prop="name">
                        <el-input v-model="form.name" />
                    </el-form-item>
                    <el-form-item label="身份证号" prop="idNumber">
                        <el-input v-model="form.idNumber" />
                    </el-form-item>
                    <el-form-item label="手机号" prop="phoneNumber">
                        <el-input v-model="form.phoneNumber" />
                    </el-form-item>
                    <el-form-item label="密码" prop="password">
                        <el-input v-model="form.password" />
                    </el-form-item>
                    <el-form-item>
                        <div style="width: 25px;"></div>
                        <el-button type="primary" @click="register(formRef)" style="margin: auto">注册</el-button>
                    </el-form-item>
                </el-form>
            </div>

        </el-main>
    </el-container>
</template>

<script setup>
import UserHeader from '@/components/user/UserHeader.vue';
import { reactive, ref, inject } from 'vue'
import {ElMessage, ElMessageBox} from 'element-plus'
import {useRouter} from 'vue-router'


const axios = inject('axios') //获取一个全局共享的 Axios 实例
const formRef = ref()
const form = ref({
     name: '',
})

const router = useRouter()

async function register(formEl) {
  if (!formEl) return
  await formEl.validate((valid, fields) => {
    if (valid) {
      console.log('submit!')
      axios.post('/user/register', form.value)
          .then((response) => {
            ElMessageBox.alert('您已成功成为超市会员!您的会员卡号为: ' + response.data.account +
                ' 请牢记您的卡号以便登录，如果忘记卡号，请线下至超市查询卡号'
                , '恭喜', {
                  confirmButtonText: '我记住了',
                  callback: () => {
                    formEl.resetFields
                    router.push({path:'/user/login'})
                  }
                })
          })
    } else {
      console.log('error submit!')
      ElMessage({
        showClose: true,
        message: '输入的信息不符合要求',
        type: 'error'
      })
    }
    console.log(fields)
  })
}






const checkIDNumber = (rule, value ,callbcak) => {
    const reg = /^\d{18}$|^\d{17}(\d|X|x)$/
    if (!reg.test(value)){     // 测试字符串是否匹配
        callbcak(new Error('身份证号除最后一位外必须为数字且最后一位可以是X'))
    } else {
        callbcak()
    }
}

const checkPhoneNumber = (rule, value ,callbcak) => {
    const reg = /\d{11}/
    if (!reg.test(value)){
        callbcak(new Error('手机号码必须全为数字'))
    }else{
        callbcak()
    }
}

const rules = reactive({
    name: [{
      required: true,
      message: '请输入姓名',
      trigger: 'blur' //当用户完成输入并将焦点从当前字段移开时，才会触发该字段的验证逻辑。
    },
    {
      min: 2,
      max: 30,
      message: '姓名长度为2-30位'
    }],
    idNumber:[{
        required: true,
        message: '请输入身份证号',
        trigger: 'blur'
    },
    {
        min: 18,
        max: 18,
        message: '身份证号应为18位'
    },
    {
        validator: checkIDNumber,
        trigger: 'blur'
    }],
    phoneNumber:[{
        required: true,
        message: '请输入手机号码',
        trigger: 'blur'
    },{
        min: 11,
        max: 11,
        message: '手机号码应为11位'
    },{
        validator: checkPhoneNumber,
        trigger: 'blur'
    }],
    password:[{
        required: true,
        message: '请输入密码',
        trigger: 'blur'
    },{
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
.el-form-item {
    margin-bottom: 20px; 
}
</style>