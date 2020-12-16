package org.engrave.packup.util

import java.net.HttpURLConnection

fun HttpURLConnection.attachCookie(dummyCookie: DummyCookie) = this.apply {
    setRequestProperty("Cookie", dummyCookie.toString())
}

fun HttpURLConnection.attachCookie(cookieString: String) = this.apply {
    setRequestProperty("Cookie", cookieString)
}