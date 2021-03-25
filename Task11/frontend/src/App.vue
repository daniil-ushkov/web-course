<template>
  <div id="app">
    <Header :user="user"/>
    <Middle :users="users" :posts="posts"/>
    <Footer/>
  </div>
</template>

<script>
import Header from "./components/Header";
import Middle from "./components/Middle";
import Footer from "./components/Footer";
import axios from "axios"

export default {
  name: 'App',
  components: {
    Footer,
    Middle,
    Header
  },
  data: function () {
    return {
      user: null,
      users: [],
      posts: []
    }
  },
  beforeMount() {
    if (localStorage.getItem("jwt") && !this.user) {
      this.$root.$emit("onJwt", localStorage.getItem("jwt"));
    }

    axios.get("/api/1/users").then(response => {
      this.users = response.data;
    });

    axios.get("/api/1/posts").then(response => {
      this.posts = response.data;
    });
  },
  beforeCreate() {
    this.$root.$on("onEnter", (login, password) => {
      // Validation
      // if (password === "") {
      //   this.$root.$emit("onEnterValidationError", "Password is required");
      //   return;
      // }

      axios.post("/api/1/jwt", {
        login, password
      }).then(response => {
        localStorage.setItem("jwt", response.data);
        this.$root.$emit("onJwt", response.data);
      }).catch(error => {
        this.$root.$emit("onEnterValidationError", error.response.data);
      });
    });

    this.$root.$on("onRegister", (login, name, password) => {
      // Validation
      // if (login.length < 3 || login.length > 16) {
      //   this.$root.$emit("onRegisterValidationError", "Login length should be in range from 3 to 16 chars");
      //   return;
      // }
      // if (!/^[a-zA-Z()]+$/.test(login)) {
      //   this.$root.$emit("onRegisterValidationError", "Login should contain only latin chars");
      //   return;
      // }
      // if (name.length < 1 || name.length > 32) {
      //   this.$root.$emit("onRegisterValidationError", "Name length should be in range from 1 to 32 chars");
      //   return;
      // }
      // if (password === "") {
      //   this.$root.$emit("onRegisterValidationError", "Password is required");
      //   return;
      // }
      // if (password.length < 4 || password.length > 20) {
      //   this.$root.$emit("onRegisterValidationError", "Password length should be in range from 4 to 20 chars");
      //   return;
      // }

      axios.post("/api/1/users/register", {
        login, name, password
      }).then(() => {
        axios.get("/api/1/users").then(response => {
          this.users = response.data;
        });
        this.$root.$emit("onEnter", login, password);
      }).catch(error => {
        this.$root.$emit("onRegisterValidationError", error.response.data);
      });
    });

    this.$root.$on("onJwt", (jwt) => {
      localStorage.setItem("jwt", jwt);

      axios.get("/api/1/users/auth", {
        params: {
          jwt
        }
      }).then(response => {
        this.user = response.data;
        this.$root.$emit("onChangePage", "Index");
      }).catch(() => this.$root.$emit("onLogout"))
    });

    this.$root.$on("onLogout", () => {
      localStorage.removeItem("jwt");
      this.user = null;
    });

    this.$root.$on("onWritePost", (title, text) => {
      let jwt = localStorage.getItem('jwt');
      axios.post("/api/1/posts", {jwt, title, text})
          .then(() => {
            axios.get("/api/1/posts").then(response => {
              this.posts = response.data;
            });
            this.$root.$emit("onChangePage", "Index");
          })
          .catch((error) => this.$root.$emit("onWritePostValidationError", error.response.data));
    })

    this.$root.$on("onWriteComment", (post, text) => {
      let jwt = localStorage.getItem('jwt');
      axios.post("api/1/comments", {jwt, post, text})
          .then((response) => {
            this.posts.find(p => p.id === post.id).comments.push(response.data);
            this.$root.$emit("onWriteCommentAccepted");
          })
          .catch((error) => {
            if (error.response.status === 500) {
              this.$root.$emit("onWriteCommentValidationError", "Internal server error");
            } else {
              this.$root.$emit("onWriteCommentValidationError", error.response.data);
            }
          });
    })
  }
}
</script>

<style>
#app {

}
</style>
