<template>
    <div class="p-5">
        <n-h2>
            网站未开放密码忘记功能，请您联系网站管理员提供相关证据以重置密码。
        </n-h2>
        <div class="pt-4">
            <n-alert type="warning">
                <div class="text-xl leading-relaxed">
                    请您联系
                    <n-a :href="'mailto:' + email">网站管理员</n-a>
                    提供相关证据以重置密码。
                    证据包括但不限于：
                    <br>
                    <br>
                    <ul class="list-disc">
                        <li>注册时所填邮箱；</li>
                        <li>您的用户名；</li>
                        <li>您上传的部分文件名，文件内容等。</li>
                    </ul>
                    <br>
                    邮件主题请填写为：
                    <n-text code>[Cloudhub] 密码重置请求</n-text>
                    ，以防被拒收或误判为垃圾邮件。
                </div>

            </n-alert>
        </div>
    </div>
</template>

<script setup>
const email = ((window.cloudhub || {}).config || {}).email

</script>

<style scoped>

</style>