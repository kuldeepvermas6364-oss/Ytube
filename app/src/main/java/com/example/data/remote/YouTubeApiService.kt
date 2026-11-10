package com.example.data.remote

import com.example.data.model.CloudinaryUploadResponse
import com.example.data.model.YouTubeChannelListResponse
import com.example.data.model.YouTubeSearchResponse
import com.example.data.model.YouTubeVideoListResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface YouTubeApiService {

  @GET("youtube/v3/videos")
  suspend fun getTrendingVideos(
    @Query("part") part: String = "snippet,contentDetails,statistics",
    @Query("chart") chart: String = "mostPopular",
    @Query("maxResults") maxResults: Int = 30,
    @Query("regionCode") regionCode: String? = null,
    @Query("videoCategoryId") videoCategoryId: String? = null,
    @Query("key") apiKey: String
  ): Response<YouTubeVideoListResponse>

  @GET("youtube/v3/search")
  suspend fun searchVideos(
    @Query("part") part: String = "snippet",
    @Query("q") query: String,
    @Query("maxResults") maxResults: Int = 30,
    @Query("type") type: String = "video",
    @Query("key") apiKey: String
  ): Response<YouTubeSearchResponse>

  @GET("youtube/v3/videos")
  suspend fun getVideoDetails(
    @Query("part") part: String = "snippet,contentDetails,statistics",
    @Query("id") videoIds: String,
    @Query("key") apiKey: String
  ): Response<YouTubeVideoListResponse>

  @GET("youtube/v3/channels")
  suspend fun getChannelDetails(
    @Query("part") part: String = "snippet,statistics",
    @Query("id") channelIds: String,
    @Query("key") apiKey: String
  ): Response<YouTubeChannelListResponse>
}

interface CloudinaryApiService {
  @Multipart
  @POST("v1_1/{cloud_name}/video/upload")
  suspend fun uploadVideo(
    @Path("cloud_name") cloudName: String,
    @Part("upload_preset") uploadPreset: RequestBody,
    @Part file: MultipartBody.Part
  ): Response<CloudinaryUploadResponse>
}

object NetworkClient {
  private val moshi: Moshi by lazy {
    Moshi.Builder()
      .add(KotlinJsonAdapterFactory())
      .build()
  }

  private val okHttpClient: OkHttpClient by lazy {
    OkHttpClient.Builder()
      .connectTimeout(30, TimeUnit.SECONDS)
      .readTimeout(60, TimeUnit.SECONDS)
      .addInterceptor { chain ->
        val request = chain.request().newBuilder()
          .header("User-Agent", "YouTubeMobileApp/1.0 (Android)")
          .header("X-Android-Package", "com.aistudio.ytclone.vdxkqp")
          .header("X-Android-Cert", "0EE243D6A2C513AC29AD27A6D966BA79EECBA336")
          .build()
        chain.proceed(request)
      }
      .addInterceptor(HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
      })
      .build()
  }

  val youtubeApi: YouTubeApiService by lazy {
    Retrofit.Builder()
      .baseUrl("https://www.googleapis.com/")
      .client(okHttpClient)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()
      .create(YouTubeApiService::class.java)
  }

  val cloudinaryApi: CloudinaryApiService by lazy {
    Retrofit.Builder()
      .baseUrl("https://api.cloudinary.com/")
      .client(okHttpClient)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .build()
      .create(CloudinaryApiService::class.java)
  }
}
