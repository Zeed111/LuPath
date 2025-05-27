package com.example.lupath.data.model

import androidx.annotation.DrawableRes // If you plan to use local drawable resource IDs
import java.util.UUID // For generating unique IDs

data class Mountain(
    val id: String = UUID.randomUUID().toString(), // Unique identifier
    val name: String,
    val location: String,
    val masl: Int? = null,
    val difficultySummary: String?,
    val difficulty: String? = null,
    val tagline: String? = null,
    val hoursToSummit: String? = null,
    val bestMonthsToHike: String? = null,


    val typeVolcano: String? = null,
    val trekDurationDetails: String? = null,
    val trailTypeDescription: String? = null,
    val sceneryDescription: String? = null,
    val viewsDescription: String? = null,
    val wildlifeDescription: String? = null,
    val featuresDescription: String? = null,
    val hikingSeasonDetails: String? = null,
    val introduction: String? = null,

    // For images
    @DrawableRes val imageResId: Int? = null,
    val pictureReference: String?
)