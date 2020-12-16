package org.engrave.packup.api.pku.course

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.engrave.packup.data.deadline.DeadlineAttachedFile
import org.engrave.packup.util.*
import org.jsoup.nodes.Document
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

private const val PkuCourseDeadlineSubmissionStatusBaseUrl =
    "https://course.pku.edu.cn/webapps/calendar/launch/attempt/_blackboard.platform.gradebook2.GradableItem-"
private const val PkuCourseDeadlineBlobsBaseUrl =
    "https://course.pku.edu.cn/"
private const val PkuCourseDeadlineBlobsBaseUrlWithoutSlash =
    "https://course.pku.edu.cn"

fun fetchDeadlineDetailHtml(
    deadlineObjectId: String,
    courseLoggedCookie: DummyCookie
) = (PkuCourseDeadlineSubmissionStatusBaseUrl + deadlineObjectId)
    .openAsHttpUrlConnection()
    .apply {
        instanceFollowRedirects = true
        setRequestProperty("Cookie", courseLoggedCookie.toString())
        requestMethod = "GET"
    }.inputStream
    .scanAsString()


@Deprecated(
    "",
    ReplaceWith("fetchDeadlineDetailHtml(detailHtml).contains(\"复查提交历史记录\")")
)

fun fetchDeadlineIsSubmitted(
    deadlineObjectId: String,
    courseLoggedCookie: DummyCookie
) = fetchDeadlineDetailHtml(deadlineObjectId, courseLoggedCookie)
    .contains("复查提交历史记录")

fun getSubmissionStatusFromSubmissionPage(
    submissionPage: String
) = submissionPage.contains("复查提交历史记录")

fun fetchDeadlineSubmitPage(
    detailHtml: Document,
    courseLoggedCookie: DummyCookie,
) = (
        PkuCourseDeadlineBlobsBaseUrlWithoutSlash + detailHtml
            .select(".submit.button-1")
            .attr("onClick")
            .substringAfter("'")
            .substringBeforeLast("'")
        )
    .openAsHttpUrlConnection()
    .attachCookie(courseLoggedCookie)
    .inputStream
    .scanAsString()
    .asDocument()

fun getDescriptionFromSubmitPage(
    submitHtml: Document
) = submitHtml.select(".vtbegenerated>.vtbegenerated>p").joinToString("\n") { it.text() }

/**
 * @param detailHtml 是否有上传的页面都可以
 */
fun getAttachedFilesFromSubmitPage(
    submitHtml: Document
) = submitHtml
    .select("a")
    .filter { it.attr("href").startsWith("/bbcswebdav") }
    .map {
        DeadlineAttachedFile(
            fileName = it.text(),
            url = it.attr("href"),
            downloadStatus = 0,
            localUri = ""
        )
    }

@Suppress("BlockingMethodInNonBlockingContext") // 实际是没有问题的,由于某种编译器bug而提示出来的,可以不用管
suspend fun downloadDeadlineAttachedFiles(
    relativeCourseUrl: String,
    courseLoggedCookieString: String,
    file: File
) = withContext(Dispatchers.IO) {
    val conn = (PkuCourseDeadlineBlobsBaseUrlWithoutSlash + relativeCourseUrl)
        .openAsHttpUrlConnection()
        .attachCookie(courseLoggedCookieString)
        .apply {
            requestMethod = "GET"
            doOutput = true
            useCaches = false
            connectTimeout = 100_000
            readTimeout = 600_000
            instanceFollowRedirects = true
        }
    val httpStatus = conn.responseCode
    if (httpStatus == 200) {
        val bis = BufferedInputStream(conn.inputStream)
        val bos = BufferedOutputStream(FileOutputStream(file))
        var b = 0
        val byArr = ByteArray(1024)
        while (bis.read(byArr).also { b = it } != -1) {
            bos.write(byArr, 0, b)
        }
        bis.close()
        bos.close()
    } else throw Exception("Invalid response code while downloading file.")
    //todo: 下载中断处理
}