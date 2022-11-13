<template>
  <!--在根组件下跳转至登录界面或者主页 -->
  <router-view/>
</template>

<script>
//根目录引用bootstrap
import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap/dist/js/bootstrap';
import 'element-plus/dist/index.css'

export default {
  name: 'App',
}
</script>


<style>
body {
  background-image: url("@/assets/background/Img3.png");
  background-size: cover;
}


</style>
