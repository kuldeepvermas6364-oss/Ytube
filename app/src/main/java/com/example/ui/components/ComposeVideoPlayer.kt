package com.example.ui.components

import android.util.Log
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.example.data.model.YtVideo
import com.example.ui.theme.YtRed
import kotlinx.coroutines.delay

@Composable
fun ComposeVideoPlayer(
  video: YtVideo,
  modifier: Modifier = Modifier
) {
  val isYouTubeVideo = !video.isCloudinary && video.videoId.isNotBlank()

  Box(
    modifier = modifier
      .fillMaxWidth()
      .aspectRatio(16f / 9f)
      .background(Color.Black)
      .testTag("video_player_box")
  ) {
    if (isYouTubeVideo) {
      // Stream real YouTube Video Embed via WebView
      AndroidView(
        factory = { ctx ->
          WebView(ctx).apply {
            layoutParams = android.view.ViewGroup.LayoutParams(
              android.view.ViewGroup.LayoutParams.MATCH_PARENT,
              android.view.ViewGroup.LayoutParams.MATCH_PARENT
            )
            settings.apply {
              javaScriptEnabled = true
              domStorageEnabled = true
              loadWithOverviewMode = true
              useWideViewPort = true
              mediaPlaybackRequiresUserGesture = false
              mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
              userAgentString = "Mozilla/5.0 (Linux; Android 12; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
            }
            webViewClient = WebViewClient()
            webChromeClient = android.webkit.WebChromeClient()
            
            val videoId = video.videoId
            val html = """
              <!DOCTYPE html>
              <html>
              <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                <style>
                  body, html { margin:0; padding:0; width:100%; height:100%; background-color:#000; overflow:hidden; }
                  iframe { width:100%; height:100%; border:none; position:absolute; top:0; left:0; }
                </style>
              </head>
              <body>
                <iframe 
                  src="https://www.youtube.com/embed/$videoId?autoplay=1&playsinline=1&enablejsapi=1&rel=0&modestbranding=1" 
                  allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
                  allowfullscreen>
                </iframe>
              </body>
              </html>
            """.trimIndent()
            loadDataWithBaseURL("https://www.youtube.com", html, "text/html", "utf-8", null)
          }
        },
        update = { webView ->
          if (webView.tag != video.videoId) {
            webView.tag = video.videoId
            val videoId = video.videoId
            val html = """
              <!DOCTYPE html>
              <html>
              <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
                <style>
                  body, html { margin:0; padding:0; width:100%; height:100%; background-color:#000; overflow:hidden; }
                  iframe { width:100%; height:100%; border:none; position:absolute; top:0; left:0; }
                </style>
              </head>
              <body>
                <iframe 
                  src="https://www.youtube.com/embed/$videoId?autoplay=1&playsinline=1&enablejsapi=1&rel=0&modestbranding=1" 
                  allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
                  allowfullscreen>
                </iframe>
              </body>
              </html>
            """.trimIndent()
            webView.loadDataWithBaseURL("https://www.youtube.com", html, "text/html", "utf-8", null)
          }
        },
        modifier = Modifier.fillMaxSize()
      )
    } else {
      // Cloudinary / Standard Video Player view
      var isPlaying by remember { mutableStateOf(true) }
      var showControls by remember { mutableStateOf(true) }

      Box(
        modifier = Modifier
          .fillMaxSize()
          .clickable { showControls = !showControls }
      ) {
        AsyncImage(
          model = video.thumbnailUrl,
          contentDescription = video.title,
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )

        AnimatedVisibility(
          visible = showControls,
          enter = fadeIn(),
          exit = fadeOut(),
          modifier = Modifier.fillMaxSize()
        ) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(Color.Black.copy(alpha = 0.55f))
          ) {
            Box(
              modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(YtRed)
                .align(Alignment.Center)
                .clickable { isPlaying = !isPlaying },
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = Color.White,
                modifier = Modifier.size(34.dp)
              )
            }
          }
        }
      }
    }
  }
}
