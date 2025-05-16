package com.example.lupath.data.database.dao

import androidx.room.*
import com.example.lupath.data.database.entity.HikePlanEntity
import com.example.lupath.data.database.entity.MountainEntity // If you need to join for mountain name
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

// Data class to hold HikePlan with Mountain Name for display
data class HikePlanWithMountainName(
    @Embedded val hikePlan: HikePlanEntity,
    @ColumnInfo(name = "mountainName") val mountainName: String?,
    @ColumnInfo(name = "pictureReference") val mountainPictureReference: String? // For displaying image
)

@Dao
interface HikePlanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHikePlan(hikePlan: HikePlanEntity): Long // Returns rowId

    @Update
    suspend fun updateHikePlan(hikePlan: HikePlanEntity)

    @Delete
    suspend fun deleteHikePlan(hikePlan: HikePlanEntity)

    @Query("SELECT * FROM hike_plans WHERE hikePlanId = :hikePlanId")
    fun getHikePlanByIdFlow(hikePlanId: String): Flow<HikePlanEntity?>

    @Query("SELECT * FROM hike_plans WHERE hikePlanId = :hikePlanId")
    suspend fun getHikePlanById(hikePlanId: String): HikePlanEntity? // Non-Flow version for direct access

    // Get all hike plans, ordered by date, and join with mountains table to get mountain name
    @Query("""
        SELECT hp.*, m.mountainName, m.pictureReference
        FROM hike_plans AS hp
        LEFT JOIN mountains AS m ON hp.mountainOwnerId = m.mountainId
        ORDER BY hp.date DESC
    """)
    fun getAllHikePlansWithMountainName(): Flow<List<HikePlanWithMountainName>>

    @Query("SELECT * FROM hike_plans WHERE date = :date")
    fun getHikePlansByDate(date: LocalDate): Flow<List<HikePlanEntity>>

    @Query("DELETE FROM hike_plans")
    suspend fun deleteAllHikePlans()
}