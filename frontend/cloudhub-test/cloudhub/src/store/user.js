import $ from 'jquery';
import url from '@/store/api'

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
        logout(state) {
            state.id = "";
            state.username = "";
            state.role = "";
            state.is_login = false;
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
                        // console.log(resp)
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
        //获取用户信息
        // getUserInfo(context,data){
        //     $.ajax({
        //         url:url.url_getUserInfo,
        //         type:"get",
        //         success(resp){
        //             if (resp.message === "SUCCESS"){
        //
        //                 context.commit("updateUser",{
        //                     ...resp,
        //                     is_login: true,
        //                 });
        //                 data.success(resp);
        //             } else {
        //                 data.error(resp);
        //             }
        //         },
        //         error(resp){
        //             data.error(resp);
        //         }
        //     });
        // },
        logout(context) {
            context.commit("logout");
        }
    },

    modules: {}
}