// main.ts
import { createApp } from 'vue'
import App from './app.vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import Antd from 'ant-design-vue'
import 'ant-design-vue/dist/reset.css'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import router from './router'


const pinia = createPinia()
  .use(piniaPluginPersistedstate)

const app = createApp(App)
  .use(Antd)
  .use(ElementPlus)
  .use(router)
  .use(pinia)
  .mount('#app')
