<template>
  <div class="A" style="height: 100vh;overflow: hidden; position: relative">
    <div class="login" style="width: 300px; border-radius: 10px; margin: 0 auto; background-color: white; padding: 20px;
      position: absolute; top: 30%; left: 50%; transform: translateX(-50%) translateY(-50%);">

        <n-h5 style="text-align: center">
          <n-text type="primary">登录</n-text>
        </n-h5>


      <n-form ref="formRef">
        <n-form-item path="age" label="账号">
          <n-input />
        </n-form-item>
        <n-form-item path="password" label="密码">
          <n-input
              type="password"
              @keydown.enter.prevent
          />
        </n-form-item>

        <n-row :gutter="[0, 24]">
          <n-col :span="15">
            <div style="display: flex; justify-content: flex-end">
              <n-button >登录</n-button>
            </div>
          </n-col>
        </n-row>

      </n-form>

    </div>
  </div>
</template>

<script setup>

</script>

<style scoped>

div.login{
  border:solid 1px;
}

div.A{


}
</style>