<template>
  <div>
    <v-container fluid v-if="equalBool!=null">
      <v-row>
        <v-col cols-12>
          <v-banner elevation="1" single-line>비교결과 <strong>{{ equalBool }}</strong></v-banner>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="5">
          <div class="json">
          <v-banner elevation="1" single-line>Json1</v-banner>
          <json-box :json-data="json1" :diff="diffData1"></json-box>
          </div>
        </v-col>
        <v-col cols="5">
          <div class="json">
          <v-banner elevation="1" single-line>Json2</v-banner>
          <json-box :json-data="json2" :diff="diffData2"></json-box>
          </div>
        </v-col>
        <v-col cols="2">
          <v-banner elevation="1" single-line>차이점</v-banner>
          <v-textarea
              filled
              v-model="$store.state.diffMessage"
              rows="20"
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

import JsonBox from "@/components/jsonBox"

export default {

  name: "jsonDiffResView.vue",

  components: {JsonBox},

  data() {
    return {
      json1: null,
      diffData1: null,
      json2: null,
      diffData2: null,
      equalBool: null,
    }
  },
  created() {
    if (this.$route.query.key)
      this.getResult(this.$route.query.key);
    else
      this.$router.push("/jsonDiff")

    this.$store.commit('changeMessage' , {message : "  해당 줄을 클릭하면 메세지가 나옵니다."});
  },

  methods: {
    getResult(key) {
      axios.get("http://localhost:8765/jsondiff?key=" + key).then((res) => {

        if (res["data"]["status"] == "OK") { // 검색결과가 있을 경우
          this.json1 = res["data"]["json1"]
          this.json2 = res["data"]["json2"]
          this.diffData1 = JSON.parse(res["data"]["result1"])
          this.diffData2 = JSON.parse(res["data"]["result2"])
          this.equalBool = res["data"]["equalBool"] == true ? '일치' : '불일치'
        } else {
          alert("검색결과가 없습니다.")
          this.$router.push("/jsonDiff")
        }

      }).catch(() => {
        alert("서버와 통신이 안되거나 서버내부오류입니다.")
        this.$router.push("/jsonDiff")
      })

    },

  }
}
</script>

<style scoped>

#diffBtn {
  margin: auto;
}
.json {
  overflow: auto;
}
</style>