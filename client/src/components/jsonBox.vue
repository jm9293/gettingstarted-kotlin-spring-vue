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

  props: ["jsonData", "diff" , "caseStyles"],


  methods: {

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
      } else if (value[value.length - 1] == ',' || value[value.length - 1] == '"' || value.trim()[0] == '"') {
        if (typeof (this.dep[this.dep.length - 1]) == "number") {
          this.dep[this.dep.length - 1]++
          array[index] = {spot: this.spotToString(this.dep), value: value}
        } else {
          this.dep.push(value.slice(value.indexOf('"') + 1, value.indexOf('"', value.indexOf('"') + 1)))
          array[index] = {spot: this.spotToString(this.dep), value: value}
          this.dep.pop()
        }
      }

      array[index]["diffData"] = this.getDiffValue(array[index]["spot"] , this.diff)

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
    },

    getDiffValue(spot , diffData){
      for(let i in Object.keys(diffData)){
        let key = Object.keys(diffData)[i]
        if(key==spot || spot.includes(key)){
          return {style : this.caseStyles[diffData[key]["case"]-1] , message : diffData[key]["message"]}
        }
      }
      return {style: {}, message: ""}
    }

  },

  created() {

    this.jsonList = this.jsonData.split(/\n/)
    this.jsonList.forEach(this.spotSave)
    this.row = this.jsonList.length

  }


}
</script>

<style scoped>
.numberBox {
  display: inline-block;
}

.jsonBox {
  width: 100%;
  margin-top: 10px;
  min-width: 640px;
}

.jsonLine {
  display: block;
  font-size: 1rem;
  max-height: 18px;
  width: 100%;
  box-sizing: border-box;
  border-radius: 10px;
  padding: 0 10px;
  margin-left: 10px;
}

.jsonLine, .numberLine {
  margin-bottom: 5px;
}

.numberLine {
  display: block;
  font-family: monospace, monospace;
  text-align: right;
  font-size: 1rem;
  min-width: 30px;
}

.jsonBox {
  display: inline-block;

}

pre {
  display: inline-block;
  padding: 0;
  margin: 0;
  width: 90%;
}
</style>