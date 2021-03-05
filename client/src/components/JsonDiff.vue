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
        <v-col cols="10" class="diffBtn">
          <v-btn block @click="sample">샘플데이터 입력</v-btn>
        </v-col>
        <v-col cols="10" class="diffBtn">
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


      let param = sha256(this.json1+this.json2).toString()


      let data = {
        "json1" : JSON.stringify(json1 , null ,1),
        "json2" : JSON.stringify(json2 , null ,1)
      }

      axios.post("http://localhost:8765/jsondiff?key=" + param , data).then((res)=>{ // 서버에 보낸후 결과 값을 보기위해 push
            if(res["data"]["status"]=="OK"){
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
          let json = JSON.parse(this.json1);
          this.json1 = JSON.stringify(json,null,2)
        }else{
          let json = JSON.parse(this.json2);
          this.json2 = JSON.stringify(json,null,2)
        }

      } catch (error){}

    },


    sample(){
      this.json1= "" +
          "{\n" +
          "  \"Aidan Gillen\": {\n" +
          "    \"array\": [\n" +
          "      \"Game of Thron\\\"es\",\n" +
      "      \"The Wire\"\n" +
      "    ],\n" +
      "    \"string\": \"some string\",\n" +
      "    \"int\": 2,\n" +
      "    \"aboolean\": true,\n" +
      "    \"boolean\": true,\n" +
      "    \"object\": {\n" +
      "      \"foo\": \"bar\",\n" +
      "      \"object1\": {\n" +
      "        \"new prop1\": \"new prop value\"\n" +
      "      },\n" +
      "      \"object2\": {\n" +
      "        \"new prop1\": \"new prop value\"\n" +
      "      },\n" +
      "      \"object3\": {\n" +
      "        \"new prop1\": \"new prop value\"\n" +
      "      },\n" +
      "      \"object4\": {\n" +
      "        \"new prop1\": \"new prop value\"\n" +
      "      }\n" +
      "    }\n" +
      "  },\n" +
      "  \"Amy Ryan\": {\n" +
      "    \"one\": \"In Treatment\",\n" +
      "    \"two\": \"The Wire\"\n" +
      "  },\n" +
      "  \"Annie Fitzgerald\": [\n" +
      "    \"Big Love\",\n" +
      "    \"True Blood\"\n" +
      "  ],\n" +
      "  \"Anwan Glover\": [\n" +
      "    \"Treme\",\n" +
      "    \"The Wire\"\n" +
      "  ],\n" +
      "  \"Alexander Skarsgard\": [\n" +
      "    \"Generation Kill\",\n" +
      "    \"True Blood\"\n" +
      "  ],\n" +
      "  \"Clarke Peters\": null\n" +
      "}" ;

      this.json2 = "{\n" +
          "  \"Aidan Gillen\": {\n" +
          "    \"array\": [\n" +
          "      \"Game of Thrones\",\n" +
          "      \"The Wire\"\n" +
          "    ],\n" +
          "    \"string\": \"some string\",\n" +
          "    \"int\": \"2\",\n" +
          "    \"otherint\": 4,\n" +
          "    \"aboolean\": \"true\",\n" +
          "    \"boolean\": false,\n" +
          "    \"object\": {\n" +
          "      \"foo\": \"bar\"\n" +
          "    }\n" +
          "  },\n" +
          "  \"Amy Ryan\": [\n" +
          "    \"In Treatment\",\n" +
          "    \"The Wire\"\n" +
          "  ],\n" +
          "  \"Annie Fitzgerald\": [\n" +
          "    \"True Blood\",\n" +
          "    \"Big Love\",\n" +
          "    \"The Sopranos\",\n" +
          "    \"Oz\"\n" +
          "  ],\n" +
          "  \"Anwan Glover\": [\n" +
          "    \"Treme\",\n" +
          "    \"The Wire\"\n" +
          "  ],\n" +
          "  \"Alexander Skarsg?rd\": [\n" +
          "    \"Generation Kill\",\n" +
          "    \"True Blood\"\n" +
          "  ],\n" +
          "  \"Alice Farmer\": [\n" +
          "    \"The Corner\",\n" +
          "    \"Oz\",\n" +
          "    \"The Wire\"\n" +
          "  ]\n" +
          "}"
    }


  }



}
</script>

<style scoped>
  .diffBtn{
    margin: auto;
  }

</style>