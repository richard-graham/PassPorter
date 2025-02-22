package com.example.passporter.data.remote.api

import com.example.passporter.data.remote.model.BorderPointDto
import com.example.passporter.data.remote.model.BorderUpdateDto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class FirestoreService @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()
    private val borderPointsCollection = db.collection("borderPoints")
    private val updatesCollection = db.collection("updates")
    private val subscriptionsCollection = db.collection("subscriptions")

    fun getBorderPoints(): Flow<List<BorderPointDto>> = callbackFlow {
        val subscription = borderPointsCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val borderPoints = snapshot?.documents
                    ?.mapNotNull { doc ->
                        doc.toObject(BorderPointDto::class.java)?.copy(id = doc.id)
                    }
                    ?: emptyList()

                trySend(borderPoints)
            }

        awaitClose { subscription.remove() }
    }

    suspend fun addBorderPoint(borderPoint: BorderPointDto) {
        borderPointsCollection.document(borderPoint.id)
            .set(borderPoint)
            .await()
    }

    fun getBorderUpdates(borderPointId: String): Flow<List<BorderUpdateDto>> = callbackFlow {
        val subscription = updatesCollection
            .whereEqualTo("borderPointId", borderPointId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val updates = snapshot?.documents
                    ?.mapNotNull { doc ->
                        doc.toObject(BorderUpdateDto::class.java)?.copy(id = doc.id)
                    }
                    ?: emptyList()

                trySend(updates)
            }

        awaitClose { subscription.remove() }
    }

    suspend fun addBorderUpdate(borderUpdateDto: BorderUpdateDto) {
        updatesCollection.document(borderUpdateDto.borderPointId)
            .set(borderUpdateDto)
            .await()
    }

    suspend fun subscribeToBorderPoint(userId: String, borderPointId: String) {
        subscriptionsCollection
            .document("${userId}_${borderPointId}")
            .set(
                mapOf(
                    "userId" to userId,
                    "borderPointId" to borderPointId,
                    "timestamp" to System.currentTimeMillis()
                )
            )
            .await()
    }

    suspend fun updateBorderPoint(borderPointDto: BorderPointDto) {
        return suspendCoroutine { continuation ->
            borderPointsCollection
                .document(borderPointDto.id)
                .set(borderPointDto)
                .addOnSuccessListener {
                    continuation.resume(Unit)
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }
}