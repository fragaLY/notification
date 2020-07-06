package com.kafka.producer.demo

import com.kafka.producer.demo.notification.EventProducer
import com.kafka.producer.demo.notification.EventType
import com.kafka.producer.demo.notification.Notification
import com.kafka.producer.demo.notification.NotificationEvent
import com.kafka.producer.demo.notification.NotificationService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension

/** @author Vadzim_Kavalkou */
@ExtendWith(MockitoExtension::class)
class NotificationServiceTest {

    @Mock
    lateinit var producer: EventProducer

    @InjectMocks
    lateinit var service: NotificationService

    @Test
    fun `test create notification event`() {
        // given
        val notification = Notification(null, "from", "to")
        val event = NotificationEvent(1, notification, EventType.CREATE_NOTIFICATION)
        Mockito.doNothing().`when`(producer).produce(event)

        // when
        service.create(event)

        // then
        Mockito.verify(producer).produce(event)
    }
}