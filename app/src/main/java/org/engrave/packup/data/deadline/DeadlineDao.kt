package org.engrave.packup.data.deadline

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
@TypeConverters(DeadlineAttachedFileTypeConverter::class)
interface DeadlineDao {
    @Query("SELECT * FROM deadline")
    fun getAllDeadlines(): LiveData<List<Deadline>>

    /**
     *  调用不要加 withContext 会死锁！！
     */
    @Query("SELECT * FROM deadline")
    fun getAllDeadlinesStatic(): List<Deadline>

    @Query("SELECT * FROM deadline WHERE uid=:uid")
    fun getDeadline(uid: Int): LiveData<Deadline>

    @Query("SELECT * FROM deadline WHERE uid=:uid")
    fun getDeadlineStatic(uid: Int): Deadline

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeadline(deadline: Deadline)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllDeadlines(deadlines: List<Deadline>)

    @Query("UPDATE deadline SET is_starred=:isStarred WHERE uid = :uid")
    suspend fun setDeadlineStarred(uid: Int, isStarred: Boolean)

    @Query("UPDATE deadline SET has_submission=:isSubmitted WHERE uid=:uid")
    suspend fun setDeadlineSubmission(uid: Int, isSubmitted: Boolean)

    @Query("UPDATE deadline SET attached_file_list=:attachedFileList, attached_file_list_crawled=1 WHERE uid=:uid")
    suspend fun setDeadlineAttachedFiles(uid: Int, attachedFileList: List<DeadlineAttachedFile>)

    @Query("UPDATE deadline SET is_completed=:isCompleted WHERE uid = :uid")
    suspend fun setDeadlineCompleted(uid: Int, isCompleted: Boolean)

    @Query("UPDATE deadline SET is_deleted=:isDeleted WHERE uid = :uid")
    suspend fun setDeadlineDeleted(uid: Int, isDeleted: Boolean)

    @Query("SELECT * FROM deadline WHERE uid = :uid")
    suspend fun getDeadlineByUidStatic(uid: Int): Array<Deadline>
}