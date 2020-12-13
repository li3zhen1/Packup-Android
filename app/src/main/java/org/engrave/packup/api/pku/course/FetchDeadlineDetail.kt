package org.engrave.packup.api.pku.course

import org.engrave.packup.util.DummyCookie
import org.engrave.packup.util.scanAsString
import java.net.URL
import javax.net.ssl.HttpsURLConnection

private const val PkuCourseDeadlineSubmissionStatusBaseUrl =
    "https://course.pku.edu.cn/webapps/calendar/launch/attempt/_blackboard.platform.gradebook2.GradableItem-"
private const val PkuCourseDeadlineBlobsBaseUrl =
    "https://course.pku.edu.cn/"

fun fetchDeadlineDetailHtml(
    deadlineObjectId: String,
    courseLoggedCookie: DummyCookie
) = (
        URL(
            PkuCourseDeadlineSubmissionStatusBaseUrl + deadlineObjectId
        ).openConnection() as HttpsURLConnection)
    .apply {
        instanceFollowRedirects = true
        setRequestProperty("Cookie", courseLoggedCookie.toString())
        requestMethod = "GET"
    }.inputStream
    .scanAsString()


fun fetchDeadlineIsSubmitted(
    deadlineObjectId: String,
    courseLoggedCookie: DummyCookie
) = fetchDeadlineDetailHtml(deadlineObjectId, courseLoggedCookie)
    .contains("复查提交历史记录")

fun fetchDeadlineIsSubmitted(
    detailHtml: String
) = detailHtml.contains("复查提交历史记录")


fun downloadDeadlineDetailFiles(
    url: String,
    courseLoggedCookie: DummyCookie
) {

}