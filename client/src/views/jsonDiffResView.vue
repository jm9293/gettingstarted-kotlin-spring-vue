<template>
  <div>
    <v-container fluid v-if = ready>
      <v-row>
        <v-col cols-12>
          <v-banner elevation="1" single-line>비교결과 <strong>{{bool}}</strong></v-banner>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="6">
          <v-banner elevation="1" single-line>Json1</v-banner>
          <json-box :json-data="result1"></json-box>
        </v-col>
        <v-col cols="6">
          <v-banner elevation="1" single-line>Json2</v-banner>
<!--          <json-box :json-data="result2"></json-box>-->
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="10" id="diffBtn">
          <v-btn block to="/jsonDiff">다시비교하기</v-btn>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script>
import axios from "axios"

import JsonBox from "@/components/jsonBox"

export default {

  name: "jsonDiffResView.vue",

  components: {JsonBox},

  data() {
    return {
      result1: null,
      rows1: 3,
      result2: null,
      rows2: 3,
      bool : "",
      ready : false
    }
  },
  created() {
    if (this.$route.query.key) {
      this.getResult(this.$route.query.key);
    } else {
      this.$router.push("/jsonDiff")
    }

  },

  methods: {
    getResult(key) {
      axios.get("http://localhost:8765/jsondiff?key=" + key).then((res) => {

        if (res["data"]["status"] == "OK") { // 검색결과가 있을 경우

          this.result1  = JSON.parse(res["data"]["result1"])
          this.result2 = JSON.parse(res["data"]["result2"])

          this.bool = res["data"]["bool"] == "true" ? '일치' : '불일치'

          this.ready = true
        } else {
          alert("검색결과가 없습니다.")
          this.$router.push("/jsonDiff")
        }

      }).catch(() => {
        alert("서버와 통신이 안되거나 서버내부오류입니다.")
        this.$router.push("/jsonDiff")
      })

    },

    nullReplace(key, value){
      if(value == "#null"){ // 치환
        return null
      }
      return value
    }


  }
}
</script>

<style scoped>
#diffBtn {
  margin: auto;
}

v-col{
  overflow: hidden;
}

</style>