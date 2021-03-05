<template>
  <span @click="click" :style='item["diffData"]["style"]'>{{item["value"]}}</span>
</template>

<script>

export default {
  name: "jsonLine.vue",
  props : ['index','item'],
  methods : {
    click(){
      this.$store.commit('changeMessage' , {message : this.item["diffData"]["message"]});
    }
  },
}


</script>

<style scoped>




</style>