package org.engrave.packup.network.pku.course

import org.engrave.packup.network.pku.iaaa.fetchIaaaToken
import org.engrave.packup.util.DummyCookie
import org.engrave.packup.util.makeURL
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

private const val PkuCoursePortalUrl = "https://course.pku.edu.cn/webapps/login"
private const val appId = "blackboard"
private const val PkuIaaaOathRedirToCourseUrl =
    "https://course.pku.edu.cn/webapps/bb-sso-bb_bb60/execute/authValidate/campusLogin"
private const val PkuCourseOathValidateRefererUrl = "https://iaaa.pku.edu.cn/iaaa/oauth.jsp"

private fun fetchCoursePortalCookies(): DummyCookie {
    val conn = URL(PkuCoursePortalUrl).openConnection() as HttpURLConnection
    conn.requestMethod = "GET"
    return DummyCookie.makeCookie(conn.headerFields["Set-Cookie"]).also { conn.disconnect() }
}

private fun fetchIaaaOathValidate(iaaaToken: String, coursePortalCookie: DummyCookie) =
    (makeURL(
        PkuIaaaOathRedirToCourseUrl, mapOf(
            "_rand" to "0.5",
            "token" to iaaaToken
        )
    ).openConnection() as HttpURLConnection)
        .apply {
            requestMethod = "GET"
            setRequestProperty("Cookie", coursePortalCookie.toString())
            setRequestProperty("Referer", PkuCourseOathValidateRefererUrl)
            if (responseCode != 200)
                throw IOException("IAAA validator responded with bad response code.")
            disconnect()
        }

fun fetchCourseLoginCookies(studentId: String, password: String) =
    fetchCoursePortalCookies().also {
        fetchIaaaOathValidate(
            fetchIaaaToken(appId, studentId, password, PkuIaaaOathRedirToCourseUrl), it
        )
    }