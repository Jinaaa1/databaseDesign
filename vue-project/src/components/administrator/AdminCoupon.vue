<script setup>
import axiosT from "../../utils/requestT";
import {ref} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import { Search } from '@element-plus/icons-vue'


const couponsData = ref([])
const loading = ref(true)
const searchCoupondData = ref([])

axiosT.get('/coupon/all', null, (data) => {
  couponsData.value = data
  searchCoupondData.value = couponsData.value
  loading.value = false
})

const addCouponVisible = ref(false)
const addCouponData = ref({
  type: null,
  reductionAmount: 0,
  lowestAmount: 0,
  cost: 0
})

function addCoupon() {
  addCouponData.value.reductionAmount = 0
  addCouponData.value.lowestAmount = 0
  addCouponData.value.cost = 0
  addCouponVisible.value = true
}

function submitAddCoupon() {
  axiosT.post('/coupon/add', {
    type: null,
    reductionAmount: addCouponData.value.reductionAmount,
    lowestAmount: addCouponData.value.lowestAmount,
    cost: addCouponData.value.cost,
  }, (data) => {
    couponsData.value.push({
      type: data,
      reductionAmount: addCouponData.value.reductionAmount,
      lowestAmount: addCouponData.value.lowestAmount,
      cost: addCouponData.value.cost
    })
    ElMessage.success('添加满减券成功')
  })
  addCouponVisible.value = false
}

function deleteCoupon(type) {
  ElMessageBox.confirm(
      '确认删除该满减券？',
      '警告',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }
  ).then(() => {
    axiosT.get('/coupon/delete', {
      params: {
        couponId: type
      }
    }, () => {
      for (let i = 0; i < couponsData.value.length; i++) {
        if (couponsData.value[i].type === type) {
          couponsData.value.splice(i, 1)
          break
        }
      }
      ElMessage.success('删除成功')
    })
  }).catch(() => {
    ElMessage.info('取消删除')
  })
}

const searchInput = ref('')

function findCoupon(){
  console.log("searchInput:", searchInput.value)
  

  if (searchInput.value === '') {
        searchCoupondData.value = couponsData.value
        return;
    }

    const searchTerm = searchInput.value.trim();
  
  // 执行搜索过滤
  searchCoupondData.value = couponsData.value.filter(coupon => {
    // 1. 匹配优惠券ID（精确匹配）
    if (coupon.id === parseInt(searchTerm)) {
      console.log("1", searchCoupondData.value)
      return true;
    }
    
    // 2. 匹配"满X减Y"格式
    if (searchTerm.match(/满\d+减\d+/)) {
      const match = searchTerm.match(/满(\d+)减(\d+)/);
      console.log("2", searchCoupondData.value)
      return (
        coupon.lowestAmount === parseInt(match[1]) && 
        coupon.reductionAmount === parseInt(match[2])
      );
    }
    
    // 3. 匹配"满X"格式
    if (searchTerm.match(/满\d+/)) {
      const match = searchTerm.match(/满(\d+)/);
      console.log("3", searchCoupondData.value)
      return coupon.lowestAmount === parseInt(match[1]);
    }
    
    // 4. 匹配"减Y"格式
    if (searchTerm.match(/减\d+/)) {
      const match = searchTerm.match(/减(\d+)/);
      console.log("4", searchCoupondData.value)
      return coupon.reductionAmount === parseInt(match[1]);
    }
    
    // 5. 模糊匹配优惠券名称
    if (coupon.name && coupon.name.includes(searchTerm)) {
      console.log("5", searchCoupondData.value)
      return true;
    }
    
    // 6. 匹配积分成本
    if (parseInt(searchTerm) === coupon.cost) {
      console.log("6", searchCoupondData.value)
      return true;
    }
    
    console.log("7", searchCoupondData.value)
    return false;
  });
  
}

</script>

<template>
  <div class="top-content">
    <el-button type="primary" @click="addCoupon" size="large" class="my-button">
      添加折扣券
    </el-button>
   折扣券一览
   <el-input class="search" v-model="searchInput" placeholder="输入满减券信息：满xx减xx" :prefix-icon="Search"
    @change="findCoupon" />
  </div>
  <br>
  <div class="my-scrollbar">
    <el-scrollbar class="scroll" v-loading="loading" height="calc(100% - 1rem)">
      <el-space wrap>
        <el-card
            v-for="i in searchCoupondData"
            :key="i.type"
            class="box-card"
        >
          <template #header>
            <div class="card-header">
              <span>
                满{{ i.lowestAmount }}减{{ i.reductionAmount }}
              </span>
              <el-button
                  class="my-el-button"
                  plain text
                  size="small"
                  @click="deleteCoupon(i.type)"
              >
                删除
              </el-button>
            </div>
          </template>
          <div class="card-content">
            {{ i.cost }}积分/张
          </div>
        </el-card>
      </el-space>
    </el-scrollbar>
  </div>
  <el-dialog v-model="addCouponVisible" center :modal="true"
             title="请输入满减券信息" width="300px">
    <el-form
        :model="addCouponData"
        label-position="right"
        label-width="80px"
    >
      <el-form-item label="使用价格" prop="lowestAmount">
        <el-input-number v-model="addCouponData.lowestAmount" :min="0" :precision="2"/>
      </el-form-item>
      <el-form-item label="减免价格" prop="reductionAmount">
        <el-input-number v-model="addCouponData.reductionAmount" :min="0" :precision="2"/>
      </el-form-item>
      <el-form-item label="花费积分" prop="cost">
        <el-input-number v-model="addCouponData.cost" :min="0"
                         :precision="0" :step="1" step-strictly/>
      </el-form-item>
      <el-form-item>
        <div style="width: 20px"/>
        <el-button type="primary" @click="submitAddCoupon">
          提交
        </el-button>
      </el-form-item>
    </el-form>
  </el-dialog>
</template>

<style scoped>
div.top-content {
  text-align: center;
  font-size: 2rem;
  margin: -10px auto auto auto;
  font-weight: bold;
}

.scroll {
  padding: 10px;
  border-radius: 10px;

}

.my-scrollbar {
  height: calc(100% - 2rem - 2px);
}

.my-el-button {
  font-size: 20px;
  padding: 0 5px;
  margin: 3px -5px 0 8px;
}

.box-card {
  background-image: linear-gradient(to bottom right,
   rgb(236, 72, 153), rgb(222, 141, 181), rgb(236, 72, 153)); 
}

.card-header {
  font-size: 1.5rem;
  font-weight: bolder;
  text-align: center;
}

.card-content {
  text-align: right;
  font-size: 1rem;
}


.my-button {
  float: left;
  margin-left: 12px;
  margin-right: -800px;
}

.search {
  min-width: 100px;
  max-width: 200px;
  margin-bottom: 10px;
  float: right;
}
</style>