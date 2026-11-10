package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.YtComment
import com.example.data.model.YtLike
import com.example.data.model.YtSubscription
import com.example.data.model.YtUserProfile
import com.example.data.model.YtVideo
import com.example.data.model.YtWatchHistory
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreRepository(private val db: FirebaseFirestore) {

  constructor(context: Context) : this(
    FirebaseFirestore.getInstance(
      context.applicationContext.getString(R.string.firestore_database_id)
    )
  )

  private val auth = Firebase.auth

  private fun requireUserId(): String {
    return auth.currentUser?.uid ?: "guest_user"
  }

  // Observe User Cloudinary & Firestore Videos
  fun observeCloudinaryVideos(): Flow<List<YtVideo>> {
    return db.collection("videos")
      .orderBy("createdAt", Query.Direction.DESCENDING)
      .snapshots()
      .map { snapshot -> snapshot.toObjects(YtVideo::class.java) }
      .catch { error ->
        Log.e("FirestoreRepo", "Error observing videos", error)
        emit(emptyList())
      }
  }

  // Upload Video record to Firestore (after Cloudinary upload)
  suspend fun saveVideoRecord(video: YtVideo): String {
    val uid = requireUserId()
    val docRef = db.collection("videos").document()
    val videoId = docRef.id

    val payload = mapOf(
      "videoId" to videoId,
      "userId" to uid,
      "title" to video.title,
      "description" to video.description,
      "thumbnailUrl" to video.thumbnailUrl,
      "videoUrl" to video.videoUrl,
      "isCloudinary" to video.isCloudinary,
      "isShort" to video.isShort,
      "category" to video.category,
      "viewsCount" to video.viewsCount,
      "likesCount" to video.likesCount,
      "commentsCount" to video.commentsCount,
      "channelName" to video.channelName,
      "channelAvatarUrl" to video.channelAvatarUrl,
      "duration" to video.duration,
      "createdAt" to FieldValue.serverTimestamp()
    )

    docRef.set(payload).await()
    return videoId
  }

  // Increment view count
  suspend fun incrementViews(videoId: String) {
    if (videoId.startsWith("mock_") || videoId.isEmpty()) return
    try {
      db.collection("videos").document(videoId)
        .update("viewsCount", FieldValue.increment(1))
        .await()
    } catch (e: Exception) {
      Log.w("FirestoreRepo", "Failed to increment view count", e)
    }
  }

  // Observe Comments for a video
  fun observeComments(videoId: String): Flow<List<YtComment>> {
    return db.collection("videos").document(videoId)
      .collection("comments")
      .orderBy("createdAt", Query.Direction.DESCENDING)
      .snapshots()
      .map { snapshot -> snapshot.toObjects(YtComment::class.java) }
      .catch { error ->
        Log.e("FirestoreRepo", "Error observing comments", error)
        emit(emptyList())
      }
  }

  // Post Comment
  suspend fun addComment(videoId: String, text: String, userName: String, avatarUrl: String) {
    val uid = requireUserId()
    val docRef = db.collection("videos").document(videoId).collection("comments").document()
    val commentId = docRef.id

    val payload = mapOf(
      "commentId" to commentId,
      "videoId" to videoId,
      "userId" to uid,
      "userName" to userName,
      "userAvatarUrl" to avatarUrl,
      "text" to text,
      "likesCount" to 0L,
      "createdAt" to FieldValue.serverTimestamp()
    )

    docRef.set(payload).await()

    // Increment commentsCount on video if it exists in Firestore
    if (!videoId.startsWith("mock_")) {
      try {
        db.collection("videos").document(videoId)
          .update("commentsCount", FieldValue.increment(1))
          .await()
      } catch (e: Exception) {
        Log.w("FirestoreRepo", "Failed to increment comments count", e)
      }
    }
  }

  // Observe Subscriptions for current user
  fun observeSubscriptions(): Flow<List<YtSubscription>> {
    val uid = requireUserId()
    return db.collection("subscriptions")
      .whereEqualTo("subscriberUid", uid)
      .snapshots()
      .map { snapshot -> snapshot.toObjects(YtSubscription::class.java) }
      .catch { error ->
        Log.e("FirestoreRepo", "Error observing subscriptions", error)
        emit(emptyList())
      }
  }

  // Subscribe / Unsubscribe channel
  suspend fun toggleSubscription(channelId: String, channelName: String, channelAvatarUrl: String): Boolean {
    val uid = requireUserId()
    val subId = "${uid}_${channelId}"
    val docRef = db.collection("subscriptions").document(subId)

    val doc = docRef.get().await()
    return if (doc.exists()) {
      docRef.delete().await()
      false // Unsubscribed
    } else {
      val payload = mapOf(
        "subId" to subId,
        "subscriberUid" to uid,
        "channelId" to channelId,
        "channelName" to channelName,
        "channelAvatarUrl" to channelAvatarUrl,
        "createdAt" to FieldValue.serverTimestamp()
      )
      docRef.set(payload).await()
      true // Subscribed
    }
  }

  // Observe Likes for current user
  fun observeUserLikes(): Flow<List<YtLike>> {
    val uid = requireUserId()
    return db.collection("likes")
      .whereEqualTo("userId", uid)
      .snapshots()
      .map { snapshot -> snapshot.toObjects(YtLike::class.java) }
      .catch { error ->
        Log.e("FirestoreRepo", "Error observing user likes", error)
        emit(emptyList())
      }
  }

  // Toggle Like on video
  suspend fun toggleLike(videoId: String): Boolean {
    val uid = requireUserId()
    val likeId = "${uid}_${videoId}"
    val docRef = db.collection("likes").document(likeId)

    val doc = docRef.get().await()
    return if (doc.exists()) {
      docRef.delete().await()
      if (!videoId.startsWith("mock_")) {
        try {
          db.collection("videos").document(videoId)
            .update("likesCount", FieldValue.increment(-1))
            .await()
        } catch (e: Exception) { /* ignore */ }
      }
      false
    } else {
      val payload = mapOf(
        "likeId" to likeId,
        "userId" to uid,
        "videoId" to videoId,
        "createdAt" to FieldValue.serverTimestamp()
      )
      docRef.set(payload).await()
      if (!videoId.startsWith("mock_")) {
        try {
          db.collection("videos").document(videoId)
            .update("likesCount", FieldValue.increment(1))
            .await()
        } catch (e: Exception) { /* ignore */ }
      }
      true
    }
  }

  // Add Watch History record
  suspend fun addToWatchHistory(video: YtVideo) {
    val uid = requireUserId()
    val historyId = "${uid}_${video.id}"
    val docRef = db.collection("history").document(historyId)

    val payload = mapOf(
      "historyId" to historyId,
      "userId" to uid,
      "videoId" to video.id,
      "title" to video.title,
      "thumbnailUrl" to video.thumbnailUrl,
      "channelName" to video.channelName,
      "watchedAt" to FieldValue.serverTimestamp()
    )

    try {
      docRef.set(payload).await()
    } catch (e: Exception) {
      Log.w("FirestoreRepo", "Error recording watch history", e)
    }
  }

  // Observe Watch History
  fun observeWatchHistory(): Flow<List<YtWatchHistory>> {
    val uid = requireUserId()
    return db.collection("history")
      .whereEqualTo("userId", uid)
      .snapshots()
      .map { snapshot -> snapshot.toObjects(YtWatchHistory::class.java) }
      .catch { error ->
        Log.e("FirestoreRepo", "Error observing watch history", error)
        emit(emptyList())
      }
  }

  // Save/Update User Profile
  suspend fun syncUserProfile(name: String, email: String, photoUrl: String) {
    val uid = requireUserId()
    val docRef = db.collection("users").document(uid)

    val payload = mapOf(
      "userId" to uid,
      "displayName" to name,
      "email" to email,
      "photoUrl" to photoUrl,
      "channelName" to name,
      "handle" to "@${name.lowercase().replace(" ", "")}",
      "subscribersCount" to 0L,
      "createdAt" to FieldValue.serverTimestamp()
    )

    try {
      docRef.set(payload).await()
    } catch (e: Exception) {
      Log.w("FirestoreRepo", "Error syncing user profile", e)
    }
  }

  // Clear all videos from Firestore (Maintenance)
  suspend fun deleteAllVideos() {
    try {
      val snapshot = db.collection("videos").get().await()
      val batch = db.batch()
      for (doc in snapshot.documents) {
        batch.delete(doc.reference)
      }
      batch.commit().await()
    } catch (e: Exception) {
      Log.e("FirestoreRepo", "Error deleting all videos", e)
    }
  }
}
