package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.YtGrayChip

@Composable
fun CategoryChips(
  selectedCategory: String,
  onCategorySelected: (String) -> Unit
) {
  val categories = listOf("All", "Gaming", "Music", "Tech", "Cloudinary", "Shorts", "Travel", "Live")

  LazyRow(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 8.dp)
      .testTag("category_chips_row"),
    contentPadding = PaddingValues(horizontal = 12.dp)
  ) {
    items(categories) { category ->
      val isSelected = category == selectedCategory
      Box(
        modifier = Modifier
          .padding(horizontal = 4.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(
            if (isSelected) MaterialTheme.colorScheme.onBackground else YtGrayChip
          )
          .clickable { onCategorySelected(category) }
          .padding(horizontal = 14.dp, vertical = 6.dp)
          .testTag("chip_${category.lowercase()}")
      ) {
        Text(
          text = category,
          color = if (isSelected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onBackground,
          fontSize = 13.sp,
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
      }
    }
  }
}
