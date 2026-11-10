package com.example.data.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

class GeminiAiRepository {

  private val model by lazy {
    Firebase.ai.generativeModel("gemini-3.8-flash")
  }

  suspend fun summarizeVideo(title: String, description: String, category: String): String {
    return try {
      withTimeout(60_000L) {
        val prompt = """
          You are an expert YouTube AI Assistant.
          Summarize this video in 3 bullet points, highlight 2 key takeaways, and suggest 3 relevant hashtags.
          
          Video Title: $title
          Category: $category
          Description: $description
        """.trimIndent()

        val response = model.generateContent(prompt)
        response.text ?: "Could not generate summary."
      }
    } catch (e: TimeoutCancellationException) {
      "AI Summarization timed out after 60 seconds."
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Log.e("GeminiAiRepo", "Error generating video summary", e)
      "AI Summary unavailable: ${e.localizedMessage ?: "Unknown error"}"
    }
  }

  suspend fun generateTitleAndDescription(topic: String): Pair<String, String> {
    return try {
      withTimeout(60_000L) {
        val prompt = """
          You are a professional YouTube Creator AI.
          Given the topic: "$topic", generate:
          1. An engaging, viral YouTube Title (under 80 characters).
          2. A comprehensive YouTube Video Description with timestamps and hashtags.
          
          Format output clearly:
          TITLE: [Your Title Here]
          DESCRIPTION: [Your Description Here]
        """.trimIndent()

        val response = model.generateContent(prompt)
        val text = response.text ?: ""

        val titleMatch = "TITLE:\\s*(.+)".toRegex().find(text)?.groupValues?.get(1) ?: topic
        val descMatch = "DESCRIPTION:\\s*([\\s\\S]+)".toRegex().find(text)?.groupValues?.get(1) ?: "Uploaded via Cloudinary & YouTube AI"

        Pair(titleMatch.trim(), descMatch.trim())
      }
    } catch (e: TimeoutCancellationException) {
      Pair("Viral $topic Video", "Uploaded video about $topic")
    } catch (e: CancellationException) {
      throw e
    } catch (e: Exception) {
      Log.e("GeminiAiRepo", "Error generating AI title", e)
      Pair("Viral $topic Video", "Uploaded video about $topic")
    }
  }
}
