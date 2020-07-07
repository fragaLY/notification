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

class ConsumerIntegrationTest : KafkaIntegrationTest() {

    @field:Autowired
    lateinit var broker: EmbeddedKafkaBroker;

    @field:Autowired
    lateinit var template: KafkaTemplate<Long, String>

    @field:Autowired
    lateinit var registry: KafkaListenerEndpointRegistry

    @field:Autowired
    lateinit var repository: NotificationRepository

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
        val event = TestObjects.EVENT
        template.sendDefault(mapper.writeValueAsString(event)).get()
        val record = ProducerRecord<Long, String>("notification-event", event.key, mapper.writeValueAsString(event))

        //when
        producer.send(record)
        producer.flush()

        //then
        CountDownLatch(1).await(3, TimeUnit.SECONDS)
        val notifications = repository.findAll().toList()
        assertTrue(notifications.size == 1)
        assertEquals(TestObjects.NOTIFICATION, notifications[0])
    }

    @Test
    @Transactional
    fun `test consume update event`() {
        // given
        template.sendDefault(mapper.writeValueAsString(TestObjects.EVENT)).get()

        // when
        CountDownLatch(1).await(3, TimeUnit.SECONDS)

        // then
        val notifications = repository.findAll().toList()
        assertTrue(notifications.size == 1)
        assertEquals(TestObjects.NOTIFICATION, notifications[0])
    }
}