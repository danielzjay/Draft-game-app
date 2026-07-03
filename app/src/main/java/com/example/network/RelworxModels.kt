package com.example.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data models mirroring the Relworx Payments API (Mobile Money).
 * Docs: https://payments.relworx.com/docs/mobile_money/
 *
 * IMPORTANT: These models are shaped to match Relworx exactly, but the Android app never talks
 * to payments.relworx.com directly (see NetworkModule.kt for why). Instead it calls YOUR OWN
 * backend, which forwards the same shape of request on to Relworx using your secret API key.
 */

// ---------- Validate Mobile Number ----------
@JsonClass(generateAdapter = true)
data class ValidateMobileNumberRequest(
    val msisdn: String
)

@JsonClass(generateAdapter = true)
data class ValidateMobileNumberResponse(
    val success: Boolean,
    val message: String?,
    @Json(name = "customer_name") val customerName: String?
)

// ---------- Request Payment (collect money FROM a customer) ----------
@JsonClass(generateAdapter = true)
data class RequestPaymentRequest(
    @Json(name = "account_no") val accountNo: String,
    val reference: String,
    val msisdn: String,
    val currency: String,
    val amount: Double,
    val description: String? = null
)

@JsonClass(generateAdapter = true)
data class RelworxPaymentInitResponse(
    val success: Boolean,
    val message: String?,
    @Json(name = "internal_reference") val internalReference: String?
)

// ---------- Send Payment (payout TO a customer, e.g. tournament winnings) ----------
@JsonClass(generateAdapter = true)
data class SendPaymentRequest(
    @Json(name = "account_no") val accountNo: String,
    val reference: String,
    val msisdn: String,
    val currency: String,
    val amount: Double,
    val description: String? = null
)

// ---------- Check Request Status ----------
@JsonClass(generateAdapter = true)
data class CheckRequestStatusResponse(
    val success: Boolean,
    val status: String?, // "pending" | "success" | "failed"
    val message: String?,
    @Json(name = "customer_reference") val customerReference: String?,
    @Json(name = "internal_reference") val internalReference: String?,
    val msisdn: String?,
    val amount: Double?,
    val currency: String?,
    val provider: String?,
    val charge: Double?,
    @Json(name = "request_status") val requestStatus: String?,
    @Json(name = "provider_transaction_id") val providerTransactionId: String?,
    @Json(name = "completed_at") val completedAt: String?
)
