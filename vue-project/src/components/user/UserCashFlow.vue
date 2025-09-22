<script>
import {ElMessage} from "element-plus";

export default {
  name: 'UserBillDetails',
  created() {
    this.getBillDetailsData()
  },
  methods: {
    //监听 pagesize 改变的事件
    size_change(newSize) {
      this.pageSize = newSize
    },
    //监听 页码值 改变的事件
    current_change(newPage) {
      this.currentPage = newPage
    },
    getBillDetailsData() {
      const that = this

      function getRechargeRecords() {
        return that.axios.get('/charge/user')
      }

      function getUserOrders() {
        return that.axios.get('/order/user')
      }

      function getRedeemRecords() {
        return that.axios.get('/redeem/user')
      }

      Promise.all([getRechargeRecords(), getUserOrders(), getRedeemRecords()])
          .then((response) => {
            if (response[0].state === 1) {
              if (response[1].state === 1) {
                if (response[2].state === 1) {
                  for (const rechargeRecord of response[0].data) {
                    const temp = {
                      date: rechargeRecord.time,
                      balanceChange: '+' + rechargeRecord.amount.toFixed(2),
                      pointChange: '—',
                      id: rechargeRecord.id,
                      type: '充值'
                    }
                    that.billDetailsData.push(temp)
                  }
                  for (const userOrder of response[1].data.UserOrderList) {
                    const temp = {
                      date: userOrder.time,
                      balanceChange: '-' + userOrder.paymentAmount.toFixed(2),
                      pointChange: '+' + userOrder.rewardPoints,
                      id: userOrder.id,
                      type: '购物订单'
                    }
                    that.billDetailsData.push(temp)
                  }
                  for (const redeemRecord of response[2].data) {
                    const temp = {
                      date: redeemRecord.time,
                      balanceChange: '—',
                      pointChange: '-' + redeemRecord.value,
                      id: redeemRecord.id,
                      type: '满减券兑换'
                    }
                    that.billDetailsData.push(temp)
                  }
                  that.billDetailsData.sort(function (x, y) {
                    if (x.date < y.date) {
                      return 1
                    } else if (x.date > y.date) {
                      return -1
                    } else {
                      return 0
                    }
                  })
                  that.loading = false
                } else {
                  ElMessage.error(response[2].message)
                }
              } else {
                ElMessage.error(response[1].message)
              }
            } else {
              ElMessage.error(response[0].message)
            }
          })
          .catch((error) => {
            console.log(error)
          })
    }
  },
  data() {
    return {
      billDetailsData: [],
      loading: true,
      pageSize: 7,//每页显示条目个数
      currentPage: 1,//当前页数
    }
  },
  computed: {
    currentBillDetailsData() {
      return this.billDetailsData.slice(
          (this.currentPage - 1) * this.pageSize,
          this.currentPage * this.pageSize
      )
    }
  }
}

</script>

<template>
  <div class="container">
    <div class="header">
      <h1 class="title">流水查询</h1>
    </div>
    
    <div class="wrapper" v-loading="loading">
      <el-empty 
        v-if="currentBillDetailsData.length === 0" 
        description="您最近还没有消费记录"
        :image-size="150"
      />

      <template v-else>          
        <el-card class="table-card">
          <el-table 
            :data="currentBillDetailsData"
            stripe
            class="table"
            :header-cell-style="{ 
              background: '#f8f9fa', 
              color: '#495057',
              fontWeight: '500'
            }"
          >
            <el-table-column prop="date" label="日期" width="180">
              <template #default="scope">
                <span class="date-text">{{ scope.row.date }}</span>
              </template>
            </el-table-column>
            
            <el-table-column label="余额变动" width="120" align="center">
              <template #default="scope">
                <span class="amount" :class="{
                  'text-green': scope.row.balanceChange.startsWith('+'),
                  'text-red': scope.row.balanceChange.startsWith('-'),
                  'text-gray': scope.row.balanceChange === '—'
                }">
                  {{ scope.row.balanceChange }}
                </span>
              </template>
            </el-table-column>
            
            <el-table-column label="积分变动" width="120" align="center">
              <template #default="scope">
                <span class="points" :class="{
                  'text-green': scope.row.pointChange.startsWith('+'),
                  'text-red': scope.row.pointChange.startsWith('-'),
                  'text-gray': scope.row.pointChange === '—'
                }">
                  {{ scope.row.pointChange }}
                </span>
              </template>
            </el-table-column>
            
            <el-table-column prop="id" label="事项单号" width="120">
              <template #default="scope">
                <span class="id-text">{{ scope.row.id }}</span>
              </template>
            </el-table-column>
            
            <el-table-column prop="type" label="事项备注" width="120">
              <template #default="scope">
                <span class="type-text">{{ scope.row.type }}</span>
              </template>
            </el-table-column>
          </el-table>
          
          <el-pagination
            @size-change="size_change"
            @current-change="current_change"
            :current-page="currentPage"
            :page-size="pageSize"
            :page-sizes="[5, 7, 10, 20]"
            layout="total, sizes, prev, pager, next, jumper"
            :total="billDetailsData.length"
            class="pagination"
          />
        </el-card>
      </template>
    </div>
  </div>
</template>

<style scoped>
.container {
  padding: 20px;
  min-height: 100vh;
}

.header {
  text-align: center;
  margin-bottom: 24px;
}

.title {
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

.wrapper {
  max-width: 700px;
  margin: 0 auto;
}

.table-card {
  border: 1px solid #e9ecef;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.table {
  width: 100%;
}

.table :deep(.el-table__row) {
  height: 48px;
}

.table :deep(.el-table__row:hover td) {
  background-color: #f1f3f5 !important;
}

.date-text {
  font-size: 15px;
  font-weight: 500;
  color: #495057;
}

.amount, .points {
  font-family: 'Roboto', sans-serif;
  font-weight: 500;;
  font-size: 15px;
}

.text-green {
  color: #28a745;
}

.text-red {
  color: #cf0e3e;
}

.text-gray {
  color: #6c757d;
}

.id-text {
  font-family: 'Roboto Mono', monospace;
  color: #475569;
  font-size: 15px;
  font-weight: 500;
}

.type-text {
  font-size: 15px;
  font-weight: 400;
  color: #495057;
}

.pagination {
  margin-top: 16px;
  padding: 16px;
  border-top: 1px solid #e9ecef;
}

:deep(.el-pagination) {
  justify-content: center;
}

:deep(.el-pagination .btn-prev),
:deep(.el-pagination .btn-next),
:deep(.el-pagination .el-pager li) {
  border-radius: 4px;
  margin: 0 2px;
}

:deep(.el-pagination .el-pager li.active) {
  background-color: #007bff;
  color: white;
}

@media (max-width: 768px) {
  .container {
    padding: 16px;
  }
  
  .wrapper {
    max-width: 100%;
  }
  
  .title {
    font-size: 20px;
  }
}
</style>