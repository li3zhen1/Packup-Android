package org.engrave.packup.api.pku.portal

import kotlinx.coroutines.delay
import org.engrave.packup.api.pku.iaaa.fetchIaaaToken
import org.engrave.packup.util.DummyCookie
import org.engrave.packup.util.makeURL
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

private const val PortalLoginBaseUrl = "https://portal.pku.edu.cn/portal2017"
private const val PortalRedirUrl = "https://portal.pku.edu.cn/portal2017/ssoLogin.do"
private const val PortalValidateBaseUrl = "https://portal.pku.edu.cn/portal2017/ssoLogin.do"
private const val appId = "portal2017"

// TODO: disconnect?
fun fetchPortalCookies(): DummyCookie =
    (URL(PortalLoginBaseUrl).openConnection() as HttpURLConnection).let { urlConnection ->
        DummyCookie.makeCookie(urlConnection.headerFields["Set-Cookie"])
            .also { urlConnection.disconnect() }
    }

fun validateIaaaTokenInPortal(iaaaToken: String, cookieBuilder: DummyCookie) =
    (makeURL(
        PortalValidateBaseUrl,
        mapOf(
            "_rand" to "0.13777814720603",
            "token" to iaaaToken
        )
    ).openConnection() as HttpURLConnection)
        .apply {
            setRequestProperty("Cookie", cookieBuilder.toString())
            connect()
            if (responseCode >= 400) {
                disconnect()
                throw IOException("Portal validator returned with a bad response code.")
            }
            disconnect()
        }


suspend fun fetchPortalLoginCookies(studentId: String, password: String): DummyCookie =
    fetchPortalCookies().let {
        validateIaaaTokenInPortal(
            fetchIaaaToken(appId, studentId, password, PortalRedirUrl),
            it
        )
        delay(50)
        return it
    }
