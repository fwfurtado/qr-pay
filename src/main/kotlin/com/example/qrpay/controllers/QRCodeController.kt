package com.example.qrpay.controllers

import com.example.qrpay.services.QRCodeService
import org.springframework.core.io.FileSystemResource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/qrcode")
class QRCodeController(
    private val qrCodeService: QRCodeService
) {

    @GetMapping("/{id}")
    fun generate(@PathVariable id: String): ResponseEntity<FileSystemResource> {

        val resource = qrCodeService.generateQrCode(id) ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("image/svg+xml"))
            .body(resource)

    }
}