package org.engrave.packup.api.pku.course

import org.engrave.packup.util.DummyCookie
import org.engrave.packup.util.scanAsString
import java.net.URL
import javax.net.ssl.HttpsURLConnection

private const val PkuCourseDeadlineSubmissionStatusBaseUrl =
    "https://course.pku.edu.cn/webapps/calendar/launch/attempt/_blackboard.platform.gradebook2.GradableItem-"

fun fetchDeadlineIsSubmitted(
    deadlineObjectId: String,
    courseLoginCookie: DummyCookie
) = (
        URL(
            PkuCourseDeadlineSubmissionStatusBaseUrl + deadlineObjectId
        ).openConnection() as HttpsURLConnection)
    .apply {
        instanceFollowRedirects = true
        setRequestProperty("Cookie", courseLoginCookie.toString())
        requestMethod = "GET"
    }.inputStream
    .scanAsString()
    .contains("复查提交历史记录")