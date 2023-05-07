<template>
  <div class="p-5" >
      <n-grid x-gap="12" :cols="2">
          <n-grid-item :span="12">
              <n-statistic label="统计数据" :value="99">
                  <template #suffix>
                      / 100
                  </template>
              </n-statistic>
          </n-grid-item>
          <n-grid-item :span="12">
              <n-statistic label="活跃用户">
                  1,234,123
              </n-statistic>
          </n-grid-item>
      </n-grid>
  </div>
</template>

<script setup>
</script>
