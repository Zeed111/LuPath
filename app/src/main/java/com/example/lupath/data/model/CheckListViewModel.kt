package com.example.lupath.data.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ChecklistViewModel : ViewModel() {

    // Predefined list
    private val _predefinedItems = MutableStateFlow<List<ChecklistItem>>( // Make private
        listOf( // Initial predefined list
            ChecklistItem(text = "Sleeping Bags and Sleeping Mat"),
            ChecklistItem(text = "Water Bottles"),
            ChecklistItem(text = "Camera"),
            ChecklistItem(text = "Backpack")
        )
    )
    val predefinedItems: StateFlow<List<ChecklistItem>> = _predefinedItems.asStateFlow()

    // User's personal list

    private val _personalItems = MutableStateFlow<List<ChecklistItem>>(emptyList()) // Start empty
    val personalItems: StateFlow<List<ChecklistItem>> = _personalItems.asStateFlow()

    // --- Functions to modify state ---

    fun togglePredefinedItemChecked(itemToToggle: ChecklistItem) {
        _predefinedItems.update { currentList ->
            currentList.map { existingItem ->
                if (existingItem.id == itemToToggle.id) {
                    existingItem.copy(isChecked = !existingItem.isChecked) // Create updated copy
                } else {
                    existingItem // Keep others the same
                }
            }
        }
        // Persist change if necessary (e.g., save checked state to SharedPreferences)
    }

    fun togglePersonalItemChecked(itemToToggle: ChecklistItem) {
        _personalItems.update { currentList ->
            currentList.map { existingItem ->
                if (existingItem.id == itemToToggle.id) {
                    existingItem.copy(isChecked = !existingItem.isChecked) // Create updated copy
                } else {
                    existingItem // Keep others the same
                }
            }
        }
        // Persist change if necessary
    }


    fun addPersonalItem(text: String) {
        if (text.isNotBlank()) {
            val newItem = ChecklistItem(text = text.trim()) // Creates item with new UUID ID
            _personalItems.update { currentList ->
                currentList + newItem // Add new item creating a new list
            }
            // Persist change if necessary (e.g., save to database)
        }
    }

    fun removePersonalItem(itemToRemove: ChecklistItem) {
        _personalItems.update { currentList ->
            currentList.filterNot { it.id == itemToRemove.id } // Create new list without the item
        }
        // Persist change if necessary (e.g., delete from database)
    }

    // fun togglePredefinedItem(item: ChecklistItem) { toggleItemChecked(predefinedItems, item) }
    // fun togglePersonalItem(item: ChecklistItem) { toggleItemChecked(personalItems, item) }
}