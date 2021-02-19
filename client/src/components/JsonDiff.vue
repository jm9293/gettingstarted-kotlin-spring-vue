<template>
  <div id="jsonDiff">

    <v-container fluid>
      <v-row>
        <v-col cols-12>
          <v-banner elevation="1" single-line>Json 비교하기</v-banner>
        </v-col>
      </v-row>

      <v-row>
        <v-col
            cols="12"
            md="6"
        >
          <v-textarea
              filled
              name="json1"
              label="JSON-1"
              v-model="json1"
              @focusout="inputOfJSON(1)"
              rows="20"
          ></v-textarea>
        </v-col>
        <v-col
            cols="12"
            md="6"
        >
          <v-textarea
              filled
              name="json2"
              label="JSON-2"
              v-model="json2"
              @focusout="inputOfJSON(2)"
              rows="20"
          ></v-textarea>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="10" id="diffBtn">
          <v-btn block @click="diff">비교하기</v-btn>
        </v-col>
      </v-row>
    </v-container>

  </div>
</template>

<script>
import axios from "axios"
import sha256 from 'crypto-js/sha256';
export default {
  name : "jsonDiff",

  data() {
    return {
      json1 : "",
      json2 : "",
    }
  },

  methods: {
    diff() {
      let json1 , json2;
      try { // JSON으로 변환해서 문법검사 및 변환
        json1 = JSON.parse(this.json1);
        json2 = JSON.parse(this.json2);
      } catch (error){
        if(error instanceof SyntaxError){
          alert("json 형식이 올바르지 않습니다.")
        }else{
          alert("기타에러")
        }
        this.clear();
        return;
      }


      let param = sha256(this.json1).toString() + sha256(this.json2).toString()


      let data = {
        "json1" : JSON.stringify(json1),
        "json2" : JSON.stringify(json2)
      }

      axios.post("http://localhost:8765/jsondiff?key=" + param , data).then((res)=>{ // 서버에 보낸후 결과 값을 보기위해 push
            if(res["data"]=="OK"){
              this.$router.push("/jsonDiffRes?key=" + param)
            }else {
              alert("입력값이 잘못되었거나 서버 오류")
            }
      }).catch(()=>{
        alert("서버와 통신이 안되거나 서버내부오류입니다.")
      })

    },

    clear(){ // 비우기
      this.json1 = ""
      this.json2 = ""
    },

    inputOfJSON(num){ // 복붙했을때 json 형식이 맞으면 변환
      try {
        if(num===1){
          console.log("s")
          let json = JSON.parse(this.json1);
          this.json1 = JSON.stringify(json,null,2)
        }else{
          let json = JSON.parse(this.json2);
          this.json2 = JSON.stringify(json,null,2)
        }

      } catch (error){}

    },


  }



}
</script>

<style scoped>
  #diffBtn{
    margin: auto;
  }

</style>