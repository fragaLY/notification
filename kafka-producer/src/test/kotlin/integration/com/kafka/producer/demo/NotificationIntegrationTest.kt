package com.kafka.producer.demo

import com.kafka.producer.demo.notification.Notification
import com.kafka.producer.demo.notification.NotificationEvent
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.common.serialization.LongDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.utils.KafkaTestUtils
import java.util.*
import kotlin.collections.HashMap

/** @author Vadzim_Kavalkou */
internal class NotificationIntegrationTest : IntegrationTest() {

    @field:Autowired
    lateinit var restTemplate: TestRestTemplate

    @field:Autowired
    lateinit var broker: EmbeddedKafkaBroker

    lateinit var consumer: Consumer<Long, String>

    @BeforeEach
    internal fun setUp() {
        val properties = HashMap(KafkaTestUtils.consumerProps("group1", "true", broker))
        consumer = DefaultKafkaConsumerFactory(properties, LongDeserializer(), StringDeserializer()).createConsumer()
        broker.consumeFromAllEmbeddedTopics(consumer)
    }

    @AfterEach
    internal fun tearDown() {
        consumer.close()
    }

    @Test
    fun `test creating a new notification when notification is valid`() {
        // given
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        val request = HttpEntity(NotificationEvent(1L, Notification(UUID.randomUUID(), "from", "to"), null), headers)
        val expected =
            """{"key":1,"notification":{"id":null,"sender":"from","receiver":"to"},"type":"CREATE_NOTIFICATION"}"""

        // when
        val actual = restTemplate.exchange("/api/notifications", HttpMethod.POST, request, Void::class.java)

        // then
        assertEquals(HttpStatus.NO_CONTENT, actual.statusCode)
        assertEquals(
            expected, KafkaTestUtils.getSingleRecord(consumer, "notification-event").value()
        )
    }

    @Test
    fun `test creating a new notification when notification is not valid`() {
        // given
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        val request = HttpEntity(NotificationEvent(1L, Notification(null, "", ""), null), headers)

        // when
        val actual = restTemplate.exchange("/api/notifications", HttpMethod.POST, request, Void::class.java)

        // then
        assertEquals(HttpStatus.BAD_REQUEST, actual.statusCode)
        assertTrue(KafkaTestUtils.getRecords(consumer, 3).isEmpty)
    }

    @Test
    fun `test updating notification when notification is valid`() {
        // given
        val id = UUID.randomUUID()
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        val request = HttpEntity(NotificationEvent(1L, Notification(id, "from", "to"), null), headers)
        val expected =
            """{"key":1,"notification":{"id":"$id","sender":"from","receiver":"to"},"type":"UPDATE_NOTIFICATION"}"""

        // when
        val actual = restTemplate.exchange("/api/notifications/$id", HttpMethod.PUT, request, Void::class.java)

        // then
        assertEquals(HttpStatus.NO_CONTENT, actual.statusCode)
        assertEquals(
            expected, KafkaTestUtils.getSingleRecord(consumer, "notification-event").value()
        )
    }

    @Test
    fun `test updating notification when notification is not valid`() {
        // given
        val headers = HttpHeaders()
        headers.accept = listOf(MediaType.APPLICATION_JSON)
        val request = HttpEntity(NotificationEvent(1L, Notification(null, "", ""), null), headers)

        // when
        val actual = restTemplate.exchange("/api/notifications/asb", HttpMethod.PUT, request, Void::class.java)

        // then
        assertEquals(HttpStatus.BAD_REQUEST, actual.statusCode)
        assertTrue(KafkaTestUtils.getRecords(consumer, 3).isEmpty)
    }
}