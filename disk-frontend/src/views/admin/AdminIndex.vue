<template>
    <div class="p-5">
        <div class="py-3">
            <n-h2 class="pl-2">
                系统数据概览
            </n-h2>
            <n-grid cols="3" x-gap="10" y-gap="10">
                <n-gi v-for="option in dataOptions">
                    <n-card :bordered="false" embedded>
                        <div class="text-md">
                            {{ option.name }}
                        </div>
                        <div class="pt-5 text-3xl text-amber-500">
                            {{ option.value }}
                        </div>
                    </n-card>
                </n-gi>
            </n-grid>
        </div>

        <div class="py-3">
            <n-h2 class="pl-2">
                系统快捷入口
            </n-h2>
            <n-grid cols="3" x-gap="10" y-gap="10">
                <n-gi v-for="option in systemEntries">
                    <n-card :bordered="false" embedded>
                            <div class="flex py-3 text-2xl items-center">
                            <div class="text-amber-500">
                                {{ option.name }}
                            </div>
                            <div class="flex flex-fill items-center h-12 justify-end justify-items-stretch">
                                <div class="select-none text-xl transition-all duration-150
                                    font-semibold font-sans
                                    cursor-pointer
                                    hover:text-4xl hover:text-amber-500
                                    text-neutral-400 ease-in">
                                    &gt;
                                </div>
                            </div>
                        </div>
                    </n-card>
                </n-gi>
            </n-grid>

        </div>

    </div>
</template>

<script setup>
const dataOptions = [
    {
        name: "文件数量",
        value: 748
    },
    {
        name: "用户数量",
        value: 12
    },
    {
        name: "部门数量",
        value: 3
    },
    {
        name: "文件总大小",
        value: "1.2GB"
    },
    {
        name: "在线文件服务器",
        value: "12"
    },
    {
        name: "离线文件服务器",
        value: "0"
    },
    {
        name: "近期网络流量",
        value: "1.2GB"
    }
]

const systemEntries = [
    {
        name: "文件管理",
    },
    {
        name: "用户管理",
    },
    {
        name: "部门管理",
    },
    {
        name: "文件服务器管理",
    },

]

</script>
