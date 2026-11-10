package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.YtRed

@Composable
fun YouTubeHeader(
  apiKeyConnected: Boolean,
  userPhotoUrl: String?,
  onSearchClick: () -> Unit,
  onSettingsClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(56.dp)
      .background(MaterialTheme.colorScheme.background)
      .padding(horizontal = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    // YouTube Logo Icon + Brand Name
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .testTag("youtube_header_logo")
        .clickable { }
    ) {
      Box(
        modifier = Modifier
          .size(32.dp, 22.dp)
          .clip(RoundedCornerShape(6.dp))
          .background(YtRed),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.PlayArrow,
          contentDescription = "YouTube Logo",
          tint = Color.White,
          modifier = Modifier.size(20.dp)
        )
      }
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "YouTube",
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.5).sp
      )
    }

    // Action Icons (Cast, Notifications, Search, Settings / Profile)
    Row(
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(
        onClick = { },
        modifier = Modifier.testTag("header_cast_btn")
      ) {
        Icon(
          imageVector = Icons.Default.Cast,
          contentDescription = "Cast Screen",
          tint = MaterialTheme.colorScheme.onBackground
        )
      }

      IconButton(
        onClick = { },
        modifier = Modifier.testTag("header_notifications_btn")
      ) {
        Icon(
          imageVector = Icons.Default.Notifications,
          contentDescription = "Notifications",
          tint = MaterialTheme.colorScheme.onBackground
        )
      }

      IconButton(
        onClick = onSearchClick,
        modifier = Modifier.testTag("header_search_btn")
      ) {
        Icon(
          imageVector = Icons.Default.Search,
          contentDescription = "Search Videos",
          tint = MaterialTheme.colorScheme.onBackground
        )
      }

      IconButton(
        onClick = onSettingsClick,
        modifier = Modifier.testTag("header_settings_btn")
      ) {
        if (!userPhotoUrl.isNullOrBlank()) {
          AsyncImage(
            model = userPhotoUrl,
            contentDescription = "User Settings",
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
          )
        } else {
          Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            tint = MaterialTheme.colorScheme.onBackground
          )
        }
      }
    }
  }
}
