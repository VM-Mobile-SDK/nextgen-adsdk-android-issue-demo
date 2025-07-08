package com.adition.nextgen_adsdk_issue_demo.presentation.screens.inline.ad_cell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.adition.sdk_presentation_compose.api.Ad
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AdCell(viewModel: AdCellViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(DEFAULT_HEIGHT.dp)) {

        when (state) {
            is AdCellViewModel.PresentationState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }

            is AdCellViewModel.PresentationState.Loaded -> {
                val data = (state as AdCellViewModel.PresentationState.Loaded).inlineAdData
                Ad(advertisement = data.advertisement,
                    Modifier.aspectRatio(data.aspectRatio))

            }

            is AdCellViewModel.PresentationState.Error -> {
                val error = (state as AdCellViewModel.PresentationState.Error).error
                Text(
                    text = error.message ?: "Unknown error",
                    color = Color.Red,
                    modifier = Modifier.fillMaxSize(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }


    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.onAppear()
                Lifecycle.Event.ON_PAUSE -> viewModel.onDisappear()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

private val DEFAULT_HEIGHT = 200