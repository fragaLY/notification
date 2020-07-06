package com.kafka.producer.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NotificationProducer

fun main(args: Array<String>) {
    runApplication<NotificationProducer>(*args)
}
