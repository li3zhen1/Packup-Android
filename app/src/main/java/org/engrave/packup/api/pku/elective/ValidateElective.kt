package org.engrave.packup.api.pku.elective

import org.engrave.packup.api.pku.iaaa.fetchIaaaToken
import org.engrave.packup.util.DummyCookie
import org.engrave.packup.util.makeURL
import java.net.HttpURLConnection
import java.net.URL


private const val refererElectiveUrl = "https://elective.pku.edu.cn/"
private const val PkuIaaaRedirToElectiveUrl =
    "http://elective.pku.edu.cn:80/elective2008/ssoLogin.do"
private const val PkuElectivePortalUrl = "https://elective.pku.edu.cn/elective2008"
private const val PkuElectiveValidateUrl = "http://elective.pku.edu.cn/elective2008/ssoLogin.do"
private const val appId = "syllabus"

fun fetchElectivePortalCookies(): DummyCookie {
    val conn = URL(PkuElectivePortalUrl).openConnection() as HttpURLConnection
    conn.apply {
        requestMethod = "GET"
        setRequestProperty("Referer", refererElectiveUrl)
    }
    return DummyCookie.makeCookie(conn.headerFields["Set-Cookie"])
        .also { conn.disconnect() }
}

private fun validateIaaaTokenInElective(iaaaToken: String, cookie: DummyCookie) = (makeURL(
    PkuElectiveValidateUrl,
    mapOf(
        "_rand" to "0.13777814720603",
        "token" to iaaaToken
    )
).openConnection() as HttpURLConnection).apply {
    requestMethod = "GET"
    setRequestProperty("Cookie", cookie.toString())
    if (responseCode == 301) {
        val redirectedConn =
            URL(headerFields["Location"]?.get(0)).openConnection() as HttpURLConnection
        redirectedConn.apply {
            setRequestProperty("Cookie", cookie.toString())
            responseCode
            disconnect()
        }
    }
    disconnect()
}

fun fetchElectiveLoginCookies(studentId: String, password: String): DummyCookie {
    val cookie = fetchElectivePortalCookies()
    validateIaaaTokenInElective(
        fetchIaaaToken(appId, studentId, password, PkuIaaaRedirToElectiveUrl),
        cookie
    )
    return cookie
}