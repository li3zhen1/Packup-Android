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
import org.engrave.packup.data.deadline.getByUid
import org.engrave.packup.util.asDocument


class DeadlineCrawler @WorkerInject constructor(
    @Assisted val appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    private val deadlineDao: DeadlineDao,
    private val accountInfoRepository: AccountInfoRepository,
    // TODO: Inject Configs
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = try {

        setProgress(
            workDataOf(
                NEWLY_CRAWLED_DEADLINE_NUM to 0
            )
        )
        val accountInfo = accountInfoRepository.readAccountInfo()
        val loggedCookie = fetchCourseLoginCookies(
            accountInfo.studentId,
            accountInfo.password
        )
        val newDeadlinesUnparsed = withContext(Dispatchers.IO) {
            fetchCourseDeadlines(
                loggedCookie
            )
        }
        val newDeadlines = newDeadlinesUnparsed.map(Deadline::fromRawJson)
        var newlyCrawledCount = 0

        newDeadlines.forEach { newDeadline ->
            val existedDeadline = deadlineDao.getAllDeadlinesStatic().getByUid(newDeadline.uid)
//            showToast(existedDeadline?.uid.toString())
            /**
             * 不存在相同的 Ddl
             */
            if (!newDeadline.course_object_id.isNullOrEmpty())
                if (existedDeadline == null) {
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
                        downloadAttachedFiles(appContext, loggedCookie.toString())
                    }
                    deadlineDao.insertDeadline(newDeadline)
                    newlyCrawledCount += 1
                    setProgress(
                        workDataOf(
                            NEWLY_CRAWLED_DEADLINE_NUM to newlyCrawledCount
                        )
                    )
                } else if (existedDeadline.has_submission) {
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
                    if (!existedDeadline.has_submission && hasSubmission) {
                        deadlineDao.setDeadlineSubmission(
                            existedDeadline.uid,
                            true
                        )
                    }

                    if (!existedDeadline.attached_file_list_crawled) {
                        deadlineDao.setDeadlineAttachedFiles(
                            existedDeadline.uid,
                            getAttachedFilesFromSubmitPage(submitPage)
                        )
                    }
                }
            // TODO: 记录所有操作同步到 Server
        }
        Result.success(
            workDataOf(
                DATA_SUCCEED_PROCESSED_NUMBER_FIELD to newDeadlines.size
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
            Toast.makeText(appContext, msg, Toast.LENGTH_SHORT).show()
        }, 1000)
    }
}