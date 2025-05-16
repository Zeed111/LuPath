package com.example.lupath.data.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lupath.R
import com.example.lupath.data.database.dao.MountainDao
import com.example.lupath.data.database.entity.MountainEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// Helper function to map Entity to UI Model (can be outside ViewModel or in a mapper class)
fun MountainEntity.toMountainUiModel(): Mountain {
    // Basic mapping, adjust as needed based on your Mountain UI model's fields
    return Mountain(
        id = this.mountainId,
        name = this.mountainName,
        location = this.location,
        difficulty = this.difficultyText, // Assuming difficultyText in MountainEntity
        // Example fallback
        imageResId = mapImageNameToResourceId(this.pictureReference), // Helper to map string to R.drawable
        masl = this.masl
    )
}

// Example helper, you'll need a more robust solution for image mapping
fun mapImageNameToResourceId(imageName: String?): Int? {
    return when (imageName) {
        "mt_pulag_main" -> R.drawable.mt_pulag_ex // Make sure these drawables exist
         // Example, create this drawable
        // Add more mappings for your actual image resource names
        else -> R.drawable.mt_pulag_ex  // Default placeholder
    }
}


@HiltViewModel // Use Hilt for easy ViewModel creation and dependency injection
class HomeViewModel @Inject constructor( // Inject DAO via Hilt
    private val mountainDao: MountainDao
    // Or inject a Repository that uses the DAO
    // private val mountainRepository: MountainRepository
) : ViewModel() {

    private val _popularMountains = MutableStateFlow<List<Mountain>>(emptyList())
    val popularMountains: StateFlow<List<Mountain>> = _popularMountains.asStateFlow()

    private val _allMountains = MutableStateFlow<List<Mountain>>(emptyList())
    val allMountains: StateFlow<List<Mountain>> = _allMountains.asStateFlow()

    // Search query state (if you implement search)
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadPopularMountains()
        loadAllMountains() // Or load filtered mountains based on search
    }

    private fun loadPopularMountains() {
        viewModelScope.launch {
            mountainDao.getAllMountainsFlow() // Use the existing function
                .map { entityList -> entityList.take(5).map { it.toMountainUiModel() } } // Take first 5 for popular
                .collect { uiModelList ->
                    _popularMountains.value = uiModelList
                }
        }
    }

    private fun loadAllMountains() {
        viewModelScope.launch {
            // Combine with search query if implemented
            // For now, just load all
            searchQuery.collectLatest { query -> // React to search query changes
                mountainDao.getAllMountainsFlow() // Flow<List<MountainEntity>>
                    .map { entityList ->
                        val mappedList = entityList.map { it.toMountainUiModel() }
                        if (query.isBlank()) {
                            mappedList // Return all if query is blank
                        } else {
                            // Filter based on query
                            mappedList.filter { mountain ->
                                mountain.name.contains(query, ignoreCase = true) ||
                                        mountain.location?.contains(query, ignoreCase = true) == true
                            }
                        }
                    }
                    .catch { e ->
                        // Handle any errors from the flow
                        println("Error loading all mountains: $e")
                        _allMountains.value = emptyList() // Set to empty on error
                    }
                    .collect { uiModelList ->
                        _allMountains.value = uiModelList
                    }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        // loadAllMountains() will be re-triggered due to collecting searchQuery
    }
}