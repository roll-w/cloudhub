<template>
  <ContentBase>
    <h3 class="mb-4">用户信息</h3>
    <div class="d-flex flex-xl-row flex-grow-1">

      <div class="d-flex flex-fill justify-content-end">
          <button type="button" class="btn btn-outline-primary"
                  data-bs-toggle="modal"
                  data-bs-target="#UserSingleMessage">
              查询用户
          </button>

          <div class="modal fade" ref="UserSingleMessage" id="UserSingleMessage">
              <div class="modal-dialog">
                  <div class="modal-content">
                      <div class="modal-header">
                          <h5 class="modal-title">查询用户</h5>
                      </div>
                      <div class="modal-body">
                                  <form @submit.prevent="getUserByName">
                                    <div class="input-group">
                                      <input v-model="name" type="text" class="form-control"
                                             placeholder="搜索用户......">
                                      <button class="btn btn-outline-primary" type="submit">查询</button>
                                    </div>
                                  </form>
                      </div>
                      <hr>
                      <div class="modal-body">
                          <h5 style="text-align: center">
                              用户信息如下
                          </h5>
                          <h6> id:{{SingleUser.id}}</h6>
                          <br>
                          <h6>email:{{SingleUser.email}}</h6>
                          <br>
                          <h6>role:{{SingleUser.role}}</h6>

                      </div>
                      <div class="modal-footer">
                          <div class="btn-group">
                              <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">
                                  关闭
                              </button>
                          </div>
                      </div>
                  </div>
              </div>
          </div>


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
            <button type="button" class="btn btn-link" data-bs-toggle="modal"
                    :data-cfs-user-id="user.id"
                    :data-cfs-user-name="user.username"
                     data-bs-target="#UserDeleteConfirm">
                  删除
            </button>

<!--TODO:未置留后端接口-->

<!--            <button type="button"  class="btn btn-link">修改</button>-->
          </div>
        </td>
      </tr>


      <div class="modal fade" ref="UserDeleteConfirm" id="UserDeleteConfirm">
          <div class="modal-dialog">
              <div class="modal-content">
                  <div class="modal-header">
                      <h5 class="modal-title">是否确认删除</h5>
                  </div>
                  <div class="modal-body">
                      <div>是否确认删除用户：{{ username }}</div>
                  </div>
                  <div class="modal-footer">
                      <div class="btn-group">
                          <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">
                              取消
                          </button>
                          <button type="submit" class="btn btn-outline-danger" data-bs-dismiss="modal" @click="deleteUserByID">
                              确认
                          </button>
                      </div>
                  </div>
              </div>
          </div>
      </div>
      </tbody>
    </table>
  </ContentBase>
</template>

<script setup>
import ContentBase from "@/components/common/ContentBase";
// import { Modal } from 'bootstrap/dist/js/bootstrap'
import {onMounted, reactive, ref} from "vue";
import $ from 'jquery'
import url from "@/store/api";


    let name = ref('')
    let username = ref('');
    let userId = ref([]);
    let users = ref([]);
    let SingleUser = reactive({
        id:'',
        email:"",
        role:"",
    });
    let UserDeleteConfirm = ref(null)

    onMounted(() => {
        UserDeleteConfirm.value.addEventListener("show.bs.modal", (event) => {
              const button = event.relatedTarget
              userId.value = button.getAttribute("data-cfs-user-id")
              username.value = button.getAttribute("data-cfs-user-name")
          })
      });

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

    const deleteUserByID = ()=>{
        deleteUser(userId.value)
    }

      const deleteUser = (userId) => {
          $.ajax({
              url: url.url_deleteUserById,
              xhrFields: {
                  withCredentials: true
              },
              crossDomain: true,
              type: "POST",
              contentType: "application/x-www-form-urlencoded",
              data: {
                  userId: userId,
              },
              success(resp) {
                  if (resp.errorCode === "00000") {
                      getUser()
                  }
                  window.$message({
                      type: "danger",
                      content: resp.message
                  })
                  userId.value = null
              },
              error(resp) {
                  console.log(resp);
                  window.$message({
                      type: "danger",
                      content: resp.responseJSON.message
                  })
                  userId.value = null
              }
          })
      };



    const getUserByName = () =>{
      $.ajax({
        url: url.url_getUserByName,
        xhrFields: {
          withCredentials: true
        },
        crossDomain:true,
        type: "GET",
        data: {
          username:name.value,
        },
        success(resp) {
          if (resp.errorCode === "00000"){
            // Modal.getInstance("#SearchUser").show();
            // alert("ID: "+resp.data.id+" Email: "+resp.data.email+" Role: "+resp.data.role)
            SingleUser.id = resp.data.id;
            SingleUser.email = resp.data.id;
            SingleUser.role  = resp.data.role;
          }
        },
        error(resp){
          console.log(resp.data);
        }
      });
    }
</script>

<style scoped>
button {
  font-size: large;
  font-weight: normal;
  text-decoration: none;
}
</style>
