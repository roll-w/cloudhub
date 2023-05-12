import axios from 'axios'
import {useUserStore} from "@/stores/user";
import {useRouter} from "vue-router";

axios.defaults.withCredentials = true

export function createConfig(isJson = false) {
    const userStore = useUserStore()
    const config = {
        headers: {}
    }
    if (userStore.isLogin) {
        config.headers["Authorization"] = userStore.token
    }
    if (isJson) {
        config.headers["Content-Type"] = "application/json"
    }
    return config
}

const tokenErrorCodes = [
    'A1001',
    'A1002'
]

export function createAxios(onLoginExpired = () => {
}) {
    const instance = axios.create({
        withCredentials: true,
    })
    instance.interceptors.response.use(
        response => {
            console.log(response)
            if (response.data.errorCode !== '00000') {
                return Promise.reject(response.data)
            }
            return response.data
        }, error => {
            if (axios.isCancel(error)) {
                return Promise.reject({
                    tip: '请求被取消',
                    message: '请求被取消',
                    errorCode: 'CANCEL',
                    status: 500
                })
            }

            console.log(error)
            if (!error.response) {
                return Promise.reject({
                    tip: '网络错误',
                    message: '网络错误',
                    errorCode: 'D0000',
                    status: 500
                })
            }

            if (isInTokenError(
                error.response.data.errorCode || '00000')) {
                onLoginExpired()
                return Promise.reject({
                    tip: '登录过期或登录凭据无效，请重新登录',
                    message: '登录过期或登录凭据无效，请重新登录',
                    errorCode: error.response.data.errorCode,
                    status: 401
                })
            }
            return Promise.reject(error.response.data)

        }
    )
    return instance
}

function isInTokenError(errorCode = '00000') {
    return tokenErrorCodes.includes(errorCode)
}

