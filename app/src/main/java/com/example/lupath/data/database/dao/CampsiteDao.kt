package com.example.lupath.data.database.dao

import androidx.room.*
import com.example.lupath.data.database.entity.CampsiteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CampsiteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCampsite(campsite: CampsiteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCampsites(campsites: List<CampsiteEntity>)

    @Update
    suspend fun updateCampsite(campsite: CampsiteEntity)

    @Delete
    suspend fun deleteCampsite(campsite: CampsiteEntity)

    @Query("SELECT * FROM campsites WHERE mountainOwnerId = :mountainId ORDER BY name ASC")
    fun getCampsitesForMountain(mountainId: String): Flow<List<CampsiteEntity>>

    @Query("SELECT * FROM campsites WHERE campsiteId = :campsiteId")
    fun getCampsiteById(campsiteId: String): Flow<CampsiteEntity?>

    @Query("SELECT * FROM campsites ORDER BY name ASC")
    fun getAllCampsites(): Flow<List<CampsiteEntity>> // Might not be needed, but good to have
}