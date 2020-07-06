<template>
  <v-container fluid>
    <v-card-text>
      <v-text-field autofocus clearable label="Event Key" ref="event-key" required v-model="event.key"/>
      <v-text-field autofocus clearable label="Notification Id" ref="notification-id" required
                    v-model="event.notification.id"/>
      <v-text-field clearable label="Sender" ref="sender" required v-model="event.notification.sender"/>
      <v-text-field clearable label="Receiver" ref="receiver" required v-model="event.notification.receiver"/>
    </v-card-text>
    <v-divider/>
    <v-card-actions>
      <v-btn @click.prevent="create(event)" color="green" text>
        Create Notification
      </v-btn>
      <v-spacer/>
      <v-btn @click.prevent="update(event)" color="blue" text>
        Update Notification
      </v-btn>
    </v-card-actions>
  </v-container>
</template>


<script>
    import NotificationService from "../service/NotificationService";

    export default {
        name: "Notification",
        data: () => {
            return {
                event: {
                    key: '',
                    notification: {
                        id: '',
                        sender: '',
                        receiver: ''
                    }
                }
            };
        },
        methods: {
            create(event) {
                NotificationService.create(event);
            },
            update(event) {
                NotificationService.update(event);
            }
        }
    }
</script>

<style scoped>

</style>