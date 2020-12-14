package org.engrave.packup.api.pku.course

import org.engrave.packup.data.deadline.DeadlineAttachedFile
import org.engrave.packup.util.*
import org.jsoup.nodes.Document

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
) = submitHtml.select(".vtbegenerated>.vtbegenerated").text()

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
            path = ""
        )
    }

fun downloadDeadlineDetailFiles(
    url: String,
    courseLoggedCookie: DummyCookie
) {

}