<template>
  <ContentBase>
    <button type="button" class="btn btn-dark" data-bs-toggle="modal" data-bs-target="#addBucket">
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

                <!-- 桶名称 -->
                <input  type="text" class="form-control" id="name" placeholder="Name" >
              </div>
              <div class="mb-3">
                <label for="name" class="form-label">策略:</label>
                <input  type="text" class="form-control" id="name" placeholder="Visibility" >
              </div>
              <figure class="text-center">
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

    <hr>
    <table class="table table-hover" style="text-align: center">
      <thead class="table-light">
      <tr>
        <th scope="col">名称</th>
        <th scope="col">策略</th>
        <th scope="col">详情</th>
        <th scope="col">操作</th>
      </tr>
      </thead>

      <tbody>
      <tr v-for="bucket in buckets" :key="bucket.name">
        <td>{{ bucket.name }}</td>
        <td>{{ bucket.tactic }}</td>

        <td>
          <div class="btn-group" role="group" aria-label="Basic outlined example">
            <button type="button" class="btn btn-primary" @click="checkFile">查看</button>
          </div>
        </td>
        <td>
          <div class="btn-group" role="group" aria-label="Basic outlined example">
            <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#bucketAuthority">
              权限
            </button>
          </div>
          <div class="btn-group" role="group" aria-label="Basic outlined example">
            <button type="button" class="btn btn-danger" @click="deleteBucket(bucket)">删除</button>
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

            <form @submit.prevent="addBucket">

              <div class="mb-3">
                <label for="name" class="form-label">策略:</label>
                <input  type="text" class="form-control" id="name" placeholder="PRIVATE or PUBLIC_READ or PUBLIC_READ_WRITE" >
              </div>
              <div class="modal-footer">
                <button style="margin-right: 5px" type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                <button type="submit" class="btn btn-primary" >添加</button>
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
    let bucket = ref([]);
    let bucketName = ref([]);
    let bucketVisibility= ref([]);

    let buckets = [
      {
        name: "bucket1",
        storageUsed: 1234,  // 存储用量(byte)
        tactic: "私有桶", // 桶策略: 私有、公共读、公共读写
        objectNum: 0, // 对象数量: 对象数量是桶内文件夹、当前版本对象和历史版本对象的总和
      },
      {
        name: "bucket2",
        storageUsed: 0,  // 存储用量(byte)
        tactic: "公共读写", // 桶策略: 私有、公共读、公共读写
        objectNum: 0, // 对象数量: 对象数量是桶内文件夹、当前版本对象和历史版本对象的总和
      }
    ]

    const router = useRouter()

    const checkFile = () => {
      router.push('file')
      //获取指定桶的文件信息
      $.ajax({
        url: '',
        type: "get",
        success() {
        //  file = resp
        },
        error(resp){
          console.log(resp)
        }
      });
    }

    //具体对接修改相应属性
    const getBucket =()=> {
      $.ajax({
        url: url.url_getBucket,
        type: "get",

        success(resp) {
          bucket.value = resp;
        },
        error(resp){
          console.log(resp)
        }
      });
    }

    getBucket();

    const deleteBucket = (bucket)=>{
      $.ajax({
        url: url.url_deleteBucket,
        type: "post",
        data: {
          bucket_id: bucket.id,
        },
        success(resp) {
          if (resp.message === "removeSuccess"){
            getBucket();
          }
        }
      })
    };


    //获取Value，创建桶
    const addBucket =() => {

      $.ajax({
        url: url.url_addBucket,
        type: "post",
        contentType: "application/json;charset=UTF-8",
        data:JSON.stringify({
          bucketName:bucketName.value,
          bucketVisibility:bucketVisibility,
        }),
        success(resp) {
          if (resp.message === "SUCCESS") {
              console.log("刷新桶列表")
              //modal自动关闭待修复
              // getBucket()
          }
        },
        error(resp){
          console.log(resp)
        }
      });
    }



    return {
      bucket,
      buckets,
      checkFile,
      deleteBucket,
      addBucket,
    }
  }
}
</script>

<style scoped>

</style>
