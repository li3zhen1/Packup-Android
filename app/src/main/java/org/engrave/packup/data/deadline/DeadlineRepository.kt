package org.engrave.packup.data.deadline

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.engrave.packup.worker.DeadlineCrawler
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * Manage all the relevant local storage and network operations about Deadline Repository
 */
class DeadlineRepository @Inject constructor(
    private val deadlineDao: DeadlineDao,
    @ApplicationContext private val context: Context
) {
    suspend fun setDeadlineStarred(uid: Int, boolean: Boolean) = deadlineDao.setDeadlineStarred(uid, boolean)
    suspend fun setDeadlineDeleted(uid: Int, boolean: Boolean) = deadlineDao.setDeadlineDeleted(uid, boolean)
    suspend fun setDeadlineCompleted(uid: Int, boolean: Boolean) = deadlineDao.setDeadlineCompleted(uid, boolean)
    suspend fun setDeadlineDescription(uid: Int, desc: String?) = deadlineDao.setDeadlineDescription(uid, desc)
    suspend fun setDeadlineReminder(uid: Int, reminder: Long?) = deadlineDao.setDeadlineReminder(uid, reminder)
    suspend fun setDeadlineSourceFullName(uid: Int, fullNameWithSemester: String?) = deadlineDao.setDeadlineSourceFullName(uid, fullNameWithSemester)

    private val repositoryScope = CoroutineScope(Dispatchers.Default)
    val allDeadlines: LiveData<List<Deadline>> = deadlineDao.getAllDeadlines()
    val allDeadlinesStatic: List<Deadline> get() = deadlineDao.getAllDeadlinesStatic()
    fun getDeadlineAlive(uid: Int) = deadlineDao.getDeadline(uid)

    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
    val courseCrawler =
        PeriodicWorkRequestBuilder<DeadlineCrawler>(30, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

    init {
        repositoryScope.launch {
            WorkManager
                .getInstance(context)
                .enqueueUniquePeriodicWork(
                    COURSE_CRAWLER_NAME,
                    ExistingPeriodicWorkPolicy.KEEP,
                    courseCrawler
                )
        }
    }

    suspend fun commitNewDeadline(deadline: Deadline) = deadlineDao.insertDeadline(deadline)


    companion object {
        const val COURSE_CRAWLER_NAME = "course-crawler"
    }
}