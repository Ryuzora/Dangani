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
import java.io.InputStream
import com.ryuzora.dangani.R

object FcmSender {

    private const val FCM_API_URL = "https://fcm.googleapis.com/v1/projects/%s/messages:send"
    private val client = OkHttpClient()

    // Replace with your actual Firebase Project ID!
    // Or better, extract it from the service_account.json automatically.
    private const val PROJECT_ID = "dangani-fb" // Placeholder

    suspend fun sendPushNotification(
        context: Context,
        targetToken: String,
        title: String,
        body: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            // Read Service Account JSON from res/raw
            val inputStream: InputStream = context.resources.openRawResource(
                context.resources.getIdentifier("fcm_service_account", "raw", context.packageName)
            )

            // Generate OAuth2 token using Google Auth Library
            val credentials = GoogleCredentials.fromStream(inputStream)
                .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))
            credentials.refreshIfExpired()
            val accessToken = credentials.accessToken.tokenValue

            // Read project ID from the JSON dynamically so it doesn't need to be hardcoded
            inputStream.reset() // Won't work unless buffered, so let's just parse it fresh or rely on the user providing it
            // For simplicity, we assume PROJECT_ID is correct or we parse it:
            val jsonString = context.resources.openRawResource(
                context.resources.getIdentifier("fcm_service_account", "raw", context.packageName)
            ).bufferedReader().use { it.readText() }
            val projectId = JSONObject(jsonString).getString("project_id")

            val url = String.format(FCM_API_URL, projectId)

            // Construct FCM HTTP v1 JSON Payload
            val messageJson = JSONObject().apply {
                put("message", JSONObject().apply {
                    put("token", targetToken)
                    put("notification", JSONObject().apply {
                        put("title", title)
                        put("body", body)
                    })
                    // Optional: Data payload
                    put("data", JSONObject().apply {
                        put("click_action", "FLUTTER_NOTIFICATION_CLICK") // Adjust based on your needs
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
