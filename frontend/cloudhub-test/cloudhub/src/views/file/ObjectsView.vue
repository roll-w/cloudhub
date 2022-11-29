<template>
  <ContentBase>
    <div class="mb-4 ms-2 d-flex flex-grow-1 flex-fill">
      <h3>桶对象管理：{{ bucketName }} </h3>
      <div class="d-flex flex-fill justify-content-end">
        <button type="button" class="btn btn-outline-primary ms-2" @click="back">返回桶列表</button>
      </div>

    </div>
    <div class="input-group mt-5">
      <form @submit.prevent="uploadObject" enctype="multipart/form-data">
        <label for="formFile" class="form-label">选择文件</label>
        <input class="form-control" type="file" id="formFile" @change="get"/>
        <button type="submit" class="btn btn-link">上传文件</button>
      </form>
    </div>
    <hr class="mt-2">
    <table class="table table-hover text-center">
      <thead class="table-light">
      <tr>
        <th scope="col">对象名称</th>
        <th scope="col">最后修改日期</th>
        <th scope="col">操作</th>
        <th scope="col">详情信息</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="file in obj" :key="file.objectName">
        <th scope="row">{{ file.objectName }}</th>
        <th scope="row">{{ convertTimestamp(file.createTime) }}</th>

        <th scope="row">
          <div class="d-grid gap-2 d-md-flex justify-content-center" role="group">
            <button type="button" class="btn btn-link" @click="open(file)">下载</button>
            <button type="button" class="btn btn-link">删除</button>
          </div>
        </th>
        <th scope="row">
          <button type="button" class="btn btn-link" @click="viewInfo(file)">查看</button>
        </th>
      </tr>
      </tbody>
    </table>
  </ContentBase>
</template>

<script>
import ContentBase from "@/components/common/ContentBase";
import url from '@/store/api';
import {convertTimestamp} from "@/util/time";
import {useRouter} from "vue-router";
import {ref} from "vue";
import $ from 'jquery'

export default {
  name: "FileView",
  components: {
    ContentBase
  },
  setup() {

    let obj = ref([]);
    const router = useRouter()
    let bucketName = router.currentRoute.value.params.bucket
    const isAdminPage = () => {
      return router.currentRoute.value.name === "object_admin_list";
    }
    const back = () => {
      if (isAdminPage()) {
        router.push({name: "bucket_admin_index"})
      } else {
        router.push({name: "bucket_index"})
      }
    }


    const getObject = () => {
      let realUrl = isAdminPage()
          ? url.url_adminGetObjectByBucketName
          : url.url_getObjectByBucketName
      $.ajax({
        url: realUrl,
        type: "get",
        xhrFields: {
          withCredentials: true
        },
        crossDomain: true,
        data: {
          bucketName: bucketName,
        },
        success(resp) {
          obj.value = resp.data;
        },
        error(resp) {
          console.log(resp)
        }
      });
    };

    getObject();

    const objName = ref([]);

    const get = (event) => {
      const files = event.target.files;
      objName.value = files[0];
      console.log(objName.value)
      console.log(objName.value.name);
    }

    const uploadObject = () => {
      let form = new FormData();
      form.append("object", objName.value);
      let realUrl = isAdminPage()
          ? url.url_adminObjectV1
          : url.url_objectV1
      $.ajax({
        url: realUrl / bucketName / objName.value.name,
        type: "PUT",
        xhrFields: {
          withCredentials: true
        },
        crossDomain: true,
        contentType: "multipart/form-data",
        data: {
          form
        },
        success(resp) {
          console.log(resp);
          getObject();
        },
        error(resp) {
          console.log(resp);
        }
      });
    };

    const open = (object) => {
      if (isAdminPage()) {
        window.open(url.url_adminObjectV1 + "/" + object.bucketName + object.objectName,
            '_blank');
        return
      }
      window.open(url.url_objectV1 + "/" + object.bucketName + object.objectName,
          '_blank');
    };

    const viewInfo = (object) => {
      if (isAdminPage()) {
        router.push({
          name: "object_admin_info",
          params: {
            bucket: object.bucketName,
            object: object.objectName
          }
        })
        return
      }
      router.push({
        name: "object_info",
        params: {
          bucket: object.bucketName,
          object: object.objectName
        }
      })
    };

    return {
      obj,
      back,
      get,
      open,
      viewInfo,
      bucketName,
      convertTimestamp,
      uploadObject,
    }
  }

}
</script>

<style scoped>
button {
  text-decoration: none;
  font-size: large;
}
</style>
