package com.example.lupath.data.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lupath.data.database.dao.ChecklistItemDao
import com.example.lupath.data.database.entity.ChecklistItemEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// Helper to convert Entity to UI Model (ChecklistItem for your UI)
fun ChecklistItemEntity.toUiModel() = ChecklistItem(
    id = this.itemId,
    text = this.name,
    isChecked = this.isChecked,
    isPreMade = this.isPreMade
)

// Helper to convert UI Model back to Entity for saving
fun ChecklistItem.toEntity() = ChecklistItemEntity(
    itemId = this.id,
    name = this.text,
    isChecked = this.isChecked,
    isPreMade = this.isPreMade
)

@HiltViewModel
class ChecklistViewModel @Inject constructor(
    // Assuming Hilt or manual injection of the DAO
    private val checklistItemDao: ChecklistItemDao
) : ViewModel() {

    private val _predefinedItems = MutableStateFlow<List<ChecklistItem>>(emptyList())
    val predefinedItems: StateFlow<List<ChecklistItem>> = _predefinedItems.asStateFlow()

    private val _personalItems = MutableStateFlow<List<ChecklistItem>>(emptyList())
    val personalItems: StateFlow<List<ChecklistItem>> = _personalItems.asStateFlow()

    init {
        loadItems()
    }

    private fun loadItems() {
        viewModelScope.launch {
            checklistItemDao.getPredefinedItemsFlow().collect { entities ->
                _predefinedItems.value = entities.map { it.toUiModel() }
            }
        }
        viewModelScope.launch {
            checklistItemDao.getPersonalItemsFlow().collect { entities ->
                _personalItems.value = entities.map { it.toUiModel() }
            }
        }
    }

    fun toggleItemChecked(itemToToggle: ChecklistItem) {
        viewModelScope.launch {
            val updatedItemEntity = itemToToggle.copy(isChecked = !itemToToggle.isChecked).toEntity()
            checklistItemDao.updateItem(updatedItemEntity)
            // The flows will automatically update because they observe the database
        }
    }

    fun addPersonalItem(text: String) {
        if (text.isNotBlank()) {
            viewModelScope.launch {
                val newItemEntity = ChecklistItemEntity(
                    name = text.trim(),
                    isPreMade = false, // User-added items are not pre-made
                    isChecked = false
                )
                checklistItemDao.insertItem(newItemEntity)
            }
        }
    }

    fun removePersonalItem(itemToRemove: ChecklistItem) {
        viewModelScope.launch {
            // Only remove items that are not predefined
            if (!itemToRemove.isPreMade) {
                checklistItemDao.deleteItem(itemToRemove.toEntity())
            }
        }
    }
}