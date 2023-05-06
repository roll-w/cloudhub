<template>
    <div class="p-5">
        <div class="text-3xl py-2">
            文件搜索
        </div>
        <div class="py-2">
            <n-input-group>
                <n-input v-model:value="inputRef" placeholder="筛选条件输入框" type="text"/>
                <n-button-group>
                    <n-button type="primary">搜索</n-button>
                    <n-button secondary @click="handleReset">重置</n-button>
                </n-button-group>
            </n-input-group>

        </div>
        <n-card embedded>
            <div class="text-xl py-2">
                条件筛选
            </div>
            <n-checkbox-group :value="conditions" @update:value="handleUpdateValue">
                <n-space item-style="display: flex;" vertical>
                    <div class="grid grid-cols-10" v-for="(condition, i) in conditionOptions">
                        <div class="mr-3">
                            <n-checkbox :value="condition.value">
                                {{ condition.label }}
                            </n-checkbox>
                        </div>
                        <div class="col-span-9">
                            <n-input :disabled="!isSelected(condition.value)"
                                     :on-update:value="handleUpdateValue(conditions)"
                                     v-model:value="inputGroupRefs[i]" type="text"/>
                        </div>
                    </div>
                </n-space>
            </n-checkbox-group>
            <div class="pt-3">
                <n-alert type="info">
                    暂不支持单条件多值筛选
                </n-alert>
            </div>
        </n-card>

        <div class="text-xl py-3">
            搜索结果
        </div>
    </div>
</template>

<script setup>

import {ref} from "vue";

const inputRef = ref('')
const conditions = ref([])

const conditionOptions = [
    {
        label: '名称',
        value: '名称'
    },
    {
        label: '文件类型',
        value: '文件类型'
    },
    {
        label: '文书类型',
        value: '文书类型'
    },
    {
        label: '诉讼类型',
        value: '诉讼类型'
    },
    {
        label: '审理阶段',
        value: '审理阶段'
    },
    {
        label: '创建者',
        value: '创建者'
    }
]

const isSelected = (value) => {
    return conditions.value.includes(value)
}

const inputGroupRefs = ref([])

const findInputRef = (value) => {
    const index = conditionOptions.findIndex((condition) => {
        return condition.value === value
    })
    return inputGroupRefs.value[index]
}

const initialRefs = () => {
    for (let conditionOption of conditionOptions) {
        inputGroupRefs.value.push('')
    }
}

initialRefs()

const handleUpdateValue = (value) => {
    conditions.value = value
    let query = ''
    for (let valueElement of value) {
        query += valueElement + ":" + findInputRef(valueElement) + " "
    }
    inputRef.value = query
}

const handleReset = () => {
    inputRef.value = ''
    conditions.value = []
    for (let i = 0; i < inputGroupRefs.value.length; i++) {
        inputGroupRefs.value[i] = ''
    }
}

</script>

<style scoped>

</style>