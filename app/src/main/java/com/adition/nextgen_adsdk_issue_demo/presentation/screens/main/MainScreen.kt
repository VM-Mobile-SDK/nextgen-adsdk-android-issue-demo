package com.adition.nextgen_adsdk_issue_demo.presentation.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
fun MainScreen(
    viewModel: MainViewModel = viewModel(),
    navController: NavHostController,
) {
    val uiState by viewModel.state.collectAsState()
    val trackingResult by viewModel.trackingResult.collectAsState()

    var isTrackingAlertPresented by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.onLoad()
    }

    LaunchedEffect(trackingResult) {
        isTrackingAlertPresented = trackingResult != null
    }

    when (uiState) {
        is MainViewModel.PresentationState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is MainViewModel.PresentationState.Error -> {
            val error = (uiState as MainViewModel.PresentationState.Error).adError
            Text(text = error.description, color = Color.Red)
        }

        is MainViewModel.PresentationState.Loaded -> {
            LoadedContent(
                viewModel,
                navController
            )
        }
    }

    if (isTrackingAlertPresented && trackingResult != null) {
        AlertDialog(
            onDismissRequest = {
                viewModel.trackingResult.value = null
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.trackingResult.value = null
                }) {
                    Text("OK")
                }
            },
            title = { Text("Tracking processed") },
            text = { Text(trackingResult!!.message) }
        )
    }
}

@Composable
fun LoadedContent(
    viewModel: MainViewModel,
    navController: NavHostController,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        val uiState by viewModel.state.collectAsState()

        if (uiState.isInlineButtonShown) {
            Button(onClick = { navController.navigate("inline") }) {
                Text("Show Inline View")
            }
        }

        if (uiState.isInterstitialButtonShown) {
            Button(onClick = { navController.navigate("interstitial") }) {
                Text("Show Interstitial View")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        if (uiState.isTagButtonShown) {
            Button(onClick = { viewModel.tagRequest() }) {
                Text("Tag Request")
            }
        }

        if (uiState.isTrackingButtonShown) {
            Button(onClick = { viewModel.trackingRequest() }) {
                Text("Tracking Request")
            }
        }
    }
}