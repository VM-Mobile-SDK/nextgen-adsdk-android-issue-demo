package com.adition.nextgen_adsdk_issue_demo.presentation.screens.inline

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.adition.nextgen_adsdk_issue_demo.presentation.screens.inline.ad_cell.AdCell

@Composable
fun InlineView(viewModel: InlineViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.onLoad()
    }

    when (state) {
        is InlineViewModel.PresentationState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }

        is InlineViewModel.PresentationState.Loaded -> {
            val dataSource = (state as InlineViewModel.PresentationState.Loaded).items
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(dataSource) { item ->
                    AdCell(viewModel = item)
                }
            }
        }

        is InlineViewModel.PresentationState.Error -> {
            val error = (state as InlineViewModel.PresentationState.Error).error
            Text(
                text = error.description,
                color = Color.Red,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        }
    }
}
