<script>
import { ElMessage } from 'element-plus'


export default {
    data() {
        return {
            userCoupon: [],
            loading: true
        }
    },
    created() {
        const that = this

        // 获取系统所有优惠券信息
        function getCouponInfo() {
            return that.axios.get('/coupon/all')
        }

        // 获取用户持有的优惠券
        function getUserCoupon() {
            return that.axios.get('/user_coupon/user')
        }

        // 并行发送两个请求
        Promise.all([getCouponInfo(), getUserCoupon()])
            .then(function (response) {
                // response[0] 是所有优惠券信息
                // response[1] 是用户优惠券信息
                if (response[0].state === 1) {
                    if (response[1].state === 1) {
                        // 遍历用户持有的每张优惠券
                        for (const userCouponResponse of response[1].data) {// 会员持有的
                            // 在系统优惠券中查找匹配项
                            for (const couponInfoResponse of response[0].data) {
                                // 匹配条件：类型相同且数量>0
                                if (userCouponResponse.couponType === couponInfoResponse.type &&
                                    userCouponResponse.quantity > 0) {
                                    // 合并数据后存入组件的 userCoupon 数组
                                    that.userCoupon.push({
                                        id: userCouponResponse.id,
                                        couponType: userCouponResponse.couponType,
                                        reductionAmount: couponInfoResponse.reductionAmount,  // 满减金额
                                        lowestAmount: couponInfoResponse.lowestAmount,        // 最低消费额
                                        quantity: userCouponResponse.quantity                // 持有数量
                                    })
                                    break
                                }
                            }
                        }
                         that.loading = false  // 关闭加载状态
                    } else {
                        ElMessage({
                            type: 'error',
                            message: response[1].message
                        })
                    }
                } else {
                    ElMessage({
                        type: 'error',
                        message: response[0].message
                    })
                }
            })
            .catch(function (error) {  // 捕获异常
          console.log(error[0])
          console.log(error[1])
        })
    }
}


</script>

<template>
    <div>
        <el-scrollbar class="scroll" v-loading="loading" height="100%">
            <el-empty v-if="userCoupon.length === 0" description="您还没有折扣券，快去兑换吧！"
                :image-size="300"/>
            <el-space wrap>
                <el-card v-for="i in userCoupon" :key="i.id" class="box-card">
                    <template #header>
                        <div class="card-header">
                            <span>{{ i.reductionAmount }}元折扣券</span>
                        </div>
                    </template>
                    <div>
                        <div class="condition">
                                <span>使用条件:</span>
                                <span>满{{ i.lowestAmount }}元起</span>
                            </div>
                    </div>
                    <br>
                            <div class="quantity">
                                <span>{{ i.quantity }}张</span>
                            </div>
                </el-card>
            </el-space>
        </el-scrollbar>
    </div>
</template>

<style>
.scroll {
  padding: 10px;
  border-radius: 10px;
  height: auto;
}

.box-card {
  background-image: linear-gradient(to bottom right,
  rgb(100, 83, 172), rgb(222, 141, 181), rgb(100, 83, 172)); 
}

.card-header {
    font-size: 35px;    
    line-height: 2rem;    
    font-weight: 700;     
    color: rgb(17 24 39); 
}

.condition{
    font-size: 1.3rem;
}

.quantity{
    text-align: right;
    font-size: 1.5rem;
}


.card-content {
  text-align: right;
  font-size: 25px;
}</style>