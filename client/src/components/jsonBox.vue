<template>
  <div class="jsonBox">
    {{int}}
    <div class="numberBox">
      <div class="numberLine" v-for="(item,index) in list">{{ index+1 }}</div>
    </div>
    <pre><span class="jsonLine" v-for="item in list">{{ item }}</span></pre>
  </div>
</template>

<script>
export default {
  name: "jsonBox",

  data() {
    return {
      list : []
    }
  },

  props : ["jsonData"],

  methods : {

    jsonSplit() {
      return  JSON.stringify(this.jsonData,this.diffDataParse,2).split(/\n/)
    },

    diffDataParse(key, value) {
      if(value["msg"]){
        // console.log(key + " , " + value["msg"])
        if(!(value["origin"] instanceof Object)){
          console.log(value["origin"])
        }

        return value["origin"]
      }

      if(!(value instanceof Object)){
        console.log(value)
      }
      return value
    },



  },

  created() {
    this.list =  this.jsonSplit()
  },


}
</script>

<style scoped>
.numberBox {
  display: inline-block;
}

.jsonBox {
  overflow: auto;
  min-width: 100%;
  margin-top: 10px;
}

.jsonLine{
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

.jsonLine , .numberLine {
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