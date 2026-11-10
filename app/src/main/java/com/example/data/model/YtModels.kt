package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class YtVideo(
  @DocumentId val id: String = "",
  val videoId: String = "",
  val userId: String = "",
  val title: String = "",
  val description: String = "",
  val thumbnailUrl: String = "",
  val videoUrl: String = "",
  val isCloudinary: Boolean = false,
  val isShort: Boolean = false,
  val category: String = "General",
  val viewsCount: Long = 0L,
  val likesCount: Long = 0L,
  val commentsCount: Long = 0L,
  val channelName: String = "Creator Channel",
  val channelAvatarUrl: String = "",
  val duration: String = "03:45",
  val publishedAt: String = "",
  @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class YtComment(
  @DocumentId val id: String = "",
  val commentId: String = "",
  val videoId: String = "",
  val userId: String = "",
  val userName: String = "User",
  val userAvatarUrl: String = "",
  val text: String = "",
  val likesCount: Long = 0L,
  @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class YtSubscription(
  @DocumentId val id: String = "",
  val subId: String = "",
  val subscriberUid: String = "",
  val channelId: String = "",
  val channelName: String = "",
  val channelAvatarUrl: String = "",
  @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class YtLike(
  @DocumentId val id: String = "",
  val likeId: String = "",
  val userId: String = "",
  val videoId: String = "",
  @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class YtWatchHistory(
  @DocumentId val id: String = "",
  val historyId: String = "",
  val userId: String = "",
  val videoId: String = "",
  val title: String = "",
  val thumbnailUrl: String = "",
  val channelName: String = "",
  @ServerTimestamp val watchedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class YtUserProfile(
  @DocumentId val id: String = "",
  val userId: String = "",
  val displayName: String = "",
  val email: String = "",
  val photoUrl: String = "",
  val channelName: String = "",
  val handle: String = "",
  val subscribersCount: Long = 0L,
  @ServerTimestamp val createdAt: Timestamp? = null
)

// YouTube Data API v3 Response Data Classes
data class YouTubeSearchResponse(
  val items: List<YouTubeSearchResultItem>? = null
)

data class YouTubeSearchResultItem(
  val id: YouTubeIdItem? = null,
  val snippet: YouTubeSnippetItem? = null
)

data class YouTubeIdItem(
  val kind: String? = null,
  val videoId: String? = null,
  val channelId: String? = null
)

data class YouTubeVideoListResponse(
  val items: List<YouTubeVideoItem>? = null
)

data class YouTubeVideoItem(
  val id: String? = null,
  val snippet: YouTubeSnippetItem? = null,
  val statistics: YouTubeStatisticsItem? = null,
  val contentDetails: YouTubeContentDetailsItem? = null
)

data class YouTubeSnippetItem(
  val publishedAt: String? = null,
  val channelId: String? = null,
  val title: String? = null,
  val description: String? = null,
  val thumbnails: YouTubeThumbnailsItem? = null,
  val channelTitle: String? = null,
  val categoryId: String? = null
)

data class YouTubeThumbnailsItem(
  val high: YouTubeThumbnailDetails? = null,
  val medium: YouTubeThumbnailDetails? = null,
  val default: YouTubeThumbnailDetails? = null,
  val standard: YouTubeThumbnailDetails? = null,
  val maxres: YouTubeThumbnailDetails? = null
)

data class YouTubeThumbnailDetails(
  val url: String? = null,
  val width: Int? = null,
  val height: Int? = null
)

data class YouTubeStatisticsItem(
  val viewCount: String? = null,
  val likeCount: String? = null,
  val commentCount: String? = null
)

data class YouTubeContentDetailsItem(
  val duration: String? = null
)

data class YouTubeChannelListResponse(
  val items: List<YouTubeChannelItem>? = null
)

data class YouTubeChannelItem(
  val id: String? = null,
  val snippet: YouTubeSnippetItem? = null,
  val statistics: YouTubeStatisticsItem? = null
)

data class CloudinaryUploadResponse(
  val secure_url: String? = null,
  val url: String? = null,
  val public_id: String? = null,
  val format: String? = null,
  val duration: Double? = null,
  val error: CloudinaryError? = null
)

data class CloudinaryError(
  val message: String? = null
)
