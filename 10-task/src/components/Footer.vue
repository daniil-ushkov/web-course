<template>
  <footer>
    <a href="/">Codeforces</a> &copy; 2020 by Mike Mirzayanov
    <div>{{ usersNumber }} registered users</div>
    <div>{{ postsNumber }} posts written</div>
  </footer>
</template>

<script>
export default {
  name: "Footer",
  props: ["usersNumber", "postsNumber"]
}
</script>

<style scoped>

</style>