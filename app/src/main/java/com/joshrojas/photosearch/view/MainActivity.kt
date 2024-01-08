package com.joshrojas.photosearch.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import com.joshrojas.photosearch.data.remote.RemoteBuilder
import com.joshrojas.photosearch.data.repository.FlickrRepository
import com.joshrojas.photosearch.view.detail.DetailScreen
import com.joshrojas.photosearch.view.main.HomeScreen
import com.joshrojas.photosearch.view.main.HomeViewModel
import com.joshrojas.photosearch.view.main.HomeViewModelFactory
import com.joshrojas.photosearch.view.ui.theme.PhotoSearchTheme
import com.joshrojas.photosearch.view.util.VoiceToTextParser

class MainActivity : ComponentActivity() {

    private val voiceToTextParser by lazy { VoiceToTextParser(applicationContext) }

    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = FlickrRepository(RemoteBuilder.createFilckrAPI())
        val viewModel by viewModels<HomeViewModel> { HomeViewModelFactory(repository) }

        setContent {
            PhotoSearchTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "/start") {
                        composable("/start") {
                            HomeScreen(
                                viewModel,
                                voiceToTextParser,
                                { navController.navigate("/detail") })
                        }

                        composable("/detail") {
                            DetailScreen(viewModel)
                        }
                    }

                }
            }
        }
    }
}
