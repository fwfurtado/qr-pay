package com.example.qrpay

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class QrPayApplication

fun main(args: Array<String>) {
    runApplication<QrPayApplication>(*args)
}
