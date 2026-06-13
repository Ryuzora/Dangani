package com.ryuzora.dangani.data.remote.fcm

import android.content.Context
import com.google.auth.oauth2.GoogleCredentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayInputStream

object FcmSender {

    private const val FCM_API_URL = "https://fcm.googleapis.com/v1/projects/%s/messages:send"
    private val client = OkHttpClient()

    suspend fun sendPushNotification(
        context: Context,
        targetToken: String,
        title: String,
        body: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val serviceAccountBytes = context.resources.openRawResource(
                context.resources.getIdentifier("fcm_service_account", "raw", context.packageName)
            ).use { it.readBytes() }

            val credentials = GoogleCredentials.fromStream(ByteArrayInputStream(serviceAccountBytes))
                .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))
            credentials.refreshIfExpired()
            val accessToken = credentials.accessToken.tokenValue

            val jsonString = serviceAccountBytes.toString(Charsets.UTF_8)
            val projectId = JSONObject(jsonString).getString("project_id")
            val url = String.format(FCM_API_URL, projectId)

            val messageJson = JSONObject().apply {
                put("message", JSONObject().apply {
                    put("token", targetToken)
                    put("notification", JSONObject().apply {
                        put("title", title)
                        put("body", body)
                    })
                    put("data", JSONObject().apply {
                        put("title", title)
                        put("body", body)
                    })
                })
            }

            val requestBody = messageJson.toString().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .addHeader("Authorization", "Bearer $accessToken")
                .addHeader("Content-Type", "application/json")
                .build()

            val response = client.newCall(request).execute()
            return@withContext response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }
}
