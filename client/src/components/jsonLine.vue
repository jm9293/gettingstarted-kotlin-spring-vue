<template>
  <span :style="style">{{item["value"]}}<hr/></span>
</template>

<script>
export default {
  name: "jsonLine.vue",
  props : ['index','item',"diffData"],
  data(){
    return{
      style : this.getStyle()
    }
  },
  methods : {
    getStyle(){
      for(let i in Object.keys(this.diffData)){
        let key = Object.keys(this.diffData)[i]
        if(key==this.item["spot"] || this.item["spot"].includes(key)){
          return this.diffData[key]
        }
      }
      return {}
    }
  }

}


</script>

<style scoped>


</style>