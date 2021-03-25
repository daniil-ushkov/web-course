<template>
  <div class="comment">
    <div class="author">By {{ user }}</div>
    <div class="text">{{ comment.text }}</div>
  </div>
</template>

<script>
export default {
  name: "Comment",
  props: ["user", "comment"]
}
</script>

<style scoped>
.comment {
  margin-top: 1rem;
  background-color: #E1E1E1;
}
.comment .author {
  color: grey;
  margin-bottom: 0.2rem;
}

.comment .text {
  padding: 0.1rem;
  white-space: pre-line;
  word-break: break-word;
}
</style>