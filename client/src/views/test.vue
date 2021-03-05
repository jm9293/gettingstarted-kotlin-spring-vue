<template>
  <div>


    <v-container fluid>
      <v-row>
        <v-col cols-12>
          <v-banner elevation="1" single-line>비교결과 <strong>{{ bool }}</strong></v-banner>
        </v-col>
      </v-row>
      <v-row>
        <v-col cols="6">
          <v-banner elevation="1" single-line>Json1</v-banner>
          <div class="jsonBox">
            <div class="numberBox">
              <div class="numberLine" v-for="(item,index) in list">{{ index }}</div>
            </div>
            <pre><span class="jsonLine" v-for="item in list">{{ item }}</span></pre>
          </div>
        </v-col>
        <v-col cols="6">
          <v-banner elevation="1" single-line>Json2</v-banner>
          <div class="jsonBox">
            <div class="numberBox">
              <div class="numberLine" v-for="(item,index) in list">{{ index }}</div>
            </div>
            <pre><span class="jsonLine" v-for="item in list">{{ item }}</span></pre>
          </div>
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
import jsonLine from "@/components/jsonLine";

export default {
  name: "test.vue",

  data() {
    return {
      json: {"null": null},
      list: []
    }
  },

  components: {
    jsonLine
  },

  methods: {

    jsonSplit() {
      let json = "" +
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
          "}";

      this.list = json.split(/\n/)
    }

  },

  created() {
    this.jsonSplit()
  }


}
</script>

<style scoped>


.numberBox {
  display: inline-block;
}

.jsonBox {
  width: 100%;
}

.jsonLine{
  display: block;
}

.numberLine {
  background-color: bisque;
  display: block;
  font-family: monospace, monospace;
  text-align: right;
}

.jsonBox {
  display: inline-block;
  border: 1px solid black;
}

pre {
  display: inline-block;
  padding: 0;
  margin: 0;
}


</style>