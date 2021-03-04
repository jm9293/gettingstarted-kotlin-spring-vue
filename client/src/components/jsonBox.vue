<template>
  <div class="jsonBox" v-if="jsonData != null">
    <div class="numberBox">
      <div class="numberLine" v-for="item in row">{{ item }}</div>
    </div>
    <pre><json-line class="jsonLine" v-for="item in jsonList" :item="item"></json-line></pre>
  </div>
</template>

<script>
import JsonLine from "@/components/jsonLine"

export default {

  name: "jsonBox",

  components: {JsonLine},

  data() {
    return {
      jsonList: [],
      row: 0,
      diffResult: {},
      dep: []
    }
  },

  props: ["jsonData"],


  methods: {

    getJsonList(json) {
      return JSON.stringify(json, this.diffDataParse, " ").split(/\n/)
    },

    diffDataParse(key, value) {

      if (value instanceof Object && value.hasOwnProperty('value')) {
        return value["value"]
      }
      return value

    },

    spotSave(value, index, array) {

      if (value[value.length - 1] == "{") {
        this.dep.push(value.trim().substring(1, value.trim().length - 4))
        array[index] = {spot: this.spotToString(this.dep), value: value}
      } else if (value.trim() == '}' || value.trim() == '},') {
        array[index] = {spot: this.spotToString(this.dep), value: value}
        this.dep.pop()
      } else if (value[value.length - 1] == '[') {
        this.dep.push(value.slice(value.indexOf('"') + 1, value.indexOf('"', value.indexOf('"') + 1)))
        array[index] = {spot: this.spotToString(this.dep), value: value}
        this.dep.push(-1)
      } else if (value.trim() == ']' || value.trim() == '],') {
        this.dep.pop()
        array[index] = {spot: this.spotToString(this.dep), value: value}
        this.dep.pop()
      } else if (value[value.length - 1] == ',' || value[value.length - 1] == '"') {
        if (typeof (this.dep[this.dep.length - 1]) == "number") {
          this.dep[this.dep.length - 1]++
          array[index] = {spot: this.spotToString(this.dep), value: value}
        } else {
          this.dep.push(value.slice(value.indexOf('"') + 1, value.indexOf('"', value.indexOf('"') + 1)))
          array[index] = {spot: this.spotToString(this.dep), value: value}
          this.dep.pop()
        }
      }

    },

    spotToString(spotArray) {
      let result = "/"
      for (let i in spotArray) {
        if (spotArray[i] != "{") {
          if (spotArray[i] != -1) {
            result += spotArray[i] + "/"
          }
        }
      }
      return result
    }

  },

  created() {
    this.jsonList = this.getJsonList(this.jsonData)
    this.jsonList.forEach(this.spotSave)
    this.row = this.jsonList.length
    console.log(this.diffResult)
  }


}
</script>

<style scoped>
.numberBox {
  display: inline-block;
}

.jsonBox {
  overflow: auto;
  width: 100%;
  margin-top: 10px;
}

.jsonLine {
  display: block;
  font-size: 1rem;
  max-height: 18px;
  width: 640px;
  box-sizing: border-box;
  background-color: aqua;
  border-radius: 10px;
  padding: 0 10px;
  margin-left: 10px;
}

.jsonLine, .numberLine {
  margin-bottom: 5px;
}

.numberLine {
  background-color: bisque;
  display: block;
  font-family: monospace, monospace;
  text-align: right;
  font-size: 1rem;
  min-width: 30px;
  border-radius: 3px;
}

.jsonBox {
  display: inline-block;

}

pre {
  display: inline-block;
  padding: 0;
  margin: 0;
}
</style>