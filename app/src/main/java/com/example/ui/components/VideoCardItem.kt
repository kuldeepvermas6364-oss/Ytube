package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.ui.theme.YtBlue

@Composable
fun VideoCardItem(
  video: YtVideo,
  onClick: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(bottom = 16.dp)
      .testTag("video_card_${video.id.ifEmpty { video.videoId }}")
  ) {
    // Thumbnail Container with Aspect Ratio 16:9
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(16f / 9f)
        .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
      AsyncImage(
        model = video.thumbnailUrl,
        contentDescription = video.title,
        modifier = Modifier.fillMaxWidth(),
        contentScale = ContentScale.Crop
      )

      // Cloudinary Badge
      if (video.isCloudinary) {
        Box(
          modifier = Modifier
            .align(Alignment.TopStart)
            .padding(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF0038FF))
            .padding(horizontal = 6.dp, vertical = 3.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.CloudDone,
              contentDescription = "Cloudinary",
              tint = Color.White,
              modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Cloudinary",
              color = Color.White,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      // Duration Overlay Pill
      Box(
        modifier = Modifier
          .align(Alignment.BottomEnd)
          .padding(8.dp)
          .clip(RoundedCornerShape(4.dp))
          .background(Color.Black.copy(alpha = 0.8f))
          .padding(horizontal = 6.dp, vertical = 2.dp)
      ) {
        Text(
          text = video.duration,
          color = Color.White,
          fontSize = 11.sp,
          fontWeight = FontWeight.Medium
        )
      }
    }

    // Video Meta Details (Avatar, Title, Channel Info)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(top = 10.dp, start = 12.dp, end = 4.dp),
      verticalAlignment = Alignment.Top
    ) {
      AsyncImage(
        model = video.channelAvatarUrl.ifEmpty { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&auto=format&fit=crop&q=80" },
        contentDescription = video.channelName,
        modifier = Modifier
          .size(38.dp)
          .clip(CircleShape),
        contentScale = ContentScale.Crop
      )

      Spacer(modifier = Modifier.width(10.dp))

      Column(
        modifier = Modifier.weight(1f)
      ) {
        Text(
          text = video.title,
          color = MaterialTheme.colorScheme.onBackground,
          fontSize = 14.sp,
          fontWeight = FontWeight.Medium,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis,
          lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = "${video.channelName} • ${formatViews(video.viewsCount)} views • ${formatPublishedAt(video.publishedAt)}",
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 12.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }

      IconButton(
        onClick = { },
        modifier = Modifier.size(36.dp)
      ) {
        Icon(
          imageVector = Icons.Default.MoreVert,
          contentDescription = "Options",
          tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}

fun formatPublishedAt(publishedAt: String): String {
  if (publishedAt.isBlank()) return "Recently"
  return try {
    val instant = java.time.Instant.parse(publishedAt)
    val now = java.time.Instant.now()
    val seconds = java.time.Duration.between(instant, now).seconds
    
    when {
      seconds < 60 -> "Just now"
      seconds < 3600 -> "${seconds / 60} minutes ago"
      seconds < 86400 -> "${seconds / 3600} hours ago"
      seconds < 2592000 -> "${seconds / 86400} days ago"
      seconds < 31536000 -> "${seconds / 2592000} months ago"
      else -> "${seconds / 31536000} years ago"
    }
  } catch (e: Exception) {
    "Recently"
  }
}

fun formatViews(count: Long): String {
  return when {
    count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
    count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
    else -> count.toString()
  }
}
