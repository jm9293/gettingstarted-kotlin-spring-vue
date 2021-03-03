<template>
  <div>

    <div class="numberLine">
      <div v-for="(item,index) in list">{{ index }}</div>
    </div>
    <pre><json-line :item="item" :index="index" v-for="(item , index) in list"></json-line></pre>
  </div>
</template>

<script>
import jsonLine from "@/components/jsonLine";

export default {
  name: "test.vue",

  data() {
    return {
      json: {"null": null},
      list: []
    }
  },

  components: {
    jsonLine
  },

  methods: {

    jsonSplit() {
      let json = {
        data: null,
        arr: [1, 2, 3]
      }

      this.list = JSON.stringify(json, null, 2).split(/\n/)
    }

  },

  created() {
    this.jsonSplit()
  }


}
</script>

<style scoped>
span {
  display: block;
}

div {
  display: block;
  font-family: monospace,monospace;
}

.numberLine{
  display: inline-block;
}

pre{
  display: inline-block;
  padding: 0;
  margin: 0;
}



</style>