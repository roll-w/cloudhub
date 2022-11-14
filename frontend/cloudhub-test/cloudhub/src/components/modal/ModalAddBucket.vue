<template>
  <!-- Modal -->
  <div class="modal fade" id="addBucket" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="exampleModalLabel">配置桶</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">

          <form>
            <div class="mb-3">
              <label for="name" class="form-label">桶名称:</label>

              <!-- 桶名称 -->
              <input v-model.lazy="bucket.name" type="text" class="form-control" id="name"
                     placeholder="" >
              <span>
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                     class="bi bi-exclamation-circle warning-icon" viewBox="0 0 16 16">
                  <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z"/>
                  <path
                      d="M7.002 11a1 1 0 1 1 2 0 1 1 0 0 1-2 0zM7.1 4.995a.905.905 0 1 1 1.8 0l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 4.995z"/>
                </svg>
                <span class="warning-font">不能和本用户已有桶重名</span>
              </span>
              <span>
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                     class="bi bi-exclamation-circle warning-icon" viewBox="0 0 16 16">
                  <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z"/>
                  <path
                      d="M7.002 11a1 1 0 1 1 2 0 1 1 0 0 1-2 0zM7.1 4.995a.905.905 0 1 1 1.8 0l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 4.995z"/>
                </svg>
                <span class="warning-font">不能和其他用户已有的桶重名</span>
              </span>
              <span>
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor"
                     class="bi bi-exclamation-circle warning-icon" viewBox="0 0 16 16">
                  <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14zm0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16z"/>
                  <path
                      d="M7.002 11a1 1 0 1 1 2 0 1 1 0 0 1-2 0zM7.1 4.995a.905.905 0 1 1 1.8 0l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 4.995z"/>
                </svg>
                <span class="warning-font">创建成功后不支持修改</span>
              </span>
            </div>
            <div>
              <div>桶策略:</div>
              <el-radio-group v-model="bucket.tactic" size="large">
                <el-radio-button label="私有" value="private"/>
                <el-radio-button label="公共读" value="publicR"/>
                <el-radio-button label="公共读写" value="publicRW"/>
              </el-radio-group>
            </div>

            <br>
            <div class="modal-footer">
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
              <button type="button" class="btn btn-primary" @click="addBucket">添加</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>

import {ElRadioButton, ElRadioGroup} from 'element-plus'
import {ref} from 'vue'

export default {

  name: "ModalAddBucket",
  components: {
    ElRadioButton,
    ElRadioGroup
  },
  setup() {
    // const tactic = ref('')
    const bucket = ref({
      name: "",
      tactic: ""
    })

    // 添加用户的方法
    const addBucket = () => {
      console.log(bucket.value) // 在控制台打印表单信息

      if (bucket.value.name === "" || bucket.value.tactic === "") {
        console.log("表单存在空值");
      } else {
        console.log("表单正常");
      }

    }

    return {
      bucket,
      addBucket,
      // tactic
    }
  }
}
</script>

<style scoped>

.warning-font{
  font-size: 1px;
}

.warning-icon{
  color: red;
}

</style>