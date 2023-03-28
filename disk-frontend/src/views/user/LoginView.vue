<template>
  <div class="center">
    <n-card>
      <LoginForm v-if="isLoginPage()"/>
      <RegisterForm v-else/>
    </n-card>
  </div>
</template>

<script setup>
import RegisterForm from "@/components/user/login/RegisterForm.vue";
import LoginForm from "@/components/user/login/LoginForm.vue";
import {useRouter} from "vue-router";
import {useUserStore} from "@/stores/user";
import {index, login} from "@/router";
import {getCurrentInstance} from "vue";

const router = useRouter()
const userStore = useUserStore()

const checkLogin = () => {
  if (userStore.isLogin) {
    router.push({
      name: index
    })
  }
}

checkLogin()

const isLoginPage = () => {
  return router.currentRoute.value.name === login
}

const code = `public class LampBlog {
  public static void main(String[] args) {
    System.out.println("Hello, Lamp Blog!");
  }
}`
</script>

<style scoped>
.center {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 64px);
  justify-content: center;
  position: relative;
}
</style>
