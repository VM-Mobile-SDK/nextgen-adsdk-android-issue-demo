package com.adition.nextgen_adsdk_issue_demo.presentation.screens.interstitial

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adition.nextgen_adsdk_issue_demo.core.AdConfiguration
import com.adition.nextgen_adsdk_issue_demo.presentation.screens.main.MainViewModel
import com.adition.nextgen_adsdk_issue_demo.presentation.screens.main.MainViewModel.PresentationState
import com.adition.sdk_core.api.core.AdService
import com.adition.sdk_core.api.core.Advertisement
import com.adition.sdk_core.api.entities.AdInterstitialState
import com.adition.sdk_core.api.entities.exception.AdError
import com.adition.sdk_core.api.entities.request.AdPlacementType
import com.adition.sdk_core.api.entities.response.AdMetadata
import com.adition.sdk_core.api.services.event_listener.AdEventListener
import com.adition.sdk_core.api.services.event_listener.AdEventType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InterstitialViewModel: ViewModel() {

    private val _state = MutableStateFlow<PresentationState>(PresentationState.Loading)
    val state: StateFlow<PresentationState> get() = _state

    private var adInterstitialState: AdInterstitialState? = null

    var isLoaded: Boolean = false
        private set

    fun onLoad() {
        viewModelScope.launch {
            if (isLoaded || !AdConfiguration.Ad.isPreloadingContent) {
                _state.value = PresentationState.Loaded
                return@launch
            }

            onLoadAdvertisement()
        }
    }

    suspend fun onLoadAdvertisement() {
        val request = AdConfiguration.Ad.interstitialRequest ?: return

        _state.value = PresentationState.Loading

            val adEventListener: AdEventListener = object : AdEventListener {
                override fun eventProcessed(adEventType: AdEventType, adMetadata: AdMetadata) {
                    Log.d("AdFullscreen events", "Collected EVENT - $adEventType")
                    if (adEventType == AdEventType.UnloadRequest)
                        adInterstitialState?.hide()
                }
            }

            val advertisementResult = AdService.makeAdvertisement(
                adRequest = request,
                placementType = AdPlacementType.INTERSTITIAL,
                adEventListener = adEventListener
            )

            advertisementResult.get(
                onSuccess = { ad ->
                    isLoaded = true
                    _state.value = PresentationState.Loaded(advertisement = ad)
                },
                onError = { error ->
                    _state.value = PresentationState.Error(error)
                }
            )
    }

    fun presentAd() {
        viewModelScope.launch {
            val advertisement = (_state.value as? PresentationState.Loaded)?.advertisement
            if (advertisement != null) {
                adInterstitialState = AdInterstitialState(advertisement, viewModelScope)
                adInterstitialState?.presentIfLoaded()
                _state.value = PresentationState.Presented
            }
        }
    }

    sealed class PresentationState {
        data object Loading : PresentationState()
        data class Loaded(val advertisement: Advertisement?) : PresentationState()
        data class Error(val error: AdError) : PresentationState()
        data object Hidden : PresentationState()
        data object Presented : PresentationState()
    }
}