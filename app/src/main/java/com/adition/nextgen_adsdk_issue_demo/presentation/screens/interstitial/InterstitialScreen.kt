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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adition.nextgen_adsdk_issue_demo.core.AdConfiguration
import com.adition.sdk_core.api.core.Advertisement
import com.adition.sdk_core.api.entities.AdInterstitialState
import com.adition.sdk_presentation_compose.api.Interstitial
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun InterstitialScreen(viewModel: InterstitialViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    val interstitialState by viewModel.interstitialState.collectAsState()
    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(Unit) {
        viewModel.onLoad()
    }

    when (state) {
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
                if (viewModel.isLoaded) {
                    val ad = viewModel.advertisement
                    if (ad != null) {
                        val adInterstitialState = remember(ad) { AdInterstitialState(ad, coroutineScope) }
                        LaunchedEffect(adInterstitialState) {
                            adInterstitialState.presentIfLoaded()
                        }
                        Interstitial(adInterstitialState)
                    }
                } else {
                    Button(onClick = {
                        coroutineScope.launch {
                            viewModel.onLoadAdvertisement()
                        }
                    }) {
                        Text("Load Advertisement")
                    }
                }
            }

            // Optional: Do something based on interstitialState
            when (interstitialState) {
                InterstitialViewModel.InterstitialState.Presented -> {
                    // Show the ad or a dialog/overlay
                }
                InterstitialViewModel.InterstitialState.Hidden -> {
                    // No ad shown
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

@Composable
private fun rememberInterstitialState(
    advertisement: Advertisement,
    scope: CoroutineScope = rememberCoroutineScope()
) = remember { AdInterstitialState(advertisement, scope) }