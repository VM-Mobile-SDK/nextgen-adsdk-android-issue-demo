package com.adition.nextgen_adsdk_issue_demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.adition.nextgen_adsdk_issue_demo.core.ContextProvider
import com.adition.nextgen_adsdk_issue_demo.presentation.screens.inline.InlineView
import com.adition.nextgen_adsdk_issue_demo.presentation.screens.inline.InlineViewModel
import com.adition.nextgen_adsdk_issue_demo.presentation.screens.interstitial.InterstitialScreen
import com.adition.nextgen_adsdk_issue_demo.presentation.screens.interstitial.InterstitialViewModel
import com.adition.nextgen_adsdk_issue_demo.presentation.screens.main.MainScreen
import com.adition.nextgen_adsdk_issue_demo.presentation.screens.main.MainViewModel
import com.adition.nextgen_adsdk_issue_demo.ui.theme.NextgenadsdkissuedemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ContextProvider.setContext(applicationContext)

        setContent {
            NextgenadsdkissuedemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Navigation(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun Navigation(modifier: Modifier) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Main.route, modifier = modifier) {
        composable(Screen.Main.route) {
            val viewModel: MainViewModel = viewModel()
            MainScreen(viewModel, navController)
        }
        composable(Screen.Inline.route) {
            val viewModel: InlineViewModel = viewModel()
            InlineView(viewModel)
        }
        composable(Screen.Interstitial.route) {
            val viewModel: InterstitialViewModel = viewModel()
            InterstitialScreen(viewModel)
        }
    }
}

sealed class Screen(val route: String) {
    data object Main: Screen("main_screen")
    data object Inline: Screen("inline_screen")
    data object Interstitial: Screen("interstitial_screen")
}
