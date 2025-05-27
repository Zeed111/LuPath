package com.example.lupath.data.model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lupath.R
import com.example.lupath.data.database.dao.MountainDao
import com.example.lupath.data.database.entity.MountainEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

fun mapPictureReferenceToDrawableRes(
    context: Context,
    pictureReference: String?
): Int? {
    if (pictureReference.isNullOrBlank()) {
        return R.drawable.mt_pulag_ex // Default placeholder if no reference
    }
    val resId = context.resources.getIdentifier(pictureReference, "drawable", context.packageName)
    return if (resId != 0) resId else R.drawable.mt_pulag_ex // Fallback if not found
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mountainDao: MountainDao,
    @ApplicationContext private val applicationContext: Context // Inject context for resource mapping
) : ViewModel() {

    // Update mapping function to populate imageResId
    private fun MountainEntity.toMountainUiModel(): Mountain {
        return Mountain(
            id = this.mountainId,
            name = this.mountainName,
            location = this.location,
            masl = this.masl,
            difficultySummary = this.difficultySummary,
            difficulty = this.difficultyText,
            tagline = this.tagline,
            hoursToSummit = this.hoursToSummit,
            bestMonthsToHike = this.bestMonthsToHike,
            typeVolcano = this.typeVolcano,
            trekDurationDetails = this.trekDurationDetails,
            trailTypeDescription = this.trailTypeDescription,
            sceneryDescription = this.sceneryDescription,
            viewsDescription = this.viewsDescription,
            wildlifeDescription = this.wildlifeDescription,
            featuresDescription = this.featuresDescription,
            hikingSeasonDetails = this.hikingSeasonDetails,
            introduction = this.introduction,
            imageResId = mapPictureReferenceToDrawableRes(applicationContext, this.pictureReference), // Populate imageResId
            pictureReference = this.pictureReference
        )
    }

    private val _popularMountains = MutableStateFlow<List<Mountain>>(emptyList())
    val popularMountains: StateFlow<List<Mountain>> = _popularMountains.asStateFlow()

    private val _allMountains = MutableStateFlow<List<Mountain>>(emptyList())
    val allMountains: StateFlow<List<Mountain>> = _allMountains.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadMountains()
    }

    private fun loadMountains() {
        viewModelScope.launch {
            searchQuery.collectLatest { query ->
                mountainDao.getAllMountainsFlow()
                    .map { entityList ->
                        val mappedList = entityList.map { it.toMountainUiModel() } // Use the updated mapping
                        if (query.isBlank()) {
                            _popularMountains.value = mappedList.take(5) // Update popular mountains
                            mappedList // Return all if query is blank
                        } else {
                            // Filter based on query
                            val filteredList = mappedList.filter { mountain ->
                                mountain.name.contains(query, ignoreCase = true) ||
                                        mountain.location.contains(query, ignoreCase = true) == true
                            }
                            // Update popular mountains based on filtered list or clear it
                            _popularMountains.value = filteredList.take(5)
                            filteredList
                        }
                    }
                    .catch { e ->
                        // Handle any errors from the flow
                        println("Error loading mountains: $e")
                        _allMountains.value = emptyList()
                        _popularMountains.value = emptyList() // Also clear popular on error
                    }
                    .collect { uiModelList ->
                        _allMountains.value = uiModelList
                    }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }
}