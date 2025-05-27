package com.example.lupath.data.model

import java.time.LocalDate
import java.util.*

data class HikePlan(
    val id: String = UUID.randomUUID().toString(),
    val mountainId: String, // FK to MountainEntity
    val mountainName: String,
    val date: LocalDate,
    val difficulty: String,
    val imageResourceName: String? = null,
    val notes: String?
)