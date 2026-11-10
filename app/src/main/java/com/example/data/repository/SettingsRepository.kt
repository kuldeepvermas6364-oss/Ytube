package com.example.data.repository

import android.content.Context
import com.example.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsRepository(context: Context) {
  private val prefs = context.getSharedPreferences("yt_app_settings", Context.MODE_PRIVATE)

  private val buildConfigSecret = try {
    val buildKey = BuildConfig.YOUTUBE_API
    if (!buildKey.isNullOrBlank() && buildKey != "MY_YOUTUBE_API_KEY" && buildKey != "DEFAULT_SECRET_VALUE") buildKey else ""
  } catch (e: Exception) {
    ""
  }

  private val savedKey = prefs.getString("youtube_api_key", "") ?: ""

  private val _youtubeApiKey = MutableStateFlow(
    when {
      savedKey.isNotBlank() -> savedKey
      buildConfigSecret.isNotBlank() -> buildConfigSecret
      else -> ""
    }
  )
  val youtubeApiKey: StateFlow<String> = _youtubeApiKey

  private val _cloudinaryCloudName = MutableStateFlow(
    prefs.getString("cloudinary_cloud_name", "demo") ?: "demo"
  )
  val cloudinaryCloudName: StateFlow<String> = _cloudinaryCloudName

  private val _cloudinaryUploadPreset = MutableStateFlow(
    prefs.getString("cloudinary_upload_preset", "unsigned_preset") ?: "unsigned_preset"
  )
  val cloudinaryUploadPreset: StateFlow<String> = _cloudinaryUploadPreset

  fun saveYouTubeApiKey(key: String) {
    prefs.edit().putString("youtube_api_key", key).apply()
    _youtubeApiKey.value = key
  }

  fun saveCloudinarySettings(cloudName: String, uploadPreset: String) {
    prefs.edit()
      .putString("cloudinary_cloud_name", cloudName)
      .putString("cloudinary_upload_preset", uploadPreset)
      .apply()
    _cloudinaryCloudName.value = cloudName
    _cloudinaryUploadPreset.value = uploadPreset
  }
}
