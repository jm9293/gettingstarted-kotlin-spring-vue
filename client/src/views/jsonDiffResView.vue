<template>
  <div>
    <v-container fluid>
      <v-row>
        <v-col cols-12>
          <v-banner elevation="1" single-line>비교결과 <strong>{{bool}}</strong></v-banner>
        </v-col>
      </v-row>
      <v-row>

        <v-col cols="6">
          <v-banner elevation="1" single-line>Json1</v-banner>
          <v-textarea
              filled
              name="result1"
              v-model="result1"
              v-bind:rows="rows1"
              readonly
          ></v-textarea>
        </v-col>
        <v-col cols="6">
          <v-banner elevation="1" single-line>Json2</v-banner>
          <v-textarea
              filled
              name="result2"
              v-model="result2"
              v-bind:rows="rows2"
              readonly
          ></v-textarea>
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

export default {
  name: "jsonDiffResView.vue",
  data() {
    return {
      result1: "",
      rows1: 3,
      result2: "",
      rows2: 3,
      bool : ""
    }
  },
  created() {
    if (this.$route.query.key) {
      this.getResult(this.$route.query.key);
    } else { //key 가 없다면
      this.$router.push("/jsonDiff")
    }

  },

  methods: {
    getResult(key) {
      axios.get("http://localhost:8765/jsondiff?key=" + key).then((res) => {

        if (res["data"]["status"] == "OK") { // 검색결과가 있을 경우

          let json1 = JSON.parse(res["data"]["result1"])
          let json2 = JSON.parse(res["data"]["result2"])

          this.bool = res["data"]["bool"] == "true" ? '일치' : '불일치'

          this.result1 += JSON.stringify(json1, this.nullReplace, 2);
          this.rows1 = this.result1.match(/\n/g).length + 3;


          this.result2 += JSON.stringify(json2,this.nullReplace , 2);
          this.rows2 = this.result1.match(/\n/g).length + 3;

        } else {
          alert("검색결과가 없습니다.")
          this.$router.push("/jsonDiff")
        }

      }).catch(() => {
        alert("서버와 통신이 안되거나 서버내부오류입니다.")
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
</style>