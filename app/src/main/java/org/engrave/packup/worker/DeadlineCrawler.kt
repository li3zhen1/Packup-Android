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
import org.engrave.packup.api.pku.course.*
import org.engrave.packup.data.account.AccountInfoRepository
import org.engrave.packup.data.deadline.Deadline
import org.engrave.packup.data.deadline.DeadlineDao
import org.engrave.packup.util.asDocument


class DeadlineCrawler @WorkerInject constructor(
    @Assisted val appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    private val deadlineDao: DeadlineDao,
    private val accountInfoRepository: AccountInfoRepository,
    // TODO: Inject Configs
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = try {

//        showToast("Worker Dowork")
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

        val newDealines = newDeadlinesUnparsed.map(Deadline::fromRawJson)

        newDealines.forEach { newDeadline ->
            var existSameKey = false
            var conflictDeadline: Deadline? = null
            if (deadlines != null) {
                for (existedDdl in deadlines) {
                    if (newDeadline.keyFieldsSameWith(existedDdl)) {
                        existSameKey = true
                        conflictDeadline = existedDdl
                        break
                    }
                }
            }

            /**
             * 不存在相同的 Ddl
             */
            if (!newDeadline.course_object_id.isNullOrEmpty())
                if (!existSameKey) {
                    /**
                     * 数据库里没有相同 DDL ==> 全量更新
                     */
                    val detailHtmlStr = withContext(Dispatchers.IO) {
                        fetchDeadlineDetailHtml(
                            newDeadline.course_object_id,
                            loggedCookie
                        )
                    }
                    val detailPage = detailHtmlStr.asDocument()
                    val isSubmitted = getSubmissionStatusFromSubmissionPage(detailHtmlStr)
                    val submitPage =
                        if (isSubmitted) fetchDeadlineSubmitPage(detailPage, loggedCookie)
                        else detailPage
                    val attachedFiles = getAttachedFilesFromSubmitPage(submitPage)
                    val desc = getDescriptionFromSubmitPage(submitPage)

                    newDeadline.apply {
                        description = desc
                        attached_file_list = attachedFiles
                        has_submission = isSubmitted
                        attached_file_list_crawled = true
                    }
                    deadlineDao.insertDeadline(newDeadline)
                } else if (conflictDeadline != null && !conflictDeadline.has_submission) {
                    val detailHtmlStr by lazy {
                        fetchDeadlineDetailHtml(
                            newDeadline.course_object_id,
                            loggedCookie
                        )
                    }
                    val hasSubmission by lazy {
                        getSubmissionStatusFromSubmissionPage(detailHtmlStr)
                    }
                    val submitPage by lazy {
                        if (hasSubmission) fetchDeadlineSubmitPage(
                            detailHtmlStr.asDocument(),
                            loggedCookie
                        )
                        else detailHtmlStr.asDocument()
                    }

                    /**
                     * 有相同无提交 => 检查有没有提交，仅更新 has_submission 字段
                     */
                    if (!conflictDeadline.has_submission && hasSubmission) {
                        deadlineDao.setDeadlineSubmission(
                            conflictDeadline.uid,
                            true
                        )
                    }

                    if (!conflictDeadline.attached_file_list_crawled) {
                        deadlineDao.setDeadlineAttachedFiles(
                            conflictDeadline.uid,
                            getAttachedFilesFromSubmitPage(submitPage)
                        )
                    }
                }

            // TODO: 记录所有操作同步到 Server
        }
        Result.success(
            workDataOf(
                DATA_SUCCEED_PROCESSED_NUMBER_FIELD to newDealines.size
            )
        )
    } catch (e: Exception) {
        showToast(e.message.toString())
        Result.failure(
            workDataOf(
                DATA_EXCEPTION_MESSAGE_FIELD to e.message
            )
        )
    }

    private fun showToast(msg: String) {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({ // Run your task here
            Toast.makeText(appContext, msg, Toast.LENGTH_LONG).show()
        }, 1000)
    }
}