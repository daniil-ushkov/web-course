<template>
  <div class="middle">
    <Sidebar :posts="viewPosts"/>
    <main>
      <Index v-if="page === 'Index'" :posts="viewPostsIndex" :users="users" :comments="comments"/>
      <Post v-if="page.name === 'Post'" :post="page.post" :login="authorLogin(page.post)"
            :commentsNumber="postCommentsWithUsers(page.post).length" :commentsWithUsers = "postCommentsWithUsers(page.post)"/>
      <Register v-if="page === 'Register'"/>
      <Enter v-if="page === 'Enter'"/>
      <WritePost v-if="page === 'WritePost'"/>
      <EditPost v-if="page === 'EditPost'"/>
      <Users v-if="page === 'Users'" :users="viewUsers"/>
    </main>
  </div>
</template>

<script>
import Sidebar from "@/components/sidebar/Sidebar";
import Index from "@/components/middle/Index";
import Post from "@/components/middle/post/Post";
import Enter from "@/components/middle/auth/Enter";
import Register from "@/components/middle/auth/Register";
import WritePost from "@/components/middle/WritePost";
import EditPost from "@/components/middle/EditPost";
import Users from "@/components/middle/usertable/Users";

export default {
  name: "Middle",
  data: function () {
    return {
      page: "Index"
    }
  },
  components: {
    WritePost,
    Enter,
    Register,
    Index,
    Post,
    Sidebar,
    EditPost,
    Users
  },
  props: ["users", "posts", "comments"],
  computed: {
    viewUsers: function () {
      return Object.values(this.users).sort((a, b) => a.id - b.id).slice(0, 40);
    },
    viewPosts: function () {
      return Object.values(this.posts).sort((a, b) => b.id - a.id).slice(0, 2);
    },
    viewPostsIndex: function () {
      return Object.values(this.posts).sort((a, b) => b.id - a.id).slice(0, 10);
    }
  },
  methods: {
    authorLogin: function (post) {
      return this.users[post.userId].login;
    },
    postCommentsWithUsers: function (post) {
      return Object.values(this.comments)
          .filter(c => c.postId === post.id)
          .sort((a, b) => a.id - b.id)
          .map(c => { return {comment: c, user: this.users[c.userId].login}});
    }
  },
  beforeCreate() {
    this.$root.$on("onChangePage", (page) => this.page = page)
  }
}
</script>

<style scoped>

</style>