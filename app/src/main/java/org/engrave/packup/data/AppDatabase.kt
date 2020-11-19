package org.engrave.packup.data

import androidx.room.Database
import androidx.room.RoomDatabase
import org.engrave.packup.data.course.ClassInfo
import org.engrave.packup.data.course.ClassInfoDao
import org.engrave.packup.data.deadline.Deadline
import org.engrave.packup.data.deadline.DeadlineDao

@Database(entities = [ClassInfo::class, Deadline::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun classInfoDao(): ClassInfoDao

    abstract fun deadlineDao(): DeadlineDao
}