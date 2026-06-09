package com.ryuzora.dangani.data.remote

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class FirebaseStorageService {

    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    suspend fun uploadFile(path: String, fileUri: Uri): Result<String> {
        return try {
            val ref = storage.reference.child(path)
            ref.putFile(fileUri).await()
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
