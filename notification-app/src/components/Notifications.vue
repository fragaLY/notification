<template>
  <v-container fluid>
    <v-data-table :headers="headers" :items="events" class="elevation-3" v-if="events.length !== 0"/>
  </v-container>
</template>

<script>
    import {mapState} from "vuex";

    export default {
        name: 'Notifications',
        data: () => {
            return {
                headers: [
                    {text: 'Event Key', value: 'key'},
                    {text: 'Notification Id', value: 'notification.id'},
                    {text: 'Sender', value: 'notification.sender'},
                    {text: 'Receiver', value: 'notification.receiver'}
                ]
            };
        },
        metaInfo: () => {
            return {
                meta: [
                    {name: '_csrf', content: ''},
                    {name: '_csrf_header', content: ''},
                ]
            }
        },
        computed: {
            ...mapState(['events'])
        },
    }
</script>

<style scoped>
</style>
