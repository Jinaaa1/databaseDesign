<template>
  <div class="header-top">
    <div class="content">超市会员管理系统</div>
    <div style="flex: 1"></div>
    
    <div style="display: inline-flex;">
      <div style="margin: 15px 10px 15px 5px;">
            <el-button size="small" @click="playMusic">
                <el-icon><Headset /></el-icon>
            </el-button>
        </div>


      <div class="logout">
        <el-button type="danger" size="small" @click="logOut" style="margin-top: 17.8px">
          <el-icon><SwitchButton/></el-icon>
          <span> 注销</span>
        </el-button>
      </div>
    </div>
  
  
  </div>
</template>

<script setup>
import {useRouter} from 'vue-router';
import { ref } from "vue";

const router = useRouter()

//整活音乐播放器
const isPlaying = ref(false);
const audio = ref(null);

const playMusic = () => {
  if (!audio.value) {
    audio.value = new Audio('/ManBoNoMore.mp3');
  }
  
  if (isPlaying.value) {
    audio.value.pause();
  } else {
    audio.value.play()
      .catch(e => console.error('播放失败:', e));
  }
  
  isPlaying.value = !isPlaying.value;
};

function logOut() {
  router.push({
    name: "AdminLogin"
  })
}

</script>

<style scoped>
.content {
  font-size: 2rem;
  font-weight: bold;
}

.header-top {
  height: 100%;
  width: 100%;
  display: flex;
  align-items: center;
}

.logout {
  margin-right: 1rem;
}


</style>