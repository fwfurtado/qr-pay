package com.example.qrpay.controllers

import com.example.qrpay.services.TransferService
import com.fasterxml.jackson.annotation.JsonProperty
import com.soywiz.klock.seconds
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@RestController
@RequestMapping("transfer")
class TransferController(
    private val transferService: TransferService
) {
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    data class TransferRequest(val amount: Int)
    data class TransferResponse(@field:JsonProperty("qr_code_url") val qrCodeUrl: String, val id: String)

    @PostMapping("/request")
    @ResponseStatus(HttpStatus.CREATED)
    fun request(@RequestBody request: TransferRequest): TransferResponse {
        val hash = transferService.request(request.amount)

        return TransferResponse("http://localhost:8080/qrcode/$hash", hash)
    }


    data class TransferStatusResponse(val status: String)

    @GetMapping("/listen/{hash}")
    fun listen(@PathVariable hash: String): ResponseEntity<SseEmitter> {
        val emmiter = transferService.listen(hash) ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok()
            .body(emmiter)
    }

    data class TransferCommandRequest(val amount: Int, val destination: String)

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun transfer(@RequestBody request: TransferCommandRequest) {
        transferService.transfer(request.destination, request.amount)
    }
}