import Vue from "vue";
import Vuex from "vuex";

Vue.use(Vuex);

export default new Vuex.Store({
    state: {
        diffMessage : ""
    },
    mutations: {
        changeMessage(state , payload){
            state.diffMessage = payload.message
        }
    },
});