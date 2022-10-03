package com.example.qrpay.services

import com.fasterxml.jackson.annotation.JsonProperty

data class TransferResult (
    @JsonProperty("status") var status: String = "",
    @JsonProperty("reason") var reason: String = "",
)