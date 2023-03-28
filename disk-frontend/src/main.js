import { createApp } from 'vue'
import naive from "naive-ui";
import { createPinia } from 'pinia'

import App from './App.vue'
import router, {login} from './router'

import './assets/main.css'
import {useUserStore} from "@/stores/user";
import {createAxios} from "@/request/axios_config";

const app = createApp(App)

app.use(naive)
app.use(createPinia())
app.use(router)

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
