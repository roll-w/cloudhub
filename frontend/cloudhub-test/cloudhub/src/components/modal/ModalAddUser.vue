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

          <form>
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
              <div>
                权限:
              </div>
              <el-radio-group v-model="user.authority" size="large">
                <el-radio-button label="普通用户"/>
                <el-radio-button label="管理员"/>
              </el-radio-group>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
              <button type="button" class="btn btn-primary" @click="addUser">添加</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {ElRadioButton, ElRadioGroup} from 'element-plus'
import {ref} from 'vue'

export default {
  name: "ModalAddUser",
  components: {
    ElRadioButton,
    ElRadioGroup
  },
  setup() {

    const user = ref({
      username: "",
      password: "",
      authority: "" // 值为normal时为普通用户; 值为root时为管理员
    })


    // 添加用户的方法
    const addUser = () => {
      console.log(user.value) // 在控制台打印表单信息

      if (user.value.username === "" || user.value.password === "" || user.value.authority === "") {
        console.log("表单存在空值");
      } else {
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