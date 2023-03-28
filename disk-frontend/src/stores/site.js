import {defineStore} from 'pinia'

export const useSiteStore = defineStore('site', {
    state: () => ({
        locale: "",
        dark: false
    }),
    getters: {
        isDark: state => state.dark,
        getLocale: state => state.locale
    },
    actions: {
        /**
         * @param locale {string}
         */
        setLocale(locale) {
            this.locale = locale
        },
        /**
         * @param dark {boolean}
         */
        setDark(dark) {
            this.dark = dark
        },

        toggleTheme() {
            this.dark = !this.dark
        }
    }
})