package com.example.lupath.data.model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lupath.data.database.dao.MountainDao
import com.example.lupath.data.database.dao.MountainWithDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MountainDetailViewModel @Inject constructor(
    private val mountainDao: MountainDao,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val mountainId: String = savedStateHandle.get<String>("mountainId") ?: ""

    val mountainWithDetails: StateFlow<MountainWithDetails?> =
        mountainDao.getMountainWithDetails(mountainId) // This returns a Flow
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}