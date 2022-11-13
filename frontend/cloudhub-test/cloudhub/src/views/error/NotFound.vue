<template>
<!--  <ContentBase >-->
    <div>
      <img :src="error_Img" class="rounded mx-auto d-block">
    </div>
<!--  </ContentBase>-->
</template>

<script>
// import ContentBase from "@/components/ContentBase";
import error_Img from "../../assets/images/404.gif"

export default {
  name: "NotFound",
  components: {
    // ContentBase
  },
  data() {
    return {
      error_Img
    }
  }

}
</script>

<style scoped>

</style>