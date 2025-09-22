import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'  // Vue 3 的 Element Plus
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import VueAxios from 'vue-axios'
import service from './utils/requset'

const app = createApp(App)

/**
 * 引入vue-axios，一个对axios的轻度封装
 */
app.use(VueAxios, service)
app.provide('axios', app.config.globalProperties.axios)  // provide 'axios'

app.use(router)
app.use(ElementPlus)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')