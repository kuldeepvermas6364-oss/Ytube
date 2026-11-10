package com.example.ui.screens

import android.app.Activity
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.YtRed
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun AuthScreen(
  onAuthSuccess: () -> Unit,
  onContinueAsGuest: () -> Unit
) {
  val context = LocalContext.current
  val coroutineScope = rememberCoroutineScope()
  var isLoading by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(24.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.fillMaxWidth()
    ) {
      // YouTube Logo Icon
      Box(
        modifier = Modifier
          .size(72.dp, 50.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(YtRed),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.PlayArrow,
          contentDescription = "YouTube Logo",
          tint = Color.White,
          modifier = Modifier.size(44.dp)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "Welcome to YouTube",
        color = MaterialTheme.colorScheme.onBackground,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold
      )

      Text(
        text = "Stream videos, shorts, upload to Cloudinary & sync via Firebase Firestore",
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 13.sp,
        modifier = Modifier.padding(top = 6.dp)
      )

      Spacer(modifier = Modifier.height(36.dp))

      if (errorMessage != null) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFD32F2F).copy(alpha = 0.2f))
            .padding(12.dp)
        ) {
          Text(text = errorMessage!!, color = Color(0xFFFF5252), fontSize = 12.sp)
        }
      }

      // Google Sign-In Button
      Button(
        onClick = {
          isLoading = true
          errorMessage = null
          performGoogleSignIn(
            activity = context as Activity,
            onSuccess = {
              isLoading = false
              onAuthSuccess()
            },
            onError = { err ->
              isLoading = false
              errorMessage = err
            },
            onCancelled = {
              isLoading = false
            },
            scope = coroutineScope
          )
        },
        enabled = !isLoading,
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("google_sign_in_btn"),
        colors = ButtonDefaults.buttonColors(containerColor = YtRed)
      ) {
        if (isLoading) {
          CircularProgressIndicator(
            color = Color.White,
            modifier = Modifier.size(24.dp)
          )
        } else {
          Text(
            text = "Sign in with Google",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Continue as Guest Option
      OutlinedButton(
        onClick = onContinueAsGuest,
        enabled = !isLoading,
        modifier = Modifier
          .fillMaxWidth()
          .height(48.dp)
          .testTag("continue_as_guest_btn")
      ) {
        Text(
          text = "Continue as Guest",
          color = MaterialTheme.colorScheme.onBackground,
          fontSize = 14.sp
        )
      }
    }
  }
}

private fun performGoogleSignIn(
  activity: Activity,
  onSuccess: () -> Unit,
  onError: (String) -> Unit,
  onCancelled: () -> Unit,
  scope: kotlinx.coroutines.CoroutineScope
) {
  val clientId = try {
    activity.getString(R.string.default_web_client_id)
  } catch (e: Exception) {
    onError("Google Sign-In configuration missing in google-services.json")
    return
  }

  val credentialManager = CredentialManager.create(activity)
  val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
  val request = GetCredentialRequest.Builder().addCredentialOption(signInOption).build()

  scope.launch {
    try {
      val result = credentialManager.getCredential(activity, request)
      val credential = result.credential
      if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
        val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
        val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
        Firebase.auth.signInWithCredential(authCredential).await()
        onSuccess()
      } else {
        onError("Unexpected credential format received")
      }
    } catch (e: GetCredentialCancellationException) {
      Log.w("Auth", "Google Sign-In flow cancelled: ${e.message}", e)
      onCancelled()
    } catch (e: Exception) {
      Log.e("Auth", "Google Sign-In failed", e)
      onError(e.localizedMessage ?: "Sign in failed")
    }
  }
}
