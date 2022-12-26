import "utils/polyfills";
import "tslib";
import Vue from "vue";
import "./styles/main.scss";
import Router from "router.vue";
import Buefy from "buefy";
import VueShortkey from 'vue-shortkey';

Vue.use(Buefy);
Vue.use(VueShortkey);

if (process.env.NODE_ENV !== "production") {
  Vue.config.devtools = true;
  Vue.config.performance = true;
}

new Router().$mount("#app");
