package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.YtComment
import com.example.data.model.YtVideo
import com.example.ui.components.ComposeVideoPlayer
import com.example.ui.components.VideoCardItem
import com.example.ui.components.formatViews
import com.example.ui.theme.YtBlue
import com.example.ui.theme.YtGrayChip
import com.example.ui.theme.YtRed

@Composable
fun VideoDetailScreen(
  video: YtVideo,
  suggestedVideos: List<YtVideo>,
  comments: List<YtComment>,
  aiSummaryText: String?,
  isGeneratingSummary: Boolean,
  onGenerateAiSummary: () -> Unit,
  onBackClick: () -> Unit,
  onVideoClick: (YtVideo) -> Unit,
  onToggleSubscribe: (String, String, String) -> Unit,
  onToggleLike: (String) -> Unit,
  onPostComment: (String) -> Unit
) {
  BackHandler { onBackClick() }

  var isLiked by remember { mutableStateOf(false) }
  var isSubscribed by remember { mutableStateOf(false) }
  var newCommentText by remember { mutableStateOf("") }
  var isDescriptionExpanded by remember { mutableStateOf(false) }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Top Bar with Back Arrow + Video Player
    Box(modifier = Modifier.fillMaxWidth()) {
      ComposeVideoPlayer(video = video)

      IconButton(
        onClick = onBackClick,
        modifier = Modifier
          .align(Alignment.TopStart)
          .padding(8.dp)
          .clip(CircleShape)
          .background(Color.Black.copy(alpha = 0.5f))
          .testTag("video_detail_back_btn")
      ) {
        Icon(
          imageVector = Icons.Default.ArrowBack,
          contentDescription = "Back",
          tint = Color.White
        )
      }
    }

    LazyColumn(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .testTag("video_detail_scroll_column")
    ) {
      // 1. Video Info & Actions Header
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
        ) {
          // Video Title
          Text(
            text = video.title,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 22.sp
          )

          Spacer(modifier = Modifier.height(6.dp))

          // View count, time, description expander
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { isDescriptionExpanded = !isDescriptionExpanded }
          ) {
            Text(
              text = "${formatViews(video.viewsCount)} views • 2 days ago",
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 12.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (isDescriptionExpanded) "...less" else "...more",
              color = MaterialTheme.colorScheme.onBackground,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }

          AnimatedVisibility(visible = isDescriptionExpanded) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(12.dp)
            ) {
              Text(
                text = video.description.ifBlank { "No additional description provided." },
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 13.sp
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Gemini AI Video Summarizer Card Button
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFF1E2638))
              .clickable { onGenerateAiSummary() }
              .padding(12.dp)
          ) {
            Column {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Gemini AI",
                    tint = YtBlue,
                    modifier = Modifier.size(20.dp)
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "Summarize Video with Gemini AI",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                  )
                }

                if (isGeneratingSummary) {
                  CircularProgressIndicator(
                    color = YtBlue,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }

              if (!aiSummaryText.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = aiSummaryText,
                  color = Color.White.copy(alpha = 0.9f),
                  fontSize = 12.sp,
                  lineHeight = 16.sp
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Channel Bar: Avatar, Name, Subscribers, Subscribe Button
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              AsyncImage(
                model = video.channelAvatarUrl.ifEmpty { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&auto=format&fit=crop&q=80" },
                contentDescription = video.channelName,
                modifier = Modifier
                  .size(42.dp)
                  .clip(CircleShape)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = video.channelName,
                  color = MaterialTheme.colorScheme.onBackground,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "124K subscribers",
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 11.sp
                )
              }
            }

            Button(
              onClick = {
                isSubscribed = !isSubscribed
                onToggleSubscribe(video.userId, video.channelName, video.channelAvatarUrl)
              },
              colors = ButtonDefaults.buttonColors(
                containerColor = if (isSubscribed) YtGrayChip else YtRed
              ),
              modifier = Modifier.testTag("subscribe_channel_btn")
            ) {
              Text(
                text = if (isSubscribed) "Subscribed" else "Subscribe",
                color = if (isSubscribed) MaterialTheme.colorScheme.onBackground else Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Horizontal Action Pill Row (Like, Dislike, Share, Download, Save)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Like / Dislike Combined Pill
            Row(
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                modifier = Modifier
                  .clickable {
                    isLiked = !isLiked
                    onToggleLike(video.id.ifEmpty { video.videoId })
                  }
                  .testTag("like_video_btn"),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = if (isLiked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                  contentDescription = "Like",
                  tint = if (isLiked) YtRed else MaterialTheme.colorScheme.onBackground,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = formatViews(video.likesCount + if (isLiked) 1 else 0),
                  color = MaterialTheme.colorScheme.onBackground,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
              }

              Spacer(modifier = Modifier.width(10.dp))
              Divider(
                modifier = Modifier
                  .height(16.dp)
                  .width(1.dp),
                color = MaterialTheme.colorScheme.outline
              )
              Spacer(modifier = Modifier.width(10.dp))

              Icon(
                imageVector = Icons.Outlined.ThumbDown,
                contentDescription = "Dislike",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                  .size(18.dp)
                  .clickable { }
              )
            }

            // Share Pill
            Row(
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { }
                .padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Share",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(text = "Share", color = MaterialTheme.colorScheme.onBackground, fontSize = 12.sp)
            }

            // Download Pill
            Row(
              modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable { }
                .padding(horizontal = 12.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Download,
                contentDescription = "Download",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(text = "Download", color = MaterialTheme.colorScheme.onBackground, fontSize = 12.sp)
            }
          }
        }
      }

      // 2. Comments Box Header & Input
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(14.dp)
        ) {
          Text(
            text = "Comments (${comments.size})",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Add Comment Input Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedTextField(
              value = newCommentText,
              onValueChange = { newCommentText = it },
              placeholder = { Text("Add a comment...", fontSize = 13.sp) },
              modifier = Modifier
                .weight(1f)
                .testTag("comment_input_field"),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = YtRed,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
              ),
              singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
              onClick = {
                if (newCommentText.isNotBlank()) {
                  onPostComment(newCommentText)
                  newCommentText = ""
                }
              },
              modifier = Modifier.testTag("post_comment_btn")
            ) {
              Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Send Comment",
                tint = if (newCommentText.isNotBlank()) YtRed else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Recent Comments Preview List
          comments.take(3).forEach { comment ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
              verticalAlignment = Alignment.Top
            ) {
              AsyncImage(
                model = comment.userAvatarUrl.ifEmpty { "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&auto=format&fit=crop&q=80" },
                contentDescription = comment.userName,
                modifier = Modifier
                  .size(28.dp)
                  .clip(CircleShape)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = comment.userName,
                  color = MaterialTheme.colorScheme.onBackground,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = comment.text,
                  color = MaterialTheme.colorScheme.onBackground,
                  fontSize = 13.sp
                )
              }
            }
          }
        }
      }

      // 3. Up Next / Suggested Videos Section Header
      item {
        Text(
          text = "Up Next",
          color = MaterialTheme.colorScheme.onBackground,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.padding(start = 14.dp, top = 12.dp, bottom = 8.dp)
        )
      }

      // 4. Suggested Videos List
      items(suggestedVideos) { sugVideo ->
        VideoCardItem(
          video = sugVideo,
          onClick = { onVideoClick(sugVideo) }
        )
      }
    }
  }
}
