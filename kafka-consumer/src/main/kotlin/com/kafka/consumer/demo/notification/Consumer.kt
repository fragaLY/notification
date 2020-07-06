package com.kafka.consumer.demo.notification

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata
import org.springframework.kafka.support.Acknowledgment
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class Consumer(private val mapper: ObjectMapper, private val notificationService: NotificationService) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    @KafkaListener(groupId = "\${spring.kafka.consumer.group-id}", topics = ["notification-event"])
    fun onMessage(@Payload payload: String, meta: ConsumerRecordMetadata, acknowledgment: Acknowledgment) {
        val event = mapper.readValue(payload, NotificationEvent::class.java)
        val notification = when (event.type) {
            EventType.CREATE_NOTIFICATION -> notificationService.create(event.notification)
            EventType.UPDATE_NOTIFICATION -> notificationService.update(event.notification)
        }
        acknowledgment.acknowledge()
            .run { logger.info("[CONSUMER] Notification [${notification}] from partition ${meta.partition()} with offset ${meta.offset()} is processed") }
    }
}