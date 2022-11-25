<template>
  <!--  style="width: 1000px" -->
  <div class="container content-field">
    <div class="card">
      <div class="card-body">
        <slot>

        </slot>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "ContentBase"
}
</script>

<style scoped>
div.content-field{
  margin-top: 30px;
}
</style>