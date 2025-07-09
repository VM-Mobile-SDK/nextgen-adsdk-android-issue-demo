package com.adition.nextgen_adsdk_issue_demo.presentation.screens.interstitial

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adition.nextgen_adsdk_issue_demo.core.AdConfiguration
import com.adition.sdk_core.api.core.AdService
import com.adition.sdk_core.api.core.Advertisement
import com.adition.sdk_core.api.entities.AdInterstitialState
import com.adition.sdk_core.api.entities.exception.AdError
import com.adition.sdk_core.api.entities.request.AdPlacementType
import com.adition.sdk_core.api.entities.response.AdMetadata
import com.adition.sdk_core.api.services.event_listener.AdEventListener
import com.adition.sdk_core.api.services.event_listener.AdEventType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InterstitialViewModel : ViewModel() {
    private val _state = MutableStateFlow<PresentationState>(PresentationState.Loading)
    val state = _state.asStateFlow()
    internal var adInterstitialState: AdInterstitialState? = null

    fun onLoad() {
        if (!AdConfiguration.Ad.IS_PRELOADING_CONTENT)
            return
        val loadedAd = (_state.value as? PresentationState.Loaded)?.advertisement
        if (loadedAd != null) {
            _state.value = PresentationState.Loaded(loadedAd)
            return
        }
        onLoadAdvertisement()
    }

    private fun onLoadAdvertisement() {
        val request = AdConfiguration.Ad.interstitialRequest ?: return

        _state.value = PresentationState.Loading

        val adEventListener: AdEventListener = object : AdEventListener {
            override fun eventProcessed(adEventType: AdEventType, adMetadata: AdMetadata) {
                if (adEventType == AdEventType.UnloadRequest) {
                    adInterstitialState?.hide()
                    viewModelScope.launch {
                        val ad = (_state.value as? PresentationState.Loaded)?.advertisement
                        _state.value = PresentationState.Loaded(ad)
                    }
                }
            }
        }

        viewModelScope.launch {
            val advertisementResult = AdService.makeAdvertisement(
                adRequest = request,
                placementType = AdPlacementType.INTERSTITIAL,
                adEventListener = adEventListener
            )

            advertisementResult.get(
                onSuccess = { ad ->
                    adInterstitialState = AdInterstitialState(ad, viewModelScope)
                    _state.value = PresentationState.Loaded(advertisement = ad)
                },
                onError = { error ->
                    _state.value = PresentationState.Error(error)
                }
            )
        }
    }

    fun presentAd() {
        viewModelScope.launch {
            val advertisement = (_state.value as? PresentationState.Loaded)?.advertisement
            if (advertisement != null) {
                if (adInterstitialState == null) {
                    adInterstitialState = AdInterstitialState(advertisement, viewModelScope)
                }
                adInterstitialState?.presentIfLoaded()
                _state.value = PresentationState.Loaded(advertisement)
            }
        }
    }

    sealed class PresentationState {
        data object Loading : PresentationState()
        data class Loaded(val advertisement: Advertisement?) : PresentationState()
        data class Error(val error: AdError) : PresentationState()
    }
}
