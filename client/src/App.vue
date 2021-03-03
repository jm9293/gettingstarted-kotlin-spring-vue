<template>
  <div id="app">
    <v-btn to="log">로그보기</v-btn>
    <v-btn to="jsonDiff">json 비교하기</v-btn>
    <v-btn to="test">테스트</v-btn>
    <hr>
    <router-view />
  </div>
</template>

<script>

export default {
  name: 'App',

  created() {
  },
  methods: {
  }
}
</script>

<style>

</style>
