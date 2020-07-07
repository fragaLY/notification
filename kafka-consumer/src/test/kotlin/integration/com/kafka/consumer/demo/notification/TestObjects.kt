package com.kafka.consumer.demo.notification

import java.util.UUID

class TestObjects {

    companion object {
        private val ID = UUID.fromString("dc44bc33-494d-4f2a-a7d2-5b1062f3d062")
        val NOTIFICATION = Notification(ID, "sender", "receiver")
        val EVENT = NotificationEvent(1, NOTIFICATION, EventType.UPDATE_NOTIFICATION)
    }
}
