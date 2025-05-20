package com.example.lupath.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "campsites",
    foreignKeys = [ForeignKey(
        entity = MountainEntity::class,
        parentColumns = ["mountainId"],
        childColumns = ["mountainOwnerId"], // Name of the FK column in this table
        onDelete = ForeignKey.CASCADE // If mountain is deleted, delete its campsites
    )],
    indices = [Index(value = ["mountainOwnerId"])] // Index for faster queries
)
data class CampsiteEntity(
    @PrimaryKey val campsiteId: String,
    val mountainOwnerId: String, // Foreign Key
    val name: String? = null,
    val description: String? = null // e.g., "Sheltered and flat", "Near Rockies and summit areas"
    // Add other campsite specific fields from your detailed research if needed
    // val trekTimeToCampsite: String?,
    // val waterSourceAvailable: String?,
    // val bestFor: String?
)