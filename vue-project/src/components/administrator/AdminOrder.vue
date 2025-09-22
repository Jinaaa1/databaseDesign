<script setup>
import { ref, inject, watch, computed } from "vue";
import { ElMessage } from "element-plus";
import { Search } from '@element-plus/icons-vue'
import { compareAsc, format, parseISO } from "date-fns";

const axios = inject('axios')

// 后端返回的订单list，不包含商品信息
const UserOrderList = ref([])
const displayedUserOlderList = ref([])
const loading = ref(true)

//获取会员订单
axios.get('/order/administrator/between', {
    // params: {
    //   startDate: '2022-08-01',
    //   endDate: '2022-09-22'
    // }

    params: {
        startDate: '2025-07-04',
        endDate: '2025-09-22'
    }
})
    .then((response) => {
        if (response.state === 1) {
            UserOrderList.value = response.data
            console.log(UserOrderList.value)
            for (const element of UserOrderList.value) {
                element.paymentAmount = element.paymentAmount.toFixed(2)
                if (element.couponInfo === null) {
                    element.couponInfo = '无'
                } else {
                    const lowestAmount = element.couponInfo.substring(0, element.couponInfo.indexOf('/'))
                    const reliefAmount = element.couponInfo.substring(element.couponInfo.indexOf('/') + 1)
                    element.couponInfo = '满' + lowestAmount + '减' + reliefAmount
                }
            }
            displayedUserOlderList.value = UserOrderList.value
            loading.value = false
        } else {
            ElMessage({
                type: 'error',
                message: response.message
            })
        }
    })
    .catch((error) => {
        console.log(error)
    })

const searchInput = ref('')

function search() {
    const dataList = dateRange.value === null ? UserOrderList.value
        : displayedUserOlderList.value
    if (searchInput.value === '') {
        displayedUserOlderList.value = dataList.value
    }
    displayedUserOlderList.value = dataList.filter(function (order) {
        return order.id.toString().indexOf(searchInput.value) !== -1
            || order.userId.toString().indexOf(searchInput.value) !== -1
    })
}

const dateRange = ref(null)

const changeDate = (dateRangeParam) => {
    if (dateRangeParam === null) {
        return -1
    }
    const startDate = parseISO(dateRangeParam[0])
    const endDate = parseISO(dateRangeParam[1])
    console.log('startDate')
    console.log(startDate)
    console.log('endDate')
    console.log(endDate)
    displayedUserOlderList.value = UserOrderList.value.filter(function (order) {
        const date = parseISO(format(parseISO(order.time), 'yyyy-MM-dd'))
        return compareAsc(date, startDate) >= 0 && compareAsc(date, endDate) <= 0
    })
}

function filterOrders() {
    changeDate(dateRange.value)
    search()
}

const disabledDate = (time) => {
    return time.getTime() > Date.now()
}

const shortcuts = [
    {
        text: '今天',
        value: new Date(),
    },
    {
        text: '昨天',
        value: () => {
            const date = new Date()
            date.setTime(date.getTime() - 3600 * 1000 * 24)
            return date
        },
    },
    {
        text: '一周前',
        value: () => {
            const date = new Date()
            date.setTime(date.getTime() - 3600 * 1000 * 24 * 7)
            return date
        },
    },
]

// 后端返来的订单商品List，其中只包含商品id，数量，买时的原价，折扣价格
// 选择要看的订单的商品的详细信息，需要再次向后端请求数据
const currentOrderCommoditiesList = ref([])
const commoditiesDialogVisible = ref(false)
const dialogTableLoading = ref(true)
const count = ref(0)

async function getCommoditiesInfo(id) {
    currentOrderCommoditiesList.value = []
    // console.log('OrderCommodityList')
    // console.log(that.OrderCommodityList)
    await axios.get('/order/commodity/administrator', {
        params: {
            orderId: id
        }
    }).then((response) => {
        if (response.state === 1) {
            currentOrderCommoditiesList.value = response.data
        } else {
            ElMessage.error(response.message)
        }
    }).catch((error) => {
        console.log(error)
        return -1
    })
    count.value += currentOrderCommoditiesList.value.length
    for (const commodity of currentOrderCommoditiesList.value) {
        axios.get('/commodity/query', {
            params: {
                commodityId: commodity.commodityId
            }
        }).then((response) => {
            if (response.state === 1) {
                // console.log('commodity')
                // console.log(commodity)
                commodity.commodityName = response.data.name
                commodity.originalPrice = commodity.originalPrice.toFixed(2)
                commodity.actualPrice = commodity.actualPrice.toFixed(2)
                count.value--
                //console.log('response count = ' + that.count)
            } else {
                ElMessage({
                    type: "error",
                    message: response.message
                })
            }
        }).catch((error) => {
            console.log(error)
        })
        if (count.value === 0) {
            dialogTableLoading.value = false
            //console.log('没有监听')
        } else {
            //console.log('开始监听')
            const unwatch = watch(count, (newVal) => {
                //console.log('newVal')
                //console.log(newVal)
                if (newVal === 0) {
                    dialogTableLoading.value = false
                    unwatch()
                }
            }
            )
            //console.log('监听成功')
        }
    }
}

function checkOrderCommodities(id) {
    commoditiesDialogVisible.value = true
    getCommoditiesInfo(id)
    // console.log(this.OrderCommodityList)
}

// 计算总金额
const totalAmount = computed(() => {
    return displayedUserOlderList.value.reduce((sum, order) => {
        return sum + (parseFloat(order.paymentAmount) || 0)
    }, 0).toFixed(2)
})

// 计算总积分
const totalPoints = computed(() => {
    return displayedUserOlderList.value.reduce((sum, order) => {
        return sum + (parseInt(order.rewardPoints) || 0)
    }, 0)
})

</script>

<template>
    <div class="container">
        <h1>订单一览</h1>
        <div style="text-align: center; max-width: 840px; margin: auto">
            <el-date-picker style="float: left; width: 260px" v-model="dateRange" type="daterange"
                start-placeholder="起始日期" end-placeholder="截止日期" value-format="YYYY-MM-DD" :disabled-date="disabledDate"
                :shortcuts="shortcuts" @change="filterOrders" />
            <el-input v-model="searchInput" class="search" placeholder="输入订单号或会员卡号" :prefix-icon="Search"
                @input="filterOrders" />
            <br>
            <br>
            <br>
            <el-table :data="displayedUserOlderList" class="modern-table" empty-text="暂无数据" v-loading="loading"
                max-height="calc(100vh - 250px)" :default-sort="{ prop: 'time', order: 'descending' }"
                style="width: 100%">
                <el-table-column prop="time" label="日期" width="170" sortable align="center" header-align="center">
                    <template #header>
                        <span class="table-header">日期</span>
                    </template>
                </el-table-column>

                <el-table-column prop="id" label="订单号" width="90" header-align="center">
                    <template #header>
                        <span class="table-header">订单号</span>
                    </template>
                </el-table-column>

                <el-table-column prop="userId" label="会员卡号" width="90" header-align="center">
                    <template #header>
                        <span class="table-header">会员卡号</span>
                    </template>
                </el-table-column>

                <el-table-column prop="paymentAmount" label="订单金额" width="140" sortable align="right"
                    header-align="center">
                    <template #header>
                        <span class="table-header">订单金额</span>
                    </template>
                    <template #default="{ row }">
                        <span class="money-cell">¥{{ row.paymentAmount }}</span>
                    </template>
                </el-table-column>

                <el-table-column prop="rewardPoints" label="获得积分" width="100" align="right" header-align="center">
                    <template #header>
                        <span class="table-header">获得积分</span>
                    </template>
                    <template #default="{ row }">
                        <span class="points-cell">{{ row.rewardPoints }}</span>
                    </template>
                </el-table-column>

                <el-table-column prop="couponInfo" label="使用满减券" width="150" align="center" header-align="center">
                    <template #header>
                        <span class="table-header">使用满减券</span>
                    </template>
                    <template #default="{ row }">
                        <el-tag :type="row.couponInfo === '无' ? 'info' : 'success'" size="small" effect="plain" round>
                            {{ row.couponInfo }}
                        </el-tag>
                    </template>
                </el-table-column>

                <el-table-column label="订单详情" width="120" align="center" header-align="center">
                    <template #header>
                        <span class="table-header">操作</span>
                    </template>
                    <template #default="{ row }">
                        <el-button type="primary" link size="small" @click="checkOrderCommodities(row.id)"
                            class="action-btn">
                            <span class="btn-text">查看详情</span>
                            <el-icon :size="14" style="margin-left: 4px">
                                <ArrowRight />
                            </el-icon>
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>

            <!--下方总结框-->
            <div class="summary-container">
                <div class="stats-box">
                    <span class="stat-item">
                        <span class="stat-label">共找到</span>
                        <span class="stat-value">{{ displayedUserOlderList.length }}</span>
                        <span class="stat-label">条订单</span>
                    </span>

                    <span class="divider">|</span>

                    <span class="stat-item">
                        <span class="stat-label">总金额</span>
                        <span class="stat-value money">¥{{ totalAmount }}</span>
                    </span>

                    <span class="divider">|</span>

                    <span class="stat-item">
                        <span class="stat-label">总积分</span>
                        <span class="stat-value points">{{ totalPoints }}</span>
                    </span>
                </div>
            </div>
        </div>
    </div>

    <el-dialog v-model="commoditiesDialogVisible" center draggable :modal="false" title="订单商品详情" width="530px">
        <el-table :data="currentOrderCommoditiesList" class="commodity-table" v-loading="dialogTableLoading"
            max-height="calc(60vh - 2rem)" highlight-current-row stripe lazy
            :default-sort="{ prop: 'originalPrice', order: 'descending' }">
            <el-table-column prop="commodityId" label="商品号" width="80" />
            <el-table-column prop="commodityName" label="名称" width="110" align="center" />
            <el-table-column prop="quantity" label="数量" width="90" sortable align="center" />
            <el-table-column prop="originalPrice" label="原价" width="100" align="center" sortable />
            <el-table-column prop="actualPrice" label="折扣价" width="100" align="center" />
        </el-table>
    </el-dialog>
</template>

<style scoped>
h1 {
    text-align: center;
}

.commodity-table {
    max-width: 480px;
    margin: auto;
}

.search {
    min-width: 100px;
    max-width: 200px;
    margin-bottom: 10px;
    float: right;
}

.summary-container {
    margin: 1.5rem 0;
    display: flex;
    justify-content: center;
}

.stats-box {
    display: inline-flex;
    align-items: center;
    gap: 1.5rem;
    padding: 0.75rem 1.5rem;
    background-color: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(8px);
    border-radius: 12px;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05),
        0 2px 4px -1px rgba(0, 0, 0, 0.02);
    border: 1px solid rgba(229, 231, 235, 0.5);
}

.stat-item {
    display: flex;
    align-items: baseline;
    gap: 0.375rem;
}

.stat-label {
    color: #6b7280;
    font-size: 0.875rem;
    font-weight: 500;
}

.stat-value {
    color: #111827;
    font-size: 1rem;
    font-weight: 600;
}

.money {
    /* 红色表示金额 */
    color: #cf0e3e;
}

.points {
    /* 蓝色表示积分 */
    color: #3b82f6;
}

.divider {
    color: #e5e7eb;
    font-weight: 300;
    user-select: none;
}

/* 悬停效果 */
.stats-box:hover {
    box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.05),
        0 4px 6px -2px rgba(0, 0, 0, 0.02);
    transform: translateY(-1px);
    transition: all 0.2s ease;
}

/* 响应式设计 */
@media (max-width: 640px) {
    .stats-box {
        flex-direction: column;
        align-items: flex-start;
        gap: 0.75rem;
        padding: 1rem;
    }

    .divider {
        display: none;
    }
}

/* 表格整体样式 */
.modern-table {
    --el-table-border-color: rgba(229, 231, 235, 0.5);
    --el-table-header-bg-color: #f8fafc;
    --el-table-row-hover-bg-color: #f1f5f9;
    --el-table-current-row-bg-color: #e0f2fe;
    max-width: 840px;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.05);
}

/* 表头样式 */
:deep(.modern-table .el-table__header th) {
    background-color: #f8fafc;
    height: 52px;
}

.table-header {
    font-weight: 600;
    color: #334155;
    font-size: 15px;
}

/* 单元格样式 */
:deep(.modern-table .el-table__cell) {
    padding: 12px 0;
    font-size: 14px;
    font-weight: 500;
}

/* 斑马纹样式 */
:deep(.modern-table .el-table__body tr.el-table__row--striped td) {
    background-color: #f8fafc;
}

/* 金额特殊样式 */
.money-cell {
    font-family: 'Roboto', sans-serif;
    font-weight: 500;
    color: #cf0e3e;
}

/* 积分特殊样式 */
.points-cell {
    font-weight: 500;
    color: #3b82f6;
}

/* 操作按钮样式 */
.action-btn {
    transition: all 0.2s ease;
}

.btn-text {
    position: relative;
}

.btn-text::after {
    content: '';
    position: absolute;
    bottom: -2px;
    left: 0;
    width: 0;
    height: 1px;
    background-color: var(--el-color-primary);
    transition: width 0.2s ease;
}

.action-btn:hover .btn-text::after {
    width: 100%;
}

/* 空状态样式 */
:deep(.modern-table .el-table__empty-block) {
    min-height: 200px;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: #f8fafc;
    border-radius: 0 0 12px 12px;
}

/* 加载状态样式 */
:deep(.modern-table .el-loading-mask) {
    border-radius: 12px;
    background-color: rgba(248, 250, 252, 0.8);
}


</style>