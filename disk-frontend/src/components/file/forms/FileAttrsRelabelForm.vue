<template>
    <div v-if="storageType !== 'FILE'" class="py-3 text-xl">
        此文件类型不支持重新标记
    </div>
    <div v-else class="py-2">
        <div>
            <n-tabs animated type="line">
                <n-tab-pane name="自动重标记" tab="自动重标记">
                    <n-alert type="info">
                        提交自动重标记后，系统将会自动根据文件内容进行重标记，此过程可能需要一段时间，请耐心等待。
                    </n-alert>
                    <div class="pt-3">
                        <n-button-group>
                            <n-button type="primary" @click="handleAutoRelabel">
                                确认
                            </n-button>
                            <n-button secondary type="default" @click="handleCancel">
                                取消
                            </n-button>
                        </n-button-group>
                    </div>

                </n-tab-pane>
                <n-tab-pane name="手动更改" tab="手动更改">
                    <div class="text-xl pb-2">
                        更改文件标记
                    </div>
                    <n-alert type="info">
                        请在下方输入框中输入新的标记值，点击提交修改后，系统将会根据您的输入进行重标记。
                    </n-alert>
                    <n-table :bordered="false" class="py-2">
                        <thead>
                        <tr>
                            <th>名称</th>
                            <th>值</th>
                            <th>操作</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr v-for="(tag, i) in inputs || []" class="transition-all ease-in-out">
                            <td>
                                <div v-if="tag.disable">
                                    {{ tag.name }}
                                </div>
                                <n-input v-else v-model:value="inputs[i].name"/>
                            </td>
                            <td>
                                <div v-if="tag.disable">
                                    {{ tag.value }}
                                </div>
                                <n-input v-else v-model:value="inputs[i].value"/>
                            </td>
                            <td>
                                <n-button-group>
                                    <n-button :disabled="tag.disable" secondary
                                              type="primary"
                                              @click="handleRemoveTag(i)">
                                        删除
                                    </n-button>
                                </n-button-group>
                            </td>
                        </tr>
                        <tr>
                            <td colspan="3">
                                <div class="text-center">
                                    <n-button secondary type="primary" @click="handleAddTag">
                                        添加标记
                                    </n-button>
                                </div>
                            </td>
                        </tr>

                        </tbody>
                    </n-table>
                    <div class="pt-3">
                        <n-button-group>
                            <n-button type="primary" @click="handleConfirm">
                                提交修改
                            </n-button>
                            <n-button secondary type="default" @click="handleCancel">
                                取消
                            </n-button>
                        </n-button-group>
                    </div>
                </n-tab-pane>
            </n-tabs>

        </div>

    </div>
</template>

<script setup>
import {ref, getCurrentInstance} from "vue";
import {useRouter} from "vue-router";
import {useNotification, useMessage, useDialog} from "naive-ui";
import {createConfig} from "@/request/axios_config";
import api from "@/request/api";
import {popUserErrorTemplate} from "@/views/util/error";

const router = useRouter()
const {proxy} = getCurrentInstance()
const notification = useNotification()
const message = useMessage()
const dialog = useDialog()

const props = defineProps({
    storageId: {
        type: Number,
        required: true
    },
    storageType: {
        type: String,
        required: true
    },
    onClickCancel: {
        type: Function,
        default: () => {
        }
    },
    onClickConfirm: {
        type: Function,
        default: () => {
        }
    },
    onBeforeAction: {
        type: Function,
        default: () => {
        }
    },
    onAfterAction: {
        type: Function,
        default: () => {
        }
    },
    ownerType: {
        type: String,
        default: 'user'
    },
    ownerId: {
        type: Number,
        required: true
    },
    attributes: {
        type: Array,
        default: []
    }
})

const inputs = ref([])

const createInputs = () => {
    props.attributes.forEach((tag, index) => {
        inputs.value.push({
            name: tag.name,
            value: tag.value,
            index: index,
            disable: tag.dataType === 'fileType'
        })
    })
}


createInputs()

const handleAddTag = () => {
    inputs.value.push({
        name: '',
        value: '',
        index: inputs.value.length,
        disable: false
    })
}

const handleRemoveTag = (index) => {
    inputs.value.splice(index, 1)
}

const handleAutoRelabel = () => {
    props.onClickConfirm()
    props.onBeforeAction()
    const config = createConfig()
    proxy.$axios.post(api.storageTags(props.ownerType.toLowerCase(), props.ownerId,
            props.storageType.toLowerCase(), props.storageId, false),
            {}, config).then(res => {
        message.success('已提交重标记请求')
    }).catch(err => {
        popUserErrorTemplate(notification, err, '提交重标记请求失败', "存储请求错误")
    }).finally(() => {
        props.onAfterAction()
    })
}

const handleConfirm = () => {
    const data = {}
    inputs.value.forEach(input => {
        data[input.name] = input.value
    })
    props.onClickConfirm(data)
}

const handleCancel = () => {
    props.onClickCancel()
}

</script>

<style scoped>

</style>