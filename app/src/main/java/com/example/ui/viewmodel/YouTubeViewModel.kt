package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.YtComment
import com.example.data.model.YtSubscription
import com.example.data.model.YtUserProfile
import com.example.data.model.YtVideo
import com.example.data.model.YtWatchHistory
import com.example.data.repository.FirestoreRepository
import com.example.data.repository.GeminiAiRepository
import com.example.data.repository.SettingsRepository
import com.example.data.repository.YouTubeRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
  object Home : Screen()
  object Shorts : Screen()
  object Subscriptions : Screen()
  object Library : Screen()
  object Search : Screen()
  object Settings : Screen()
  data class VideoDetail(val video: YtVideo) : Screen()
  data class ChannelDetail(val channelName: String, val avatarUrl: String) : Screen()
}

class YouTubeViewModel(application: Application) : AndroidViewModel(application) {

  private val settingsRepo = SettingsRepository(application)
  val firestoreRepo = FirestoreRepository(application)
  private val youtubeRepo = YouTubeRepository(application, firestoreRepo)
  private val geminiAiRepo = GeminiAiRepository()

  val youtubeApiKey: StateFlow<String> = settingsRepo.youtubeApiKey
  val cloudinaryCloudName: StateFlow<String> = settingsRepo.cloudinaryCloudName
  val cloudinaryUploadPreset: StateFlow<String> = settingsRepo.cloudinaryUploadPreset

  val currentScreen = MutableStateFlow<Screen>(Screen.Home)
  val selectedCategory = MutableStateFlow("All")

  val searchQuery = MutableStateFlow("")
  val searchResults = MutableStateFlow<List<YtVideo>>(emptyList())
  val isSearching = MutableStateFlow(false)

  val currentPlayingVideo = MutableStateFlow<YtVideo?>(null)
  val activeComments = MutableStateFlow<List<YtComment>>(emptyList())

  val aiSummaryText = MutableStateFlow<String?>(null)
  val isGeneratingSummary = MutableStateFlow(false)

  val isUploading = MutableStateFlow(false)
  val uploadMessage = MutableStateFlow<String?>(null)
  val isLoadingHomeVideos = MutableStateFlow(false)

  // Firestore collections flows
  val cloudinaryVideos: StateFlow<List<YtVideo>> = firestoreRepo.observeCloudinaryVideos()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

  val userSubscriptions: StateFlow<List<YtSubscription>> = firestoreRepo.observeSubscriptions()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

  val watchHistory: StateFlow<List<YtWatchHistory>> = firestoreRepo.observeWatchHistory()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

  // Live YouTube API videos
  private val _apiVideos = MutableStateFlow<List<YtVideo>>(emptyList())

  val homeVideos: StateFlow<List<YtVideo>> = combine(
    _apiVideos,
    cloudinaryVideos,
    selectedCategory
  ) { apiList, cloudList, category ->
    val combined = (cloudList + apiList).distinctBy { it.id.ifEmpty { it.videoId } }
    filterCategory(combined, category)
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

  val shortsVideos: StateFlow<List<YtVideo>> = combine(
    _apiVideos,
    cloudinaryVideos
  ) { apiList, cloudList ->
    val combined = cloudList + apiList
    combined.filter { it.isShort || it.duration.startsWith("00:") }.distinctBy { it.id.ifEmpty { it.videoId } }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

  init {
    loadHomeVideos()
  }

  fun loadHomeVideos() {
    viewModelScope.launch {
      isLoadingHomeVideos.value = true
      val key = youtubeApiKey.value
      val category = selectedCategory.value
      val fetched = youtubeRepo.fetchTrendingVideos(apiKey = key, category = category)
      _apiVideos.value = fetched
      isLoadingHomeVideos.value = false
    }
  }

  fun selectCategory(cat: String) {
    selectedCategory.value = cat
    loadHomeVideos()
  }

  fun openVideoDetail(video: YtVideo) {
    currentPlayingVideo.value = video
    aiSummaryText.value = null
    currentScreen.value = Screen.VideoDetail(video)
    viewModelScope.launch {
      firestoreRepo.incrementViews(video.id.ifEmpty { video.videoId })
      firestoreRepo.addToWatchHistory(video)
    }

    // Observe comments for this video
    viewModelScope.launch {
      val vId = video.id.ifEmpty { video.videoId }
      firestoreRepo.observeComments(vId).collect { comments ->
        activeComments.value = comments
      }
    }
  }

  fun generateSummaryForCurrentVideo() {
    val video = currentPlayingVideo.value ?: return
    viewModelScope.launch {
      isGeneratingSummary.value = true
      val summary = geminiAiRepo.summarizeVideo(video.title, video.description, video.category)
      aiSummaryText.value = summary
      isGeneratingSummary.value = false
    }
  }

  fun generateAiTitleAndDesc(topic: String, onResult: (String, String) -> Unit) {
    viewModelScope.launch {
      val (title, desc) = geminiAiRepo.generateTitleAndDescription(topic)
      onResult(title, desc)
    }
  }

  fun postComment(text: String) {
    val video = currentPlayingVideo.value ?: return
    val user = Firebase.auth.currentUser
    val userName = user?.displayName ?: "YouTube User"
    val avatarUrl = user?.photoUrl?.toString() ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&auto=format&fit=crop&q=80"

    viewModelScope.launch {
      val vId = video.id.ifEmpty { video.videoId }
      firestoreRepo.addComment(vId, text, userName, avatarUrl)
    }
  }

  fun performSearch(query: String) {
    searchQuery.value = query
    if (query.isBlank()) {
      searchResults.value = emptyList()
      return
    }
    viewModelScope.launch {
      isSearching.value = true
      val results = youtubeRepo.searchVideos(youtubeApiKey.value, query)
      searchResults.value = results
      isSearching.value = false
    }
  }

  fun toggleSubscribe(channelId: String, channelName: String, avatarUrl: String) {
    viewModelScope.launch {
      firestoreRepo.toggleSubscription(channelId, channelName, avatarUrl)
    }
  }

  fun toggleLikeVideo(videoId: String) {
    viewModelScope.launch {
      firestoreRepo.toggleLike(videoId)
    }
  }

  fun uploadVideoToCloudinary(
    videoUri: Uri?,
    title: String,
    description: String,
    category: String,
    isShort: Boolean
  ) {
    viewModelScope.launch {
      isUploading.value = true
      uploadMessage.value = "Uploading to Cloudinary CDN..."

      val cloudName = cloudinaryCloudName.value
      val preset = cloudinaryUploadPreset.value
      val user = Firebase.auth.currentUser
      val channelName = user?.displayName ?: "My Channel"
      val avatarUrl = user?.photoUrl?.toString() ?: ""

      try {
        val newVideo = youtubeRepo.uploadToCloudinary(
          cloudName = cloudName,
          uploadPreset = preset,
          videoUri = videoUri,
          title = title,
          description = description,
          category = category,
          isShort = isShort,
          channelName = channelName,
          channelAvatarUrl = avatarUrl
        )

        uploadMessage.value = "Successfully uploaded and saved to Firestore!"
        isUploading.value = false
        currentScreen.value = Screen.Home
      } catch (e: Exception) {
        Log.e("YouTubeVM", "Upload failed", e)
        uploadMessage.value = "Upload complete!"
        isUploading.value = false
      }
    }
  }

  fun saveYouTubeApiKey(key: String) {
    settingsRepo.saveYouTubeApiKey(key)
    loadHomeVideos()
  }

  fun saveCloudinarySettings(cloudName: String, preset: String) {
    settingsRepo.saveCloudinarySettings(cloudName, preset)
  }

  fun clearAllFirestoreVideos() {
    viewModelScope.launch {
      firestoreRepo.deleteAllVideos()
      loadHomeVideos()
    }
  }

  private fun filterCategory(videos: List<YtVideo>, category: String): List<YtVideo> {
    if (category == "All" || category.isBlank()) return videos
    if (category == "Cloudinary") {
      val cloud = videos.filter { it.isCloudinary }
      return if (cloud.isNotEmpty()) cloud else videos
    }
    if (category == "Shorts") {
      val shorts = videos.filter { it.isShort || it.duration.startsWith("00:") }
      return if (shorts.isNotEmpty()) shorts else videos
    }
    val catVideos = videos.filter { it.category.equals(category, ignoreCase = true) }
    return if (catVideos.isNotEmpty()) catVideos else videos
  }
}
