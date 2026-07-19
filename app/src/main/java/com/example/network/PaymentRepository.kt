package com.example.network

import kotlinx.coroutines.delay
import java.util.UUID

sealed class PaymentResult {
    data class Success(val status: CheckRequestStatusResponse) : PaymentResult()
    data class Failed(val reason: String) : PaymentResult()
    data class Pending(val internalReference: String) : PaymentResult()
}

class PaymentRepository(
    private val api: PaymentApiService = NetworkModule.paymentApi,
    private val accountNo: String, // your Relworx business account number, e.g. "RELJH012BV45P"
) {

    /** Confirms a phone number is a valid, registered MTN/Airtel Uganda mobile money account. */
    suspend fun validateMobileNumber(msisdn: String): Result<ValidateMobileNumberResponse> {
        return try {
            val response = api.validateMobileNumber(ValidateMobileNumberRequest(msisdn))
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Validation failed: HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Requests money FROM a player's mobile money account (e.g. buying an in-app coin bundle).
     * Relworx rate-limits this endpoint to 5 requests / 10 minutes per msisdn.
     */
    suspend fun requestPayment(
        msisdn: String,
        amount: Double,
        currency: String = "UGX",
        description: String = "Draughts Combat coin purchase"
    ): Result<RelworxPaymentInitResponse> {
        return try {
            val reference = UUID.randomUUID().toString().replace("-", "").take(8).uppercase()
            val response = api.requestPayment(
                RequestPaymentRequest(
                    accountNo = accountNo,
                    reference = reference,
                    msisdn = msisdn,
                    currency = currency,
                    amount = amount,
                    description = description
                )
            )
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Request payment failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Sends money TO a player, e.g. tournament winnings or a refund. */
    suspend fun sendPayment(
        msisdn: String,
        amount: Double,
        currency: String = "UGX",
        description: String = "Draughts Combat payout"
    ): Result<RelworxPaymentInitResponse> {
        return try {
            val reference = UUID.randomUUID().toString().replace("-", "").take(8).uppercase()
            val response = api.sendPayment(
                SendPaymentRequest(
                    accountNo = accountNo,
                    reference = reference,
                    msisdn = msisdn,
                    currency = currency,
                    amount = amount,
                    description = description
                )
            )
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Send payment failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Mobile money confirmation is not instant — the customer has to approve a USSD/app prompt
     * on their phone. Poll check-request-status until it resolves instead of assuming success.
     */
    suspend fun pollUntilResolved(
        internalReference: String,
        maxAttempts: Int = 10,
        intervalMillis: Long = 3000
    ): PaymentResult {
        repeat(maxAttempts) {
            try {
                val response = api.checkRequestStatus(internalReference, accountNo)
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    when (body.requestStatus ?: body.status) {
                        "success" -> return PaymentResult.Success(body)
                        "failed" -> return PaymentResult.Failed(body.message ?: "Payment failed")
                        else -> { /* still pending, keep polling */ }
                    }
                }
            } catch (_: Exception) {
                // transient network error while polling — try again next iteration
            }
            delay(intervalMillis)
        }
        return PaymentResult.Pending(internalReference)
    }
}
