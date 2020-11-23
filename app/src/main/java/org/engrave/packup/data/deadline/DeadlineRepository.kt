package org.engrave.packup.data.deadline

import android.content.Context
import androidx.lifecycle.LiveData
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
    private val repositoryScope = CoroutineScope(Dispatchers.Default)
    val allDeadlines: LiveData<List<Deadline>> = deadlineDao.getAllDeadlines()
    val allDeadlinesStatic: List<Deadline> get() = deadlineDao.getAllDeadlinesStatic()

    init {
        repositoryScope.launch {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val courseCrawler =
                PeriodicWorkRequestBuilder<DeadlineCrawler>(30, TimeUnit.MINUTES)
                    //.setConstraints(constraints)
                    .build()
            //WorkManager.getInstance(context).enqueue(courseCrawler)
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                COURSE_CRAWLER_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                courseCrawler
            )
        }
    }


    companion object {
        const val COURSE_CRAWLER_NAME = "course-crawler"
    }
}