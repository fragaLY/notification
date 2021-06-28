package com.kafka.producer.demo

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource

@TestPropertySource(properties = ["spring.kafka.producer.bootstrap-servers=\${spring.embedded.kafka.brokers}"])
@ActiveProfiles("test")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = ["spring.kafka.auto-startup:true"]
)
@EmbeddedKafka(topics = ["notification-event"], partitions = 1, controlledShutdown = true)
abstract class IntegrationTest
