package com.example.lupath.data.database.dao

import androidx.room.*
import com.example.lupath.data.database.entity.TrailEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrail(trail: TrailEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTrails(trails: List<TrailEntity>)

    @Update
    suspend fun updateTrail(trail: TrailEntity)

    @Delete
    suspend fun deleteTrail(trail: TrailEntity)

    @Query("SELECT * FROM trails WHERE mountainOwnerId = :mountainId ORDER BY name ASC")
    fun getTrailsForMountain(mountainId: String): Flow<List<TrailEntity>>

    @Query("SELECT * FROM trails WHERE trailId = :trailId")
    fun getTrailById(trailId: String): Flow<TrailEntity?>

    @Query("SELECT * FROM trails ORDER BY name ASC")
    fun getAllTrails(): Flow<List<TrailEntity>>
}