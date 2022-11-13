<template>
  <!-- Modal -->
  <div class="modal fade" id="ModalNode" tabindex="-1" aria-labelledby="exampleModalLabel" aria-hidden="true">
    <div class="modal-dialog">
      <div class="modal-content" style="width: 900px">
        <!--        <div class="modal-header">-->
        <!--          <h5 class="modal-title" id="exampleModalLabel">服务器节点信息</h5>-->
        <!--          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>-->
        <!--        </div>-->
        <div class="modal-body">
          <table class="table table-hover">
            <thead style="color: #909399">
            <tr>
              <th scope="col">node</th>
              <th scope="col">state</th>
              <th scope="col">load</th>
              <th scope="col">phymem</th>
              <th scope="col">ncpus</th>
              <th scope="col">allmem</th>
              <th scope="col">resi</th>
              <th scope="col">usrs</th>
              <th scope="col">tasks</th>
              <th scope="col">jobidlist</th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="node in server.nodeList" :key="node.name">
              <td>{{ node.name }}</td>
              <td>{{ node.state }}</td>
              <td>{{ node.load  }}</td>
              <td>{{ node.phymem }}</td>
              <td>{{ node.ncpus }}</td>
              <td>{{ node.allmem }}</td>
              <td>{{ node.resi }}</td>
              <td>{{ node.usrs }}</td>
              <td>{{ node.tasks }}</td>
              <td>{{ node.joblist }}</td>
            </tr>
            </tbody>
          </table>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: ['server'],
  name: "ModalNode"
}
</script>

<style scoped>

</style>