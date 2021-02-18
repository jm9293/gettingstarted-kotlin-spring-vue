<template>
  <div>
  <v-btn v-on:click="toggle=!toggle">로그 {{toggle ? "접기" : "펴기"}}</v-btn>
  <v-card v-if="toggle">
    <v-card-title>접속 기록</v-card-title>
    <v-card-text>
      <v-row v-for="log in logs" :key="log.id">
        <v-col>{{log.id}}</v-col>
        <v-col>{{log.payload}}</v-col>
        <v-col>{{moment(log.createTime).fromNow()}}</v-col>
      </v-row>
    </v-card-text>
  </v-card>
  </div>
</template>

<script>
import axios from "axios"

export default {
    name: 'LogsView',
    data() {
        return {
            logs: [],
            toggle: false
        }
    },
    async created() {
        await this.createLog()
        await this.loadLogs()
    },
    methods: {
        async createLog() {
            await axios.post('http://localhost:8765/logs', {payload: location.href})
        },
        async loadLogs() {
            let {data} = await axios.get('http://localhost:8765/logs')
            this.logs = data.logs
        }
    }
}
</script>

<style scoped>
</style>
