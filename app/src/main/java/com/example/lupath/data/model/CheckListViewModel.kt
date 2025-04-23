package com.example.lupath.data.model

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class ChecklistViewModel : ViewModel() {

    // Predefined list
    val predefinedItems = mutableStateListOf(
        ChecklistItem(text = "Sleeping Bags and Sleeping Mat"),
        ChecklistItem(text = "Water Bottles"),
        ChecklistItem(text = "Camera"),
        ChecklistItem(text = "Backpack")
    )

    // User's personal list
    val personalItems = mutableStateListOf<ChecklistItem>()

    // --- Functions to modify state ---

    fun toggleItemChecked(list: MutableList<ChecklistItem>, item: ChecklistItem) {
        val index = list.indexOfFirst { it.id == item.id }
        if (index != -1) {
            list[index] = item.copy(isChecked = !item.isChecked)
        }
    }

    fun addPersonalItem(text: String) {
        if (text.isNotBlank()) {
            personalItems.add(ChecklistItem(text = text.trim()))
        }
    }

    fun removePersonalItem(item: ChecklistItem) {
        personalItems.remove(item)
    }

    // fun togglePredefinedItem(item: ChecklistItem) { toggleItemChecked(predefinedItems, item) }
    // fun togglePersonalItem(item: ChecklistItem) { toggleItemChecked(personalItems, item) }
}