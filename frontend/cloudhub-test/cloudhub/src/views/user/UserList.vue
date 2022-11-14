<template>
  <ContentBase>
    <button type="button" class="btn btn-outline-primary" data-bs-toggle="modal" data-bs-target="#adduser">
      +新增角色
    </button>
    <ModalAddUser></ModalAddUser>

    <hr>
    <table class="table table-hover" style="text-align: center">
      <thead class="table-light">
      <tr>
        <th scope="col">角色ID</th>
        <th scope="col">用户名</th>
        <th scope="col">用户密码</th>
        <th scope="col">用户文件</th>
        <th scope="col">操作</th>
      </tr>
      </thead>

      <tbody>

      <tr v-for="user in user" :key="user.id">
        <td>{{ user.id }}</td>
        <td>{{ user.name }}</td>
        <td>{{ user.password }}</td>
        <td>
          <div class="btn-group" role="group" aria-label="Basic outlined example">
            <button type="button" class="btn btn-primary">查看</button>
          </div>
        </td>
        <td>
          <div class="btn-group" role="group" aria-label="Basic outlined example">
            <button type="button" class="btn btn-secondary">编辑</button>
          </div>
        </td>
      </tr>

      </tbody>
    </table>
  </ContentBase>
</template>

<script>
import ContentBase from "@/components/ContentBase";
import ModalAddUser from "@/components/modal/ModalAddUser";

export default {
  name: "UserList",
  components: {
    ContentBase,
    ModalAddUser
  },
  setup() {
    //  用户列表
    let user = [
      {
        id: "1",
        name: "one",
        password: "123456"
      },
      {
        id: "2",
        name: "two",
        password: "123456"
      },
      {
        id: "3",
        name: "three",
        password: "123456"
      },
      {
        id: "4",
        name: "four",
        password: "123456"
      }
    ]
    const addUser = () => {

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
