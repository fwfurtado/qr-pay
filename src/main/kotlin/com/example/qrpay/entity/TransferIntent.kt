package com.example.qrpay.entity

import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
class TransferIntent {

    @Id
    var id: String = UUID.randomUUID().toString()

    var amount: Int = 0

    var hash: String = ""
}