<template>
  <div class="middle">
    <Sidebar :posts="viewPostsSidebar"/>
    <main>
      <Index v-if="page === 'Index'" :posts="viewPostsIndex" :users="viewUsers"/>
      <Enter v-if="page === 'Enter'"/>
      <Register v-if="page === 'Register'"/>
      <Post v-if="page.name === 'Post'" :post="page.post" :show-comments="true"/>
      <Users v-if="page === 'Users'" :users="viewUsers"/>
      <WritePost v-if="page === 'WritePost'"/>
    </main>
  </div>
</template>

<script>
import Sidebar from "@/components/sidebar/Sidebar";
import Index from "@/components/middle/Index";
import Enter from "@/components/middle/Enter";
import Register from "@/components/middle/Register";
import Users from "@/components/middle/usertable/Users";
import Post from "@/components/middle/post/Post";
import WritePost from "@/components/middle/WritePost";

export default {
  name: "Middle",
  data: function () {
    return {
      page: "Index"
    }
  },
  components: {
    WritePost,
    Post,
    Users,
    Register,
    Enter,
    Index,
    Sidebar
  },
  props: ["users", "posts"],
  computed: {
    viewUsers: function () {
      return Object.values(this.users).sort((a, b) => a.id - b.id).slice(0, 40);
    },
    viewPostsSidebar: function () {
      return Object.values(this.posts).sort((a, b) => b.id - a.id).slice(0, 2);
    },
    viewPostsIndex: function () {
      return Object.values(this.posts).sort((a, b) => b.id - a.id).slice(0, 10);
    }
  },
  methods: {
  },
  beforeCreate() {
    this.$root.$on("onChangePage", (page) => this.page = page)
  }
}
</script>

<style scoped>

</style>