<template>
  <div class="row justify-content-md-center">
    <div class="col-3">
      <form>
        <div class="mb-3">
          <label for="username" class="form-label">用户名</label>
          <input  type="text" class="form-control" id="username">
        </div>
        <div class="mb-3">
          <label for="password" class="form-label">密码</label>
          <input  type="password" class="form-control" id="password">
        </div>
        <div class="mb-3">
          <label for="password_confirm" class="form-label">确认密码</label>
          <input  type="password" class="form-control" id="password_confirm">
        </div>
        <button type="submit" class="btn btn-primary">注册</button>
      </form>
    </div>
  </div>
</template>

<script>
import ContentBase from "@/components/ContentBase";
export default {
  // eslint-disable-next-line vue/multi-word-component-names
  name: "Register",
  components:{
    // eslint-disable-next-line vue/no-unused-components
    ContentBase
  }
}
</script>

<style scoped>

</style>