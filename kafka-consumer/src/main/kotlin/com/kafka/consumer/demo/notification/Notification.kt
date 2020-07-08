package com.kafka.consumer.demo.notification

import org.hibernate.annotations.GenericGenerator
import org.hibernate.annotations.Type
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table

enum class EventType { CREATE_NOTIFICATION, UPDATE_NOTIFICATION }

data class NotificationEvent(val key: Long? = null, val notification: Notification, val type: EventType)

@Entity
@Table(name = "notification")
data class Notification(

    @Id @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    var id: UUID? = null,

    @Column(nullable = false, length = 25)
    var sender: String = "",

    @Column(nullable = false, length = 25)
    var receiver: String = ""
)

@Repository
interface NotificationRepository : CrudRepository<Notification, UUID>

@Service
class NotificationService(private val repository: NotificationRepository) {

    fun create(notification: Notification) = repository.save(notification)

    fun update(notification: Notification) =
        if (notification.id == null) throw IllegalArgumentException("The notification should not be null for update request")
        else repository.findById(notification.id!!).orElseThrow { RuntimeException("Notification not found") }
            .run { repository.save(notification) }
}