package com.example.lupath.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "guidelines",
    foreignKeys = [ForeignKey(
        entity = MountainEntity::class,
        parentColumns = ["mountainId"],
        childColumns = ["mountainOwnerId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["mountainOwnerId"])]
)
data class GuidelineEntity(
    @PrimaryKey val guidelineId: String,
    val mountainOwnerId: String, // Foreign Key
    val category: String, // e.g., "Registration Fee", "Safety Tip", "Environmental Concern"
    val description: String
)