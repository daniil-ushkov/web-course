<template>
  <div class="form">
    <div class="body">
      <form class="comment-form" @submit.prevent="onWriteComment" v-if="!hidden">
        <div class="field">
          <div class="name">
            <label for="text">Write your comment</label>
          </div>
          <div class="value">
            <textarea id="text" name="text" v-model="text"></textarea>
          </div>
        </div>
        <div class="error">{{ error }}</div>
        <div class="button-field">
          <input type="submit" value="Write">
        </div>
      </form>
    </div>
  </div>
</template>

<script>
export default {
  name: "WriteComment",
  props: ["post"],
  data: function () {
    return {
      text: "",
      error: "",
      hidden: false,
    }
  },
  beforeMount() {
    this.$root.$on("onWriteCommentValidationError", error => {
      this.error = error;
      this.hidden = false;
    });
    this.$root.$on("onWriteCommentAccepted", () => {
      this.text = "";
      this.hidden = false;
    });
  },
  methods: {
    onWriteComment: function () {
      this.hidden = true;
      this.$root.$emit("onWriteComment", this.post, this.text);
    }
  }
}
</script>

<style scoped>
.comment-form {
  margin: 1rem auto;
}

.comment-form .field {
  margin-bottom: 1rem;
}

.comment-form .field input, .comment-form .field textarea {
  width: 100%;
}

.comment-form .field textarea {
  min-height: 4rem;
  max-height: 10rem;
  min-width: 99.6%;
  max-width: 99.6%;
}

.comment-form .button-field input {
  padding: 0.1rem 1.5rem;
}
</style>