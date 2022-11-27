<template>
  <ContentBase>
    <button type="button"  class="btn btn-link" data-bs-toggle="modal" data-bs-target="#addBucket">
      创建桶
    </button>
<!--// TODO: 创建modal  -->
    <div class="modal fade" id="addBucket" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="exampleModalLabel">创建</h5>
          </div>
          <div class="modal-body">

            <form @submit.prevent="addBucket">
              <div class="mb-3">
                <label for="name" class="form-label">名称:</label>
                <input  type="text" v-model="bucketName" class="form-control" id="name" placeholder="Name">
              </div>
              <div class="mb-3">
                <label for="name" class="form-label">策略:</label>
                <input  type="text" v-model="visibility" class="form-control" id="name" placeholder="Visibility">
              </div>
              <figure class="text-center">
<!--                后期修改样式-->
                PRIVATE or PUBLIC_READ or PUBLIC_READ_WRITE
              </figure>
              <div class="modal-footer">
                <button style="margin-right: 5px" type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                <button type="submit" class="btn btn-primary" >添加</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
    <form @submit.prevent="getBucketByName">
      <div class="input-group" style="width: 400px; margin-left: auto">
        <input style="width: 200px" v-model="bucketName" type="text" class="form-control" placeholder="Search By BucketName">
        <button class="btn btn-dark" type="submit">查询</button>
      </div>
    </form>
    <hr>
    <table class="table table-hover" style="text-align: center">
      <thead class="table-light">
      <tr>
        <th scope="col">名称</th>
        <th scope="col">策略</th>
        <th scope="col">详情</th>
        <th scope="col">编辑</th>
      </tr>
      </thead>

      <tbody>
      <tr v-for="bucket in buckets" :key="bucket.name">
        <td>{{ bucket.name }}</td>
        <td>{{ bucket.bucketVisibility }}</td>
        <td>
          <div class="btn-group" role="group" aria-label="Basic outlined example">
            <button type="button" class="btn btn-link" @click="checkFile">查看</button>
          </div>
        </td>
        <td>
          <div class="btn-group" role="group" aria-label="Basic outlined example">
            <button type="button" class="btn btn-link" data-bs-toggle="modal" data-bs-target="#bucketAuthority">
              权限
            </button>
          </div>
          <div class="btn-group" role="group" aria-label="Basic outlined example">
            <button type="button" class="btn btn-link" @click="deleteBucket(bucket)">删除</button>
          </div>
        </td>
      </tr>

      </tbody>
    </table>

    <div class="modal fade" id="bucketAuthority" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="exampleModalLabel">修改桶权限</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">

            <form @submit.prevent="settingVisibility">
              <div class="mb-3">
                <label for="name" class="form-label">名称:</label>
                <input  type="text" v-model="bucketName" class="form-control" id="name" placeholder="Name">
              </div>
              <div class="mb-3">
                <label for="name" class="form-label">策略:</label>
                <input  type="text" v-model="visibility" class="form-control" id="name" placeholder="PRIVATE or PUBLIC_READ or PUBLIC_READ_WRITE" >
              </div>
              <div class="modal-footer">
                <button style="margin-right: 5px" type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                <button type="submit" class="btn btn-primary">添加</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

  </ContentBase>
</template>

<script>
import ContentBase from "@/components/common/ContentBase";
import { Modal } from 'bootstrap/dist/js/bootstrap'
import {useRouter} from 'vue-router'
import {ref} from 'vue';
import $ from 'jquery'
import url from '@/store/api'
export default {
  name: "BucketView",
  components: {
    ContentBase,
  },

  setup() {
    //接口
    let buckets = ref([]);
    let bucketName = ref([]);
    let visibility= ref([]);
    const router = useRouter()

    const checkFile = () => {
      router.push('file')
      //获取指定桶的文件信息
      $.ajax({
        url: '',
        type: "",
        success() {
        //  file = resp
        },
        error(resp){
          console.log(resp)
        }
      });
    };

    //TODO:获得桶列表
    const getBucket =()=> {
      $.ajax({
        url: url.url_getBucket,
        type: "GET",
        xhrFields: {
          withCredentials: true // 携带跨域cookie  //单个设置
        },
        crossDomain:true,
        success(resp) {
          console.log(resp.data)
          buckets.value = resp.data;
          console.log("Successfully obtained the bucket list！")
        },
        error(){
          console.log("Bucket list acquisition failed！！！")
        }
      });
    };

    getBucket();

    //TODO：删除某个桶
    const deleteBucket = (bucket)=>{
      $.ajax({
        url: url.url_deleteBucket,
        xhrFields: {
          withCredentials: true // 携带跨域cookie  //单个设置
        },
        crossDomain:true,
        type: "DELETE",
        contentType: "application/json;charset=UTF-8",
        data: JSON.stringify({
          bucketName: bucket.name,
        }),
        success(resp) {
          if (resp.errorCode === "00000"){
            getBucket();
          }
        },
        error(resp){
          console.log(resp);
        }
      })
    };

    //TODO：创建桶
    const addBucket =() => {
      $.ajax({
        url: url.url_addBucket,
        type: "PUT",
        xhrFields: {
          withCredentials: true // 携带跨域cookie  //单个设置
        },
        crossDomain:true,
        contentType: "application/json;charset=UTF-8",
        data:JSON.stringify({
          bucketName:bucketName.value,
          visibility:visibility.value,
        }),
        success(resp) {
          if (resp.errorCode === "00000") {
            Modal.getInstance("#addBucket").hide();
              getBucket()
          }
        },
        error(resp){
          console.log(resp)
        }
      });
    };

    const settingVisibility = ()=>{
      $.ajax({
        url: url.url_settingVisibility,
        type: "POST",
        xhrFields: {
          withCredentials: true // 携带跨域cookie  //单个设置
        },
        crossDomain:true,
        contentType: "application/json;charset=UTF-8",
        data:JSON.stringify({
          bucketName:bucketName.value,
          visibility:visibility.value,
        }),
        success(resp) {
          if (resp.errorCode === "00000") {
            Modal.getInstance("#bucketAuthority").hide();
              getBucket()
          }
        },
        error(resp){
            console.log(resp);
        }
      });
    };

    const getBucketByName = () =>{
      $.ajax({
        url: url.url_getBucketByName,
        xhrFields: {
          withCredentials: true
        },
        crossDomain:true,
        type: "GET",
        data: {
         bucketName:bucketName.value,
        },
        success(resp) {
          if (resp.errorCode === "00000"){
            alert("Name: "+resp.data.name+" Email: "+resp.data.bucketVisibility)
          }
        },
        error(resp){
          console.log(resp.data);
        }
      });
    }



    return {
      buckets,
      bucketName,
      visibility,
      checkFile,
      getBucketByName,
      deleteBucket,
      addBucket,
      settingVisibility,
    }
  }
}
</script>

<style scoped>
button {
  /*style="font-size: large; font-weight: bolder"*/
  font-size: large;
  font-weight: normal;
  text-decoration: none;
}
</style>
