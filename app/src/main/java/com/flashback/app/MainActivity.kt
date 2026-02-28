package com.flashback.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.flashback.app.ui.history.HistoryScreen
import com.flashback.app.ui.history.HistoryViewModel
import com.flashback.app.ui.main.MainScreen
import com.flashback.app.ui.main.MainViewModel
import com.flashback.app.ui.navigation.FlashbackNavigation
import com.flashback.app.ui.settings.SettingsScreen
import com.flashback.app.ui.settings.SettingsViewModel
import com.flashback.app.ui.theme.FlashbackTheme

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlashbackTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = FlashbackNavigation.Main
                    ) {
                        composable<FlashbackNavigation.Main> {
                            MainScreen(
                                viewModel = mainViewModel,
                                onNavigateToSettings = {
                                    navController.navigate(FlashbackNavigation.Settings)
                                },
                                onNavigateToHistory = {
                                    navController.navigate(FlashbackNavigation.History)
                                }
                            )
                        }
                        composable<FlashbackNavigation.Settings> {
                            SettingsScreen(
                                viewModel = settingsViewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable<FlashbackNavigation.History> {
                            HistoryScreen(
                                viewModel = HistoryViewModel(mainViewModel.historyRepository),
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
