<template>
  <v-app>
    <v-app-bar app flat id="inspire">
      <v-switch
          hide-details
          v-model="$vuetify.theme.dark"
      ></v-switch>
      <v-spacer></v-spacer>
      <v-toolbar-title class="text-uppercase">
        <span>Notification Platform</span>
      </v-toolbar-title>
    </v-app-bar>
    <v-content>
      <v-container fluid>
        <div v-if="messages.length">
          <div class="alert alert-warning" v-bind:key="index" v-for="(message, index) in messages">{{message}}</div>
        </div>
        <Notifications/>
        <Notification/>
      </v-container>
    </v-content>
  </v-app>
</template>

<script>
    const Notification = () => import("./components/Notification");
    const Notifications = () => import("./components/Notifications");
    import {mapState} from "vuex";
    import store from "./_store";

    export default {
        name: "app",
        components: {Notification, Notifications},
        beforeCreate() {
            store.dispatch('updateMessages', []);
        },
        computed: {
            ...mapState(['theme', 'messages'])
        }
    };
</script>

<style>
</style>
