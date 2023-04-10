<template>
    <div :style="{'background-image': 'url(https://naive-ui.oss-cn-beijing.aliyuncs.com/carousel-img/carousel4.jpeg)'}"
         class="bg-no-repeat bg-cover bg-center">

        <div class="backdrop-blur-sm">
            <div class="bg-gradient-to-b from-transparent to-gray-600">
                <div class="overscroll-contain container mx-auto my-auto px-10 py-5" style="max-width: 768px;">
                    <div class="justify-items-center">
                        <n-card >
                            <LoginForm v-if="isLoginPage()"/>
                            <RegisterForm v-else/>
                        </n-card>
                    </div>
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
</style>
