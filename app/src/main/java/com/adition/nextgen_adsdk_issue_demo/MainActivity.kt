package com.adition.nextgen_adsdk_issue_demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adition.nextgen_adsdk_issue_demo.ui.theme.NextgenadsdkissuedemoTheme
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ContextProvider.setContext(this)
        setContent {
            NextgenadsdkissuedemoTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyApp(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MyApp(modifier: Modifier) {
    val navController = rememberNavController()

    MaterialTheme {
        NavHost(navController = navController, startDestination = "main") {
            composable("main") {
                val viewModel: MainViewModel = viewModel()
                MainScreen(viewModel,navController)
            }
            composable("inline") {
                val viewModel: InlineViewModel = viewModel(navController.currentBackStackEntry!!)
                InlineView(viewModel)
            }
            composable("interstitial") {
                val viewModel: InterstitialViewModel = viewModel()
                InterstitialScreen(viewModel)
            }
        }
    }
}