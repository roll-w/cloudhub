import { createApp } from 'vue'
import naive from "naive-ui";
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

import './assets/main.css'

const app = createApp(App)

app.use(naive)
app.use(createPinia())
app.use(router)

const meta = document.createElement('meta')
meta.name = 'naive-ui-style'
document.head.appendChild(meta)


app.mount('#app')
