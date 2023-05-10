import {createApp} from 'vue'
import {createPinia} from 'pinia'

import App from './App.vue'
import router, {login} from './router'

import './assets/main.css'
import {useUserStore} from "@/stores/user";
import {createAxios} from "@/request/axios_config";
import '@/util/string_ext'

import * as echarts from 'echarts'

const app = createApp(App)

app.config.globalProperties.$echarts = echarts

app.use(createPinia())
app.use(router)

const debug = import.meta.env.MODE === 'development'

if (!debug) {
    console.log = () => {
    }
}

const meta = document.createElement('meta')
meta.name = 'naive-ui-style'
document.head.appendChild(meta)

const onLoginExpired = () => {
    console.log('登录已过期，请重新登录')
    window.$message.error('登录已过期，请重新登录')
    const userStore = useUserStore()
    userStore.logout()

    router.push({
        name: login
    }).then((failure) => {
        console.log(failure)
    })
}

const axios = createAxios(onLoginExpired)
app.config.globalProperties.$axios = axios


app.mount('#app')
