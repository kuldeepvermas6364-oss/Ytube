package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ui.components.BottomNavigationBar
import com.example.ui.components.CloudinaryUploadModal
import com.example.ui.components.YouTubeHeader
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ShortsScreen
import com.example.ui.screens.SubscriptionsScreen
import com.example.ui.screens.VideoDetailScreen
import com.example.ui.theme.YouTubeTheme
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.YouTubeViewModel
import com.example.util.configureAppCheck
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

  private val viewModel: YouTubeViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Configure App Check with launch intent before any UI or Firebase AI operations
    configureAppCheck(this, intent)

    enableEdgeToEdge()

    setContent {
      YouTubeTheme {
        YouTubeAppMain(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun YouTubeAppMain(viewModel: YouTubeViewModel) {
  var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }
  var isGuestMode by remember { mutableStateOf(false) }
  var showUploadSheet by remember { mutableStateOf(false) }

  val scope = rememberCoroutineScope()

  // Listen to Firebase Auth changes
  DisposableEffect(Unit) {
    val listener = FirebaseAuth.AuthStateListener { auth ->
      currentUser = auth.currentUser
      if (currentUser != null) {
        scope.launch {
          viewModel.firestoreRepo.syncUserProfile(
            name = currentUser?.displayName ?: "YouTube User",
            email = currentUser?.email ?: "",
            photoUrl = currentUser?.photoUrl?.toString() ?: ""
          )
        }
      }
    }
    Firebase.auth.addAuthStateListener(listener)
    onDispose { Firebase.auth.removeAuthStateListener(listener) }
  }

  // Auth Gate
  if (currentUser == null && !isGuestMode) {
    AuthScreen(
      onAuthSuccess = { currentUser = Firebase.auth.currentUser },
      onContinueAsGuest = { isGuestMode = true }
    )
    return
  }

  val currentScreen by viewModel.currentScreen.collectAsState()
  val selectedCategory by viewModel.selectedCategory.collectAsState()
  val homeVideos by viewModel.homeVideos.collectAsState()
  val shortsVideos by viewModel.shortsVideos.collectAsState()
  val cloudinaryVideos by viewModel.cloudinaryVideos.collectAsState()
  val userSubscriptions by viewModel.userSubscriptions.collectAsState()
  val watchHistory by viewModel.watchHistory.collectAsState()
  val activeComments by viewModel.activeComments.collectAsState()

  val aiSummaryText by viewModel.aiSummaryText.collectAsState()
  val isGeneratingSummary by viewModel.isGeneratingSummary.collectAsState()
  val isLoadingHomeVideos by viewModel.isLoadingHomeVideos.collectAsState()

  val apiKey by viewModel.youtubeApiKey.collectAsState()
  val cloudName by viewModel.cloudinaryCloudName.collectAsState()
  val uploadPreset by viewModel.cloudinaryUploadPreset.collectAsState()

  val searchQuery by viewModel.searchQuery.collectAsState()
  val searchResults by viewModel.searchResults.collectAsState()
  val isSearching by viewModel.isSearching.collectAsState()

  val isUploading by viewModel.isUploading.collectAsState()
  val uploadMessage by viewModel.uploadMessage.collectAsState()

  val hideTopHeader = currentScreen is Screen.VideoDetail || currentScreen is Screen.Search || currentScreen is Screen.Settings || currentScreen is Screen.Shorts
  val hideBottomNav = currentScreen is Screen.VideoDetail || currentScreen is Screen.Search || currentScreen is Screen.Settings

  Scaffold(
    topBar = {
      if (!hideTopHeader) {
        YouTubeHeader(
          apiKeyConnected = apiKey.isNotBlank(),
          userPhotoUrl = currentUser?.photoUrl?.toString(),
          onSearchClick = { viewModel.currentScreen.value = Screen.Search },
          onSettingsClick = { viewModel.currentScreen.value = Screen.Settings }
        )
      }
    },
    bottomBar = {
      if (!hideBottomNav) {
        BottomNavigationBar(
          currentScreen = currentScreen,
          onScreenSelected = { screen -> viewModel.currentScreen.value = screen },
          onUploadClicked = { showUploadSheet = true }
        )
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .background(MaterialTheme.colorScheme.background)
    ) {
      when (val screen = currentScreen) {
        is Screen.Home -> {
          HomeScreen(
            videos = homeVideos,
            selectedCategory = selectedCategory,
            isLoading = isLoadingHomeVideos,
            onCategorySelected = { cat -> viewModel.selectCategory(cat) },
            onVideoClick = { video -> viewModel.openVideoDetail(video) },
            onOpenSettings = { viewModel.currentScreen.value = Screen.Settings }
          )
        }

        is Screen.Shorts -> {
          ShortsScreen(
            shorts = shortsVideos,
            onVideoClick = { video -> viewModel.openVideoDetail(video) }
          )
        }

        is Screen.Subscriptions -> {
          SubscriptionsScreen(
            subscriptions = userSubscriptions,
            feedVideos = homeVideos,
            onVideoClick = { video -> viewModel.openVideoDetail(video) }
          )
        }

        is Screen.Library -> {
          LibraryScreen(
            watchHistory = watchHistory,
            cloudinaryVideos = cloudinaryVideos,
            allVideos = homeVideos,
            onVideoClick = { video -> viewModel.openVideoDetail(video) },
            onOpenSettings = { viewModel.currentScreen.value = Screen.Settings },
            onSignOut = {
              Firebase.auth.signOut()
              isGuestMode = false
            }
          )
        }

        is Screen.VideoDetail -> {
          val currentVidId = screen.video.id.ifEmpty { screen.video.videoId }
          VideoDetailScreen(
            video = screen.video,
            suggestedVideos = homeVideos.filter { (it.id.ifEmpty { it.videoId }) != currentVidId },
            comments = activeComments,
            aiSummaryText = aiSummaryText,
            isGeneratingSummary = isGeneratingSummary,
            onGenerateAiSummary = { viewModel.generateSummaryForCurrentVideo() },
            onBackClick = { viewModel.currentScreen.value = Screen.Home },
            onVideoClick = { video -> viewModel.openVideoDetail(video) },
            onToggleSubscribe = { cId, cName, avatar -> viewModel.toggleSubscribe(cId, cName, avatar) },
            onToggleLike = { vId -> viewModel.toggleLikeVideo(vId) },
            onPostComment = { text -> viewModel.postComment(text) }
          )
        }

        is Screen.Search -> {
          SearchScreen(
            searchQuery = searchQuery,
            searchResults = searchResults,
            isSearching = isSearching,
            onQueryChange = { q -> viewModel.searchQuery.value = q },
            onSearchSubmit = { q -> viewModel.performSearch(q) },
            onVideoClick = { video -> viewModel.openVideoDetail(video) },
            onBackClick = { viewModel.currentScreen.value = Screen.Home }
          )
        }

        is Screen.Settings -> {
          SettingsScreen(
            currentApiKey = apiKey,
            currentCloudName = cloudName,
            currentUploadPreset = uploadPreset,
            onSaveApiKey = { key -> viewModel.saveYouTubeApiKey(key) },
            onSaveCloudinary = { name, preset -> viewModel.saveCloudinarySettings(name, preset) },
            onDeleteAllVideos = { viewModel.clearAllFirestoreVideos() },
            onBackClick = { viewModel.currentScreen.value = Screen.Home }
          )
        }

        else -> {
          HomeScreen(
            videos = homeVideos,
            selectedCategory = selectedCategory,
            isLoading = isLoadingHomeVideos,
            onCategorySelected = { cat -> viewModel.selectCategory(cat) },
            onVideoClick = { video -> viewModel.openVideoDetail(video) },
            onOpenSettings = { viewModel.currentScreen.value = Screen.Settings }
          )
        }
      }

      // Cloudinary Video Upload Bottom Sheet
      if (showUploadSheet) {
        CloudinaryUploadModal(
          cloudName = cloudName,
          uploadPreset = uploadPreset,
          isUploading = isUploading,
          uploadStatusMessage = uploadMessage,
          onDismiss = { showUploadSheet = false },
          onUpload = { uri, title, desc, cat, short ->
            viewModel.uploadVideoToCloudinary(uri, title, desc, cat, short)
            showUploadSheet = false
          }
        )
      }
    }
  }
}
