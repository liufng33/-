package com.example.videoplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.videoplayer.ui.player.PlayerScreen
import com.example.videoplayer.ui.theme.VideoPlayerTheme
import com.example.videoplayer.ui.url.UrlInputScreen
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLDecoder
import java.net.URLEncoder

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoPlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    VideoPlayerNavigation()
                }
            }
        }
    }
}

@Composable
fun VideoPlayerNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "url_input"
    ) {
        composable("url_input") {
            UrlInputScreen(
                onNavigateToPlayer = { videoUrl, videoTitle ->
                    val encodedUrl = URLEncoder.encode(videoUrl, "UTF-8")
                    val encodedTitle = URLEncoder.encode(videoTitle, "UTF-8")
                    navController.navigate("player/$encodedUrl/$encodedTitle")
                }
            )
        }

        composable(
            route = "player/{videoUrl}/{videoTitle}",
            arguments = listOf(
                navArgument("videoUrl") { type = NavType.StringType },
                navArgument("videoTitle") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val encodedUrl = backStackEntry.arguments?.getString("videoUrl") ?: ""
            val encodedTitle = backStackEntry.arguments?.getString("videoTitle") ?: ""
            val videoUrl = URLDecoder.decode(encodedUrl, "UTF-8")
            val videoTitle = URLDecoder.decode(encodedTitle, "UTF-8")

            PlayerScreen(
                videoUrl = videoUrl,
                videoTitle = videoTitle,
                onBackPressed = { navController.popBackStack() }
            )
        }
    }
}
