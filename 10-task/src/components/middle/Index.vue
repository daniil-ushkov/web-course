<template>
  <div>
    <Post v-for="post in posts" :post="post" :login="authorLogin(post)" :commentsNumber="commentsNumber(post)"
          :commentsWithUsers="[]" :key="post.id"/>
  </div>
</template>

<script>
import Post from "@/components/middle/post/Post";

export default {
  name: "Index",
  components: {
    Post
  },
  props: ["users", "posts", "comments"],
  methods: {
    authorLogin: function (post) {
      return this.users[post.userId].login;
    },
    commentsNumber: function (post) {
      return Object.values(this.comments).filter(c => c.postId === post.id).length;
    }
  }
}
</script>

<style scoped>

</style>