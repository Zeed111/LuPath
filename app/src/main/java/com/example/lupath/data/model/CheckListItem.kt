package com.example.lupath.data.model

import java.util.UUID

data class ChecklistItem(
    val id: String = UUID.randomUUID().toString(), // Unique ID for stable keys
    val text: String,
    val isChecked: Boolean = false
)