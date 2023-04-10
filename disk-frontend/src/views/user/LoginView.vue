<template>
    <div class="bg-img h-100vh w-100 bg-no-repeat bg-cover bg-center">
        <div class="h-100 w-100 backdrop-saturate-150">
            <div class="w-100 h-100 px-10 py-5">
                <div class="pt-16 m-auto justify-items-center" style="max-width: 768px;">
                    <n-card>
                        <LoginForm v-if="isLoginPage()"/>
                        <RegisterForm v-else/>
                    </n-card>
                </div>
            </div>
        </div>
    </div>

</template>

<script setup>
import RegisterForm from "@/components/user/login/RegisterForm.vue";
import LoginForm from "@/components/user/login/LoginForm.vue";
import {useRouter} from "vue-router";
import {useUserStore} from "@/stores/user";
import {index, login} from "@/router";
import {getCurrentInstance} from "vue";
import Logo from "@/components/icon/Logo.vue";

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

</script>

<style scoped>
.center {
    display: flex;
    padding-right: 16px;
    padding-left: 16px;
    flex-direction: column;
    height: calc(100vh - 64px);
    justify-content: center;
    position: relative;
}

.bg-img {
    background-image: url('/public/img/open_mind.jpg');
}

</style>
