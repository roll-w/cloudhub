<template>
    <div class="p-5">
        <n-h2>
            文件标签
        </n-h2>
        <div class="pb-4">
            <n-alert type="info">
                仅展示当前文件中存在的标签，不会展示当前文件不存在的标签。
            </n-alert>
        </div>
        <n-card class="mb-5">
            <template #header>
                <div class="text-2xl">
                    了解标签处理过程
                </div>
            </template>
            <template #default>
                <n-steps>
                    <n-step
                            description="采集文件信息，文件名、文件内容等数据。"
                            status="process"
                            title="数据获取"/>
                    <n-step
                            description="基于自然语言处理等技术提取关键词。"
                            status="process"
                            title="信息提取"/>
                    <n-step
                            description="基于关键词，从知识库中查询标签进行匹配。"
                            status="process"
                            title="标签匹配"/>
                    <n-step
                            description="将匹配到的标签与文件关联，完成分类。"
                            status="process"
                            title="标签关联"/>
                </n-steps>
            </template>
        </n-card>
        <n-tree
                :data="tagTreeOption"
                expand-on-click
        />
    </div>

</template>

<script setup>


const tagTreeOption = [
    {
        label: '文件类型',
        key: 'type',
        children: [
            {
                label: '文档',
                key: 'type_1'
            },
            {
                label: '图片',
                key: 'type_2'
            },
            {
                label: '视频',
                key: 'type_3'
            },
            {
                label: '音频',
                key: 'type_4'
            },
            {
                label: '其他',
                key: 'type_5'
            }
        ]
    },
    {
        label: '案件类型',
        key: 'type_case',
        children: [
            {
                label: '民事案件',
                key: 'type_case_1'
            },
            {
                label: '刑事案件',
                key: 'type_case_2'
            },
            {
                label: '行政案件',
                key: 'type_case_3'
            },
            {
                label: '其他',
                key: 'type_case_4'
            }
        ]
    },
    {
        label: '文书类型',
        key: 'type_ar',
        children: [
            {
                label: '裁定书',
                key: 'type_ar_1'
            },
            {
                label: '调解书',
                key: 'type_ar_2'
            },
            {
                label: '判决书',
                key: 'type_ar_3'
            },
            {
                label: '其他',
                key: 'type_ar_4'
            }
        ]
    },
    {
        label: '审理阶段',
        key: 'stage_ar',
        children: [
            {
                label: '一审',
                key: 'stage_ar_1'
            },
            {
                label: '二审',
                key: 'stage_ar_2'
            },
            {
                label: '再审',
                key: 'stage_ar_3'
            },
            {
                label: '其他',
                key: 'stage_ar_4'
            }
        ]
    }
]
</script>