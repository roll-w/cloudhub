<template>
  <ContentBase>
    <h3 class="mb-4">用户信息</h3>
    <div class="d-flex flex-xl-row flex-grow-1">
      <button type="button" class="btn btn-outline-primary" data-bs-toggle="modal" data-bs-target="#addUser">
        创建用户
      </button>
      <div class="d-flex flex-fill justify-content-end">
        <form @submit.prevent="getUserByName">
          <div class="input-group">
            <input v-model="name" type="text" class="form-control"
                   placeholder="搜索用户......">
            <button class="btn btn-outline-primary" type="submit">查询</button>
          </div>
        </form>
      </div>
    </div>

    <hr>
    <table class="table table-hover text-center">
      <thead class="table-light">
      <tr>
        <th scope="col">id</th>
        <th scope="col">name</th>
        <th scope="col">email</th>
        <th scope="col">role</th>
        <th scope="col">操作</th>
      </tr>
      </thead>

      <tbody>

      <tr v-for="user in users" :key="user.id">
        <td>{{ user.id }}</td>
        <td>{{ user.username }}</td>
        <td>{{ user.email }}</td>
        <td>{{ user.role }}</td>

        <td>
          <div class="btn-group" role="group" aria-label="Basic outlined example">
            <button type="button"  class="btn btn-link" @click="deleteUserById(user)">删除</button>
            <button type="button"  class="btn btn-link">修改</button>
          </div>
        </td>
      </tr>
      </tbody>
    </table>
  </ContentBase>
</template>

<script>
import ContentBase from "@/components/common/ContentBase";
// import { Modal } from 'bootstrap/dist/js/bootstrap'
import {ref} from "vue";
import $ from 'jquery'
import url from "@/store/api";
export default {
  name: "UserList",
  components: {
    ContentBase,
  },
  setup() {
    let username = ref('');
    let users = ref([]);
    const addUser = () => {

    }

    const getUser = () => {
      $.ajax({
        url: url.url_getUser,
        type: "GET",
        xhrFields: {
          withCredentials: true
        },
        crossDomain:true,
        success(resp) {
          users.value = resp.data;

        },
        error(){
          console.log("User list acquisition failed！！！")
        }
      });
    };

    getUser();

    const deleteUserById = (user) =>{
      $.ajax({
        url: url.url_deleteUserById,
        xhrFields: {
          withCredentials: true
        },
        crossDomain:true,
        type: "POST",
        contentType: "application/x-www-form-urlencoded",
        data: {
          userId: user.id,
        },
        success(resp) {
          if (resp.errorCode === "00000"){
              getUser()
          }
        },
        error(resp){
          console.log(resp);
        }
      });
    };

    const getUserByName = () =>{
      $.ajax({
        url: url.url_getUserByName,
        xhrFields: {
          withCredentials: true
        },
        crossDomain:true,
        // jsonp: "callback",
        type: "GET",
        data: {
          username:username.value,
        },
        success(resp) {
          if (resp.errorCode === "00000"){
            // Modal.getInstance("#SearchUser").show();
            alert("ID: "+resp.data.id+" Email: "+resp.data.email+" Role: "+resp.data.role)
          }
        },
        error(resp){
          console.log(resp.data);
        }
      });
    }

    return {
      username,
      users,
      addUser,
      deleteUserById,
      getUserByName
    }
  }

}
</script>

<style scoped>
button {
  font-size: large;
  font-weight: normal;
  text-decoration: none;
}
</style>
