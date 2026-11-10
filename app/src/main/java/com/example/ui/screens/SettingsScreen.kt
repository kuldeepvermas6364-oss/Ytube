package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.YtBlue
import com.example.ui.theme.YtRed

@Composable
fun SettingsScreen(
  currentApiKey: String,
  currentCloudName: String,
  currentUploadPreset: String,
  onSaveApiKey: (String) -> Unit,
  onSaveCloudinary: (String, String) -> Unit,
  onDeleteAllVideos: () -> Unit,
  onBackClick: () -> Unit
) {
  BackHandler { onBackClick() }

  val context = LocalContext.current
  var apiKey by remember { mutableStateOf(currentApiKey) }
  var cloudName by remember { mutableStateOf(currentCloudName) }
  var uploadPreset by remember { mutableStateOf(currentUploadPreset) }
  var saveSuccessMessage by remember { mutableStateOf<String?>(null) }

  val databaseId = try {
    context.getString(R.string.firestore_database_id)
  } catch (e: Exception) {
    "Default Provisioned Database"
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Header
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(56.dp)
        .padding(horizontal = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(onClick = onBackClick) {
        Icon(
          imageVector = Icons.Default.ArrowBack,
          contentDescription = "Back",
          tint = MaterialTheme.colorScheme.onBackground
        )
      }
      Text(
        text = "API & Storage Settings",
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
      )
    }

    Column(
      modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(16.dp)
    ) {
      if (saveSuccessMessage != null) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF2E7D32))
            .padding(12.dp)
        ) {
          Text(text = saveSuccessMessage!!, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
      }

      // 1. YouTube Data API v3 Configuration Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Key,
              contentDescription = "YouTube API",
              tint = YtRed,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "YouTube Data API v3",
              color = MaterialTheme.colorScheme.onBackground,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Enter your YouTube Data API v3 Key to stream live YouTube search, trending videos, and channel metadata directly from YouTube.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            lineHeight = 16.sp
          )

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = apiKey,
            onValueChange = { apiKey = it },
            label = { Text("YouTube API Key") },
            placeholder = { Text("AIzaSy...") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("settings_api_key_field"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = YtRed,
              unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(10.dp))

          Button(
            onClick = {
              onSaveApiKey(apiKey)
              saveSuccessMessage = "YouTube Data API Key Saved!"
            },
            colors = ButtonDefaults.buttonColors(containerColor = YtRed),
            modifier = Modifier
              .align(Alignment.End)
              .testTag("save_api_key_btn")
          ) {
            Text(text = "Save API Key", color = Color.White, fontWeight = FontWeight.Bold)
          }
        }
      }

      // 2. Cloudinary CDN Configuration Card
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Cloud,
              contentDescription = "Cloudinary CDN",
              tint = YtBlue,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Cloudinary Storage Setup",
              color = MaterialTheme.colorScheme.onBackground,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Configure your Cloudinary Cloud Name and Unsigned Upload Preset to host user-uploaded videos on Cloudinary CDN.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            lineHeight = 16.sp
          )

          Spacer(modifier = Modifier.height(12.dp))

          OutlinedTextField(
            value = cloudName,
            onValueChange = { cloudName = it },
            label = { Text("Cloudinary Cloud Name") },
            placeholder = { Text("demo or your_cloud_name") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("settings_cloud_name_field"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = YtBlue,
              unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = uploadPreset,
            onValueChange = { uploadPreset = it },
            label = { Text("Unsigned Upload Preset") },
            placeholder = { Text("unsigned_preset") },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("settings_upload_preset_field"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = YtBlue,
              unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            singleLine = true
          )

          Spacer(modifier = Modifier.height(10.dp))

          Button(
            onClick = {
              onSaveCloudinary(cloudName, uploadPreset)
              saveSuccessMessage = "Cloudinary Settings Saved!"
            },
            colors = ButtonDefaults.buttonColors(containerColor = YtBlue),
            modifier = Modifier
              .align(Alignment.End)
              .testTag("save_cloudinary_btn")
          ) {
            Text(text = "Save Cloudinary Config", color = Color.White, fontWeight = FontWeight.Bold)
          }
        }
      }

      // 3. Firebase Backend Connection Info Card
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Storage,
              contentDescription = "Firebase Firestore",
              tint = Color(0xFFFFCA28),
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Firebase Firestore Backend",
              color = MaterialTheme.colorScheme.onBackground,
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Database ID: $databaseId",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )

          Text(
            text = "Status: Provisioned & Rules Deployed • Realtime sync for videos, comments, and subscriptions.",
            color = Color(0xFF4CAF50),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )

          Spacer(modifier = Modifier.height(16.dp))

          Button(
            onClick = onDeleteAllVideos,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("delete_all_videos_btn")
          ) {
            Text(text = "Clear All Cloud Database Videos", color = Color.White, fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
