<template>
  <div>
    <v-container fluid>
      <v-row>
        <v-col cols-12>
          <v-banner elevation="1" single-line>Json1과 비교결과</v-banner>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="12">
          <v-textarea
              filled
              name="result"
              v-model="result"
              v-bind:rows="rows"
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
      result : "",
      rows : 3
    }
  },
  created() {
    if(this.$route.query.key){
      this.getResult(this.$route.query.key);
    }else{ //key 가 없다면
      this.$router.push("/jsonDiff")
    }

  },

  methods:{
    getResult(key){
      axios.get("http://localhost:8765/jsondiff?key="+key).then((res)=>{

        if(res["data"]["status"]=="OK"){ // 검색결과가 있을 경우

        let json = JSON.parse(res["data"]["result"])
        this.result = res["data"]["bool"] == "true" ? '일치\n' : '불일치\n'
        this.result += JSON.stringify(json,null,2);
        this.rows = this.result.match(/\n/g).length+3;

        }else{
          alert("검색결과가 없습니다.")
          this.$router.push("/jsonDiff")
        }

      }).catch(()=>{
        alert("서버와 통신이 안되거나 서버내부오류입니다.")
      })
    }
  }
}
</script>

<style scoped>
  #diffBtn{
    margin: auto;
  }
</style>