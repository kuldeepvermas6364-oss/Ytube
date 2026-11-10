package com.example.data.remote

import com.example.data.model.YtComment
import com.example.data.model.YtVideo

object MockYouTubeData {

  val sampleVideos = listOf(
    YtVideo(
      id = "mock_v1",
      videoId = "dQw4w9WgXcQ",
      userId = "c_rick",
      title = "Building a Full Stack Android YouTube Clone in Jetpack Compose & Kotlin",
      description = "Learn how to build a complete YouTube Clone on Android with YouTube Data API v3, Firebase Firestore database, and Cloudinary video upload integration!",
      thumbnailUrl = "https://images.unsplash.com/photo-1611162617213-7d7a39e9b1d7?w=800&auto=format&fit=crop&q=80",
      videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
      isCloudinary = false,
      isShort = false,
      category = "Tech",
      viewsCount = 1254300L,
      likesCount = 89400L,
      commentsCount = 3420L,
      channelName = "Android Dev Pro",
      channelAvatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200&auto=format&fit=crop&q=80",
      duration = "14:20"
    ),
    YtVideo(
      id = "mock_v2",
      videoId = "9bZkp7q19f0",
      userId = "c_music",
      title = "Lofi Chill Beats - 24/7 Relaxing Music Stream for Coding & Study",
      description = "Relaxing lofi hip hop beats to study, code, and chill out to. Perfect ambient background music.",
      thumbnailUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=800&auto=format&fit=crop&q=80",
      videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
      isCloudinary = false,
      isShort = false,
      category = "Music",
      viewsCount = 5420900L,
      likesCount = 312000L,
      commentsCount = 12900L,
      channelName = "Lofi Girl Studio",
      channelAvatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200&auto=format&fit=crop&q=80",
      duration = "2:45:10"
    ),
    YtVideo(
      id = "mock_v3",
      videoId = "kJQP7kiw5Fk",
      userId = "c_gaming",
      title = "Unreal Engine 5 Next-Gen Graphics Benchmark Showcase 4K 60FPS",
      description = "Deep dive into Unreal Engine 5 Nanite and Lumen raytracing performance across high-end GPUs.",
      thumbnailUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=800&auto=format&fit=crop&q=80",
      videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
      isCloudinary = false,
      isShort = false,
      category = "Gaming",
      viewsCount = 892010L,
      likesCount = 45200L,
      commentsCount = 1890L,
      channelName = "Game Tech Hub",
      channelAvatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200&auto=format&fit=crop&q=80",
      duration = "08:45"
    ),
    YtVideo(
      id = "mock_v4",
      videoId = "fJ9rUzIMcZQ",
      userId = "c_cloud",
      title = "Cloudinary Video API Integration Tutorial for Mobile Applications",
      description = "Upload videos seamlessly from your mobile app to Cloudinary CDN storage with automated transformations.",
      thumbnailUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=800&auto=format&fit=crop&q=80",
      videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
      isCloudinary = true,
      isShort = false,
      category = "Tech",
      viewsCount = 342000L,
      likesCount = 21000L,
      commentsCount = 920L,
      channelName = "Cloud Systems Tech",
      channelAvatarUrl = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=200&auto=format&fit=crop&q=80",
      duration = "10:15"
    ),
    YtVideo(
      id = "mock_v5",
      videoId = "L_LUpnjgPso",
      userId = "c_travel",
      title = "Exploring Tokyo 4K - Autumn Night Lights in Shibuya & Shinjuku",
      description = "Walking through Tokyo Japan under the neon lights of Shibuya Crossing, Akihabara, and Shinjuku.",
      thumbnailUrl = "https://images.unsplash.com/photo-1503899036084-c55cdd92da26?w=800&auto=format&fit=crop&q=80",
      videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
      isCloudinary = false,
      isShort = false,
      category = "Travel",
      viewsCount = 1928000L,
      likesCount = 120500L,
      commentsCount = 4310L,
      channelName = "Nomad Japan",
      channelAvatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=200&auto=format&fit=crop&q=80",
      duration = "22:30"
    )
  )

  val sampleShorts = listOf(
    YtVideo(
      id = "mock_s1",
      videoId = "short_1",
      userId = "c_code",
      title = "Top 3 Jetpack Compose Modifiers every Android Developer should know! 🚀 #shorts #android",
      description = "Master Modifier.offset, Modifier.graphicsLayer and Modifier.composed in 60 seconds!",
      thumbnailUrl = "https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=600&auto=format&fit=crop&q=80",
      videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
      isCloudinary = false,
      isShort = true,
      category = "Tech",
      viewsCount = 450000L,
      likesCount = 38000L,
      commentsCount = 1200L,
      channelName = "Kotlin Quick Tips",
      channelAvatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&auto=format&fit=crop&q=80",
      duration = "00:45"
    ),
    YtVideo(
      id = "mock_s2",
      videoId = "short_2",
      userId = "c_life",
      title = "When your Cloudinary API upload succeeds on first try! 😂 #programming #humor #shorts",
      description = "Every backend developer knows that feeling when storage uploads work instantly!",
      thumbnailUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=600&auto=format&fit=crop&q=80",
      videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
      isCloudinary = false,
      isShort = true,
      category = "Comedy",
      viewsCount = 1200000L,
      likesCount = 95000L,
      commentsCount = 2800L,
      channelName = "Dev Life Shorts",
      channelAvatarUrl = "https://images.unsplash.com/photo-1527980965255-d3b416303d12?w=200&auto=format&fit=crop&q=80",
      duration = "00:30"
    ),
    YtVideo(
      id = "mock_s3",
      videoId = "short_3",
      userId = "c_music",
      title = "Epic Acoustic Guitar Solo 🎸🔥 #shorts #guitar #music",
      description = "Improvised fingerstyle acoustic melody live studio session.",
      thumbnailUrl = "https://images.unsplash.com/photo-1510915361894-db8b60106cb1?w=600&auto=format&fit=crop&q=80",
      videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
      isCloudinary = false,
      isShort = true,
      category = "Music",
      viewsCount = 890000L,
      likesCount = 74000L,
      commentsCount = 1950L,
      channelName = "Acoustic Vibe",
      channelAvatarUrl = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=200&auto=format&fit=crop&q=80",
      duration = "00:55"
    )
  )

  val sampleComments = listOf(
    YtComment(
      id = "c1",
      commentId = "c1",
      videoId = "mock_v1",
      userId = "u_alex",
      userName = "Alex Rivers",
      userAvatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=200&auto=format&fit=crop&q=80",
      text = "Awesome YouTube clone! The integration with Cloudinary and Firestore is super clean 🔥",
      likesCount = 42L
    ),
    YtComment(
      id = "c2",
      commentId = "c2",
      videoId = "mock_v1",
      userId = "u_sarah",
      userName = "Sarah Connor",
      userAvatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=200&auto=format&fit=crop&q=80",
      text = "Loved the Jetpack Compose UI layout. Looks just like the official YouTube dark theme!",
      likesCount = 19L
    ),
    YtComment(
      id = "c3",
      commentId = "c3",
      videoId = "mock_v1",
      userId = "u_dev",
      userName = "Code Master",
      userAvatarUrl = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=200&auto=format&fit=crop&q=80",
      text = "Can we upload custom videos directly to Cloudinary from the + button? Works perfectly!",
      likesCount = 11L
    )
  )
}
