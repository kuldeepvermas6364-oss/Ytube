package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.YtVideo
import com.example.data.model.YtWatchHistory
import com.example.ui.components.VideoCardItem
import com.example.ui.theme.YtBlue
import com.example.ui.theme.YtRed
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@Composable
fun LibraryScreen(
  watchHistory: List<YtWatchHistory>,
  cloudinaryVideos: List<YtVideo>,
  allVideos: List<YtVideo>,
  onVideoClick: (YtVideo) -> Unit,
  onOpenSettings: () -> Unit,
  onSignOut: () -> Unit
) {
  val user = Firebase.auth.currentUser

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 14.dp)
      .testTag("library_screen_column")
  ) {
    // 1. Profile Header Box
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        AsyncImage(
          model = user?.photoUrl?.toString() ?: "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&auto=format&fit=crop&q=80",
          contentDescription = "Profile Photo",
          modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = user?.displayName ?: "Guest Creator",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = user?.email ?: "@guest_account • View Channel",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
          )
        }

        Button(
          onClick = onOpenSettings,
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
          modifier = Modifier.testTag("library_settings_btn")
        ) {
          Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }

    // 2. History Section Header & Horizontal Row
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.History,
            contentDescription = "History",
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "History",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      if (watchHistory.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(14.dp)
        ) {
          Text(
            text = "Videos you watch will appear here.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
          )
        }
      } else {
        LazyRow(
          contentPadding = PaddingValues(vertical = 8.dp),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(watchHistory) { historyItem ->
            val matchVideo = allVideos.find { it.id == historyItem.videoId || it.videoId == historyItem.videoId }
            Card(
              modifier = Modifier
                .width(140.dp)
                .clickable {
                  matchVideo?.let { onVideoClick(it) }
                },
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
              Column {
                AsyncImage(
                  model = historyItem.thumbnailUrl,
                  contentDescription = historyItem.title,
                  modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
                  contentScale = ContentScale.Crop
                )
                Text(
                  text = historyItem.title,
                  color = MaterialTheme.colorScheme.onBackground,
                  fontSize = 12.sp,
                  maxLines = 2,
                  overflow = TextOverflow.Ellipsis,
                  modifier = Modifier.padding(6.dp)
                )
              }
            }
          }
        }
      }
    }

    // 3. My Cloudinary Uploaded Videos
    item {
      Spacer(modifier = Modifier.height(16.dp))

      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 8.dp)
      ) {
        Icon(
          imageVector = Icons.Default.CloudDone,
          contentDescription = "Cloudinary Uploads",
          tint = YtBlue,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "My Cloudinary Uploads (${cloudinaryVideos.size})",
          color = MaterialTheme.colorScheme.onBackground,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
      }

      if (cloudinaryVideos.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(14.dp)
        ) {
          Text(
            text = "No Cloudinary videos uploaded yet. Tap '+' in the bottom bar to upload videos!",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
          )
        }
      }
    }

    items(cloudinaryVideos) { cloudVideo ->
      VideoCardItem(
        video = cloudVideo,
        onClick = { onVideoClick(cloudVideo) }
      )
    }

    // 4. Sign Out button if authenticated
    item {
      Spacer(modifier = Modifier.height(24.dp))
      if (user != null) {
        Button(
          onClick = onSignOut,
          colors = ButtonDefaults.buttonColors(containerColor = YtRed),
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .testTag("sign_out_btn")
        ) {
          Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = "Sign Out",
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "Sign Out of Google Account", color = Color.White, fontWeight = FontWeight.Bold)
        }
      }
      Spacer(modifier = Modifier.height(32.dp))
    }
  }
}
