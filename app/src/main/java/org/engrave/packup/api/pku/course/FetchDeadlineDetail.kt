package org.engrave.packup.api.pku.course

import org.engrave.packup.util.DummyCookie
import org.engrave.packup.util.asDocument
import org.engrave.packup.util.attachCookie
import org.engrave.packup.util.scanAsString
import org.jsoup.Jsoup
import java.net.URL
import javax.net.ssl.HttpsURLConnection

private const val PkuCourseDeadlineSubmissionStatusBaseUrl =
    "https://course.pku.edu.cn/webapps/calendar/launch/attempt/_blackboard.platform.gradebook2.GradableItem-"
private const val PkuCourseDeadlineBlobsBaseUrl =
    "https://course.pku.edu.cn/"
private const val PkuCourseDeadlineBlobsBaseUrlWithoutSlash =
    "https://course.pku.edu.cn"

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


@Deprecated(
    "",
    ReplaceWith("fetchDeadlineDetailHtml(detailHtml).contains(\"复查提交历史记录\")")
)

fun fetchDeadlineIsSubmitted(
    deadlineObjectId: String,
    courseLoggedCookie: DummyCookie
) = fetchDeadlineDetailHtml(deadlineObjectId, courseLoggedCookie)
    .contains("复查提交历史记录")

fun fetchDeadlineIsSubmitted(
    detailHtml: String
) = detailHtml.contains("复查提交历史记录")

private fun fetchDeadlineDescriptionUrl(
    detailHtml: String
) = PkuCourseDeadlineBlobsBaseUrlWithoutSlash + Jsoup.parse(detailHtml)
    .select(".submit.button-1")
    .attr("onClick")
    .substringAfter("'")
    .substringBeforeLast("'")

fun fetchDeadlineDescription(
    courseLoggedCookie: DummyCookie,
    detailHtml: String
) = (URL(fetchDeadlineDescriptionUrl(detailHtml)).openConnection() as HttpsURLConnection)
    .attachCookie(courseLoggedCookie)
    .inputStream
    .scanAsString()
    .asDocument()
    .select(".vtbegenerated>.vtbegenerated")
    .text()


fun downloadDeadlineDetailFiles(
    url: String,
    courseLoggedCookie: DummyCookie
) {

}