<template>
  <ContentBase>
    <button type="button"  class="btn btn-light btn-primary" disabled >用户信息</button>
    <form @submit.prevent="getUserByName">
      <div class="input-group" style="width: 400px; margin-left: auto">
        <input style="width: 200px" v-model="username" type="text" class="form-control" placeholder="Search By Username">
        <button class="btn btn-dark" type="submit">查询</button>
      </div>
    </form>


    <hr>
    <table class="table table-hover" style="text-align: center">
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
          </div>
        </td>
      </tr>

      </tbody>
    </table>
  </ContentBase>
</template>

<script>
import ContentBase from "@/components/common/ContentBase";
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
          console.log("Successfully obtained the user list！")
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
    // TODO:存在跨域问题为解决
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
