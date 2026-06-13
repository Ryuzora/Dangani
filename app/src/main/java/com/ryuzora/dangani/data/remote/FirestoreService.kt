package com.ryuzora.dangani.data.remote

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreService {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    fun getCollection(collectionName: String): Flow<List<DocumentSnapshot>> = callbackFlow {
        val listenerRegistration: ListenerRegistration = firestore
            .collection(collectionName)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    trySend(snapshot.documents)
                }
            }
        awaitClose { listenerRegistration.remove() }
    }

    suspend fun getDocument(collectionName: String, documentId: String): DocumentSnapshot? {
        return try {
            firestore.collection(collectionName).document(documentId).get().await()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addDocument(collectionName: String, data: Map<String, Any?>): String {
        val docRef = firestore.collection(collectionName).add(data).await()
        return docRef.id
    }

    suspend fun setDocument(collectionName: String, documentId: String, data: Map<String, Any?>) {
        firestore.collection(collectionName).document(documentId).set(data).await()
    }

    suspend fun updateDocument(collectionName: String, documentId: String, data: Map<String, Any?>) {
        firestore.collection(collectionName).document(documentId).update(data).await()
    }

    suspend fun registerFcmToken(userId: String, token: String) {
        firestore.collection("users").document(userId).update(
            mapOf(
                "fcmToken" to token,
                "fcmTokens" to FieldValue.arrayUnion(token)
            )
        ).await()
    }

    suspend fun deleteDocument(collectionName: String, documentId: String) {
        firestore.collection(collectionName).document(documentId).delete().await()
    }

    fun queryCollection(
        collectionName: String,
        field: String,
        value: Any
    ): Flow<List<DocumentSnapshot>> = callbackFlow {
        val listenerRegistration: ListenerRegistration = firestore
            .collection(collectionName)
            .whereEqualTo(field, value)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    trySend(snapshot.documents)
                }
            }
        awaitClose { listenerRegistration.remove() }
    }

    fun queryCollectionTwoFields(
        collectionName: String,
        field1: String,
        value1: Any,
        field2: String,
        value2: Any
    ): Flow<List<DocumentSnapshot>> = callbackFlow {
        val listenerRegistration: ListenerRegistration = firestore
            .collection(collectionName)
            .whereEqualTo(field1, value1)
            .whereEqualTo(field2, value2)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    trySend(snapshot.documents)
                }
            }
        awaitClose { listenerRegistration.remove() }
    }
}

