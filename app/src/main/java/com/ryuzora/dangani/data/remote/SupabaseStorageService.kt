package com.ryuzora.dangani.data.remote

import android.net.Uri
import com.ryuzora.dangani.DanganiApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.concurrent.TimeUnit

class SupabaseStorageService {

    companion object {
        private const val PROJECT_URL = "https://junxbnbydtuakzwxovbg.supabase.co"
        private const val ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imp1bnhibmJ5ZHR1YWt6d3hvdmJnIiwicm9sZSI6ImFub24iLCJpYXQiOjE3ODEwNzczNTMsImV4cCI6MjA5NjY1MzM1M30.9NhZnlNtGBMfLoJ0JeypDYSFgGj4u6rticHy7drtVTs"
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Uploads a file to Supabase Storage.
     * @param bucket The storage bucket name (e.g. "proof" or "avatars")
     * @param path The file path within the bucket (e.g. "taskId/filename.jpg")
     * @param fileUri The content:// or file:// URI of the file to upload
     * @return Result containing the public URL of the uploaded file
     */
    suspend fun uploadFile(bucket: String, path: String, fileUri: Uri): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val context = DanganiApplication.instance
                
                // Copy URI content to a temp file
                val tempFile = File.createTempFile("supabase_upload_", null, context.cacheDir)
                try {
                    val inputStream = context.contentResolver.openInputStream(fileUri)
                        ?: throw Exception("Tidak dapat membaca file yang dipilih")
                    
                    tempFile.outputStream().use { output ->
                        inputStream.use { input ->
                            input.copyTo(output)
                        }
                    }

                    // Determine content type
                    val contentType = context.contentResolver.getType(fileUri) ?: "application/octet-stream"

                    val requestBody = tempFile.asRequestBody(contentType.toMediaType())

                    val request = Request.Builder()
                        .url("$PROJECT_URL/storage/v1/object/$bucket/$path")
                        .addHeader("Authorization", "Bearer $ANON_KEY")
                        .addHeader("apikey", ANON_KEY)
                        .post(requestBody)
                        .build()

                    val response = client.newCall(request).execute()

                    if (!response.isSuccessful) {
                        val errorBody = response.body?.string() ?: "Unknown error"
                        throw Exception("Upload gagal (${response.code}): $errorBody")
                    }

                    // Return the public URL
                    val publicUrl = "$PROJECT_URL/storage/v1/object/public/$bucket/$path"
                    Result.success(publicUrl)
                } finally {
                    tempFile.delete()
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Deletes a file from Supabase Storage.
     */
    suspend fun deleteFile(bucket: String, path: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder()
                    .url("$PROJECT_URL/storage/v1/object/$bucket/$path")
                    .addHeader("Authorization", "Bearer $ANON_KEY")
                    .addHeader("apikey", ANON_KEY)
                    .delete()
                    .build()

                val response = client.newCall(request).execute()
                if (!response.isSuccessful) {
                    throw Exception("Delete gagal (${response.code})")
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
