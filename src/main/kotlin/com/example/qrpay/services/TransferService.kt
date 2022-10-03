package com.example.qrpay.services

import com.example.qrpay.entity.TransferIntent
import com.example.qrpay.repositories.TransferRepository
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import org.springframework.util.DigestUtils
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Service
class TransferService(
    private val repository: TransferRepository,
    private val sseService: SSEService,
    private val rabbitTemplate: RabbitTemplate
) {

    private val emitterMap = mutableMapOf<String, SseEmitter>()
    private val log = LoggerFactory.getLogger(TransferService::class.java)

    fun request(amount: Int): String {
        val intent = TransferIntent().apply {
            this.amount = amount
            hash = DigestUtils.md5DigestAsHex(id.toByteArray())
        }


        repository.save(intent)

        return intent.hash
    }


    fun listen(hash: String): SseEmitter? {
        val intent = repository.findByHash(hash) ?: return null
        log.info("Found intent: $intent")

        return sseService.createEmitter(hash)
    }

    fun transfer(hash: String, amount: Int) {
        val intent = repository.findByHash(hash) ?: return
        log.info("Found intent: $intent")
        var status = "SUCCESS"
        var reason = ""

        if (amount != intent.amount) {
            log.info("Amount mismatch: $amount != ${intent.amount}")
            status = "FAILED"
            reason = "Amount mismatch"
        }

        rabbitTemplate.convertAndSend("transfer", hash, TransferResult(status, reason))
    }

}
