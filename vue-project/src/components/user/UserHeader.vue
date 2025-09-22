<template>
    <div class="header_top">
      <div class="content">超市会员系统</div>
      <div style="flex: 1"></div> <!-- 显示在最右边 -->
      <div v-if="userInfo.user" style="display: inline-flex;">
      <div class="user">
         <img src="/photo.png">
        <div style="width: 10px"></div>
        <div class="my-content">
          尊敬的{{ level }}会员，{{ userInfo.user.name }}
        </div>
      </div>

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
import {userInfo} from "../../Info/userInfo";
import {useRouter} from 'vue-router'
import {computed, onBeforeMount, ref} from "vue";


//整活音乐播放器
const isPlaying = ref(false);
const audio = ref(null);

const router = useRouter()

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


onBeforeMount(() => {
  if (userInfo.value.user === null || userInfo.value.membershipLevel === null) {
    userInfo.value.user = JSON.parse(sessionStorage.getItem("user"))
    userInfo.value.membershipLevel = JSON.parse(sessionStorage.getItem("membership_level_all"))
  }
})

window.addEventListener("beforeunload", () => {
  sessionStorage.setItem("user", JSON.stringify(userInfo.value.user))
  sessionStorage.setItem("membership_level_all", JSON.stringify(userInfo.value.membershipLevel))
})


const level = computed(() => {
  const membershipLevel = JSON.parse(sessionStorage.getItem("membership_level_all"))
  return membershipLevel[userInfo.value.user.level - 1 ].name
})

function logOut() {
  router.push({
    name: "UserLogin"
  })
}


</script>

<style scoped>
.content {
  font-size: 3rem;
  font-weight: bold;
  color: #faf8f8;
}

.user {
  display: flex;
  margin-right: 1rem;
  height: auto;
  width: auto;
}

.logout {
  margin-right: 1rem;
}

.header_top {
  height: 100%;
  width: 100%;
  display: flex;
  align-items: center;
}

.el-switch {
  height: auto;
  align-items: center;
}

img {
  margin-top: 5px;
  width: 50px;
  height: 50px;
  border-radius: 100%;
  background-color: rgb(12, 177, 0);
}

div .my-content {
  text-align: center;
  line-height: 60px;
  font-weight: normal;
}

.my-switch {
  --el-switch-on-color: #424242;
}
</style>