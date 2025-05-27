package com.example.lupath.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "general_checklist_items")
data class ChecklistItemEntity(
    @PrimaryKey val itemId: String = UUID.randomUUID().toString(),
    val name: String,
    val isPreMade: Boolean,
    var isChecked: Boolean = false // Checked state is part of the item itself now
)