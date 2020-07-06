package com.kafka.consumer.demo.notification

import org.slf4j.LoggerFactory
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.util.ErrorHandler
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

enum class EventType { CREATE_NOTIFICATION, UPDATE_NOTIFICATION }

data class NotificationEvent(val key: Long? = null, val notification: Notification, val type: EventType)

@Entity
data class Notification(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) var id: Long? = null,
    var sender: String = "",
    var receiver: String = ""
)

@Repository
interface NotificationH2Repository : CrudRepository<Notification, Long>

@Service
class NotificationService(private val repository: NotificationH2Repository) {

    fun create(notification: Notification) = repository.save(notification)

    fun update(notification: Notification) =
        if (notification.id == null) throw IllegalArgumentException("The notification should not be null for update request")
        else repository.findById(notification.id!!).orElseThrow { RuntimeException("Notification not found") }
            .run { repository.save(notification) }
}

class ErrorHandlingLogger : ErrorHandler {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun handleError(exception: Throwable) {
        logger.error("[CONSUMER] $exception")
    }
}