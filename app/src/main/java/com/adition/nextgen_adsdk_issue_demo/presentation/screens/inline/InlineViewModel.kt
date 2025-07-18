package com.adition.nextgen_adsdk_issue_demo.presentation.screens.inline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adition.nextgen_adsdk_issue_demo.core.AdConfiguration
import com.adition.nextgen_adsdk_issue_demo.presentation.screens.inline.ad_cell.AdCellViewModel
import com.adition.sdk_core.api.entities.exception.AdError
import com.adition.sdk_core.api.entities.request.AdRequest
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InlineViewModel : ViewModel() {
    private val _state = MutableStateFlow<PresentationState>(PresentationState.Loading)
    val state = _state.asStateFlow()

    fun onLoad() {
        viewModelScope.launch {
            val requests = AdConfiguration.Ad.inlineRequests ?: return@launch
            val dataSource = getDataSource(requests)
            _state.value = PresentationState.Loaded(dataSource)
        }
    }

    private suspend fun getDataSource(
        requests: List<AdRequest>,
    ): List<AdCellViewModel> = coroutineScope {
        requests
            .map { request ->
                async {
                    val cell = AdCellViewModel(request)
                    cell.preloadIfNeeded()
                    return@async cell
                }
            }
            .awaitAll()
    }

    sealed class PresentationState {
        data object Loading : PresentationState()
        data class Error(val error: AdError) : PresentationState()
        data class Loaded(val items: List<AdCellViewModel>) : PresentationState()
    }
}
