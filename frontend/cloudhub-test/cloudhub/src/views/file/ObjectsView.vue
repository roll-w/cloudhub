<template>
  <ContentBase>
    <div class="mb-4 ms-2 d-flex flex-grow-1 flex-fill">
      <h3>桶对象管理：{{ bucketName }} </h3>
      <div class="d-flex flex-fill justify-content-end">
        <button type="button" class="btn btn-outline-primary ms-2" @click="back">返回桶列表</button>
      </div>
    </div>
    <div class="btn btn-outline-primary" data-bs-toggle="modal"
         data-bs-target="#uploadObjectModal">上传文件
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
            <button type="button" class="btn btn-link" @click="deleteObject(file.objectName)">删除</button>
          </div>
        </th>
        <th scope="row">
          <button type="button" class="btn btn-link" @click="viewInfo(file)">查看</button>
        </th>
      </tr>
      </tbody>
    </table>
    <div class="modal fade" id="uploadObjectModal">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">上传对象</h5>
          </div>
          <div class="modal-body">
            <div class="alert alert-warning">
              <p>上传对象时，填写框中可以写下对象路径，使用 / 分割。不填则为根路径。</p>
            </div>
            <form @submit.prevent="null" enctype="multipart/form-data">
              <div class="form-floating pb-2">
                <input type="text" id="dir-input_file" v-model="dirPath" class="form-control" placeholder="填写文件夹">
                <label for="dir-input_file">填写文件夹路径</label>
              </div>
              <div class="form-floating pb-2">
                <input type="text" class="form-control" v-model="overrideName" id="override-input_file" placeholder="覆盖文件名">
                <label for="override-input_file" class="form-label">覆盖默认文件名</label>
              </div>
              <div class="pb-2">
                <label for="formFile" class="form-label">选择文件</label>
                <input class="form-control" type="file" id="formFile" @change="get"/>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <div class="btn-group">
              <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">
                取消
              </button>
              <button type="submit" class="btn btn-outline-danger" data-bs-dismiss="modal" @click="uploadObject">
                确认
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </ContentBase>
</template>

<script>
import ContentBase from "@/components/common/ContentBase";
import url from '@/store/api';
import {convertTimestamp} from "@/util/time";
import {useRouter} from "vue-router";
import {ref} from "vue";
import $ from 'jquery'
import axios from "axios";

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
    const overrideName = ref(null);
    const dirPath = ref('');

    const get = (event) => {
      const files = event.target.files;
      objName.value = files[0];
    }

    const deleteObject = (object) => {
      console.log("Delete " + object)
    }

    const uploadObject = () => {
      let form = new FormData();
      form.append("object", objName.value);
      let realUrl = isAdminPage()
          ? url.url_adminObjectV1
          : url.url_objectV1
      let name = overrideName.value === null ? objName.value.name : overrideName.value;
      let path = dirPath.value === '' ? encodeURI(name) : dirPath.value + '/' + encodeURI(name);
      axios.put(`${realUrl}/${bucketName}/${path}`, form, {
        headers: {
          'Content-Type': 'multipart/form-data'
        },
        origin: true,
        withCredentials: true,
      }).then(resp => {
        overrideName.value = null
        dirPath.value = null
        objName.value = null

        console.log(resp)
        getObject();
      }).catch(resp => {
        console.log(resp)
      })
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
      dirPath,
      overrideName,
      deleteObject
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
