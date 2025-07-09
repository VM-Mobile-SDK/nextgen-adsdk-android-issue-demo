package com.adition.nextgen_adsdk_issue_demo.presentation.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adition.nextgen_adsdk_issue_demo.core.AdConfiguration
import com.adition.nextgen_adsdk_issue_demo.core.ContextProvider
import com.adition.sdk_core.api.core.AdService
import com.adition.sdk_core.api.entities.exception.AdError
import com.adition.sdk_core.api.entities.request.AdRequestGlobalParameters
import com.adition.sdk_core.api.entities.request.TrackingGlobalParameters
import com.adition.sdk_presentation_compose.api.configure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _state = MutableStateFlow<PresentationState>(PresentationState.Loading)
    val state = _state.asStateFlow()
    val trackingResult = MutableStateFlow<TrackingResult?>(null)

    fun onLoad() {
        viewModelScope.launch {
            val adServiceResult = AdService.configure(
                networkId = AdConfiguration.NETWORK_ID,
                cacheSizeInMb = AdConfiguration.CACHE_SIZE.toUShort(),
                context = ContextProvider.appContext,
            )

            adServiceResult.get(
                onSuccess = { _ ->
                    setAdRequestGlobalParameters()
                    setTrackingGlobalParameters()
                    _state.value = PresentationState.Loaded
                },
                onError = { error ->
                    _state.value = PresentationState.Error(error)
                }
            )
        }
    }

    fun tagRequest() {
        val request = AdConfiguration.Tracking.tagRequest ?: return

        viewModelScope.launch {
            AdService.tagUser(request)
                .get(
                    onSuccess = {
                        trackingResult.value = TrackingResult.Success(TrackingType.TAG_REQUEST)
                    },
                    onError = { error ->
                        trackingResult.value =
                            TrackingResult.Failure(TrackingType.TAG_REQUEST, error)
                    }
                )
        }
    }

    fun trackingRequest() {
        val request = AdConfiguration.Tracking.trackingRequest ?: return

        viewModelScope.launch {
            AdService.trackingRequest(request)
                .get(
                    onSuccess = {
                        trackingResult.value = TrackingResult.Success(TrackingType.TRACKING_REQUEST)
                    },
                    onError = { error ->
                        trackingResult.value =
                            TrackingResult.Failure(TrackingType.TRACKING_REQUEST, error)
                    }
                )
        }
    }

    private fun setAdRequestGlobalParameters() {
        val parameters = AdConfiguration.Ad.globalParameters

        AdService.setAdRequestGlobalParameter(
            AdRequestGlobalParameters::externalUID,
            parameters.externalUID
        )
        AdService.setAdRequestGlobalParameter(AdRequestGlobalParameters::gdpr, parameters.gdpr)
        AdService.setAdRequestGlobalParameter(
            AdRequestGlobalParameters::isOptOutEnabled,
            parameters.isOptOutEnabled
        )
        AdService.setAdRequestGlobalParameter(
            AdRequestGlobalParameters::userIds,
            parameters.userIds
        )
        AdService.setAdRequestGlobalParameter(
            AdRequestGlobalParameters::isSHBEnabled,
            parameters.isSHBEnabled
        )
        AdService.setAdRequestGlobalParameter(AdRequestGlobalParameters::dsa, parameters.dsa)
        AdService.setAdRequestGlobalParameter(
            AdRequestGlobalParameters::isIpIdentified,
            parameters.isIpIdentified
        )
        AdService.setAdRequestGlobalParameter(
            AdRequestGlobalParameters::cookiesAccess,
            parameters.cookiesAccess
        )
    }

    private fun setTrackingGlobalParameters() {
        val parameters = AdConfiguration.Tracking.globalParameters

        AdService.setTrackingGlobalParameter(
            TrackingGlobalParameters::externalUID,
            parameters.externalUID
        )
        AdService.setTrackingGlobalParameter(TrackingGlobalParameters::gdpr, parameters.gdpr)
        AdService.setTrackingGlobalParameter(
            TrackingGlobalParameters::isOptOutEnabled,
            parameters.isOptOutEnabled
        )
        AdService.setTrackingGlobalParameter(TrackingGlobalParameters::userIds, parameters.userIds)
    }

    sealed class TrackingResult {
        data class Success(val type: TrackingType) : TrackingResult()
        data class Failure(val type: TrackingType, val error: AdError) : TrackingResult()

        val message: String
            get() = when (this) {
                is Success -> "${type.name} processed successfully."
                is Failure -> "${type.name} failed with error: $error"
            }
    }

    enum class TrackingType(val type: String) {
        TAG_REQUEST("Tag Request"),
        TRACKING_REQUEST("Tracking Request")
    }

    sealed class PresentationState {
        data object Loading : PresentationState()
        data class Error(val adError: AdError) : PresentationState()
        data object Loaded : PresentationState()

        val isInterstitialButtonShown: Boolean
            get() = AdConfiguration.Ad.interstitialRequest != null

        val isInlineButtonShown: Boolean
            get() = AdConfiguration.Ad.inlineRequests != null

        val isTrackingButtonShown: Boolean
            get() = AdConfiguration.Tracking.trackingRequest != null

        val isTagButtonShown: Boolean
            get() = AdConfiguration.Tracking.tagRequest != null
    }
}
