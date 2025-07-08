package com.adition.nextgen_adsdk_issue_demo.core

import android.content.Context

object ContextProvider {
    /**
     * App appContext
     */
    @Volatile
    lateinit var appContext: Context

    var isStoreVersion: Boolean = false

    fun setContext(context: Context) {
        appContext = context
    }
}