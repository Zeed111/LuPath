package com.example.lupath.data.database.dao

import androidx.room.*
import com.example.lupath.data.database.entity.ChecklistItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ChecklistItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllItems(items: List<ChecklistItemEntity>)

    @Update
    suspend fun updateItem(item: ChecklistItemEntity) // Used for toggling isChecked

    @Delete
    suspend fun deleteItem(item: ChecklistItemEntity) // For deleting personal items

    @Query("SELECT * FROM general_checklist_items ORDER BY isPreMade DESC, name ASC")
    fun getAllItemsFlow(): Flow<List<ChecklistItemEntity>>

    @Query("SELECT * FROM general_checklist_items WHERE isPreMade = 1 ORDER BY name ASC")
    fun getPredefinedItemsFlow(): Flow<List<ChecklistItemEntity>>

    @Query("SELECT * FROM general_checklist_items WHERE isPreMade = 0 ORDER BY name ASC")
    fun getPersonalItemsFlow(): Flow<List<ChecklistItemEntity>>

    // Optional: if you need to fetch by name (e.g., to check if a personal item already exists)
    @Query("SELECT * FROM general_checklist_items WHERE name = :name LIMIT 1")
    suspend fun findItemByName(name: String): ChecklistItemEntity?
}