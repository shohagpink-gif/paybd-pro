package com.paybd.pro.network

import com.paybd.pro.data.TransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object WebhookClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

    data class WebhookResult(
        val success: Boolean,
        val statusCode: Int = 0,
        val message: String = ""
    )

    suspend fun sendTransaction(
        webhookUrl: String,
        authToken: String,
        transaction: TransactionEntity
    ): WebhookResult = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject().apply {
                put("trxId", transaction.trxId)
                put("amount", transaction.amount)
                put("balance", transaction.balance)
                put("sender", transaction.sender)
                put("status", transaction.status.name)
                put("rawMessage", transaction.rawMessage)
                put("timestamp", transaction.timestamp)
            }

            val requestBody = json.toString().toRequestBody(JSON_MEDIA_TYPE)

            val requestBuilder = Request.Builder()
                .url(webhookUrl)
                .post(requestBody)
                .addHeader("Content-Type", "application/json")

            if (authToken.isNotBlank()) {
                requestBuilder.addHeader("Authorization", "Bearer $authToken")
            }

            val response = client.newCall(requestBuilder.build()).execute()
            val responseBody = response.body?.string() ?: ""

            WebhookResult(
                success = response.isSuccessful,
                statusCode = response.code,
                message = if (response.isSuccessful) "Sent successfully" else "Error: $responseBody"
            )
        } catch (e: Exception) {
            WebhookResult(
                success = false,
                message = "Network error: ${e.message}"
            )
        }
    }
}
