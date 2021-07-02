package com.kafka.producer.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableTransactionManagement
class NotificationProducer

fun main(args: Array<String>) {
    runApplication<NotificationProducer>(*args)
}
