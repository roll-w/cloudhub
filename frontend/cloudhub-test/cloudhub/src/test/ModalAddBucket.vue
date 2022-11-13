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


            <div class="mb-3">
              <label for="exampleFormControlTextarea1" class="form-label">桶策略:</label>
              <!-- 桶策略 -->
              <div class="form-check">
                <input v-model="bucket.tactic" value="private" class="form-check-input" type="radio"
                       name="flexRadioDefault"
                       id="private">
                <label class="form-check-label" for="private">
                  私有
                </label>
              </div>
              <div class="form-check">
                <input v-model="bucket.tactic" value="publicR" class="form-check-input" type="radio"
                       name="flexRadioDefault"
                       id="publicR">
                <label class="form-check-label" for="publicR">
                  公共读
                </label>
              </div>
              <div class="form-check">
                <input v-model="bucket.tactic" value="publicRW" class="form-check-input" type="radio"
                       name="flexRadioDefault"
                       id="publicRW">
                <label class="form-check-label" for="publicRW">
                  公共读写
                </label>
              </div>
            </div>



            <div>
              <el-radio-group v-model="radio1" size="large">
                <el-radio-button label="New York" />
                <el-radio-button label="Washington" />
                <el-radio-button label="Los Angeles" />
                <el-radio-button label="Chicago" />
              </el-radio-group>
            </div>


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

import {ElRadioButton,ElRadioGroup} from 'element-plus'
import { ref } from 'vue'

export default {

  name: "ModalAddBucket",
  components:{
    ElRadioButton,
    ElRadioGroup
  },
  setup() {
    const radio1 = ref('New York')
    const bucket = {
      name: "",
      tactic: "" //
    }


    // 添加用户的方法
    const addBucket = () => {
      console.log(bucket) // 在控制台打印表单信息

      if (bucket.name === "" || bucket.tactic === "") {
        console.log("表单存在空值");
      } else {
        console.log("表单正常");
      }

    }

    return {
      bucket,
      addBucket,
      radio1
    }
  }
}
</script>

<style scoped>

</style>