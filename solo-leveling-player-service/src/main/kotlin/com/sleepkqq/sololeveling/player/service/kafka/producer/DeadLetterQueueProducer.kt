package com.sleepkqq.sololeveling.player.service.kafka.producer

import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

@Service
class DeadLetterQueueProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>
) {
    private val log = LoggerFactory.getLogger(DeadLetterQueueProducer::class.java)

    fun sendToDlq(
        originalTopic: String,
        key: String,
        value: String,
        exception: Exception,
        headers: Map<String, String> = emptyMap()
    ) {
        val dlqTopic = "dlq.$originalTopic"
        val dlqMessage = createDlqMessage(originalTopic, value, exception, headers)
        
        try {
            kafkaTemplate.send(dlqTopic, key, dlqMessage)
            log.info("Message sent to DLQ: topic={}", dlqTopic)
        } catch (ex: Exception) {
            log.error("Failed to send message to DLQ: topic={}, error={}", dlqTopic, ex.message, ex)
        }
    }

    private fun createDlqMessage(
        originalTopic: String,
        originalValue: String,
        exception: Exception,
        headers: Map<String, String>
    ): String {
        return """
            {
                "originalTopic": "$originalTopic",
                "originalValue": $originalValue,
                "exception": {
                    "message": "${exception.message}",
                    "type": "${exception.javaClass.simpleName}",
                    "stackTrace": "${exception.stackTraceToString().replace("\"", "\\\"")}"
                },
                "headers": ${headers.toJson()},
                "timestamp": "${LocalDateTime.now()}",
                "dlqId": "${UUID.randomUUID()}"
            }
        """.trimIndent()
    }

    private fun Map<String, String>.toJson(): String {
        return entries.joinToString(",", "{", "}") { "\"${it.key}\": \"${it.value}\"" }
    }
} 