package com.example.lupath.data.model

import java.time.LocalDate
import java.util.*

data class HikePlan(
    val id: String = UUID.randomUUID().toString(),
    val mountainId: String, // FK to MountainEntity
    val mountainName: String, // For display purposes
    val date: LocalDate,
    val difficulty: String, // For display
    // Add other details you might want to quickly display in a HikePlan list
    val imageResourceName: String? = null // For display
)