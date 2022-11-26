<template>
  <main>
    <NavBar/>
    <div class="d-flex flex-column p-4 flex-grow-1 allow-rolling">
      <router-view/>
      <FooterView/>
    </div>

  </main>
</template>

<script>
import NavBar from "@/components/common/NavBar";
import FooterView from "@/components/common/Footer";
export default {
  name: "HomeView",
  components:{
    FooterView,
    NavBar,
  }
}
</script>

<style scoped>
body{
  min-height: 100vh;
}

th,td{
  text-align: center;
}
.div-right-float {
  float: right;
}

main{
  display: flex;
  flex-wrap: nowrap;
  width: auto;
  height: 100vh;
  max-height: 100vh;
  overflow-x: auto;
  overflow-y: hidden
}

.allow-rolling{
  overflow: auto;
}

/* 由于作为根组件的APP中的元素 position有值 fixed,因此为使得对话框正常显示需要添加此CSS语句  */
/*.modal-backdrop {*/
/*  z-index: -1;*/
/*}*/
</style>

