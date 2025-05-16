package com.example.lupath.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "hike_plans",
    foreignKeys = [ForeignKey(
        entity = MountainEntity::class,
        parentColumns = ["mountainId"],
        childColumns = ["mountainOwnerId"],
        onDelete = ForeignKey.SET_NULL // Or CASCADE, depending on how you want to handle deletion
    )],
    indices = [Index(value = ["mountainOwnerId"])]
)
data class HikePlanEntity(
    @PrimaryKey val hikePlanId: String,
    val mountainOwnerId: String?, // Foreign Key
    val date: LocalDate // Room needs a TypeConverter for this

)