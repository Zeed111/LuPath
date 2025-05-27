package com.example.lupath.data.database.dao

import androidx.room.*
import com.example.lupath.data.database.entity.GuidelineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GuidelineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuideline(guideline: GuidelineEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllGuidelines(guidelines: List<GuidelineEntity>)

    @Update
    suspend fun updateGuideline(guideline: GuidelineEntity)

    @Delete
    suspend fun deleteGuideline(guideline: GuidelineEntity)

    @Query("SELECT * FROM guidelines WHERE mountainOwnerId = :mountainId ORDER BY category ASC, description ASC")
    fun getGuidelinesForMountain(mountainId: String): Flow<List<GuidelineEntity>>

    @Query("SELECT * FROM guidelines WHERE guidelineId = :guidelineId")
    fun getGuidelineById(guidelineId: String): Flow<GuidelineEntity?>

    @Query("SELECT * FROM guidelines ORDER BY category ASC, description ASC")
    fun getAllGuidelines(): Flow<List<GuidelineEntity>>
}