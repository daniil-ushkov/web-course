<template>
  <div>
    <Post v-for="post in posts" :post="post" :show-comments="false" :key="post.id"/>
  </div>
</template>

<script>
import Post from "@/components/middle/post/Post";

export default {
  name: "Index",
  components: {
    Post
  },
  props: ["posts"],
  methods: {
  }
}
</script>

<style scoped>

</style>