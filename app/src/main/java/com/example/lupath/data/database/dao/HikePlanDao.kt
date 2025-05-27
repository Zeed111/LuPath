package com.example.lupath.data.database.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.lupath.data.database.entity.HikePlanEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

data class HikePlanWithMountainName(
    @Embedded val hikePlan: HikePlanEntity,
    @ColumnInfo(name = "mountainName") val mountainName: String?,
    @ColumnInfo(name = "pictureReference") val mountainPictureReference: String?
)

@Dao
interface HikePlanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHikePlan(hikePlan: HikePlanEntity): Long

    @Update
    suspend fun updateHikePlan(hikePlan: HikePlanEntity)

    @Delete
    suspend fun deleteHikePlan(hikePlan: HikePlanEntity)

    @Query("SELECT * FROM hike_plans WHERE hikePlanId = :hikePlanId")
    fun getHikePlanByIdFlow(hikePlanId: String): Flow<HikePlanEntity?>

    @Query("SELECT * FROM hike_plans WHERE hikePlanId = :hikePlanId")
    suspend fun getHikePlanById(hikePlanId: String): HikePlanEntity?

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