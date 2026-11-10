package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.YtVideo
import com.example.ui.components.formatViews
import com.example.ui.theme.YtRed

@Composable
fun ShortsScreen(
  shorts: List<YtVideo>,
  onVideoClick: (YtVideo) -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black)
      .testTag("shorts_feed_column")
  ) {
    items(shorts) { short ->
      ShortItemView(
        short = short,
        onClick = { onVideoClick(short) }
      )
    }
  }
}

@Composable
private fun ShortItemView(
  short: YtVideo,
  onClick: () -> Unit
) {
  var isLiked by remember { mutableStateOf(false) }
  var isSubscribed by remember { mutableStateOf(false) }

  Box(
    modifier = Modifier
      .fillMaxWidth()
      .height(680.dp)
      .clickable { onClick() }
      .testTag("short_item_${short.id.ifEmpty { short.videoId }}")
  ) {
    // Full Screen Background Image / Video Canvas
    AsyncImage(
      model = short.thumbnailUrl,
      contentDescription = short.title,
      modifier = Modifier.fillMaxSize(),
      contentScale = ContentScale.Crop
    )

    // Gradient Overlay at bottom for readable text
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(260.dp)
        .align(Alignment.BottomCenter)
        .background(
          Brush.verticalGradient(
            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
          )
        )
    )

    // Left Bottom: Channel Info & Title
    Column(
      modifier = Modifier
        .align(Alignment.BottomStart)
        .padding(start = 16.dp, bottom = 24.dp, end = 80.dp)
    ) {
      // Channel avatar, name & subscribe button
      Row(verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
          model = short.channelAvatarUrl.ifEmpty { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&auto=format&fit=crop&q=80" },
          contentDescription = short.channelName,
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "@${short.channelName.lowercase().replace(" ", "")}",
          color = Color.White,
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(10.dp))
        Button(
          onClick = { isSubscribed = !isSubscribed },
          colors = ButtonDefaults.buttonColors(
            containerColor = if (isSubscribed) Color.White.copy(alpha = 0.3f) else YtRed
          ),
          modifier = Modifier.height(30.dp),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 0.dp)
        ) {
          Text(
            text = if (isSubscribed) "Subscribed" else "Subscribe",
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      Text(
        text = short.title,
        color = Color.White,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(8.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.MusicNote,
          contentDescription = "Audio",
          tint = Color.White,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = "${short.channelName} • Original Audio",
          color = Color.White.copy(alpha = 0.8f),
          fontSize = 11.sp
        )
      }
    }

    // Right Column: Like, Dislike, Comment, Share, Sound disc
    Column(
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(end = 16.dp, bottom = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
      // Like
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
          onClick = { isLiked = !isLiked },
          modifier = Modifier
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.4f))
            .size(44.dp)
        ) {
          Icon(
            imageVector = if (isLiked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
            contentDescription = "Like",
            tint = if (isLiked) YtRed else Color.White
          )
        }
        Text(
          text = formatViews(short.likesCount + if (isLiked) 1 else 0),
          color = Color.White,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }

      // Dislike
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
          onClick = { },
          modifier = Modifier
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.4f))
            .size(44.dp)
        ) {
          Icon(
            imageVector = Icons.Outlined.ThumbDown,
            contentDescription = "Dislike",
            tint = Color.White
          )
        }
        Text(text = "Dislike", color = Color.White, fontSize = 11.sp)
      }

      // Comment
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
          onClick = onClick,
          modifier = Modifier
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.4f))
            .size(44.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Comment,
            contentDescription = "Comments",
            tint = Color.White
          )
        }
        Text(
          text = formatViews(short.commentsCount),
          color = Color.White,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }

      // Share
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
          onClick = { },
          modifier = Modifier
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.4f))
            .size(44.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "Share",
            tint = Color.White
          )
        }
        Text(text = "Share", color = Color.White, fontSize = 11.sp)
      }

      // Spinning Sound Disc
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(Color.White.copy(alpha = 0.2f))
          .padding(2.dp),
        contentAlignment = Alignment.Center
      ) {
        AsyncImage(
          model = short.channelAvatarUrl.ifEmpty { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&auto=format&fit=crop&q=80" },
          contentDescription = "Sound Thumbnail",
          modifier = Modifier
            .fillMaxSize()
            .clip(CircleShape),
          contentScale = ContentScale.Crop
        )
      }
    }
  }
}
