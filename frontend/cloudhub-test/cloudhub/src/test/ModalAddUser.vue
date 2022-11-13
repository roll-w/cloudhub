<template>
  <!-- Modal -->
  <div class="modal fade" id="adduser" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="exampleModalLabel">配置用户</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <div class="mb-3">
            <label for="username" class="form-label">用户名:</label>

            <!-- 用户名 -->
            <input v-model.lazy="user.username" type="text" class="form-control" id="username"
                   placeholder="please input username" required>
          </div>
          <div class="mb-3">
            <label for="password" class="form-label">密码:</label>

            <!-- 密码 -->
            <input v-model="user.password" type="password" class="form-control" id="password"
                   placeholder="please input password">
          </div>
          <div class="mb-3">
            <label for="exampleFormControlTextarea1" class="form-label">权限:</label>

            <!-- 权限 -->
            <div class="form-check">
              <input v-model="user.authority" value="normal" class="form-check-input" type="radio"
                     name="flexRadioDefault"
                     id="normal">
              <label class="form-check-label" for="normal">
                普通用户
              </label>
            </div>
            <div class="form-check">
              <input v-model="user.authority" value="root" class="form-check-input" type="radio" name="flexRadioDefault"
                     id="root">
              <label class="form-check-label" for="root">
                管理员
              </label>
            </div>
          </div>

        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
          <button type="submit" class="btn btn-primary" @click="addUser">添加</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

export default {
  name: "ModalAddUser",
  setup() {

    const user = {
      username: "",
      password: "",
      authority: "" // 值为normal时为普通用户; 值为root时为管理员
    }



    // 添加用户的方法
    const addUser = () => {
      console.log(user) // 在控制台打印表单信息

      if(user.username === "" || user.password === "" || user.authority === ""){
        console.log("表单存在空值");
      }
      else{
        console.log("表单正常");
      }

    }

    return {
      user,
      addUser
    }
  }
}
</script>

<style scoped>

</style>