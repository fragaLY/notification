package com.api.gateway.demo.filter.notification

import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

/** @author Vadzim_Kavalkou */
@Component
class NotificationPreFilter : AbstractGatewayFilterFactory<NotificationPreFilter.Config>(Config::class.java) {

    private val logger = LoggerFactory.getLogger(javaClass)

    class Config

    override fun apply(config: Config?) =
        GatewayFilter { exchange: ServerWebExchange?, chain: GatewayFilterChain ->
            chain.filter(exchange).then(Mono.fromRunnable { logger.info("[GATEWAY] Pre filter") })
        }
}