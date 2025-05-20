package com.example.lupath.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "trails",
    foreignKeys = [ForeignKey(
        entity = MountainEntity::class,
        parentColumns = ["mountainId"],
        childColumns = ["mountainOwnerId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["mountainOwnerId"])]
)
data class TrailEntity(
    @PrimaryKey val trailId: String,
    val mountainOwnerId: String, // Foreign Key
    val name: String? = null, // e.g., "Old Trail", "New Trail", "Rockies Trail"
    val distanceDescription: String? = null, // e.g., "Wider paths", "Direct ascent"
    val hoursToSummitSpecificTrail: String? = null,
    val difficultySpecificTrail: String? = null,
    val description: String? = null // e.g., "Gradual inclines, more trees..."
)