package org.engrave.packup.api.pku.iaaa

import org.engrave.packup.util.DummyCookie
import org.engrave.packup.util.makeURL
import org.engrave.packup.util.scanAsString
import java.net.HttpURLConnection


private const val BaseUrl = "https://iaaa.pku.edu.cn/iaaa/oauthlogin.do"

private const val PortalRedirUrl = "https://portal.pku.edu.cn/portal2017/ssoLogin.do"
private const val PortalValidateBaseUrl = "https://portal.pku.edu.cn/portal2017/ssoLogin.do"
private const val appId = "portal2017"


const val IAAA_TOKEN_UNKNOWN_ERROR = "Unknown error."

/**
 * 获取 IAAA Token，其它需要 IAAA 登录的功能均需要 Token 来验证身份
 * @return IaaaToken: String
 */
fun fetchIaaaToken(
    appId: String = "portal2017",
    studentId: String,
    password: String,
    redirectUrl: String = "https://portal.pku.edu.cn/portal2017/ssoLogin.do"
): String {
    val requestKeys = mapOf(
        "appid" to appId,
        "userName" to studentId,
        "password" to password,
        "randCode" to "",
        "smsCode" to "",
        "optCode" to "",
        "redirUrl" to redirectUrl
    )
    val cookiesToPost = mapOf(
        "remember" to "to",
        "username" to studentId
    )
    val conn = (makeURL(BaseUrl, requestKeys).openConnection() as HttpURLConnection).apply {
        requestMethod = "POST"
        setRequestProperty("Cookie", DummyCookie.makeCookie(cookiesToPost).toString())
    }
    val responseString = conn.inputStream.scanAsString()
    conn.disconnect()
    if (responseString.contains("\"success\":false"))
        throw Exception(responseString.substringAfter("\"msg\":\"").substringBefore("\""))
    if (responseString.contains("\"success\":true"))
        return responseString.substringAfter("\"token\":\"").substringBefore("\"")
    throw Exception(IAAA_TOKEN_UNKNOWN_ERROR)
}
