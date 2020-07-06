import Vue from 'vue'
import App from './App.vue'
import store from './_store/';
import LRU from 'lru-cache'
import Vuetify from "vuetify";
import "vuetify/dist/vuetify.min.css";
import VueMeta from 'vue-meta'

Vue.use(VueMeta)

Vue.config.productionTip = false

global.store = store;

const themeCache = new LRU({
    max: 10,
    maxAge: 1000 * 60 * 60,
})

const vuetify = new Vuetify({
    lang: {
        current: "en"
    },
    theme: {
        options: {
            themeCache,
        },
    }
});

Vue.use(Vuetify);

new Vue({
    name: 'Notification App',
    el: '#app',
    store,
    vuetify,
    render: h => h(App),
}).$mount('#app')
