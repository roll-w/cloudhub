<template>
  <ContentBase>
    <div>
      <button type="button" class="btn btn-outline-secondary" @click="back" style="margin-right: 8px">返回桶列表</button>
      <div class="input-group" style="width: 400px;margin-left: auto ">
        <form @submit.prevent="uploadObject" enctype="multipart/form-data">
        <label for="formFile" class="form-label" >选择文件</label>
        <input  class="form-control" type="file" id="formFile" @change="get"/>
        <button type="submit" class="btn btn-link">上传文件</button>
        </form>
      </div>
    </div>
    <hr style="margin-top: 10px">
    <table class="table table-hover" style="text-align: center ">
      <thead class="table-light">
      <tr>
        <th scope="col">桶名称</th>
        <th scope="col">对象名称</th>
        <th scope="col">操作</th>
        <th scope="col">详情信息</th>
      </tr>
      </thead>

      <tbody>

      <tr v-for="file in obj" :key="file.createTime">
        <th scope="row">{{ file.bucketName }}</th>
        <th scope="row">{{ file.objectName }}</th>

        <th scope="row">
          <div class="d-grid gap-2 d-md-flex justify-content-center" role="group" aria-label="Basic outlined example">
            <button type="button" class="btn btn-link">下载</button>
            <button type="button" class="btn btn-link">删除</button>
          </div>
        </th>
        <th scope="row">
          <button type="button" class="btn btn-link" data-bs-toggle="modal" data-bs-target="#fileInfo">查看</button>
        </th>
      </tr>

      </tbody>
    </table>
    <!-- 查看文件详情信息: 为避免table的CSS样式影响该组件样式，将该组件放在table外-->
    <ModalFileInfo></ModalFileInfo>
  </ContentBase>
</template>

<script>
import ContentBase from "@/components/common/ContentBase";
import ModalFileInfo from "@/components/modal/ModalFileInfo";
import url from '@/store/api';
import {useRouter} from "vue-router";
import {ref} from "vue";
import $ from 'jquery'

export default {
  name: "FileView",
  components: {
    ContentBase,
    ModalFileInfo
  },
  setup() {

    let obj = ref([]);
    // let obj_upload =this.$refs.obj_upload.value;
    // console.log(obj_upload)

    const route = useRouter()
    let bucketName = route.currentRoute.value.params.bucket
    // console.log("Got bucketName=" + bucketName)

    const back = () => {
      route.push({name:"bucket_index"})
    }

    const getObject= () => {
      $.ajax({
        url:url.url_getObjectByBucketName,
        type: "get",
        xhrFields: {
          withCredentials: true
        },
        crossDomain: true,
        data:{
          bucketName:bucketName,
        },
        success(resp) {
          obj.value = resp.data;
        },
        error(resp){
          console.log(resp)
        }
      });
    };

    getObject();

    const objName = ref([]);

    const get = (event)=>{
      const files = event.target.files;
      objName.value = files[0];
      console.log(objName.value)
      console.log(objName.value.name);
    }

    const uploadObject = ()=>{
      let form = new FormData();
      form.append("object",objName.value);

      $.ajax({
        url:url.url_uploadObject/bucketName/objName.value.name,
        type: "PUT",
        xhrFields: {
          withCredentials: true
        },
        crossDomain: true,
        contentType: "multipart/form-data",
        data:{
          form
        },
        success(resp) {
          console.log(resp);
          getObject();
        },
        error(resp){
          console.log(resp);
        }
      });
    };

    return {
      obj,
      back,
      get,
      bucketName,
      uploadObject,
    }
  }

}
</script>

<style scoped>
button{
  text-decoration: none;
  font-size: large;
}
</style>
