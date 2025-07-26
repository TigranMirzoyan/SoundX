package com.soundx.util

object SpecialLayoutVisibility {
    private lateinit var setVisibilityCallback: (Boolean) -> Unit
    private var isVisible = false

    fun init(callback: (Boolean) -> Unit) {
        setVisibilityCallback = callback
    }

    fun visible() {
        if (isVisible) return

        isVisible = true
        checkCallbackInit()
        setVisibilityCallback(true)
    }

    fun gone() {
        if (!isVisible) return

        isVisible = false
        checkCallbackInit()
        setVisibilityCallback(false)
    }

    private fun checkCallbackInit() {
        if (::setVisibilityCallback.isInitialized) return
        throw IllegalStateException("SpecialLayoutVisibility is not initialized. Call init() first.")
    }
}