package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.YtSubscription
import com.example.data.model.YtVideo
import com.example.ui.components.VideoCardItem
import com.example.ui.theme.YtBlue
import com.example.ui.theme.YtRed

@Composable
fun SubscriptionsScreen(
  subscriptions: List<YtSubscription>,
  feedVideos: List<YtVideo>,
  onVideoClick: (YtVideo) -> Unit
) {
  val defaultChannels = listOf(
    Pair("Android Dev Pro", "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200&auto=format&fit=crop&q=80"),
    Pair("Lofi Girl", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200&auto=format&fit=crop&q=80"),
    Pair("Game Tech", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200&auto=format&fit=crop&q=80"),
    Pair("Cloud Systems", "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=200&auto=format&fit=crop&q=80")
  )

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    Text(
      text = "Subscriptions",
      color = MaterialTheme.colorScheme.onBackground,
      fontSize = 20.sp,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 8.dp)
    )

    // Top Channels Row
    LazyRow(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 12.dp)
        .testTag("subscriptions_channel_row"),
      contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
      if (subscriptions.isNotEmpty()) {
        items(subscriptions) { sub ->
          ChannelAvatarItem(name = sub.channelName, avatarUrl = sub.channelAvatarUrl)
        }
      } else {
        items(defaultChannels) { (name, avatar) ->
          ChannelAvatarItem(name = name, avatarUrl = avatar)
        }
      }
    }

    // Subscribed Videos Feed
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .testTag("subscriptions_feed_list")
    ) {
      items(feedVideos) { video ->
        VideoCardItem(
          video = video,
          onClick = { onVideoClick(video) }
        )
      }
    }
  }
}

@Composable
private fun ChannelAvatarItem(
  name: String,
  avatarUrl: String
) {
  Column(
    modifier = Modifier
      .padding(horizontal = 8.dp)
      .clickable { },
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Box {
      AsyncImage(
        model = avatarUrl,
        contentDescription = name,
        modifier = Modifier
          .size(56.dp)
          .clip(CircleShape)
          .border(2.dp, YtRed, CircleShape)
      )

      // Unread notification badge
      Box(
        modifier = Modifier
          .size(12.dp)
          .clip(CircleShape)
          .background(YtBlue)
          .align(Alignment.BottomEnd)
      )
    }

    Spacer(modifier = Modifier.height(4.dp))

    Text(
      text = name,
      color = MaterialTheme.colorScheme.onBackground,
      fontSize = 11.sp,
      fontWeight = FontWeight.Medium,
      maxLines = 1,
      overflow = TextOverflow.Ellipsis,
      modifier = Modifier.size(60.dp, 16.dp)
    )
  }
}
