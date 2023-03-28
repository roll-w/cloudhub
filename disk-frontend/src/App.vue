<script setup>

import {ref} from "vue";
import {darkTheme, enUS, lightTheme, zhCN} from "naive-ui";
import TopNavBar from "@/components/TopNavBar.vue";
import {tokenKey, userDataKey, userKey, useUserStore} from "@/stores/user";
import router from "@/router";
import {useSiteStore} from "@/stores/site";
import MessageApi from "@/MessageApi.vue";


const userStore = useUserStore()

const prefix = "YWQ5ZWM0MGQ3N2FlNzA3OTE5OGUwZjMwNjNiNGJjNDNiZmM"

const encodeToken = (token) => {
  if (!token) {
    return null
  }

  return prefix + window.btoa(token)
}

const restoreToken = (token) => {
  if (!token) {
    return null
  }

  const removePrefix = token.substring(prefix.length)
  return window.atob(removePrefix)
}

const loadFromLocal = () => {
  const token = restoreToken(localStorage.getItem(tokenKey))
  const user = JSON.parse(localStorage.getItem(userKey))
  const userData = JSON.parse(localStorage.getItem(userDataKey))
  return {user, token, userData}
}

const loadFromSession = () => {
  const token = restoreToken(sessionStorage.getItem(tokenKey))

  const user = JSON.parse(sessionStorage.getItem(userKey))
  const userData = JSON.parse(sessionStorage.getItem(userDataKey))
  return {user, token, userData}
}

const local = loadFromLocal()
const session = loadFromSession()

const tryLoginFromState = () => {
  if (local.user && local.token) {
    userStore.loginUser(local.user, local.token, true)
    if (local.userData) {
      userStore.setUserData(local.userData)
    }
    return
  }

  if (session.user && session.token) {
    userStore.loginUser(session.user, session.token, false)
  }
  if (session.userData) {
    userStore.setUserData(session.userData)
  }
}
tryLoginFromState()

userStore.$subscribe((mutation, state) => {
  const now = new Date()
  console.log(now.toLocaleString() + ' ' + now.getMilliseconds() + ' ' + 'App.vue' + ' ' + 'subscribe', state)

  if (!state.remember) {
    sessionStorage.setItem(tokenKey, encodeToken(state.token))
    sessionStorage.setItem(userKey, JSON.stringify(state.user))
    sessionStorage.setItem(userDataKey, JSON.stringify(state.userData))
    return
  }
  localStorage.setItem(tokenKey, encodeToken(state.token))
  localStorage.setItem(userKey, JSON.stringify(state.user))
  localStorage.setItem(userDataKey, JSON.stringify(state.userData))
})


/**
 * @type import('naive-ui').GlobalThemeOverrides
 */
const themeOverrides = {
  "common": {
    "primaryColor": "#f5a737",
    "baseColor": "#F4F4F4FF",
    "primaryColorHover": "#f19155",
    "primaryColorPressed": "#f6a46a",
    "primaryColorSuppl": "#d08e5f",
    "warningColor": "#edc33b",
    "warningColorHover": "#fcba40",
    "warningColorPressed": "#c99e10",
    "warningColorSuppl": "#fcc040",
    "errorColor": "#D03050FF",
    "errorColorHover": "#DE5782FF",
    "errorColorPressed": "#AB1F39FF",
    "errorColorSuppl": "#E85364FF",
    // "infoColor": "#6530d0",
    // "infoColorHover": "#7957de",
    // "infoColorPressed": "#421fab",
    // "infoColorSuppl": "#536ce8",
    "fontFamily": `'Muli', '思源黑体', 'Source Han Sans',  -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen-Sans, Ubuntu, Cantarell, 'Helvetica Neue', sans-serif`,
    "fontSize": "16px",
    "fontSizeMini": "14px",
    "fontSizeTiny": "14px",
    "fontSizeSmall": "16px",
    "fontSizeMedium": "16px",
    "fontSizeLarge": "17px",
    "fontSizeHuge": "18px",
  },
}


const dark = ref(lightTheme)
const locale = ref({
  locale: zhCN
})

const extractLocale = (locale) => {
  if (!locale) {
    return zhCN
  }
  switch (locale) {
    case "zh-CN":
    case "zh-TW":
      return zhCN
    case "en-US":
      return enUS

  }
  return zhCN
}

const setupSiteState = (isDark, localeString) => {
  if (isDark) {
    dark.value = darkTheme
  } else {
    dark.value = lightTheme
  }
  locale.value.locale = extractLocale(localeString)
}


const siteStore = useSiteStore()

const loadSiteDataLocal = () => {
  let data = localStorage.getItem("site");
  if (!data) {
    return
  }
  const site = JSON.parse(data)
  siteStore.setLocale(site.locale)
  siteStore.setDark(site.dark)

  setupSiteState(site.dark, site.locale)
}

loadSiteDataLocal()

router.afterEach((to, from) => {
  loadSiteDataLocal()
})

siteStore.$subscribe((mutation, state) => {
  setupSiteState(state.dark, state.locale)
  localStorage.setItem("site", JSON.stringify(state))
})


</script>


<template>
  <n-config-provider :locale="locale.locale"
                     :theme="dark" :theme-overrides="themeOverrides" class="h-100">
    <n-loading-bar-provider>
      <n-message-provider>
        <message-api/>
        <n-notification-provider :max="5">
          <n-dialog-provider>
            <n-layout position="absolute">
              <TopNavBar/>
              <router-view/>
            </n-layout>
          </n-dialog-provider>
        </n-notification-provider>
      </n-message-provider>
    </n-loading-bar-provider>
    <n-global-style/>
  </n-config-provider>
</template>

<style scoped>
</style>
