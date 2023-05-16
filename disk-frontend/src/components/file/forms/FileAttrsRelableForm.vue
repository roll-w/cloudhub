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
                            <n-button type="primary" @click="handleConfirm">
                                确认
                            </n-button>
                            <n-button secondary type="default" @click="handleCancel()">
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
                        若您输入的标记不存在，系统将会自动创建一个新的标记。
                    </n-alert>
                    <n-table class="py-2" :bordered="false">
                        <thead>
                        <tr>
                            <th>名称</th>
                            <th>值</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr v-for="(tag, i) in attributes || []">
                            <td>{{ tag.name }}</td>
                            <td>
                                <n-input v-model:value="inputs[i].value" :disabled="inputs[i].disable"/>
                            </td>
                        </tr>
                        </tbody>
                    </n-table>
                    <div class="pt-3">
                        <n-button-group>
                            <n-button type="primary" @click="handleConfirm">
                                提交修改
                            </n-button>
                            <n-button secondary type="default" @click="handleCancel()">
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

import {ref} from "vue";

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