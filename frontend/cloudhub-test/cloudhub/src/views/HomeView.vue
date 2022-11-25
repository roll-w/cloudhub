<template>
  <NavBar/>
  <router-view/>
</template>

<script>
import NavBar from "@/components/common/NavBar";

export default {
  name: "HomeView",
  components:{
    NavBar,
  }
}
</script>

<style scoped>

th,td{
  text-align: center;
}
.div-right-float {
  float: right;
}

/* 由于作为根组件的APP中的元素 position有值 fixed,因此为使得对话框正常显示需要添加此CSS语句  */
/*.modal-backdrop {*/
/*  z-index: -1;*/
/*}*/
</style>

