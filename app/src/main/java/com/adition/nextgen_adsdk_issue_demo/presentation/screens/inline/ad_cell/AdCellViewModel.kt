package com.adition.nextgen_adsdk_issue_demo.presentation.screens.inline.ad_cell

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adition.nextgen_adsdk_issue_demo.core.AdConfiguration
import com.adition.sdk_core.api.core.AdService
import com.adition.sdk_core.api.core.Advertisement
import com.adition.sdk_core.api.entities.request.AdPlacementType
import com.adition.sdk_core.api.entities.request.AdRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdCellViewModel(
    val id: Int,
    private val request: AdRequest
) : ViewModel() {

    // MARK: - Internal Properties
    val state: StateFlow<PresentationState> get() = _state
    private val _state = MutableStateFlow<PresentationState>(PresentationState.Loading)

    // MARK: - Private Properties
    private var advertisement: Advertisement? = null
    private var adLoadingJob: Job? = null
    private val isLoaded: Boolean get() = advertisement != null

    // MARK: - Init
    init {
        if (AdConfiguration.Ad.isPreloadingContent) {
            viewModelScope.launch {
                loadAdvertisement()
            }
        }
    }

    // MARK: - Internal Methods
    fun onAppear() {
        if (!AdConfiguration.Ad.isPreloadingContent && !isLoaded) {
            adLoadingJob = viewModelScope.launch {
                loadAdvertisement()
            }
        }
    }

    fun onDisappear() {
        if (!AdConfiguration.Ad.isPreloadingContent) {
            adLoadingJob?.cancel()
            adLoadingJob = null
            _state.value = PresentationState.Loading
            advertisement = null
        }
    }

    // MARK: - Private Methods
    private suspend fun loadAdvertisement() {
        _state.value = PresentationState.Loading
        try {
            val advertisement = withContext(Dispatchers.IO) {
                AdService.makeAdvertisement(request, AdPlacementType.INLINE)
            }

            val aspectRatio = advertisement.getOrNull()?.adMetadata?.aspectRatio
            this.advertisement = advertisement.getOrNull()

            _state.value = advertisement.getOrNull()?.let {
                InlineAdData(
                    advertisement = it,
                    aspectRatio = (aspectRatio ?: C.defaultAspectRatio) as Float
                )
            }?.let {
                PresentationState.Loaded(
                    it
                )
            }!!
        } catch (e: Exception) {
            _state.value = PresentationState.Error(e)
        }
    }

    // MARK: - Static Properties
    private object C {
        const val defaultAspectRatio = 2.0
    }

    // MARK: - Extensions
    // Inline Ad Data Structure
    data class InlineAdData(
        val advertisement: Advertisement,
        val aspectRatio: Float
    )

    // Presentation States (Loading, Error, Loaded)
    sealed class PresentationState {
        object Loading : PresentationState()
        data class Error(val error: Throwable) : PresentationState()
        data class Loaded(val inlineAdData: InlineAdData) : PresentationState()
    }
}