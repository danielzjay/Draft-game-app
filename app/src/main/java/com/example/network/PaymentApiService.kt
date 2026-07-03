package com.example.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * These endpoints live on YOUR backend (see the Cloud Function proxy in
 * /server/relworxProxy.js), not on payments.relworx.com directly.
 *
 * Why not call Relworx straight from the app?
 * Relworx auth is a single secret Bearer API key with NO per-user scoping. If that key is
 * bundled inside the APK, anyone can pull it out with a decompiler (takes minutes with jadx)
 * and use it to send/request payments through YOUR merchant account. The key must live only on
 * a server you control, and the app should call that server instead.
 *
 * Your backend forwards these calls 1:1 to the real Relworx endpoints and returns the same
 * response shape, so the request/response models here match Relworx's docs exactly.
 */
interface PaymentApiService {

    @POST("mobile-money/validate")
    suspend fun validateMobileNumber(
        @Body request: ValidateMobileNumberRequest
    ): Response<ValidateMobileNumberResponse>

    @POST("mobile-money/request-payment")
    suspend fun requestPayment(
        @Body request: RequestPaymentRequest
    ): Response<RelworxPaymentInitResponse>

    @POST("mobile-money/send-payment")
    suspend fun sendPayment(
        @Body request: SendPaymentRequest
    ): Response<RelworxPaymentInitResponse>

    @GET("mobile-money/check-request-status")
    suspend fun checkRequestStatus(
        @Query("internal_reference") internalReference: String,
        @Query("account_no") accountNo: String
    ): Response<CheckRequestStatusResponse>
}
