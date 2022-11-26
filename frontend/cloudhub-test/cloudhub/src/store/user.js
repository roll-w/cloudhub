import $ from 'jquery';
import url from '@/store/api'
import router from "@/router";
export default {
    state: {
        id: "",
        username: "",
        email: "",
        role: "",
        is_login: false,
    },
    getters: {},
    mutations: {
        updateUser(state, user) {
            state.id = user.id;
            state.username = user.username;
            state.email = user.email;
            state.role = user.role;
            state.is_login = user.is_login;
        },
    },

    actions: {

        login(context, data) {
            $.ajax({
                url: url.url_login,
                type: "post",
                contentType: "application/json;charset=UTF-8",
                data: JSON.stringify({
                    username: data.username,
                    password: data.password,
                }),
                xhrFields: {
                    withCredentials: true
                },
                crossDomain: true,
                success(resp) {
                    if (resp.errorCode === "00000") {
                        console.log(resp.data)
                        context.commit("updateUser", {
                            ...resp.data,
                            is_login: true,
                        })
                        data.success(resp);
                    } else {
                        data.error(resp);
                    }
                },
                error(resp) {
                    data.error(resp);
                }
            });
        },
        logout() {
            $.ajax({
                url:url.url_logout,
                type:"post",
                xhrFields: {
                    withCredentials: true // 携带跨域cookie  //单个设置
                },
                crossDomain:true,
                success(){
                    router.push({name:"login_index"});
                },
                error() {
                    console.log("退出失败");
                }
            })
        }
    },

    modules: {}
}