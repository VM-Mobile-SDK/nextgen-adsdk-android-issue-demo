package com.adition.nextgen_adsdk_issue_demo.presentation.screens.interstitial

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (presentationState.advertisement != null) {
                    Button(onClick = { viewModel.presentAd() }) {
                        Text("Show Advertisement")
                    }
                } else {
                    val adInterstitialState = viewModel.adInterstitialState
                    if (adInterstitialState != null) {
                        LaunchedEffect(adInterstitialState) {
                            adInterstitialState.presentIfLoaded()
                        }
                        Interstitial(adInterstitialState)
                    }
                }
            }
        }

        is InterstitialViewModel.PresentationState.Error -> {
            val error = (state as InterstitialViewModel.PresentationState.Error).error
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
