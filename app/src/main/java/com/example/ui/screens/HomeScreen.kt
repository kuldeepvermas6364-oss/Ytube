package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.YtVideo
import com.example.ui.components.CategoryChips
import com.example.ui.components.VideoCardItem
import com.example.ui.theme.YtRed

@Composable
fun HomeScreen(
  videos: List<YtVideo>,
  selectedCategory: String,
  isLoading: Boolean,
  onCategorySelected: (String) -> Unit,
  onVideoClick: (YtVideo) -> Unit,
  onOpenSettings: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Category Filter Chips
    CategoryChips(
      selectedCategory = selectedCategory,
      onCategorySelected = onCategorySelected
    )

    // Videos Feed List
    if (isLoading && videos.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        CircularProgressIndicator(color = YtRed)
      }
    } else if (videos.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "No videos available for category '$selectedCategory'",
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 14.sp
        )
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .testTag("home_video_list")
      ) {
        items(videos, key = { it.id.ifEmpty { it.videoId } }) { video ->
          VideoCardItem(
            video = video,
            onClick = { onVideoClick(video) }
          )
        }
      }
    }
  }
}
