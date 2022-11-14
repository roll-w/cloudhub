<template>
  <ContentBase>
    <button type="button" class="btn btn-outline-primary" data-bs-toggle="modal" data-bs-target="#addBucket">
      +创建桶
    </button>
    <ModalAddBucket></ModalAddBucket>

    <hr>
    <table class="table table-hover" style="text-align: center">
      <thead class="table-light">
      <tr>
        <th scope="col">桶名称</th>
        <th scope="col">存储用量</th>
        <th scope="col">桶策略</th>
        <th scope="col">对象数量</th>
        <th scope="col">详情信息</th>
        <th scope="col">编辑</th>
      </tr>
      </thead>

      <tbody>
      <tr v-for="bucket in buckets" :key="bucket.name">
        <td>{{ bucket.name }}</td>
        <td>{{ bucket.storageUsed }}</td>
        <td>{{ bucket.tactic }}</td>
        <td>{{ bucket.objectNum }}</td>

        <td>
          <div class="btn-group" role="group" aria-label="Basic outlined example">
            <button type="button" class="btn btn-primary" @click="checkFile">查看</button>
          </div>
        </td>
        <td>
          <div class="btn-group" role="group" aria-label="Basic outlined example">
            <button type="button" class="btn btn-secondary" data-bs-toggle="modal" data-bs-target="#bucketAuthority">
              权限
            </button>
          </div>
          <div class="btn-group" role="group" aria-label="Basic outlined example">
            <button type="button" class="btn btn-danger">删除</button>
          </div>
        </td>
      </tr>

      </tbody>
    </table>

    <!-- 修改桶的权限（把组件放在table标签之外是为了免除table的CSS样式对该组件的内容产生影响） -->
    <ModalBucketAuthority></ModalBucketAuthority>

  </ContentBase>
</template>

<script>
import ContentBase from "@/components/ContentBase";
import ModalAddBucket from "@/components/modal/ModalAddBucket";
import ModalBucketAuthority from "@/components/modal/ModalBucketAuthority";
import {useRouter} from 'vue-router'

export default {
  name: "BucketView",
  components: {
    ContentBase,
    ModalAddBucket,
    ModalBucketAuthority
  },
  setup() {
    // 桶列表
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
    }

    return {
      buckets,
      checkFile
    }
  }

}
</script>

<style scoped>

</style>
