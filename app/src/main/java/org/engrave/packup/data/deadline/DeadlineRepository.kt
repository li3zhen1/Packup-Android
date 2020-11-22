package org.engrave.packup.data.deadline

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.engrave.packup.worker.DeadlineCrawler
import javax.inject.Inject

/**
 * Manage all the relevant local storage and network operations about Deadline Repository
 */
class DeadlineRepository @Inject constructor(
    private val deadlineDao: DeadlineDao,
    @ApplicationContext private val context: Context
) {
    private val deadlineScope = CoroutineScope(Dispatchers.Default)
    val allDeadlines: LiveData<List<Deadline>> = deadlineDao.getAllDeadlines()
    val allDeadlinesStatic: List<Deadline> get() = deadlineDao.getAllDeadlinesStatic()

    init {
        deadlineScope.launch {
            val courseCrawler: WorkRequest by lazy {
                OneTimeWorkRequestBuilder<DeadlineCrawler>().build()
            }
            WorkManager.getInstance(context).enqueue(courseCrawler)
        }
    }



}