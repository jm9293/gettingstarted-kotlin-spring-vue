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
          ></v-textarea>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="10" id="diffBtn">
          <v-btn block @click="diff">비교하기</v-btn>
        </v-col>

        <v-col cols="12" v-if="result!==''">
          <v-textarea
              filled
              name="result"
              label="비교결과"
              v-model="result"
              readonly
          ></v-textarea>
        </v-col>
      </v-row>
    </v-container>

  </div>
</template>

<script>
import axios from "axios"

export default {
  name : "jsonDiff",

  data() {
    return {
      json1 : "{}",
      json2 : "{}",
      result : ""
    }
  },

  methods: {
    diff() {
      let json1 , json2;
      try {
        json1 = JSON.parse(this.json1);
        console.log(json1)
        json2 = JSON.parse(this.json2);
        console.log(json2)
      } catch (error){
        if(error instanceof SyntaxError){
          alert("json 형식이 올바르지 않습니다.")
        }else{
          alert("기타에러")
        }
        this.clear();
        return;
      }

      let param = this.hashAndString(this.json1) + this.hashAndString(this.json2)


      let data = {
        "json1" : JSON.stringify(json1),
        "json2" : JSON.stringify(json2)
      }

      axios.post("http://localhost:8765/jsondiff?key=" + param, data).then((res)=>
          this.result = JSON.stringify(res["data"],null,2)
      );

    },

    clear(){
      this.json1 = "{}"
      this.json2 = "{}"
      this.result = ""
    },

    hashAndString(input){ // 해시 코드 생성 16진수로 변환
      let hash = 0, len = input.length;
      for (let i = 0; i < len; i++) {
        hash  = ((hash << 5) - hash) + input.charCodeAt(i)
        hash |= 0;
      }
      return hash.toString(16)
    }

  }



}
</script>

<style scoped>
  #diffBtn{
    margin: auto;
  }

</style>