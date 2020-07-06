package com.kafka.consumer.demo.notification

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.LongSerializer
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.kafka.config.KafkaListenerEndpointRegistry
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.utils.ContainerTestUtils
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.BlockingQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

class ConsumerIntegrationTest : IntegrationTest() {

    @field:Autowired
    lateinit var broker: EmbeddedKafkaBroker;

    @field:Autowired
    lateinit var template: KafkaTemplate<Long, String>

    @field:Autowired
    lateinit var registry: KafkaListenerEndpointRegistry

    @field:Autowired
    lateinit var repository: NotificationH2Repository

    @field:Autowired
    lateinit var mapper: ObjectMapper

    @SpyBean
    lateinit var service: NotificationService

    @SpyBean
    lateinit var consumer: Consumer

    lateinit var producer: Producer<Long, String>

    val invocations: BlockingQueue<Notification> = LinkedBlockingQueue()

    @BeforeEach
    internal fun setUp() {
        registry.listenerContainers.forEach { ContainerTestUtils.waitForAssignment(it, broker.partitionsPerTopic) }
        producer =
            DefaultKafkaProducerFactory<Long, String>(
                KafkaTestUtils.producerProps(broker), LongSerializer(), StringSerializer()
            ).createProducer()
    }

    @Test
    @Transactional
    fun `test consume create event`() {
        // given
        val event = NotificationEvent(1, Notification(null, "from", "to"), EventType.CREATE_NOTIFICATION)
        template.sendDefault(mapper.writeValueAsString(event)).get()
        val expected = Notification(1, "from", "to")
        val record = ProducerRecord<Long, String>("notification-event", event.key, mapper.writeValueAsString(event))

        //when
        producer.send(record)
        producer.flush()

        verify(service, Mockito.times(1)).create(ArgumentMatchers.isA(Notification::class.java))

        // then
        val notifications = repository.findAll().toList()
        assertTrue(notifications.size == 1)
        assertEquals(expected, notifications[0])
    }

    @Test
    @Transactional
    fun `test consume update event`() {
        // given
        val data =
            """{"key":1,"notification":{"id":1,"sender":"sender","receiver":"receiver"},"type":"UPDATE_NOTIFICATION"}"""
        template.sendDefault(data).get()
        val expected = Notification(1, "sender", "receiver")

        // when
        CountDownLatch(1).await(1, TimeUnit.SECONDS)

        // then
        val notifications = repository.findAll().toList()
        assertTrue(notifications.size == 1)
        assertEquals(expected, notifications[0])
    }
}