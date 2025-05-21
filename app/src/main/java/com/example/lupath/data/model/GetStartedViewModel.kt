package com.example.lupath.data.model

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.core.content.edit

@HiltViewModel
class GetStartedViewModel @Inject constructor(
    private val application: Application
) : ViewModel() {
    private val _welcomeMessage =
        MutableStateFlow("Focus, relax and find your next adventure here in Lupath")
    val welcomeMessage: StateFlow<String> = _welcomeMessage

    private val prefs: SharedPreferences =
        application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_GET_STARTED_COMPLETED = "get_started_completed"
    }

    fun onGetStartedClicked() {
        // Later: Save onboarding flag or fetch data
        prefs.edit {
            putBoolean(KEY_GET_STARTED_COMPLETED, true)
        }
    }
}