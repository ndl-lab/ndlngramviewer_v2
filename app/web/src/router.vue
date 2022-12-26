<style lang="scss">
</style>

<template>
  <router-view />
</template>

<script lang="ts">
import Component from "vue-class-component";
import Vue from "vue";
import VueRouter from "vue-router";
import "./styles/main.scss";
import MainVue from "main.vue";
import * as Config from "config";

Vue.use(VueRouter);

let router: VueRouter = new VueRouter({
  mode: "history",
  base: Config.BASE_PATH,
  routes: [
    { path: "/", name: "top", component: MainVue },
    { path: "*", redirect: { name: "top" } }
  ]
});

@Component({ router: router })
export default class Router extends Vue {}
</script>