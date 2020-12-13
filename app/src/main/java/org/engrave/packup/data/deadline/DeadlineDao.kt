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

    @Query("SELECT * FROM deadline WHERE uid=:uid")
    suspend fun getDeadline(uid: Int): Deadline

//    @Query("SELECT * FROM deadline WHERE is_finished IS 1")
//    fun getAllDeadlineCompleted(): List<Deadline>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeadline(deadline: Deadline)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDeadlines(deadlines: List<Deadline>)


    @Query("UPDATE deadline SET is_starred=:isStarred WHERE uid = :uid")
    suspend fun setDeadlineStarred(uid: Int, isStarred: Boolean)

    @Query("UPDATE deadline SET has_submission=:isSubmitted WHERE uid = :uid")
    suspend fun setDeadlineSubmission(uid: Int, isSubmitted: Boolean)


}