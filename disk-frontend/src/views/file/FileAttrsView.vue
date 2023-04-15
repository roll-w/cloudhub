<template>
    <div class="p-5 flex flex-col items-stretch">
        <n-h2 class="mb-5">
            文件信息
        </n-h2>
        <div class="flex items-stretch">
            <div class="w-2/5 max-w-[40%] min-w-[40%] mr-4">
                <div class="mb-4 text-xl">
                    属性
                </div>
                <div class="h-100 flex flex-col items-stretch place-items-stretch">
                    <div class=" ">
                        <n-table :bordered="false">
                            <thead>
                            <tr>
                                <th>类型</th>
                                <th>值</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr v-for="tag in file.tags || []">
                                <td>{{ tag.name }}</td>
                                <td>
                                    <n-tag :bordered="false" type="primary">{{ tag.value }}</n-tag>
                                </td>
                            </tr>
                            </tbody>
                        </n-table>
                    </div>
                    <div class="py-3 self-end justify-end flex-fill">标记错误？
                        <n-button secondary type="primary">重新标记</n-button>
                    </div>
                </div>
            </div>
            <n-divider style="height: auto" vertical/>
            <div class="ml-4">
                <div class="text-xl mb-4">
                    文件操作日志
                </div>

                <n-timeline>
                    <n-timeline-item
                            time="2023-04-03 20:46"
                            title="修改权限">
                        <div>
                            <span class="text-amber-500">user</span> 修改文件权限为私有
                        </div>
                    </n-timeline-item>
                    <n-timeline-item
                            time="2023-04-03 16:24"
                            title="文件重命名">
                        <div>
                            <span class="text-amber-500">user</span> 重命名文件为 {{ file.name }}
                        </div>
                    </n-timeline-item>
                    <n-timeline-item
                            time="2023-04-03 16:11"
                            title="文件更新">
                        <div>
                            <span class="text-amber-500">user</span> 更新了文件内容，版本号为 2
                        </div>
                    </n-timeline-item>
                    <n-timeline-item
                            time="2023-03-23 15:26"
                            title="修改权限">
                        <div>
                            <span class="text-amber-500">user</span> 修改文件权限为 公开查看
                        </div>
                    </n-timeline-item>
                    <n-timeline-item
                            content="user 上传文件"
                            time="2023-03-23 15:23"
                            title="文件上传">
                        <div>
                            <span class="text-amber-500">user</span> 上传文件
                        </div>
                    </n-timeline-item>
                </n-timeline>
            </div>
        </div>
        <div class="pb-3">
            <div class="text-xl mb-3">版本信息</div>
            <n-alert class="my-4" type="info">
                只有内容修改会被计入版本。如修改文件名、修改权限等操作不会被计入版本。
            </n-alert>

            <div>
                <n-table>
                    <thead>
                    <tr>
                        <th>版本</th>
                        <th>修改时间</th>
                        <th>修改人</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>2</td>
                        <td>2023-04-03 16:11</td>
                        <td>user</td>
                        <td>
                            <n-button-group>
                                <n-button secondary type="primary">查看</n-button>
                                <n-button secondary type="default">回退</n-button>
                                <n-button secondary type="error">删除</n-button>
                            </n-button-group>
                        </td>
                    </tr>
                    <tr>
                        <td>1</td>
                        <td>2023-03-23 15:23</td>
                        <td>user</td>
                        <td>
                            <n-button-group>
                                <n-button secondary type="primary">查看</n-button>
                                <n-button secondary type="default">回退</n-button>
                                <n-button secondary type="error">删除</n-button>
                            </n-button-group>
                        </td>
                    </tr>
                    </tbody>
                </n-table>
            </div>
        </div>
    </div>

</template>

<script setup>

import {useRouter} from "vue-router";
import {requestFile} from "@/views/temp/files";
import {ref} from "vue";

const router = useRouter();

const id = router.currentRoute.value.params.id;
const file = ref(requestFile(id))

console.log(id, requestFile(id))

</script>