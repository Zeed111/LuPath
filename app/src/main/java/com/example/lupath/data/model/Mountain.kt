package com.example.lupath.data.model

import androidx.annotation.DrawableRes // If you plan to use local drawable resource IDs
import java.util.UUID // For generating unique IDs

data class Mountain(
    val id: String = UUID.randomUUID().toString(), // Unique identifier
    val name: String,
    val location: String,
    val masl: Int? = null, // Meters Above Sea Level (nullable if can be unknown)
    val difficulty: String? = null, // e.g., "Easy to moderate (4/9)"
    val tagline: String? = null, // A short descriptive tagline
    val hoursToSummit: String? = null, // e.g., "4-6 hours round trip" (nullable)
    val bestMonthsToHike: String? = null, // (nullable)

    // Optional detailed fields from your research (can be nullable)
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
    @DrawableRes val imageResId: Int? = null, // For local drawables (e.g., R.drawable.some_image)
    val imageUrl: String? = null // For internet URLs
)