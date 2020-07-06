package com.kafka.producer.demo

import com.fasterxml.jackson.databind.ObjectMapper
import com.kafka.producer.demo.notification.EventProducer
import com.kafka.producer.demo.notification.EventType
import com.kafka.producer.demo.notification.Notification
import com.kafka.producer.demo.notification.NotificationEvent
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.TopicPartition
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.util.concurrent.SettableListenableFuture

/** @author Vadzim_Kavalkou */
@ExtendWith(MockitoExtension::class)
class EventProducerTest {

    @Mock
    lateinit var template: KafkaTemplate<Long, String>

    @Mock
    lateinit var mapper: ObjectMapper

    @InjectMocks
    lateinit var producer: EventProducer

    @Test
    fun `test event producer on failure`() {
        // given
        val notification = Notification(null, "from", "to")
        val json = mapper.writeValueAsString(notification)
        val event = NotificationEvent(1, notification, EventType.CREATE_NOTIFICATION)
        val future = SettableListenableFuture<SendResult<Long, String>>()
        future.setException(RuntimeException("Exception calling Kafka"))
        Mockito.`when`(mapper.writeValueAsString(event)).thenReturn(json)
        Mockito.`when`(template.sendDefault(1, json)).thenReturn(future)

        // when
        producer.produce(event)

        // then
        Mockito.verify(template).sendDefault(1, json)
    }

    @Test
    fun `test event producer on success`() {
        // given
        val notification = Notification(null, "from", "to")
        val event = NotificationEvent(1, notification, EventType.CREATE_NOTIFICATION)
        val json = mapper.writeValueAsString(event)

        val record = ProducerRecord("notification-event", event.key, json)
        val partition = TopicPartition("notification-event", 1)
        val meta = RecordMetadata(partition, 1, 1, 342, System.currentTimeMillis(), 1, 2)
        val result = SendResult(record, meta)
        val future = SettableListenableFuture<SendResult<Long, String>>()
        future.set(result)

        Mockito.`when`(mapper.writeValueAsString(event)).thenReturn(json)
        Mockito.`when`(template.sendDefault(1, json)).thenReturn(future)

        // when
        producer.produce(event)
    }
}