package com.example.lupath.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mountains")
data class MountainEntity(
    @PrimaryKey val mountainId: String,
    val mountainName: String,
    val pictureReference: String?, // Drawable resource name or URL
    val location: String,
    val masl: Int?,
    @ColumnInfo(name = "difficulty_text") // More descriptive than just 'difficulty'
    val difficultySummary: String?,
    val difficultyText: String, // e.g., "Easy to moderate (4/9)"
    val hoursToSummit: String,  // e.g., "4-6 hours round trip"
    val bestMonthsToHike: String,

    // Detailed descriptive fields from research
    val typeVolcano: String? = null,
    val trekDurationDetails: String? = null, // More detailed than hoursToSummit if needed
    val trailTypeDescription: String? = null,
    val sceneryDescription: String? = null,
    val viewsDescription: String? = null,
    val wildlifeDescription: String? = null,
    val featuresDescription: String? = null,
    val hikingSeasonDetails: String? = null, // More detailed than bestMonthsToHike
    val introduction: String? = null, // For longer introductory text
    val tagline: String?,

    val mountainImageRef1: String?, // Drawable name for first carousel image (can be same as pictureReference)
    val mountainImageRef2: String?, // Drawable name for second carousel image
    val mountainImageRef3: String?  // Drawable name for third carousel image
)