package com.adition.nextgen_adsdk_issue_demo.presentation.screens.interstitial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adition.sdk_presentation_compose.api.Interstitial

@Composable
fun InterstitialScreen(viewModel: InterstitialViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onLoad()
    }

    when (val presentationState = state) {
        is InterstitialViewModel.PresentationState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is InterstitialViewModel.PresentationState.Loaded -> {
            val interstitialState = presentationState.adInterstitialState
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                if (interstitialState != null) {
                    Button(onClick = { viewModel.presentAd() }) {
                        Text("Show Advertisement")
                    }

                    Interstitial(interstitialState)
                } else {
                    Button(onClick = { viewModel.onLoadAdvertisement() }) {
                        Text("Load Advertisement")
                    }
                }
            }
        }

        is InterstitialViewModel.PresentationState.Error -> {
            val error = presentationState.error
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(text = error.description, color = Color.Red)
            }
        }
    }
}
