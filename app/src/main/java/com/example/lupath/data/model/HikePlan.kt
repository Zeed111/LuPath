package com.example.lupath.data.model

import java.time.LocalDate

data class HikePlan(
    val mountainName: String,
    val date: LocalDate,
    val difficulty: String
)