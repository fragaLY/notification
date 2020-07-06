package com.kafka.consumer.demo.notification

import org.mockito.Mockito
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.TestPropertySource
import java.util.concurrent.Callable

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["spring.kafka.auto-startup:true"]
)
@EmbeddedKafka(topics = ["notification-event"], partitions = 3, controlledShutdown = true)
@TestPropertySource(
    properties = ["spring.kafka.producer.bootstrap-servers=\${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.bootstrap-servers=\${spring.embedded.kafka.brokers}"]
)
abstract class IntegrationTest {

    protected fun <T> isMockCalled(mock: Any?) = Callable {
        !Mockito.mockingDetails(mock).invocations.isEmpty()
    }
}
