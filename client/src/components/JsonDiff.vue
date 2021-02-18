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
      alert("비교가즈아")
      this.result = this.json1
    }
  }

}
</script>

<style scoped>
  #diffBtn{
    margin: auto;
  }

</style>