<script>
import {ElMessage} from "element-plus";

export default {
  name: 'UserOrder',
  data() {
    return {
      // 后端返来的订单商品List，其中只包含商品id，数量，买时的原价，折扣价格
      OrderCommodityList: [],
      // 后端返回的订单list，不包含商品信息
      UserOrderList: [],
      loading: true,
      // 用户当前选择要看的订单的商品的详细信息，需要再次向后端请求数据
      currentOrderCommoditiesList: [],
      commoditiesDialogVisible: false,
      dialogTableLoading: true,
      count: 0
    }
  },
  created() {
    this.getData()
  },
  methods: {
    getData() {
      const that = this
      this.axios.get('/order/user')
          .then((response) => {
            if (response.state === 1) {
              that.UserOrderList = response.data.UserOrderList
              that.OrderCommodityList = response.data.OrderCommodityList
              for (const element of that.UserOrderList) {
                element.paymentAmount = element.paymentAmount.toFixed(2)
                if (element.couponInfo === null) {
                  element.couponInfo = '无'
                } else {
                  const lowestAmount = element.couponInfo.substring(0, element.couponInfo.indexOf('/'))
                  const reductionAmount = element.couponInfo.substring(element.couponInfo.indexOf('/') + 1)
                  element.couponInfo = '满' + lowestAmount + '减' + reductionAmount
                }
              }
              that.loading = false
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
    },

    
    getAllOrdersCommoditiesInfo() {
      const that = this
      let request = []
      let count = 0
      for (const orderCommodity of this.OrderCommodityList) {
        request.push(this.axios.get('/commodity/query', {
          params: {
            commodityId: orderCommodity.commodityId
          }
        }))
      }
      Promise.all(request)
          .then((response) => {
            for (const responseElement of response) {
              if (responseElement.state === 1) {
                that.OrderCommodityList[count].name = responseElement.data.name
              } else {
                ElMessage({
                  type: "error",
                  message: responseElement.message
                })
              }
              count++
            }
          })
          .catch((error) => {
            console.log(error)
          })
    },
    async getCommoditiesInfo(id) {
      this.currentOrderCommoditiesList = []
      const that = this
      // console.log('OrderCommodityList')
      // console.log(that.OrderCommodityList)
      for (const orderCommodities of this.OrderCommodityList) {
        // console.log('orderCommodities')
        // console.log(orderCommodities[0])
        if (orderCommodities[0].orderId === id) {
          that.count += orderCommodities.length
          for (const commodity of orderCommodities) {
            if (typeof(commodity.originalPrice) === 'string') {
              that.currentOrderCommoditiesList.push(commodity)
              continue
            }
            this.axios.get('/commodity/query', {
              params: {
                commodityId: commodity.commodityId
              }
            }).then((response) => {
              if (response.state === 1) {
                // console.log('commodity')
                // console.log(commodity)
                const temp = commodity
                temp.commodityName = response.data.name
                temp.originalPrice = temp.originalPrice.toFixed(2)
                temp.actualPrice = temp.actualPrice.toFixed(2)
                that.currentOrderCommoditiesList.push(temp)
                that.count--
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
          }
        }
      }
      if (that.count === 0) {
        that.dialogTableLoading = false
        //console.log('没有监听')
      } else {
        //console.log('开始监听')
        const unwatch = that.$watch(() => that.count, (newVal) => {
              //console.log('newVal')
              //console.log(newVal)
              if (newVal === 0) {
                that.dialogTableLoading = false
                unwatch()
              }
            }
        )
        //console.log('监听成功')
      }
    },
    checkOrderCommodities(id) {
      this.commoditiesDialogVisible = true
      this.getCommoditiesInfo(id)
      // console.log(this.OrderCommodityList)
    },
    handleClose(done) {
      //this.currentOrderCommoditiesList = []
      done()
    }
  }
}
</script>

<script setup>

// 价格格式化
const formatPrice = (price) => {
  const num = Number(price) || 0
  return num.toFixed(2)
}

// 日期格式化 
const formatDateTime = (dateStr) => {
  
  return dateStr 
}
</script>

<template>
  <div class="container">
    <h1>订单详情</h1>
    <div class="table-wrapper">
      <el-empty v-if="UserOrderList.length === 0" description="您最近还没有新的订单！"
      :image-size="300"/>


     <template v-else>          
     <el-table
      :data="UserOrderList"
      class="modern-order-table"
      v-loading="loading"
      max-height="calc(100vh - 200px)"
      :default-sort="{ prop: 'time', order: 'descending' }"
  >
    <!-- 日期列 -->
    <el-table-column prop="time" label="日期" width="200" sortable>
      <template #header>
        <div class="table-header-cell">日期</div>
      </template>
      <template #default="{row}">
        <div class="date-cell">
          {{ formatDateTime(row.time) }}
        </div>
      </template>
    </el-table-column>

    <!-- 订单号列 -->
    <el-table-column prop="id" label="订单号" width="90">
      <template #header>
        <div class="table-header-cell">订单号</div>
      </template>
      <template #default="{row}">
        <div class="order-id">
          {{ row.id }}
        </div>
      </template>
    </el-table-column>

    <!-- 订单金额列 -->
    <el-table-column prop="paymentAmount" label="订单金额" width="160" sortable align="right">
      <template #header>
        <div class="table-header-cell">订单金额</div>
      </template>
      <template #default="{row}">
        <div class="amount-cell">
          ¥{{ formatPrice(row.paymentAmount) }}
        </div>
      </template>
    </el-table-column>

    <!-- 积分列 -->
    <el-table-column prop="rewardPoints" label="获得积分" width="150" align="right">
      <template #header>
        <div class="table-header-cell">获得积分</div>
      </template>
      <template #default="{row}">
        <div class="points-cell">
          <el-tag size="small" effect="light" type="warning">
            {{ row.rewardPoints }} 积分
          </el-tag>
        </div>
      </template>
    </el-table-column>

    <!-- 满减券列 -->
    <el-table-column prop="couponInfo" label="使用满减券" width="150" align="center">
      <template #header>
        <div class="table-header-cell">使用满减券</div>
      </template>
      <template #default="{row}">
        <div class="coupon-cell">
          <el-tag 
            :type="row.couponInfo === '无' ? 'info' : 'success'" 
            size="small"
            effect="plain"
            round
          >
            {{ row.couponInfo }}
          </el-tag>
        </div>
      </template>
    </el-table-column>

    <!-- 操作列 -->
    <el-table-column label="订单详情" width="150" align="center">
      <template #header>
        <div class="table-header-cell">操作</div>
      </template>
      <template #default="{row}">
        <el-button 
          type="primary" 
          link 
          size="small"
          @click="checkOrderCommodities(row.id)"
          class="detail-btn"
        >
          <span>查看详情</span>
        </el-button>
      </template>
    </el-table-column>
  </el-table>
  </template>
  </div>
  </div>

  
  <el-dialog v-model="commoditiesDialogVisible" center draggable :modal="false"
             title="订单商品详情" :before-close="handleClose" width="530px">
    <el-table :data="currentOrderCommoditiesList"
              class="commodity-table"
              v-loading="dialogTableLoading"
              max-height="calc(60vh - 2rem)"
              highlight-current-row
              stripe
              lazy
              :default-sort="{ prop: 'originalPrice', order: 'descending' }"
    >
      <!-- 上行这里应该有一个函数，传入UserId -->
      <el-table-column prop="commodityId" label="商品号" width="80"/>
      <el-table-column prop="commodityName" label="名称" width="110" align="center"/>
      <el-table-column prop="quantity" label="数量" width="90" sortable align="center"/>
      <el-table-column prop="originalPrice"
                       label="原价" width="100" align="center" sortable/>
      <el-table-column prop="actualPrice" label="折扣价" width="100" align="center"/>
    </el-table>
  </el-dialog>
</template>


<style scoped>

h1 {
  text-align: center;
}


.container {
  max-width: 1200px; 
  margin: 0 auto;
  padding: 20px;
}

.table-wrapper {
  display: flex;
  justify-content: center;
  width: 100%;
  margin-top: 20px;
}

.commodity-table {
  max-width: 480px;
  margin: auto;
}

/* 表格整体样式 */
.modern-order-table {
  --el-table-border-color: rgba(229, 231, 235, 0.5);
  --el-table-header-bg-color: #f8fafc;
  --el-table-row-hover-bg-color: #f1f5f9;
  --el-table-current-row-bg-color: #e0f2fe;
  max-width: 900px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.05);
}

/* 表头样式 */
:deep(.modern-order-table .el-table__header th) {
  background-color: #f8fafc;
  height: 52px;
}

.table-header-cell {
  font-weight: 600;
  color: #334155;
  font-size: 14px;
}

/* 单元格通用样式 */
:deep(.modern-order-table .el-table__cell) {
  padding: 15px 0;

}

/* 斑马纹样式 */
:deep(.modern-order-table .el-table__body tr.el-table__row--striped td) {
  background-color: #f8fafc;
}

/* 日期单元格 */
.date-cell {
  color: #64748b;
  font-size: 15px;
  font-weight: 500;
}

/* 订单号单元格 */
.order-id {
  font-family: 'Roboto Mono', monospace;
  color: #475569;
  font-size: 15px;
  font-weight: 700;
}

/* 金额单元格 */
.amount-cell {
  font-family: 'Roboto', sans-serif;
  font-weight: 500;
  color: #cf0e3e;
  font-size: 15px;
}

/* 积分单元格 */
.points-cell {
  display: flex;
  justify-content: flex-end;
}

/* 满减券单元格 */
.coupon-cell {
  display: flex;
  justify-content: center;
}

/* 详情按钮 */
.detail-btn {
  transition: all 0.2s ease;
}

.detail-btn:hover {
  color: var(--el-color-primary);
  transform: translateX(2px);
}

/* 空状态样式 */
:deep(.modern-order-table .el-table__empty-block) {
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f8fafc;
  border-radius: 0 0 12px 12px;
}

/* 加载状态样式 */
:deep(.modern-order-table .el-loading-mask) {
  border-radius: 12px;
  background-color: rgba(248, 250, 252, 0.8);
}
</style>
