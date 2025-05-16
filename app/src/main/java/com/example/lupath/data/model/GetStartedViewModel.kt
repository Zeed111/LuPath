package com.example.lupath.data.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

open class GetStartedViewModel : ViewModel() {
    private val _welcomeMessage =
        MutableStateFlow("Focus, relax and find your next adventure here in Lupath")
    open val welcomeMessage: StateFlow<String> = _welcomeMessage

    open fun onGetStartedClicked() {
        // Later: Save onboarding flag or fetch data
    }
}