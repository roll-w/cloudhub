<template>
  <div class="p-5">
    <div v-if="tag.name">
      <div class="grid grid-cols-10 py-3">
        <div class="text-gray-600 mr-3 ">
          标签信息
        </div>
        <div class="col-span-9">
          <n-form-item label="标签ID">
            <div class="text-xl">
              {{ tag.id }}
            </div>
          </n-form-item>
          <n-form-item label="标签名称">
            <n-input v-model:value="tag.name" placeholder="输入标签名称"
                     size="large"/>
          </n-form-item>
          <n-form-item label="标签描述">
            <n-input v-model:value="tag.description"
                     placeholder="输入标签描述"
                     size="large"/>
          </n-form-item>
          <n-form-item label="标签创建时间">
            <div class="text-xl">
              {{ formatTimestamp(tag.createTime) }}
            </div>
          </n-form-item>
          <n-form-item label="标签更新时间">
            <div class="text-xl">
              {{ formatTimestamp(tag.updateTime) }}
            </div>
          </n-form-item>
        </div>
      </div>
      <div class="grid grid-cols-10 py-3">
        <div class="text-gray-600 mr-3">
          标签关键词
        </div>
        <div class="col-span-9">
          <n-table striped>
            <thead>
            <tr>
              <th>名称</th>
              <th>权重</th>
              <th>操作</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="keyword in tag.keywords">
              <td>
                <n-tag :bordered="false" size="large" type="primary">
                  {{ keyword.name }}
                </n-tag>

              </td>
              <td>{{ keyword.weight }}</td>
              <td>
                <n-button-group>
                  <n-button secondary type="primary">修改</n-button>
                  <n-button secondary type="error">
                    删除
                  </n-button>
                </n-button-group>
              </td>
            </tr>
            <tr>
              <td class="text-center" colspan="3">
                <n-button>添加关键词</n-button>
              </td>
            </tr>
            </tbody>
          </n-table>
        </div>
      </div>
      <div class="flex">
        <div class="flex-grow"></div>
        <div class="py-5">
          <n-button-group>
            <n-button type="primary">保存</n-button>
            <n-button secondary>关闭</n-button>
          </n-button-group>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import {formatTimestamp} from "@/util/format";

const props = defineProps({
  tag: {
    type: Object,
    required: true
  },
})


</script>

<style scoped>

</style>