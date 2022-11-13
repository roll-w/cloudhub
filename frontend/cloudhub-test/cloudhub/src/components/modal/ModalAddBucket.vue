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
                     placeholder="please enter the name of the bucket" required>
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

</style>