<template>
  <section>
    <div class="header">
      {{ post.title }}
    </div>
    <div class="body">
      {{ post.text }}
    </div>
    <div class="footer">
      <a href="#" @click="openPost">View all</a>
    </div>
  </section>
</template>

<script>
export default {
  name: "SidebarPost",
  props: ["post"],
  methods: {
    openPost: function () {
      this.$root.$emit("onChangePage", {name: 'Post', post: this.post});
    }
  }
}
</script>

<style scoped>
section {
  margin-bottom: 1rem;
}
.header {
  word-break: break-word;
}
.body {
  word-break: break-word;

}
</style>