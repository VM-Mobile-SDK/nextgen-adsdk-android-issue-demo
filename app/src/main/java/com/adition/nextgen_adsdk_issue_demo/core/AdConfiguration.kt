package com.adition.nextgen_adsdk_issue_demo.core


import com.adition.sdk_core.api.entities.request.AdRequest
import com.adition.sdk_core.api.entities.request.AdRequestGlobalParameters
import com.adition.sdk_core.api.entities.request.TagRequest
import com.adition.sdk_core.api.entities.request.TrackingGlobalParameters
import com.adition.sdk_core.api.entities.request.TrackingRequest

object AdConfiguration {
    const val NETWORK_ID = "0000"
    const val CACHE_SIZE = 100u

    object Ad {
        const val IS_PRELOADING_CONTENT = true

        val inlineRequests: List<AdRequest>? = null
        val interstitialRequest: AdRequest? = null

        val globalParameters = AdRequestGlobalParameters().apply {
            gdpr = null
            externalUID = null
            isOptOutEnabled = null
            userIds = null
            isSHBEnabled = null
            dsa = null
            isIpIdentified = null
            cookiesAccess = null
        }
    }

    object Tracking {
        val tagRequest: TagRequest? = null
        val trackingRequest: TrackingRequest? = null

        val globalParameters = TrackingGlobalParameters().apply {
            gdpr = null
            externalUID = null
            isOptOutEnabled = null
            userIds = null
        }
    }
}
