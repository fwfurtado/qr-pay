package com.example.qrpay.repositories

import com.example.qrpay.entity.TransferIntent
import org.springframework.data.repository.Repository
import java.util.UUID

interface TransferRepository : Repository<TransferIntent, UUID> {

    fun save(transferIntent: TransferIntent)
    fun findByHash(hash: String): TransferIntent?
}