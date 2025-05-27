package com.example.lupath.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mountains")
data class MountainEntity(
    @PrimaryKey val mountainId: String,
    val mountainName: String,
    val pictureReference: String?,
    val location: String,
    val masl: Int?,
    @ColumnInfo(name = "difficulty_text")
    val difficultySummary: String?,
    val difficultyText: String,
    val hoursToSummit: String,
    val bestMonthsToHike: String,


    val typeVolcano: String? = null,
    val trekDurationDetails: String? = null,
    val trailTypeDescription: String? = null,
    val sceneryDescription: String? = null,
    val viewsDescription: String? = null,
    val wildlifeDescription: String? = null,
    val featuresDescription: String? = null,
    val hikingSeasonDetails: String? = null,
    val introduction: String? = null,
    val tagline: String?,

    //For Image Carousel
    val mountainImageRef1: String?,
    val mountainImageRef2: String?,
    val mountainImageRef3: String?,

    val hasSteepSections: Boolean?,     // True if there are significant steep inclines/declines
    val notableWildlife: String?,      // If not blank, can trigger a generic wildlife icon.
    val isRocky: Boolean?,             // True if the trail has significant rocky sections
    val isSlippery: Boolean?,          // True if the trail is known to be slippery
    val isEstablishedTrail: Boolean?  // True if the trail is generally clear and well-defined
)