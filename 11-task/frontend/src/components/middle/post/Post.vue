<template>
  <div>
    <article>
      <div class="title"><a href="#" @click="openPost()">{{ post.title }}</a></div>
      <div class="information">By {{ post.user.login }}</div>
      <div class="body">{{ post.text }}</div>
      <div class="footer">
<!--        <div class="left">-->
<!--          <img src="@/assets/img/voteup.png" title="Vote Up" alt="Vote Up"/>-->
<!--          <span class="positive-score">+173</span>-->
<!--          <img src="@/assets/img/votedown.png" title="Vote Down" alt="Vote Down"/>-->
<!--        </div>-->
        <div class="right">
          <img src="@/assets/img/date_16x16.png/" title="Publish Time" alt="Publish Time"/>
          {{ post.creationTime }}
          <img src="@/assets/img/comments_16x16.png/" title="Comments" alt="Comments"/>
          <a href="#">{{ post.comments.length }}</a>
        </div>
      </div>
    </article>
    <div v-if="showComments">
      <WriteComment v-if="entered()" :post="post"/>
      <Comment v-for="comment in viewComments" :comment="comment" :key="comment.id"/>
    </div>
  </div>
</template>

<script>
import WriteComment from "@/components/middle/post/WriteComment";
import Comment from "@/components/middle/post/Comment";
export default {
  name: "Post",
  components: {Comment, WriteComment},
  props: ["post", "showComments"],
  methods: {
    openPost: function () {
      this.$root.$emit("onChangePage", {name: 'Post', post: this.post});
    },
    entered: function () {
      return localStorage.getItem('jwt') !== null;
    },
  },
  computed: {
    viewComments: function() {
      return Object.values(this.post.comments).sort((a, b) => a.id - b.id).slice(0, 40);
    }
  },
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