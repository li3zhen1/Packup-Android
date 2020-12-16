package org.engrave.packup.worker

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.engrave.packup.api.pku.course.downloadDeadlineAttachedFiles
import org.engrave.packup.data.account.AccountInfoRepository
import org.engrave.packup.data.deadline.DeadlineDao

class DeadlineAttachedFileDownloader @WorkerInject constructor(
    @Assisted val appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    private val deadlineDao: DeadlineDao,
    private val accountInfoRepository: AccountInfoRepository,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val cookieString =
            inputData.getString(ATTACHED_FILE_DOWNLOAD_COOKIE) ?: return Result.failure()
        val uid = inputData.getInt(ATTACHED_FILE_DOWNLOAD_UID, -1).also {
            if (it < 0) return Result.failure()
        }
        val deadline = deadlineDao.getDeadlineStatic(uid)
        if (!deadline.attached_file_list_crawled) {
            TODO("crawl")
        } else {
            deadline.attached_file_list.forEach {

            }
        }


        return Result.success()
    }

    companion object {
        const val ATTACHED_FILE_DOWNLOAD_COOKIE = "COOKIE"
        const val ATTACHED_FILE_DOWNLOAD_UID = "UID"
    }
}