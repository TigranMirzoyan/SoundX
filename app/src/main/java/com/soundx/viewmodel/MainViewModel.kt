package com.soundx.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private val _navigationEvent = MutableLiveData<Fragments>()
    val navigationEvent: LiveData<Fragments> get() = _navigationEvent

    fun navigateTo(fragment: Fragments) {
        _navigationEvent.value = fragment
    }
}