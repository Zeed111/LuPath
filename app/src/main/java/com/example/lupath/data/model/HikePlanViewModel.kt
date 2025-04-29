package com.example.lupath.data.model

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class HikePlanViewModel : ViewModel() {

    private val _hikePlans = MutableStateFlow<List<HikePlan>>(
        // Provide initial value directly to the flow
        listOf(
            HikePlan(date = LocalDate.of(2025, 4, 28), mountainName = "Mt. Pulag", difficulty = "Beginner"),
            HikePlan(date = LocalDate.of(2025, 5, 12), mountainName = "Mt. Apo", difficulty = "Intermediate")
        )
    )

    // 2. Public immutable StateFlow exposed to the UI
    val hikePlans: StateFlow<List<HikePlan>> = _hikePlans.asStateFlow()

    // --- Methods to modify the state ---

    fun addHikePlan(hikePlan: HikePlan) {
        // Use the 'update' extension function for safe concurrent updates
        _hikePlans.update { currentList ->
            // Create a new list by adding the new plan
            currentList + hikePlan // '+' operator creates a new list
        }
        // In a real app, you'd also save this change to a database/repository
    }

    fun removeHikePlan(hikePlan: HikePlan) {
        _hikePlans.update { currentList ->
            // Create a new list excluding the plan to remove
            currentList - hikePlan // '-' operator creates a new list
        }
        // In a real app, you'd also delete this from a database/repository
    }

    // Optional: If you need to load data asynchronously
    init {
        // loadInitialPlans() // You could call a loading function here if needed
    }

    private fun loadInitialPlans() {
        viewModelScope.launch {
            // Simulate loading from a repository
            // val initialPlans = repository.getHikePlans()
            // _hikePlans.value = initialPlans
        }
    }
}