<template>
  <!--在根组件下跳转至登录界面或者主页 -->
  <router-view/>
</template>

<script>
//根目录引用bootstrap
import 'bootstrap/dist/css/bootstrap.min.css';
import 'bootstrap/dist/js/bootstrap.min';

export default {
  name: 'App',
  created () {
    if (sessionStorage.getItem("store") ) {
      this.$store.replaceState(Object.assign({},
          this.$store.state,JSON.parse(sessionStorage.getItem("store"))))
    }

    window.addEventListener("beforeunload",()=>{
      sessionStorage.setItem("store",JSON.stringify(this.$store.state))
    })
  }
}
</script>


<style>
body {
  background-color: #f3f4f7;
}
th,td{
  text-align: center;
}
.div-right-float {
  float: right;
}
</style>
