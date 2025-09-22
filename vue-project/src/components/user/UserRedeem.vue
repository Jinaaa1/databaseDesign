<script setup>
import { userInfo } from '@/Info/userInfo'
import { ElMessage } from 'element-plus';
import { inject, ref } from "vue";

const axios = inject('axios')
let loading = ref(null)
let couponsInfo = ref([])
const searchedCouponsInfo = ref([])

axios.get('/coupon/all')
    .then((response) => {
        if (response.state === 1) {
            couponsInfo.value = response.data
            searchedCouponsInfo.value = couponsInfo.value
            loading.value = false
        } else {
            ElMessage({
                type: 'error',
                message: response.message
            })
        }
    }).catch(error => {
        console.log(error)
    })

const chooseNumberDialogVisible = ref(false)
const couponNumber = ref(1)
let couponType = null

function chooseType(type) {
    couponNumber.value = 1
    couponType = type
    chooseNumberDialogVisible.value = true
}

function redeem() {
    axios.get('/user/redeem', {
        params: {
            couponType,
            number: couponNumber.value
        }
    }).then((response) => {
        if (response.state === 1) {
            let cost = null
            for (const information of couponsInfo.value) {
                if (couponType === information.type) {
                    cost = information.cost
                    break
                }
            }
            let totalCost = cost * couponNumber.value
            userInfo.value.user.points -= totalCost
            ElMessage({
                type: 'success',
                message: response.message
            })
            cancel()
        } else {
            ElMessage({
                typr: 'error',
                message: response.message
            })
        }
    }).catch((error) => {
        console.log(error)
    })
}
function cancel() {
    chooseNumberDialogVisible.value = !chooseNumberDialogVisible.value
}

const searchInput = ref('')

function findCoupon(){
    if (searchInput.value === '') {
        searchedCouponsInfo.value = couponsInfo.value
        return;
    } 

    const searchTerm = searchInput.value.trim();

    // 执行搜索过滤
  searchedCouponsInfo.value = couponsInfo.value.filter(coupon => {
    // 1. 匹配优惠券ID（精确匹配）
    if (coupon.id === parseInt(searchTerm)) {
      return true;
    }
    
    // 2. 匹配"满X减Y"格式
    if (searchTerm.match(/满\d+减\d+/)) {
      const match = searchTerm.match(/满(\d+)减(\d+)/);
      return (
        coupon.lowestAmount === parseInt(match[1]) && 
        coupon.reductionAmount === parseInt(match[2])
      );
    }
    
    // 3. 匹配"满X"格式
    if (searchTerm.match(/满\d+/)) {
      const match = searchTerm.match(/满(\d+)/);
      return coupon.lowestAmount === parseInt(match[1]);
    }
    
    // 4. 匹配"减Y"格式
    if (searchTerm.match(/减\d+/)) {
      const match = searchTerm.match(/减(\d+)/);
      return coupon.reductionAmount === parseInt(match[1]);
    }
    
    // 5. 模糊匹配优惠券名称
    if (coupon.name && coupon.name.includes(searchTerm)) {
      return true;
    }
    
    // 6. 匹配积分成本
    if (parseInt(searchTerm) === coupon.cost) {
      return true;
    }
    
    return false;
  });
}

</script>
<template>
    <div>
        <div class="title">您的积分：{{ userInfo.user.points }}</div>
        <br>
        <div style="display: flex; justify-content: space-between; align-items: center;">
            <div style="text-align:left;">折扣券只能使用在商品自身价格满足使用条件的情况下</div>
            <div>
                <el-input class="search" v-model="searchInput" placeholder="输入满减券信息：满xx减xx" :prefix-icon="Search"
                    @change="findCoupon" />
            </div>
        </div>
    </div>
    <div class="my_scrollbar">
        <el-scrollbar height="100%">
            <el-space wrap :size="20">
                <el-card v-for="coupon in searchedCouponsInfo" :key="coupon.type" class="coupon-card" shadow="never">
                    <!-- 卡片头部 -->
                    <div class="card-header">
                        <!-- 金额 -->
                        <div class="text-right">
                            <div class="header-content">{{ coupon.reductionAmount }}元满减券</div>
                        </div>
                    </div>

                    <!-- 卡片内容 -->
                    <div class="card-content">
                        <!-- 分隔线 -->
                        <el-divider class="my-4" />

                        <!-- 使用条件 -->
                        <div class="condition">
                            <span>使用条件:</span>
                            <span>满{{ coupon.lowestAmount }}元起</span>
                        </div>
                        <br>
                        <div class="cost">
                            <span>{{ coupon.cost }}积分/张</span>
                        </div>
                    </div>

                    <template #footer>
                        <div class="button">
                            <el-button color="#ec4899" plain @click="chooseType(coupon.type)">立即兑换 </el-button>
                        </div>
                    </template>
                </el-card>
            </el-space>
        </el-scrollbar>
    </div>
    <el-dialog draggable v-model="chooseNumberDialogVisible" title="兑换数量" width="430">
        <el-form :inline="true">
            <el-form-item label="数量">
                <el-input-number v-model="couponNumber" :min="1" :max="99" :step="1" :precision="0" step-strictly />
            </el-form-item>
            <el-form-item>
                <el-button @click="cancel">取消</el-button>
                <el-button type="primary" @click="redeem">确认</el-button>
            </el-form-item>
        </el-form>
    </el-dialog>
</template>

<style>
.title {
    text-align: center;
    font-size: 1.875rem;
    line-height: 2.25rem;
    font-weight: 700;
    /* color: #1521a9; */
    color: #eb0c9d;
    margin-bottom: 0.5rem;
}

.header-content {
    font-size: 1.5rem;
    line-height: 2rem;
    font-weight: 700;
    color: rgb(17 24 39);
}

.condition {
    font-size: 1.3rem;
}

.cost {
    text-align: right;
    font-size: 1.4rem;
}

.search {
    min-width: 100px;
    max-width: 200px;
    margin-bottom: 10px;
    float: right;
}

.my-scrollbar {
    height: calc(100% - 2rem - 2px);
}

.coupon-card {
    background-image: linear-gradient(to bottom right,
            rgb(236, 72, 153), rgb(222, 141, 181), rgb(236, 72, 153));
    position: relative;
    overflow: hidden;
    transition: box-shadow 300ms ease;
    border-radius: var(--el-card-border-radius);
    border: 1px solid var(--el-border-color-light);
}

.button {
    display: flex;
    justify-content: center;
    /* 水平居中 */
}
</style>