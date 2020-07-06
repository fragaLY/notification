package com.api.gateway.demo.filter.notification

import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.UUID

/** @author Vadzim_Kavalkou */
@Component
class NotificationPostFilter : AbstractGatewayFilterFactory<NotificationPostFilter.Config>(Config::class.java) {

    private val logger = LoggerFactory.getLogger(javaClass)

    class Config

    override fun apply(config: Config?): GatewayFilter {
        return GatewayFilter { exchange: ServerWebExchange?, chain: GatewayFilterChain ->
            chain.filter(exchange)
                .then(Mono.fromRunnable {
                    logger.info("[GATEWAY] Post filter")
                    when (exchange?.response?.statusCode) {
                        HttpStatus.NO_CONTENT -> exchange.response.headers["post-filter"] =
                            mutableListOf(UUID.randomUUID().toString())
                        else -> logger.warn("[GATEWAY] Something went wrong. Response status is [${exchange?.response?.statusCode}]")
                    }
                })
        }
    }
}