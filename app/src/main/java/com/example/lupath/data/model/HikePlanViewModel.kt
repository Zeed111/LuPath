package com.example.lupath.data.model

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import java.time.LocalDate

class HikePlanViewModel : ViewModel() {
//    val hikePlans = mutableStateListOf<HikePlan>()

    val hikePlans = mutableStateListOf(
        HikePlan("Mt. Pulag", LocalDate.of(2025, 4, 28), "Beginner"),
        HikePlan("Mt. Apo", LocalDate.of(2025, 5, 12), "Intermediate")
    )

    fun addHikePlan(hikePlan: HikePlan) {
        hikePlans.add(hikePlan)
    }

    fun removeHikePlan(hikePlan: HikePlan) {
        hikePlans.remove(hikePlan)
    }
}