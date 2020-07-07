package com.kafka.consumer.demo.notification

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = ["spring.kafka.auto-startup:true"]
)
@EmbeddedKafka(topics = ["notification-event"], partitions = 1, controlledShutdown = true)
abstract class KafkaIntegrationTest