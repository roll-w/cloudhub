import {defineStore} from 'pinia'

export const tokenKey = window.btoa("L2w9t0k3n")
export const userKey = "user"
export const userDataKey = "user_data"

export const useUserStore = defineStore('user', {
    state: () => ({
        /** @type {{ username: string, id: number, role: string }} */
        user: {},
        /** @type {string} */
        token: "",
        /** @type {{ avatar: string, nickname: string, setup: boolean }} */
        userData: {},
        remember: false,
    }),
    getters: {
        /** @return {boolean} */
        isLogin: state => state.token !== "",
        /** @return {{ username: string, id: number, role: string }} */
        getUser: state => state.user,
        /** @return {string} */
        getToken: state => state.token,

        getUserData: state => state.userData,

        canAccessAdmin: state => state.user.role && state.user.role !== "USER",
    },
    actions: {
        /**
         * @param {{ username: string, id: number, role: string }} user
         * @param {string} token
         * @param {boolean} remember
         */
        loginUser(user, token, remember) {
            this.user = user
            this.token = token
            this.remember = remember
        },

        logout() {
            this.user = {}
            this.token = ""
            this.userData = {}
            localStorage.removeItem(tokenKey)
            localStorage.removeItem(userKey)
            localStorage.removeItem(userDataKey)

            sessionStorage.removeItem(tokenKey)
            sessionStorage.removeItem(userKey)
            sessionStorage.removeItem(userDataKey)
        },

        /**
         * @param {{ avatar: string, nickname: string }} userData
         */
        setUserData(userData) {
            this.userData = userData
        }
    }
})
