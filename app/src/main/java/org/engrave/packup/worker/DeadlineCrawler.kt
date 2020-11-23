package org.engrave.packup.worker

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.engrave.packup.api.pku.course.fetchCourseDeadlines
import org.engrave.packup.api.pku.course.fetchCourseLoginCookies
import org.engrave.packup.api.pku.course.fetchDeadlineIsSubmitted
import org.engrave.packup.data.account.AccountInfoRepository
import org.engrave.packup.data.deadline.Deadline
import org.engrave.packup.data.deadline.DeadlineDao
import org.engrave.packup.util.applyFormat


class DeadlineCrawler @WorkerInject constructor(
    @Assisted val appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    private val deadlineDao: DeadlineDao,
    private val accountInfoRepository: AccountInfoRepository,
    // TODO: Inject Configs
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = try {

        val accountInfo = accountInfoRepository.readAccountInfo()
        val deadlines = deadlineDao.getAllDeadlines().value
        val loggedCookie = fetchCourseLoginCookies(
            accountInfo.studentId,
            accountInfo.password
        )
        val newDeadlinesUnparsed = withContext(Dispatchers.IO) {
            fetchCourseDeadlines(
                loggedCookie
            )
        }
        showToast(newDeadlinesUnparsed.joinToString("\n") { it.endDate })
        val newDealines = newDeadlinesUnparsed.map(Deadline::fromRawJson)

        newDealines.forEach { newDeadline ->
            var existFlag = false
            var occludedDeadlineHasSubmission = false

            if (deadlines != null) {
                for (existedDdl in deadlines) {
                    if (newDeadline.isOfSameContent(existedDdl)) {
                        existFlag = true
                        occludedDeadlineHasSubmission = existedDdl.has_submission
                        break
                    }
                }
            }

            if (!existFlag || !occludedDeadlineHasSubmission) {
                val isSubmitted =
                    if (newDeadline.course_object_id.isNullOrEmpty()) false
                    else withContext(Dispatchers.IO) {
                        fetchDeadlineIsSubmitted(
                            newDeadline.course_object_id,
                            loggedCookie
                        )
                    }
                if (isSubmitted) deadlineDao.insertDeadline(
                    newDeadline.copy(has_submission = true)
                )
                else deadlineDao.insertDeadline(
                    newDeadline
                )
            }
        }
        Result.success(
            workDataOf(
                DATA_SUCCEED_PROCESSED_NUMBER_FIELD to newDealines.size
            )
        )
    } catch (e: Exception) {
        Result.failure(
            workDataOf(
                DATA_EXCEPTION_MESSAGE_FIELD to e.message
            )
        )

    }


    fun showToast(msg: String){
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(Runnable { // Run your task here
            Toast.makeText(appContext, msg, Toast.LENGTH_LONG).show()
        }, 1000)
    }
}