import Vue from "vue";
import VueRouter from "vue-router";
import Log from '@/views/log.vue'
import JsonDiff from "@/views/jsonDiffVeiw.vue";
import JsonDiffRes from '@/views/jsonDiffResView.vue'


Vue.use(VueRouter);

const routes = [
    {
        path: "/",
        name: "Home",
        redirect : "/log"
    },
    {
        path: "/log",
        name: "log",
        component : Log

    },
    {
        path: "/jsonDiff",
        name: "jsonDiff",
        component : JsonDiff
    },
    {
        path: "/jsonDiffRes",
        name: "jsonDiffRes",
        component : JsonDiffRes
    },


];

const router = new VueRouter({
    mode: "history",
    base: process.env.BASE_URL,
    routes,
});

export default router;
