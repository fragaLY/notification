package com.kafka.producer.demo

import com.fasterxml.jackson.databind.ObjectMapper
import com.kafka.producer.demo.notification.Controller
import com.kafka.producer.demo.notification.EventType
import com.kafka.producer.demo.notification.Notification
import com.kafka.producer.demo.notification.NotificationEvent
import com.kafka.producer.demo.notification.NotificationService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/** @author Vadzim_Kavalkou */
@WebMvcTest(Controller::class)
@AutoConfigureMockMvc
class ControllerUnitTest {

    @Autowired
    lateinit var mvc: MockMvc

    @Autowired
    lateinit var mapper: ObjectMapper

    @MockBean
    lateinit var service: NotificationService

    @Test
    fun `create notification event test when payload is valid`() {
        // given
        val notification = Notification(null, "from", "to")
        val event = NotificationEvent(1L, notification, EventType.CREATE_NOTIFICATION)
        val payload = mapper.writeValueAsString(event)

        doNothing().`when`(service).create(event)

        // when
        mvc.perform(post("/api/notifications").content(payload).contentType(MediaType.APPLICATION_JSON))
            //then
            .andExpect(status().isNoContent)
    }

    @Test
    fun `create notification event test when payload has no the first argument`() {
        // given
        val notification = Notification(null, "", "to")
        val payload = mapper.writeValueAsString(notification)

        // when
        mvc.perform(post("/api/notifications").content(payload).contentType(MediaType.APPLICATION_JSON))
            //then
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `create notification event test when payload has no the second argument`() {
        // given
        val notification = Notification(null, "from", "")
        val payload = mapper.writeValueAsString(notification)

        // when
        mvc.perform(post("/api/notifications").content(payload).contentType(MediaType.APPLICATION_JSON))
            //then
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `create notification event test when payload has no two arguments`() {
        // given
        val notification = Notification(null, "", "")
        val payload = mapper.writeValueAsString(notification)

        // when
        mvc.perform(post("/api/notifications").content(payload).contentType(MediaType.APPLICATION_JSON))
            //then
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `create notification event test when payload is empty`() {
        // given
        val payload = "";

        // when
        mvc.perform(post("/api/notifications").content(payload).contentType(MediaType.APPLICATION_JSON))
            //then
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `create notification event test when payload is not valid`() {
        // given
        val payload = "payload";

        // when
        mvc.perform(post("/api/notifications").content(payload).contentType(MediaType.APPLICATION_JSON))
            //then
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `update notification event test when payload is valid`() {
        // given
        val notification = Notification(null, "from", "to")
        val event = NotificationEvent(2L, notification, null)
        val payload = mapper.writeValueAsString(event)

        doNothing().`when`(service).create(event)

        // when
        mvc.perform(put("/api/notifications/1").content(payload).contentType(MediaType.APPLICATION_JSON))
            //then
            .andExpect(status().isNoContent)
    }

    @Test
    fun `update notification event test when payload has no the first argument`() {
        // given
        val notification = Notification(null, "", "to")
        val payload = mapper.writeValueAsString(notification)

        // when
        mvc.perform(put("/api/notifications/1").content(payload).contentType(MediaType.APPLICATION_JSON))
            //then
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `update notification event test when payload has no the second argument`() {
        // given
        val notification = Notification(null, "from", "")
        val payload = mapper.writeValueAsString(notification)

        // when
        mvc.perform(put("/api/notifications/1").content(payload).contentType(MediaType.APPLICATION_JSON))
            //then
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `update notification event test when payload has no two arguments`() {
        // given
        val notification = Notification(null, "", "")
        val payload = mapper.writeValueAsString(notification)

        // when
        mvc.perform(put("/api/notifications/1").content(payload).contentType(MediaType.APPLICATION_JSON))
            //then
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `update notification event test when payload is empty`() {
        // given
        val payload = "";

        // when
        mvc.perform(put("/api/notifications/1").content(payload).contentType(MediaType.APPLICATION_JSON))
            //then
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `update notification event test when payload is not valid`() {
        // given
        val payload = "payload";

        // when
        mvc.perform(put("/api/notifications/1").content(payload).contentType(MediaType.APPLICATION_JSON))
            //then
            .andExpect(status().isBadRequest)
    }
}