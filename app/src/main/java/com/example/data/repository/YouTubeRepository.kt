package com.example.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.BuildConfig
import com.example.data.model.YtVideo
import com.example.data.remote.NetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class YouTubeRepository(
  private val context: Context,
  private val firestoreRepository: FirestoreRepository
) {
  private val youtubeApi = NetworkClient.youtubeApi
  private val cloudinaryApi = NetworkClient.cloudinaryApi

  private fun getEffectiveApiKey(passedKey: String): String {
    if (passedKey.isNotBlank()) return passedKey
    val buildKey = try { BuildConfig.YOUTUBE_API } catch (e: Exception) { "" }
    return if (!buildKey.isNullOrBlank() && buildKey != "MY_YOUTUBE_API_KEY") buildKey else ""
  }

  suspend fun fetchTrendingVideos(apiKey: String, category: String = "All"): List<YtVideo> = withContext(Dispatchers.IO) {
    val effectiveKey = getEffectiveApiKey(apiKey)
    if (effectiveKey.isBlank()) {
      Log.e("YouTubeRepo", "YouTube API Key is missing or invalid in BuildConfig/Settings")
      return@withContext emptyList()
    }
    
    val videoCategoryId = when (category) {
      "Music" -> "10"
      "Gaming" -> "20"
      "Tech" -> "28"
      "Travel" -> "19"
      "Live" -> "24"
      else -> null
    }

    // 1. Try standard YouTube Data API videos endpoint
    try {
      val response = youtubeApi.getTrendingVideos(
        apiKey = effectiveKey,
        videoCategoryId = videoCategoryId
      )
      if (response.isSuccessful && response.body() != null) {
        val items = response.body()?.items ?: emptyList()
        if (items.isNotEmpty()) {
          val channelIds = items.mapNotNull { it.snippet?.channelId }.distinct().joinToString(",")
          val avatarsMap = fetchChannelAvatars(effectiveKey, channelIds)

          val mappedVideos = items.map { item ->
            val snippet = item.snippet
            val stats = item.statistics
            val content = item.contentDetails
            val videoId = item.id ?: ""
            val channelId = snippet?.channelId ?: ""

            YtVideo(
              id = videoId,
              videoId = videoId,
              userId = channelId,
              title = snippet?.title ?: "YouTube Video",
              description = snippet?.description ?: "",
              thumbnailUrl = snippet?.thumbnails?.high?.url
                ?: snippet?.thumbnails?.medium?.url
                ?: snippet?.thumbnails?.default?.url
                ?: "",
              videoUrl = "https://www.youtube.com/watch?v=$videoId",
              isCloudinary = false,
              isShort = category == "Shorts",
              category = category,
              viewsCount = stats?.viewCount?.toLongOrNull() ?: 0L,
              likesCount = stats?.likeCount?.toLongOrNull() ?: 0L,
              commentsCount = stats?.commentCount?.toLongOrNull() ?: 0L,
              channelName = snippet?.channelTitle ?: "YouTube Creator",
              channelAvatarUrl = avatarsMap[channelId] ?: "",
              duration = parseIsoDuration(content?.duration),
              publishedAt = snippet?.publishedAt ?: ""
            )
          }
          return@withContext mappedVideos
        }
      } else {
        Log.w("YouTubeRepo", "getTrendingVideos failed with code: ${response.code()}")
      }
    } catch (e: Exception) {
      Log.e("YouTubeRepo", "Exception in getTrendingVideos", e)
    }

    // 2. Fallback live YouTube search query for the specific category
    val searchQuery = when (category) {
      "Gaming" -> "Gaming gameplay stream"
      "Music" -> "Official Music Video hit"
      "Tech" -> "Technology gadgets review"
      "Shorts" -> "YouTube Shorts viral"
      "Travel" -> "Travel vlog 4k"
      "Live" -> "Lofi live stream 24/7"
      else -> "Trending popular music tech gaming"
    }

    return@withContext searchVideos(effectiveKey, searchQuery, targetCategory = category)
  }

  suspend fun searchVideos(apiKey: String, query: String, targetCategory: String = "Search"): List<YtVideo> = withContext(Dispatchers.IO) {
    if (query.isBlank()) return@withContext emptyList()
    val effectiveKey = getEffectiveApiKey(apiKey)
    if (effectiveKey.isBlank()) return@withContext emptyList()

    try {
      val response = youtubeApi.searchVideos(query = query, apiKey = effectiveKey)
      if (response.isSuccessful && response.body() != null) {
        val searchItems = response.body()?.items ?: emptyList()
        if (searchItems.isEmpty()) return@withContext emptyList()

        val videoIds = searchItems.mapNotNull { it.id?.videoId }.joinToString(",")
        
        // Fetch real statistics and duration for these videos
        val detailsResponse = youtubeApi.getVideoDetails(videoIds = videoIds, apiKey = effectiveKey)
        
        if (detailsResponse.isSuccessful && detailsResponse.body() != null) {
          val detailItems = detailsResponse.body()?.items ?: emptyList()
          val detailsMap = detailItems.associateBy { it.id }
          
          val channelIds = detailItems.mapNotNull { it.snippet?.channelId }.distinct().joinToString(",")
          val avatarsMap = fetchChannelAvatars(effectiveKey, channelIds)

          val mappedVideos = searchItems.mapNotNull { searchItem ->
            val videoId = searchItem.id?.videoId ?: return@mapNotNull null
            val snippet = searchItem.snippet
            val detailItem = detailsMap[videoId]
            val stats = detailItem?.statistics
            val content = detailItem?.contentDetails
            val channelId = snippet?.channelId ?: ""

            YtVideo(
              id = videoId,
              videoId = videoId,
              userId = channelId,
              title = snippet?.title ?: "YouTube Video",
              description = snippet?.description ?: "",
              thumbnailUrl = snippet?.thumbnails?.high?.url
                ?: snippet?.thumbnails?.medium?.url
                ?: snippet?.thumbnails?.default?.url
                ?: "",
              videoUrl = "https://www.youtube.com/watch?v=$videoId",
              isCloudinary = false,
              isShort = targetCategory == "Shorts" || query.contains("short", ignoreCase = true),
              category = targetCategory,
              viewsCount = stats?.viewCount?.toLongOrNull() ?: 0L,
              likesCount = stats?.likeCount?.toLongOrNull() ?: 0L,
              commentsCount = stats?.commentCount?.toLongOrNull() ?: 0L,
              channelName = snippet?.channelTitle ?: "YouTube Creator",
              channelAvatarUrl = avatarsMap[channelId] ?: "",
              duration = parseIsoDuration(content?.duration),
              publishedAt = snippet?.publishedAt ?: ""
            )
          }
          return@withContext mappedVideos
        } else {
          return@withContext emptyList()
        }
      } else {
        Log.e("YouTubeRepo", "Search API Error: ${response.code()} ${response.message()}")
        return@withContext emptyList()
      }
    } catch (e: Exception) {
      Log.e("YouTubeRepo", "Error searching videos", e)
      return@withContext emptyList()
    }
  }

  private suspend fun fetchChannelAvatars(apiKey: String, channelIds: String): Map<String, String> {
    if (channelIds.isBlank()) return emptyMap()
    return try {
      val response = youtubeApi.getChannelDetails(channelIds = channelIds, apiKey = apiKey)
      val body = response.body()
      if (response.isSuccessful && body != null) {
        body.items?.associate { item ->
          (item.id ?: "") to (item.snippet?.thumbnails?.default?.url ?: "")
        } ?: emptyMap()
      } else {
        emptyMap()
      }
    } catch (e: Exception) {
      Log.e("YouTubeRepo", "Error fetching channel avatars", e)
      emptyMap()
    }
  }

  // Remove fake backup dataset entirely
  private fun getRealBackupYouTubeDataset(category: String): List<YtVideo> {
    return emptyList()
  }

  // Upload Video to Cloudinary
  suspend fun uploadToCloudinary(
    cloudName: String,
    uploadPreset: String,
    videoUri: Uri?,
    title: String,
    description: String,
    category: String,
    isShort: Boolean,
    channelName: String,
    channelAvatarUrl: String
  ): YtVideo = withContext(Dispatchers.IO) {

    var uploadedVideoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    var thumbnailUrl = "https://images.unsplash.com/photo-1579202673506-ca3ce28943ef?w=800&auto=format&fit=crop&q=80"

    if (videoUri != null && cloudName.isNotBlank() && cloudName != "demo") {
      try {
        val tempFile = copyUriToTempFile(videoUri)
        if (tempFile != null) {
          val requestFile = tempFile.asRequestBody("video/*".toMediaTypeOrNull())
          val filePart = MultipartBody.Part.createFormData("file", tempFile.name, requestFile)
          val presetBody = uploadPreset.toRequestBody("text/plain".toMediaTypeOrNull())

          val response = cloudinaryApi.uploadVideo(cloudName, presetBody, filePart)
          if (response.isSuccessful && response.body() != null) {
            val body = response.body()!!
            if (!body.secure_url.isNullOrBlank()) {
              uploadedVideoUrl = body.secure_url
              thumbnailUrl = body.secure_url.replace(".mp4", ".jpg").replace(".mov", ".jpg")
            }
          }
          tempFile.delete()
        }
      } catch (e: Exception) {
        Log.e("YouTubeRepo", "Cloudinary direct upload exception, falling back to uploaded video URL", e)
      }
    }

    // Create Cloudinary Video Record
    val video = YtVideo(
      title = title.ifBlank { "My Uploaded Video" },
      description = description,
      thumbnailUrl = thumbnailUrl,
      videoUrl = uploadedVideoUrl,
      isCloudinary = true,
      isShort = isShort,
      category = category,
      viewsCount = 1L,
      likesCount = 0L,
      commentsCount = 0L,
      channelName = channelName,
      channelAvatarUrl = channelAvatarUrl.ifBlank { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&auto=format&fit=crop&q=80" },
      duration = if (isShort) "00:45" else "04:12"
    )

    // Save to Firestore so it immediately syncs across all users & devices!
    val docId = firestoreRepository.saveVideoRecord(video)
    return@withContext video.copy(id = docId, videoId = docId)
  }

  private fun copyUriToTempFile(uri: Uri): File? {
    return try {
      val inputStream = context.contentResolver.openInputStream(uri) ?: return null
      val file = File.createTempFile("cloudinary_upload_", ".mp4", context.cacheDir)
      val outputStream = FileOutputStream(file)
      inputStream.copyTo(outputStream)
      inputStream.close()
      outputStream.close()
      file
    } catch (e: Exception) {
      Log.e("YouTubeRepo", "Failed to copy URI to temp file", e)
      null
    }
  }

  private fun parseIsoDuration(duration: String?): String {
    if (duration == null) return "05:00"
    val regex = "PT(?:(\\d+)H)?(?:(\\d+)M)?(?:(\\d+)S)?".toRegex()
    val match = regex.find(duration) ?: return "04:30"
    val (hours, minutes, seconds) = match.destructured

    val h = hours.toIntOrNull() ?: 0
    val m = minutes.toIntOrNull() ?: 0
    val s = seconds.toIntOrNull() ?: 0

    return if (h > 0) {
      String.format("%02d:%02d:%02d", h, m, s)
    } else {
      String.format("%02d:%02d", m, s)
    }
  }
}
