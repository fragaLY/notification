package com.kafka.producer.demo.notification

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.lang.Nullable
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.util.concurrent.ListenableFutureCallback
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

enum class EventType { CREATE_NOTIFICATION, UPDATE_NOTIFICATION }

data class Notification(
    val id: Long?,
    @field:NotBlank(message = "The sender should not be blank") val sender: String,
    @field:NotBlank(message = "The receiver should not be blank") val receiver: String
)

data class NotificationEvent(
    @field:NotNull(message = "The event id should not be null") val key: Long,
    @field:Valid val notification: Notification,
    val type: EventType?
)

@RequestMapping("/api/notifications")
interface Api {

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun create(@NotNull(message = "The event cannot be null") @Valid @RequestBody event: NotificationEvent): ResponseEntity<Unit>

    @PutMapping(value = ["/{id}"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(
        @NotNull(message = "The notification id cannot be null") @PathVariable id: Long,
        @NotNull(message = "The event cannot be null") @Valid @RequestBody event: NotificationEvent
    ): ResponseEntity<Unit>
}

@RestController
class Controller(private val service: NotificationService) : Api {
    override fun create(event: NotificationEvent) =
        service.create(event).run { ResponseEntity.noContent().build<Unit>() }

    override fun update(id: Long, event: NotificationEvent) =
        service.update(id, event).run { ResponseEntity.noContent().build<Unit>() }
}

@Service
class NotificationService(private val producer: EventProducer) {

    fun create(event: NotificationEvent) = producer.produce(
        event.copy(type = EventType.CREATE_NOTIFICATION, notification = event.notification.copy(id = null))
    )

    fun update(id: Long, event: NotificationEvent) = producer.produce(
        event.copy(type = EventType.UPDATE_NOTIFICATION, notification = event.notification.copy(id = id))
    )
}

@Component
class EventProducer(private val template: KafkaTemplate<Long, String>, private val mapper: ObjectMapper) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun produce(event: NotificationEvent) {
        val result = template.sendDefault(event.key, mapper.writeValueAsString(event))
        result.addCallback(object : ListenableFutureCallback<SendResult<Long, String>> {
            override fun onFailure(exception: Throwable) =
                logger.error("[NOTIFICATION EVENT] Error producing event [$exception]")

            override fun onSuccess(@Nullable result: SendResult<Long, String>?) =
                logger.info("[NOTIFICATION EVENT] The event with key [${result?.producerRecord?.key()}] successfully produced a message in topic [${result?.recordMetadata?.topic()}] in partition [${result?.recordMetadata?.partition()}] with offset [${result?.recordMetadata?.offset()}]")
        })
    }
}