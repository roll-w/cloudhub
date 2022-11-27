import $ from 'jquery';
import url from '@/store/api'
export default {
    state: {
        meta:{
            cpu:{

            },
            jvm:{

            },
            //    此处省略拿net举例
            net:{
                recv:"",
                sent:"",
                speed:"",
            }
        }

    },
    getters: {},
    mutations: {
        updateNet(state, meta){
            state.meta.net.recv = meta.net.recv
            state.meta.net.sent = meta.net.sent
            state.meta.net.speed = meta.net.speed
        },
    },

    actions: {
         getNet(context,data){
            $.ajax({
                url:url.url_metaServer,
                type:"GET",
                data:{
                    serverId:"meta"
                },
                xhrFields: {
                    withCredentials: true
                },
                crossDomain: true,
                success(resp){
                    if (resp.errorCode === "00000"){
                        context.commit("updateNet",{
                            ...resp.data.net
                        })
                        console.log(resp);
                    }
                },
                error(){
                    console.log("获取失败")
                }
            })
        }
    },

    modules: {}
}