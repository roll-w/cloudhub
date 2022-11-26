<template>
  <!--在根组件下跳转至登录界面或者主页 -->
  <router-view/>
</template>

<script>
//根目录引用bootstrap
import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap/dist/js/bootstrap';

export default {
  name: 'App',
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
