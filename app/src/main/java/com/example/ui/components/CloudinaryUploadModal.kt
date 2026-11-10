package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.ui.theme.YtBlue
import com.example.ui.theme.YtRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CloudinaryUploadModal(
  cloudName: String,
  uploadPreset: String,
  isUploading: Boolean,
  uploadStatusMessage: String?,
  onDismiss: () -> Unit,
  onUpload: (Uri?, String, String, String, Boolean) -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Tech") }
  var isShort by remember { mutableStateOf(false) }
  var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }

  // Media Picker for video selection
  val videoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
  ) { uri: Uri? ->
    selectedVideoUri = uri
    if (title.isBlank() && uri != null) {
      title = "My Cloudinary Video ${System.currentTimeMillis() % 1000}"
    }
  }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 12.dp)
        .verticalScroll(rememberScrollState())
        .testTag("upload_bottom_sheet")
    ) {
      // Sheet Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.CloudUpload,
            contentDescription = "Cloudinary Upload",
            tint = YtBlue,
            modifier = Modifier.size(28.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Upload Video to Cloudinary",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
          )
        }

        IconButton(onClick = onDismiss) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      // Cloudinary Account Info Box
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 10.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant)
          .padding(12.dp)
      ) {
        Column {
          Text(
            text = "Cloudinary Account: $cloudName",
            color = YtBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Upload Preset: $uploadPreset • Cloud Storage CDN",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
          )
        }
      }

      // Video File Picker Trigger
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(110.dp)
          .clip(RoundedCornerShape(12.dp))
          .border(
            width = 1.5.dp,
            color = if (selectedVideoUri != null) YtBlue else MaterialTheme.colorScheme.outline,
            shape = RoundedCornerShape(12.dp)
          )
          .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
          .clickable { videoPickerLauncher.launch("video/*") }
          .padding(12.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Default.VideoFile,
            contentDescription = "Select Video File",
            tint = if (selectedVideoUri != null) YtBlue else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(36.dp)
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = if (selectedVideoUri != null) "Video Selected: ${selectedVideoUri?.lastPathSegment}" else "Tap to Select Video File from Device",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Video Title Field
      OutlinedTextField(
        value = title,
        onValueChange = { title = it },
        label = { Text("Video Title") },
        placeholder = { Text("Enter a descriptive title...") },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("upload_title_field"),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = YtRed,
          unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Description Field
      OutlinedTextField(
        value = description,
        onValueChange = { description = it },
        label = { Text("Description") },
        placeholder = { Text("Add video description, tags...") },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("upload_description_field"),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = YtRed,
          unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        minLines = 2,
        maxLines = 4
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Category Field
      OutlinedTextField(
        value = category,
        onValueChange = { category = it },
        label = { Text("Category (Tech, Music, Gaming, etc.)") },
        modifier = Modifier
          .fillMaxWidth()
          .testTag("upload_category_field"),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = YtRed,
          unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Is Short Switch
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant)
          .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Upload as YouTube Short",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
          )
          Text(
            text = "Vertical full-screen 60s video",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
          )
        }

        Switch(
          checked = isShort,
          onCheckedChange = { isShort = it },
          colors = SwitchDefaults.colors(checkedThumbColor = YtRed)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      if (isUploading) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          CircularProgressIndicator(
            color = YtRed,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(12.dp))
          Text(
            text = uploadStatusMessage ?: "Uploading to Cloudinary...",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 14.sp
          )
        }
      }

      // Submit Button
      Button(
        onClick = {
          if (title.isNotBlank() || selectedVideoUri != null) {
            onUpload(selectedVideoUri, title, description, category, isShort)
          }
        },
        enabled = !isUploading && (title.isNotBlank() || selectedVideoUri != null),
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("upload_submit_btn"),
        colors = ButtonDefaults.buttonColors(containerColor = YtRed)
      ) {
        Text(
          text = "Upload Video & Save to Firestore",
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp
        )
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }
}
