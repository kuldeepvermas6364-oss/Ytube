package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.YtVideo
import com.example.ui.components.VideoCardItem
import com.example.ui.theme.YtRed

@Composable
fun SearchScreen(
  searchQuery: String,
  searchResults: List<YtVideo>,
  isSearching: Boolean,
  onQueryChange: (String) -> Unit,
  onSearchSubmit: (String) -> Unit,
  onVideoClick: (YtVideo) -> Unit,
  onBackClick: () -> Unit
) {
  BackHandler { onBackClick() }

  val sampleSuggestions = listOf("Jetpack Compose", "Android Development", "Cloudinary Tutorial", "Lofi Music", "Unreal Engine 5")

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Search Top Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      IconButton(onClick = onBackClick) {
        Icon(
          imageVector = Icons.Default.ArrowBack,
          contentDescription = "Back",
          tint = MaterialTheme.colorScheme.onBackground
        )
      }

      OutlinedTextField(
        value = searchQuery,
        onValueChange = {
          onQueryChange(it)
          onSearchSubmit(it)
        },
        placeholder = { Text("Search YouTube...", fontSize = 14.sp) },
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search Icon",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { onQueryChange("") }) {
              Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Clear",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        },
        modifier = Modifier
          .weight(1f)
          .testTag("search_input_field"),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = YtRed,
          unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        singleLine = true
      )
    }

    if (isSearching) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        CircularProgressIndicator(color = YtRed)
      }
    } else if (searchQuery.isBlank()) {
      // Suggestions list
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        Text(
          text = "Trending Searches",
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(10.dp))

        sampleSuggestions.forEach { suggestion ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                onQueryChange(suggestion)
                onSearchSubmit(suggestion)
              }
              .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Suggestion",
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
              text = suggestion,
              color = MaterialTheme.colorScheme.onBackground,
              fontSize = 14.sp
            )
          }
        }
      }
    } else {
      // Results list
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .testTag("search_results_list")
      ) {
        items(searchResults) { video ->
          VideoCardItem(
            video = video,
            onClick = { onVideoClick(video) }
          )
        }
      }
    }
  }
}
