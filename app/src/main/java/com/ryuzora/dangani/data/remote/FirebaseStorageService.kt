package com.ryuzora.dangani.data.remote

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.ryuzora.dangani.DanganiApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File

class FirebaseStorageService {

    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    /**
     * Uploads a file to Firebase Storage.
     * Handles content:// URIs by copying to a temp file first,
     * since putFile() can fail with "object does not exist at location"
     * for certain content provider URIs.
     */
    suspend fun uploadFile(path: String, fileUri: Uri): Result<String> {
        return try {
            val ref = storage.reference.child(path)

            if (fileUri.scheme == "content") {
                // Content URIs need to be read via ContentResolver
                // Copy to a temp file, then upload that
                val tempFile = withContext(Dispatchers.IO) {
                    val context = DanganiApplication.instance
                    val inputStream = context.contentResolver.openInputStream(fileUri)
                        ?: throw Exception("Tidak dapat membaca file yang dipilih")
                    val temp = File.createTempFile("upload_", null, context.cacheDir)
                    temp.outputStream().use { output ->
                        inputStream.use { input ->
                            input.copyTo(output)
                        }
                    }
                    temp
                }

                try {
                    ref.putFile(Uri.fromFile(tempFile)).await()
                } finally {
                    // Clean up temp file
                    tempFile.delete()
                }
            } else {
                ref.putFile(fileUri).await()
            }

            val downloadUrl = ref.downloadUrl.await().toString()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteFile(path: String): Result<Unit> {
        return try {
            storage.reference.child(path).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
