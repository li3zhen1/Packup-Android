package org.engrave.packup.data.deadline

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DeadlineDao {
    @Query("SELECT * FROM deadline")
    fun getAllDeadlines(): LiveData<List<Deadline>>

    @Query("SELECT * FROM deadline")
    fun getAllDeadlinesStatic(): List<Deadline>

//    @Query("SELECT * FROM deadline WHERE is_finished IS 1")
//    fun getAllDeadlineCompleted(): List<Deadline>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeadline(deadline: Deadline)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDeadlines(deadlines: List<Deadline>)
}