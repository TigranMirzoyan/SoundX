package com.soundx.appfragment

interface SpecialFragmentContainer {
    fun setSpecialLayoutVisibility(state: Boolean)
    fun makeSpecialLayoutVisible() = setSpecialLayoutVisibility(true)
    fun makeSpecialLayoutGone() = setSpecialLayoutVisibility(false)
}