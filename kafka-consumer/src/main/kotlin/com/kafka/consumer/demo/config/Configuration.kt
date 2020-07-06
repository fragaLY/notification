package com.kafka.consumer.demo.config

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.ErrorHandler
import org.springframework.retry.backoff.FixedBackOffPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate
import org.springframework.stereotype.Component

@Configuration
@EnableKafka
class NotificationEventsConsumer {

    @Bean
    fun factory(
        errorHandler: KafkaErrorHandlingLogger,
        configurer: ConcurrentKafkaListenerContainerFactoryConfigurer,
        consumerFactory: ConsumerFactory<Any, Any>,
        retryTemplate: RetryTemplate
    ): ConcurrentKafkaListenerContainerFactory<Any, Any> {
        val factory = ConcurrentKafkaListenerContainerFactory<Any, Any>()
        configurer.configure(factory, consumerFactory)
        factory.setConcurrency(3)
        factory.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL
        factory.setErrorHandler(errorHandler)
        factory.setRetryTemplate(retryTemplate)
        return factory
    }

    @Bean
    fun retry(backOffPolicy: FixedBackOffPolicy, retryPolicy: SimpleRetryPolicy): RetryTemplate {
        val template = RetryTemplate()
        template.setBackOffPolicy(backOffPolicy)
        template.setRetryPolicy(retryPolicy)
        return template
    }

    @Bean
    fun retryPolicy() = SimpleRetryPolicy(3)

    @Bean
    fun backOffPolicy(): FixedBackOffPolicy {
        val policy = FixedBackOffPolicy()
        policy.backOffPeriod = 1000L //default value
        return policy
    }
}

@Component
class KafkaErrorHandlingLogger : ErrorHandler {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun handle(exception: Exception, data: ConsumerRecord<*, *>) {
        logger.error("[CONSUMER] Error during processing the topic [${data.topic()}] message[key = [${data.key()}], value = [${data.value()}]] from partition [${data.partition()}] with offset [${data.offset()}]: $exception")
    }
}
