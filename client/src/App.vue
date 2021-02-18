<template>
  <div id="app">
    <LogsView></LogsView>
    <JsonDiff></JsonDiff>
  </div>
</template>

<script>
import LogsView from "@/components/LogsView"
import JsonDiff from "@/components/JsonDiff"
export default {
  name: 'App',
  components: {
      LogsView,
      JsonDiff
  },
  created() {
  },
  methods: {
  }
}
</script>

<style>

</style>
