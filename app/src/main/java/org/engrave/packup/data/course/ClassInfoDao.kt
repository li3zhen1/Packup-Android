package org.engrave.packup.data.course

import androidx.lifecycle.LiveData
import androidx.room.*

@Suppress("SpellCheckingInspection")

/**
 * An android abstraction layer over SQLite.
 * @see <a href="https://developer.android.google.cn/topic/libraries/architecture/room"> androidx.room </a>
 */
@Dao
interface ClassInfoDao {

    @Query("SELECT * FROM classinfo")
    fun getAll(): LiveData<List<ClassInfo>>

    @Query("SELECT * FROM classinfo")
    fun getAllStatic(): List<ClassInfo>

    @Update
    fun update(classInfo: ClassInfo)

    @Query(
        "SELECT * FROM classinfo WHERE courseName = :courseName AND description = :desc AND examInfo = :exam AND classTime = :classTimeListStr AND semester =:semesterCode"
    )
    fun getAllWithSameInfo(
        courseName: String,
        desc: String,
        exam: String,
        classTimeListStr: String,
        semesterCode: String
    ): List<ClassInfo>

    fun isAlreadyContained(classInfo: ClassInfo): Int? = with(classInfo) {
        getAllWithSameInfo(
            courseName,
            description,
            examInfo,
            classTime.serializedString(),
            semester.asCode()
        ).run {
            if (isNullOrEmpty()) null
            else first().uid
        }
    }

    suspend fun insertOrUpdate(classInfo: ClassInfo) {
        val newUid = isAlreadyContained(classInfo)
        if (newUid == null) {
            insert(classInfo)
        } else {
            update(
                classInfo.copy(
                    uid = newUid
                )
            )
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(classInfo: ClassInfo)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(classInfo: List<ClassInfo>)

    @Query("DELETE FROM classinfo")
    suspend fun deleteAll()


}