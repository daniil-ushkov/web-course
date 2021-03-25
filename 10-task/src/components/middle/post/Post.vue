<template>
  <div>
  <article>
    <div class="title"><a href="#" @click="openPost()">{{ post.title }}</a></div>
    <div class="information">By {{ login }}</div>
    <div class="body">{{ post.text }}</div>
    <ul class="attachment">
      <li>Announcement of <a href="#">Codeforces Round #510 (Div. 1)</a></li>
      <li>Announcement of <a href="#">Codeforces Round #510 (Div. 2)</a></li>
    </ul>
    <div class="footer">
      <div class="left">
        <img src="@/assets/img/voteup.png" title="Vote Up" alt="Vote Up"/>
        <span class="positive-score">+173</span>
        <img src="@/assets/img/votedown.png" title="Vote Down" alt="Vote Down"/>
      </div>
      <div class="right">
        <img src="@/assets/img/date_16x16.png/" title="Publish Time" alt="Publish Time"/>
        <img src="@/assets/img/comments_16x16.png/" title="Comments" alt="Comments"/>
        <a href="#">{{ commentsNumber }}</a>
      </div>
    </div>
  </article>
  <Comment v-for="commentWithUser in commentsWithUsers" :user="commentWithUser.user" :comment="commentWithUser.comment"
           :key="commentWithUser.id"/>
  </div>
</template>

<script>
import Comment from "@/components/middle/post/Comment";
export default {
  name: "Post",
  components: {
    Comment
  },
  props: ["post", "login", "commentsNumber", "commentsWithUsers"],
  methods: {
    openPost: function () {
      this.$root.$emit("onChangePage", {name: 'Post', post: this.post});
    }
  }
}
</script>

<style scoped>
.title {
  word-break: break-word;
}

.body {
  word-break: break-word;
}
</style>