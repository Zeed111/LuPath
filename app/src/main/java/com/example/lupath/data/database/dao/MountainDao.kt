package com.example.lupath.data.database.dao

import androidx.room.*
import com.example.lupath.data.database.entity.*
import kotlinx.coroutines.flow.Flow

// Data class for combined query result
data class MountainWithDetails(
    @Embedded val mountain: MountainEntity,
    @Relation(
        parentColumn = "mountainId",
        entityColumn = "mountainOwnerId"
    )
    val campsites: List<CampsiteEntity>,
    @Relation(
        parentColumn = "mountainId",
        entityColumn = "mountainOwnerId"
    )
    val trails: List<TrailEntity>,
    @Relation(
        parentColumn = "mountainId",
        entityColumn = "mountainOwnerId"
    )
    val guidelines: List<GuidelineEntity>
)

@Dao
interface MountainDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMountains(mountains: List<MountainEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCampsites(campsites: List<CampsiteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTrails(trails: List<TrailEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllGuidelines(guidelines: List<GuidelineEntity>)

    @Query("SELECT * FROM mountains ORDER BY mountainName ASC")
    fun getAllMountainsFlow(): Flow<List<MountainEntity>>

    @Transaction
    @Query("SELECT * FROM mountains WHERE mountainId = :mountainId")
    fun getMountainWithDetails(mountainId: String): Flow<MountainWithDetails?>

    @Query("SELECT * FROM mountains WHERE mountainId = :id")
    suspend fun getMountainByIdNonFlow(id: String): MountainEntity?
}