<script setup>
import { userInfo } from "@/Info/userInfo";
import axiosT from "@/utils/requestT";
import { ElMessage } from "element-plus";
import { inject, ref } from "vue";

const selectValue = ref('default')
const couponValue = ref('')
const searchInput = ref(null)

const axios = inject('axios')

const dialogVisible = ref(false)

const searchCommoditiesInfo = ref([])

const currentCommodity = ref(null)
const CommodityNumber = ref(1)

const userCoupons = ref([])
const availableCoupons = ref([])

let commoditiesInfo = ref([])

//订单信息
const orderData = ref({
    account: userInfo.value.user.account,
    order: {
        commodityDTOList: [],
        couponType: null
    }
})

//点击卡片中的购买按钮时 展示出对话框以及加载用户的折扣券信息
const showDetail = (commodity) => {
    currentCommodity.value = commodity
    dialogVisible.value = true
    
    axiosT.get('/user_coupon/user', {
    params: {
      account: orderData.value.account
    }
  }, (data) => {
    orderData.value.order.couponType = null
    if (couponLoading.value) {
      ElMessage.error('还未获取所有折扣券信息')
    } else {
      for (let i = 0; i < data.length; i++) {
        if (data[i].quantity === 0) {
          data.splice(i, 1)
        }
      }
      //清空可用券，否则会重复
      availableCoupons.value = []
      userCoupons.value = data
      // 补充优惠券详细信息
      for (const userCoupon of userCoupons.value) {
        for (const couponInfoElement of couponInfo.value) {
          if (userCoupon.couponType === couponInfoElement.type) {
            userCoupon.reductionAmount = couponInfoElement.reductionAmount
            userCoupon.lowestAmount = couponInfoElement.lowestAmount
            break
          }
        }
      }
       // 筛选可用的优惠券
      usableCoupon(userCoupons,commodity)
    }
  })
}

const usableCoupon = (userCoupons, commodity) => {
     // 每次调用前先清空数组
    availableCoupons.value = []

    for (const coupon of userCoupons.value){
        if (coupon.lowestAmount  <= commodity.price) {
            availableCoupons.value.push(coupon)
        } 
    }
    return availableCoupons
}


/**
 * 获取商品图片完整位置
 */
const getImageUrl = (relativePath) => {
    // console.log(relativePath)
    // console.log( process.env.VUE_APP_BASE_URL + relativePath )
    return process.env.VUE_APP_BASE_URL + relativePath
}

/**
 * 获取全部商品信息
 */
axios.get("/commodity/all")
    .then((response) => {
        if (response.state === 1) {
            commoditiesInfo.value = response.data
            searchCommoditiesInfo.value = commoditiesInfo.value
            console.log(searchCommoditiesInfo.value)
        } else {
            ElMessage({
                type: 'error',
                message: response.message
            })
        }
    }).catch(error => {
        console.log(error)
    })

function search() {
    if (searchInput.value === '') {
        searchCommoditiesInfo.value = commoditiesInfo.value
    }
    searchCommoditiesInfo.value = commoditiesInfo.value.filter(function (commodity) {
        return commodity.name.indexOf(searchInput.value) !== -1
            || commodity.id === parseInt(searchInput.value)
            || (searchInput.value.toUpperCase() === "暂无分类" && commodity.category === null)
            || (commodity.category !== null && commodity.category.indexOf(searchInput.value) !== -1)
    })
}

function sort(sortBy) {
    console.log(sortBy)
    // console.log(searchCommoditiesInfo.value)
    if (!searchCommoditiesInfo.value || searchCommoditiesInfo.value.length === 0) return;
    let sorted = [...searchCommoditiesInfo.value];
    switch (sortBy) {
        case 'price-low':
            sorted.sort((a, b) => a.price - b.price);
            break;

        case 'price-high':
            sorted.sort((a, b) => b.price - a.price);
            break;

        case 'rating':
            sorted.sort((a, b) => {
                // 处理可能的undefined值
                const ratingA = a.rating || 0;
                const ratingB = b.rating || 0;
                return ratingB - ratingA; // 降序排列
            });
            break;

        case 'default':
        default:
            // 默认恢复原始排序（假设commoditiesInfo是原始顺序）
            sorted = [...commoditiesInfo.value];
            break;
    }

    searchCommoditiesInfo.value = sorted;
}

// 所有的满减券信息
const couponInfo = ref([])
const couponLoading = ref(true)
axiosT.get('/coupon/all', null, (data) => {
    couponInfo.value = data
    // console.log(couponInfo.value)
    couponLoading.value = false
})

function generateCouponName(couponType){
    for (const couponInfoElement of couponInfo.value) {
    if (couponInfoElement.type === couponType) {
      return '满' + couponInfoElement.lowestAmount + '减' + couponInfoElement.reductionAmount
    }
  }
}

function pay() {
    console.log("当前商品ID:", currentCommodity.value.id);
    console.log("购买数量:", CommodityNumber.value);
    console.log("选择的优惠券类型:", couponValue.value);
    console.log("account:", orderData.value.account);
    // 更新订单信息
    orderData.value.order = {
        commodityDTOList: [
            {
                commodityId: currentCommodity.value.id,
                number: CommodityNumber.value
            }
        ],
        couponType: couponValue.value || null  // 加入优惠券类型
    };

    console.log("orderData.value:", orderData.value)

    axiosT.post('/order/checkout', orderData.value, (data) => {
        let price = data.finalPrice
        console.log(price)
        ElMessage.success('结算成功。扣除卡内余额' + price.toFixed(2) +
            '。获得积分' + data.points)
        orderData.value.order.couponType = null
        CommodityNumber.value = 1
        orderData.value.order.commodityDTOList = []
    })
    dialogVisible.value = false
}

const options = [
    {
        value: 'default',
        label: '默认排序',
    },
    {
        value: 'price-low',
        label: '价格从低到高',
    },
    {
        value: 'price-high',
        label: '价格从高到低',
    },
    {
        value: 'rating',
        label: '评分最高',
    },
]
</script>


<template>
    <!--页面上部分-->
    <div class="search-container">
        <el-input v-model="searchInput" placeholder="搜索商品..." prefix-icon="Search" clearable size="large"
            class="search-input" @input="search" />

        <el-select v-model="selectValue" placeholder="排序方式" size="large" style="width: 120px"
            @change="sort(selectValue)">
            <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
    </div>

    <!--主要内容部分-->
    <div>
        <el-scrollbar class="scroll" height="100%">
            <el-space wrap :size="25">
                <el-card class="card" v-for="i in searchCommoditiesInfo" :key="i.id" style="max-width: 270px">
                    <template #header>
                        <div class="commodities_title"> {{ i.name }} </div>
                    </template>
                    <!-- src="https://shadow.elemecdn.com/app/element/hamburger.9cf7b091-55e9-11e9-a976-7f4d0b07eef6.png" -->
                    <!-- :src="getImageUrl(i.image)" -->
                    <!-- src="../../../../commodityPictures/Olivia_Champagne.jpg" -->
                    <img :src="getImageUrl(i.image)" style="width: 100%" />
                    <template #footer>
                        <div class="footer-stack">
                            <div>
                                <el-rate v-model="i.rating" disabled show-score text-color="#ff9900"
                                    score-template="{value}星" />
                            </div>

                            <div v-if=" i.category != null ">
                                <el-tag type="primary">{{ i.category  }}</el-tag>
                            </div>
                            <div v-else>
                                <el-tag type="primary">暂无分类</el-tag>
                            </div>

                            <div class="pr1ce">￥{{ i.price }}</div>
                            <el-button size="default" type="primary" @click="showDetail(i)">
                                立即购买
                            </el-button>
                        </div>
                        <div>

                        </div>
                    </template>
                </el-card>
                <!--显示购物详情 el-dialog -->
                <div>
                    <el-dialog v-model="dialogVisible" fullscreen>
                        <div v-if="currentCommodity" class="commodity-detail-container">
                            <!--左侧商品图片区域-->
                            <div class="commodity-image-container">
                                <el-image :src="getImageUrl(currentCommodity.image)" alt="商品主图" class="commodity-image"
                                    fit="contain" />
                            </div>
                            <!--右侧商品商品信息区域-->
                            <div class="commodity-info-container">
                                <h1 class="text-3xl font-bold mb-2">{{ currentCommodity.name }}</h1>
                                <el-rate v-model="currentCommodity.rating" disabled show-score text-color="#ff9900"
                                    score-template="{value}星" />
                                <div class="price-section mt-4">
                                    <span class="price-text">￥{{ currentCommodity.price }}</span>
                                </div>
                                <div style="margin-bottom: 30px;">
                                    <span style="font-size: 20px; font-weight: bold; margin-right: 30px;">数量</span>
                                    <el-input-number v-model="CommodityNumber" :min="1" :max="99" :step="1"
                                        :precision="0" step-strictly />
                                </div>

                                <div style="margin-bottom: 20px;">
                                    <span style="font-size: 20px; font-weight: bold; margin-right: 30px;">选择折扣券</span>
                                    <el-select v-model="couponValue" clearable placeholder="选择折扣券" size="large"
                                        style="width: 150px" >
                                        <el-option v-for="c in availableCoupons" 
                                        :key="c.id" 
                                        :label="generateCouponName(c.couponType)"
                                        :value="c.couponType" />
                                    </el-select>
                                </div>
                                <div class="action-buttons mt-6">
                                    <el-button type="primary" @click="pay">立即购买</el-button>
                                </div>
                            </div>
                        </div>
                        <!--下方商品描述-->
                        <div class="description-container">
                            <div class="description-title">产品介绍</div>
                            <div class="description-info"> {{ currentCommodity.description}}</div>
                        </div>
                    </el-dialog>
                </div>
            </el-space>
        </el-scrollbar>
    </div>
</template>


<style>
.search-container {
    display: flex;
    flex-wrap: wrap;
    /* 允许换行 */
    gap: 16px;
    align-items: center;
    width: 100%;
    margin-bottom: 20px;
}

.search-input {
    flex: 1;
    min-width: 200px;
    /* 设置最小宽度 */
}

.sort-select {
    width: 240px;
}

/* 当屏幕宽度小于 930px 时 */
@media (max-width: 930px) {
    .search-input {
        min-width: 100%;
        /* 搜索框占满整行 */
    }

    .sort-select {
        width: 100%;
        /* 选择框占满整行 */
    }
}

.view-toggle-buttons {
    display: inline-flex;
    border: 1px solid var(--el-border-color);
    border-radius: 4px;
    overflow: hidden;
}

/* 移除按钮间的边框 */
.view-toggle-buttons .el-button {
    border-radius: 0;
    border: none;
    margin: 0;
}

.pr1ce {
    font-size: 18px;
    font-weight: bold;
    color: #f56c6c;
    text-align: left;
}

.footer-stack {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.commodities_title {
    font-weight: 600;
    font-size: 25px;
}

.commodity-detail-container {
    display: flex;
    height: 100%;
    gap: 40px;
    padding: 20px;
}

.commodity-image-container {
    flex: 1;
    max-width: 50%;
    display: flex;
    justify-content: center;
    align-items: center;
    background-color: #f5f5f5;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1); /* 可选：添加轻微阴影增强立体感 */
}

.commodity-image {
    max-width: 100%;
    max-height: 70vh;
    object-fit: contain;
}

.commodity-info-container {
    flex: 1;
    padding: 20px;
    display: flex;
    flex-direction: column;
    background-color: rgba(205, 214, 222, 0.6);
    box-shadow: 0 2px 8px rgba(0,0,0,0.1); /* 可选：添加轻微阴影增强立体感 */
}

.price-section {
    margin: 20px 0;
}

.price-text {
    font-size: 24px;
    color: #f56c6c;
    font-weight: bold;
}

@media (max-width: 930px) {
    .commodity-detail-container {
        flex-direction: column;
    }

    .commodity-image-container {
        max-width: 100%;
        margin-bottom: 20px;
    }
}
.description-container{
    flex: 1; /* 占据剩余所有空间 */
    padding: 2rem;
    background-color: #f5f5f5;
    border-radius: 12px; /* 添加圆角 */
    box-shadow: 0 2px 8px rgba(0,0,0,0.1); /* 可选：添加轻微阴影增强立体感 */
}

.description-title {
  font-size: 30px;
  font-weight: 600;
  margin-bottom: 20px;
}

.description-info {
  font-size: 24px;
  font-weight: 400;
  color: #040404;
}


/* .card{
    width: 400px;
    height: 600px;
} */
</style>