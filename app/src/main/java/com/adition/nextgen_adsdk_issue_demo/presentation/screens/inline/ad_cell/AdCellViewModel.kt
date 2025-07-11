package com.adition.nextgen_adsdk_issue_demo.presentation.screens.inline.ad_cell

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adition.nextgen_adsdk_issue_demo.core.AdConfiguration
import com.adition.sdk_core.api.core.AdService
import com.adition.sdk_core.api.core.Advertisement
import com.adition.sdk_core.api.entities.exception.AdError
import com.adition.sdk_core.api.entities.request.AdPlacementType
import com.adition.sdk_core.api.entities.request.AdRequest
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdCellViewModel(private val request: AdRequest) : ViewModel() {

    private val _state = MutableStateFlow<PresentationState>(PresentationState.Loading)
    val state = _state.asStateFlow()

    private var advertisement: Advertisement? = null
    private val isLoaded: Boolean get() = advertisement != null

    private var loadingJob: Job? = null

    suspend fun preloadIfNeeded() {
        if (!AdConfiguration.Ad.IS_PRELOADING_CONTENT) return

        loadAdvertisement()
    }

    fun onAppear() {
        if (!AdConfiguration.Ad.IS_PRELOADING_CONTENT && !isLoaded) {
            loadingJob = viewModelScope.launch {
                loadAdvertisement()
            }
        }
    }

    fun onDisappear() {
        if (!AdConfiguration.Ad.IS_PRELOADING_CONTENT) {
            loadingJob?.cancel()
            loadingJob = null
            _state.value = PresentationState.Loading
            advertisement = null
        }
    }

    private suspend fun loadAdvertisement() {
        if (loadingJob?.isActive == true) return

        _state.value = PresentationState.Loading

        val advertisement = AdService.makeAdvertisement(request, AdPlacementType.INLINE)

        advertisement.get(
            onSuccess = { ad ->
                this@AdCellViewModel.advertisement = ad
                val inlineAdData = InlineAdData(
                    advertisement = ad,
                    aspectRatio = ad.adMetadata?.aspectRatio ?: DEFAULT_ASPECT_RATIO
                )
                _state.value = PresentationState.Loaded(inlineAdData)
            },
            onError = { error ->
                _state.value = PresentationState.Error(error)
            }
        )
    }

    companion object {
        const val DEFAULT_ASPECT_RATIO = 2.0f
    }

    data class InlineAdData(
        val advertisement: Advertisement,
        val aspectRatio: Float,
    )

    sealed class PresentationState {
        data object Loading : PresentationState()
        data class Error(val error: AdError) : PresentationState()
        data class Loaded(val inlineAdData: InlineAdData) : PresentationState()
    }
}
