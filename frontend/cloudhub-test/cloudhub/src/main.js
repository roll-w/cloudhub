import {createApp} from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import {mes} from './util/message'
import * as echarts from 'echarts'

const app = createApp(App)

mes()

app.config.globalProperties.$echarts = echarts // 全局挂载echarts
app.use(store).use(router).mount('#app')
