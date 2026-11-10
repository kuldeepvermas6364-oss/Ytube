package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material.icons.outlined.VideoLibrary
import androidx.compose.material3.Icon
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
import com.example.ui.theme.YtRed
import com.example.ui.viewmodel.Screen

@Composable
fun BottomNavigationBar(
  currentScreen: Screen,
  onScreenSelected: (Screen) -> Unit,
  onUploadClicked: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(60.dp)
      .background(MaterialTheme.colorScheme.surface)
      .windowInsetsPadding(WindowInsets.navigationBars),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceAround
  ) {
    // 1. Home Tab
    val isHome = currentScreen is Screen.Home
    NavItem(
      label = "Home",
      selected = isHome,
      iconSelected = Icons.Filled.Home,
      iconUnselected = Icons.Outlined.Home,
      testTag = "nav_home",
      onClick = { onScreenSelected(Screen.Home) }
    )

    // 2. Shorts Tab
    val isShorts = currentScreen is Screen.Shorts
    NavItem(
      label = "Shorts",
      selected = isShorts,
      iconSelected = Icons.Filled.VideoLibrary,
      iconUnselected = Icons.Outlined.VideoLibrary,
      testTag = "nav_shorts",
      onClick = { onScreenSelected(Screen.Shorts) }
    )

    // 3. Upload (+) Button
    Box(
      modifier = Modifier
        .size(42.dp)
        .clip(CircleShape)
        .background(MaterialTheme.colorScheme.surfaceVariant)
        .clickable { onUploadClicked() }
        .testTag("nav_upload_btn"),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.Add,
        contentDescription = "Upload Video to Cloudinary",
        tint = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.size(28.dp)
      )
    }

    // 4. Subscriptions Tab
    val isSubs = currentScreen is Screen.Subscriptions
    NavItem(
      label = "Subscriptions",
      selected = isSubs,
      iconSelected = Icons.Filled.Subscriptions,
      iconUnselected = Icons.Outlined.Subscriptions,
      testTag = "nav_subscriptions",
      onClick = { onScreenSelected(Screen.Subscriptions) }
    )

    // 5. You / Library Tab
    val isLib = currentScreen is Screen.Library
    NavItem(
      label = "You",
      selected = isLib,
      iconSelected = Icons.Filled.Person,
      iconUnselected = Icons.Outlined.Person,
      testTag = "nav_library",
      onClick = { onScreenSelected(Screen.Library) }
    )
  }
}

@Composable
private fun NavItem(
  label: String,
  selected: Boolean,
  iconSelected: androidx.compose.ui.graphics.vector.ImageVector,
  iconUnselected: androidx.compose.ui.graphics.vector.ImageVector,
  testTag: String,
  onClick: () -> Unit
) {
  Column(
    modifier = Modifier
      .clickable { onClick() }
      .padding(horizontal = 12.dp, vertical = 6.dp)
      .testTag(testTag),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    Icon(
      imageVector = if (selected) iconSelected else iconUnselected,
      contentDescription = label,
      tint = if (selected) YtRed else MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.size(24.dp)
    )
    Text(
      text = label,
      color = if (selected) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant,
      fontSize = 10.sp,
      fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
    )
  }
}
