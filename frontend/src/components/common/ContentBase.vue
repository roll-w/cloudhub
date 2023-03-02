<template>
  <div class="container mt-4">
    <div class="card">
      <div class="card-body">
        <slot></slot>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "ContentBase"
}
</script>