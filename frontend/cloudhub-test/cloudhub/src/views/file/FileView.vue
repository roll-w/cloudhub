<template>
  <ContentBase>
    <div>
      <button type="button" class="btn btn-outline-secondary" @click="back">返回桶列表</button>
      <button type="button" class="btn btn-outline-primary" @click="getFile">+上传文件</button>
      <button type="button" class="btn btn-outline-success" style="float: right">下载选中</button>
      <button type="button" class="btn btn-outline-danger " style="float: right">删除选中</button>
    </div>
    <hr>
    <table class="table table-hover" style="text-align: center ">
      <thead class="table-light">
      <tr>
        <th></th>
        <th scope="col">名称</th>
        <th scope="col">修改日期</th>
        <th scope="col">类型</th>
        <th scope="col">大小</th>
        <th scope="col">文件管理</th>
        <th scope="col">详情信息</th>
      </tr>
      </thead>

      <tbody>

      <tr v-for="file in files" :key="file.fileSize">
        <th>
          <input class="form-check-input mt-0" type="checkbox" value="" aria-label="Checkbox for following text input">
        </th>
        <th scope="row">{{ file.fileName }}</th>
        <th scope="row">{{ file.fileDate }}</th>
        <th scope="row">{{ file.fileType }}</th>
        <th scope="row">{{ file.fileSize }}</th>
        <th scope="row">
          <div class="d-grid gap-2 d-md-flex justify-content-center" role="group" aria-label="Basic outlined example">
            <button type="button" class="btn btn-success">下载</button>
            <button type="button" class="btn btn-danger">删除</button>
          </div>
        </th>
        <th scope="row">
          <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#fileInfo">查看</button>
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
    // 模拟windows资源管理器
    let file = ([]);
    let files = ref([
      {
        fileName: "word",
        fileDate: "2022/10/1 12:17",
        fileType: "txt",  // 后缀
        fileSize: "128",  // 单位MB
      },
      {
        fileName: "cloudhub",
        fileDate: "2022/10/1 16:17",
        fileType: "rar",  // 后缀
        fileSize: "16",  // 单位MB
      },
      {
        fileName: "photo",
        fileDate: "2022/9/5 16:17",
        fileType: "img",  // 后缀
        fileSize: "12",  // 单位MB
      },
      {
        fileName: "video",
        fileDate: "2022/11/11 11:11",
        fileType: "mp4",  // 后缀
        fileSize: "666",  // 单位MB
      }

    ])

    const route = useRouter()

    const back = () => {
      route.push('bucket')
    }

    const getFile = () => {
      $.ajax({
        url: "http://127.0.0.1:port/",
        type: "get",
        headers: {
        //
        },
        success(resp) {
         file.value = resp;
          console.log("The file was uploaded successfully")
        },
        error(resp){
          console.log(resp)
        }
      })
    }


    return {
      files,
      getFile,
      back
    }
  }

}
</script>

<style scoped>
.file_text {
  margin-top: 20px;
}
</style>
